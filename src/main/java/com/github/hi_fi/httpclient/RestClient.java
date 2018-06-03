package com.github.hi_fi.httpclient;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.WinHttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.github.hi_fi.httpclient.domain.Authentication;
import com.github.hi_fi.httpclient.domain.Proxy;
import com.github.hi_fi.httpclient.domain.Session;
import com.github.hi_fi.httpclient.extend.CustomRedirectStrategy;
import com.github.hi_fi.httpclient.extend.HttpDeleteWithBody;
import com.github.hi_fi.httpclient.security.Auth;
import com.github.hi_fi.httpclient.security.Certificate;

public class RestClient {

	private final Log logger = LogFactory.getLog(RestClient.class);
	private static Map<String, Session> sessions = new HashMap<String, Session>();

	public Session getSession(String alias) {
		return sessions.get(alias);
	}

	public void createSession(String alias, String url, Map<String, String> headers, Authentication auth, String verify,
			Boolean debug) {
		this.createSession(alias, url, headers, auth, verify, debug, "", "", true, false);
	}

	public void createSession(String alias, String url, Map<String, String> headers, Authentication auth, String verify,
			Boolean debug, Proxy proxy) {
		this.createSession(alias, url, headers, auth, verify, debug, "", "", true, false, proxy);
	}

	public void createSession(String alias, String url, Map<String, String> headers, Authentication auth, String verify,
			Boolean debug, String password, boolean verifyHost, boolean selfSigned, Proxy proxy) {
		this.createSession(alias, url, headers, auth, verify, debug, "", password, verifyHost, selfSigned, proxy);
	}

	public void createSession(String alias, String url, Map<String, String> headers, Authentication auth, String verify,
			Boolean debug, String loggerClass, String password, boolean verifyHost, boolean selfSigned) {
		this.createSession(alias, url, headers, auth, verify, false, "", password, verifyHost, selfSigned, null);
	}

	public void createSession(String alias, String url, Map<String, String> headers, Authentication auth, String verify,
			Boolean debug, String loggerClass, String password, boolean verifyHost, boolean selfSigned, Proxy proxy) {

		HostnameVerifier defaultHostnameVerifier = verifyHost ? null : NoopHostnameVerifier.INSTANCE;
		TrustStrategy trustStrategy = selfSigned ? new TrustSelfSignedStrategy() : null;

		if (!loggerClass.isEmpty()) {
			System.setProperty("org.apache.commons.logging.Log", loggerClass);
			System.setProperty("org.apache.commons.logging.robotlogger.log.org.apache.http", debug ? "DEBUG" : "INFO");
		}
		HttpHost target;
		try {
			target = URIUtils.extractHost(new URI(url));
		} catch (URISyntaxException e) {
			throw new RuntimeException("Parsing of URL failed. Error message: " + e.getMessage());
		}
		Session session = new Session();
		if (proxy != null) { session.setProxy(proxy); }
		session.setContext(this.createContext(auth, target));
		session.setClient(this.createHttpClient(auth, verify, target, false, password, null, null, proxy));
		session.setUrl(url);
		session.setHeaders(headers);
		session.setHttpHost(target);
		session.setVerify(verify);
		session.setAuthentication(auth);
		session.setPassword(password);
		session.setHostnameVerifier(defaultHostnameVerifier);
		session.setTrustStrategy(trustStrategy);
		sessions.put(alias, session);
	}

	public void makeGetRequest(String alias, String uri, Map<String, String> headers, Map<String, String> parameters,
			boolean allowRedirects) {
		logger.debug("Making GET request");
		HttpGet getRequest = new HttpGet(this.buildUrl(alias, uri, parameters));
		getRequest = this.setHeaders(getRequest, headers);
		getRequest.setConfig(RequestConfig.custom().setRedirectsEnabled(allowRedirects).build());
		Session session = this.getSession(alias);
		this.makeRequest(getRequest, session);
	}

	public void makeHeadRequest(String alias, String uri, Map<String, String> headers, Boolean allowRedirects) {
		logger.debug("Making HEAD request");
		HttpHead headRequest = new HttpHead(this.buildUrl(alias, uri));
		headRequest = this.setHeaders(headRequest, headers);
		headRequest.setConfig(RequestConfig.custom().setRedirectsEnabled(allowRedirects).build());
		Session session = this.getSession(alias);
		this.makeRequest(headRequest, session);
	}

	public void makeOptionsRequest(String alias, String uri, Map<String, String> headers, Boolean allowRedirects) {
		logger.debug("Making OPTIONS request");
		HttpOptions patchRequest = new HttpOptions(this.buildUrl(alias, uri));
		patchRequest = this.setHeaders(patchRequest, headers);

		if (allowRedirects) {
			Session session = this.getSession(alias);
			session.setClient(this.createHttpClient(session.getAuthentication(), session.getVerify(),
					session.getHttpHost(), true));
		}
		Session session = this.getSession(alias);
		this.makeRequest(patchRequest, session);
	}

	public void makePatchRequest(String alias, String uri, Object data, Map<String, String> headers,
			Map<String, String> files, Boolean allowRedirects) {
		logger.debug("Making PATCH request");
		HttpPatch patchRequest = new HttpPatch(this.buildUrl(alias, uri));
		patchRequest = this.setHeaders(patchRequest, headers);
		if (data.toString().length() > 0) {
			logger.debug(data);
			patchRequest.setEntity(this.createDataEntity(data));
		}
		if (files.entrySet().size() > 0) {
			logger.debug(files);
			patchRequest.setEntity(this.createFileEntity(files));
		}
		if (allowRedirects) {
			Session session = this.getSession(alias);
			session.setClient(this.createHttpClient(session.getAuthentication(), session.getVerify(),
					session.getHttpHost(), true));
		}
		Session session = this.getSession(alias);
		this.makeRequest(patchRequest, session);
	}

	public void makePostRequest(String alias, String uri, Object data, Map<String, String> parameters,
			Map<String, String> headers, Map<String, String> files, Boolean allowRedirects) {
		logger.debug("Making POST request");
		HttpPost postRequest = new HttpPost(this.buildUrl(alias, uri, parameters));
		postRequest = this.setHeaders(postRequest, headers);
		if (data.toString().length() > 0) {
			logger.debug(data);
			postRequest.setEntity(this.createDataEntity(data));
		}
		if (files.entrySet().size() > 0) {
			logger.debug(files);
			postRequest.setEntity(this.createFileEntity(files));
		}
		if (allowRedirects) {
			Session session = this.getSession(alias);
			session.setClient(
					this.createHttpClient(session.getAuthentication(), session.getVerify(), session.getHttpHost(), true,
							session.getPassword(), session.getTrustStrategy(), session.getHostnameVerifier()));
		}
		Session session = this.getSession(alias);
		this.makeRequest(postRequest, session);
	}

	public void makePutRequest(String alias, String uri, Object data, Map<String, String> parameters,
			Map<String, String> headers, Map<String, String> files, Boolean allowRedirects) {
		logger.debug("Making PUT request");
		HttpPut putRequest = new HttpPut(this.buildUrl(alias, uri, parameters));
		putRequest = this.setHeaders(putRequest, headers);
		if (data.toString().length() > 0) {
			logger.debug(data);
			putRequest.setEntity(this.createDataEntity(data));
		}
		if (files.entrySet().size() > 0) {
			logger.debug(files);
			putRequest.setEntity(this.createFileEntity(files));
		}
		if (allowRedirects) {
			Session session = this.getSession(alias);
			session.setClient(this.createHttpClient(session.getAuthentication(), session.getVerify(),
					session.getHttpHost(), true));
		}
		Session session = this.getSession(alias);
		this.makeRequest(putRequest, session);
	}

	public void makeDeleteRequest(String alias, String uri, Object data, Map<String, String> parameters,
			Map<String, String> headers, Boolean allowRedirects) {
		logger.debug("Making DELETE request");
		HttpDeleteWithBody deleteRequest = new HttpDeleteWithBody(this.buildUrl(alias, uri, parameters));
		deleteRequest = this.setHeaders(deleteRequest, headers);
		if (data.toString().length() > 0) {
			logger.debug(data);
			deleteRequest.setEntity(this.createDataEntity(data));
		}

		if (allowRedirects) {
			Session session = this.getSession(alias);
			session.setClient(this.createHttpClient(session.getAuthentication(), session.getVerify(),
					session.getHttpHost(), true));
		}
		Session session = this.getSession(alias);
		this.makeRequest(deleteRequest, session);
	}

	@SuppressWarnings("unchecked")
	private HttpEntity createDataEntity(Object data) {
		try {
			if (data instanceof Map) {
				List<NameValuePair> params = new ArrayList<NameValuePair>(0);
				for (Entry<String, Object> entry : ((Map<String, Object>) data).entrySet()) {
					params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
				}
				return new UrlEncodedFormEntity(params, "UTF-8");
			} else {
				return new StringEntity(data.toString(), "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unsupported encoding noticed. Error message: " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private HttpEntity createFileEntity(Object files) {
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		for (Entry<String, Object> entry : ((Map<String, Object>) files).entrySet()) {
			if (new File(entry.getValue().toString()).exists()) {
				builder.addPart(entry.getKey(),
						new FileBody(new File(entry.getValue().toString()), ContentType.DEFAULT_BINARY));
			} else {
				builder.addPart(entry.getKey(), new StringBody(entry.getValue().toString(), ContentType.DEFAULT_TEXT));
			}
		}
		return builder.build();
	}

	private void makeRequest(HttpRequestBase request, Session session) {
		if (session.getProxy().isInUse()) {
			request.getConfig();
			request.setConfig(RequestConfig.custom().setProxy(session.getProxy().getHttpHost()).build());
		}
		request = this.setHeaders(request, session.getHeaders());
		try {
			session.setResponse(session.getClient().execute(request, session.getContext()));
		} catch (ClientProtocolException e) {
			throw new RuntimeException("Client protocol Exception. Message: " + e.getMessage());
		} catch (IOException e) {
			throw new RuntimeException("IO exception. Message: " + e.getMessage());
		}
	}

	private HttpClientContext createContext(Authentication auth, HttpHost target) {
		HttpClientContext httpClientContext = HttpClientContext.create();
		CookieStore cookieStore = new BasicCookieStore();
		httpClientContext.setCookieStore(cookieStore);
		if (auth.usePreemptiveAuthentication()) {
			httpClientContext.setAuthCache(new Auth().getAuthCache(auth, target));
		}
		return httpClientContext;
	}

	private HttpClient createHttpClient(Authentication auth, String verify, HttpHost target, Boolean postRedirects) {
		return createHttpClient(auth, verify, target, postRedirects, null, null, null);
	}

	private HttpClient createHttpClient(Authentication auth, String verify, HttpHost target, Boolean postRedirects,
			String password, TrustStrategy keystoreTrustStrategy, HostnameVerifier keystoreHostnameVerifier) {
		return createHttpClient(auth, verify, target, postRedirects, null, null, null, null);
	}

	private HttpClient createHttpClient(Authentication auth, String verify, HttpHost target, Boolean postRedirects,
			String password, TrustStrategy keystoreTrustStrategy, HostnameVerifier keystoreHostnameVerifier,
			Proxy proxy) {
		Certificate certificate = new Certificate();
		Auth authHelper = new Auth();
		HttpClientBuilder httpClientBuilder = WinHttpClients.custom();
		Builder requestConfig = RequestConfig.custom();
		requestConfig.setCookieSpec(CookieSpecs.DEFAULT);

		logger.debug("Verify value: " + verify);
		logger.debug((new File(verify).getAbsolutePath()));

		if (new File(verify).exists()) {
			logger.debug("Loading custom keystore");
			httpClientBuilder.setSSLSocketFactory(
					certificate.allowAllCertificates(certificate.createCustomKeyStore(verify.toString(), password),
							password, keystoreTrustStrategy, keystoreHostnameVerifier));
		} else if (!Boolean.parseBoolean(verify.toString())) {
			logger.debug("Allowing all certificates");
			httpClientBuilder.setSSLSocketFactory(certificate.allowAllCertificates(null));
		}

		if (auth.isAuthenticable()) {
			httpClientBuilder.setDefaultCredentialsProvider(authHelper.getCredentialsProvider(auth, target));
		}

		if (proxy != null && proxy.isInUse()) {
			logger.debug("Enabling proxy");
			if (proxy.isAuthenticable()) {
				logger.debug("Setting proxy credentials");
				httpClientBuilder.setDefaultCredentialsProvider(
						authHelper.getCredentialsProvider(proxy.getAuth(), proxy.getHttpHost()));
			}
			requestConfig.setProxy(proxy.getHttpHost());
		}

		if (postRedirects) {
			httpClientBuilder.setRedirectStrategy(new CustomRedirectStrategy());
		}
		httpClientBuilder.setDefaultRequestConfig(requestConfig.build());

		return httpClientBuilder.build();
	}

	private String buildUrl(String alias, String uri) {
		return this.buildUrl(alias, uri, new HashMap<String, String>());
	}

	private String buildUrl(String alias, String uri, Map<String, String> parameters) {
		String url = this.getSession(alias).getUrl();
		if (uri.length() > 0) {
			String separator = uri.startsWith("/") ? "" : "/";
			url = url + separator + uri;
		}
		String parameterString = "";
		if (parameters != null) {
			for (String key : parameters.keySet()) {
				try {
					parameterString += key + "=" + URLEncoder.encode(parameters.get(key), "UTF-8") + "&";
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("Unsupported encoding noticed. Error message: " + e.getMessage());
				}
			}
		}
		url += parameterString.length() > 1 ? "?" + parameterString.substring(0, parameterString.length() - 1) : "";
		return url;
	}

	private <T> T setHeaders(T request, Map<String, String> headers) {
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				((HttpRequest) request).setHeader(entry.getKey(), entry.getValue());
			}
		}
		return request;
	}

}
