package com.github.hi_fi.httpclient.domain;

import java.io.IOException;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

public class Session {
	private String alias;
	private String url;
	private HttpClientContext context;
	private HttpClient client;
	private ResponseData responseData = new ResponseData();
	private HttpResponse response;
	private Authentication authentication;
	private HttpHost httpHost;
	private String verify;
	private Map<String, String> headers;
    private HostnameVerifier hostnameVerifier;
    private TrustStrategy trustStrategy;	
    private String password;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public HttpClientContext getContext() {
		return context;
	}

	public void setContext(HttpClientContext context) {
		this.context = context;
	}

	public HttpClient getClient() {
		return client;
	}

	public void setClient(HttpClient client) {
		this.client = client;
	}

	public HttpResponse getResponse() {
		return response;
	}

	public void setResponse(HttpResponse response) {
		this.response = response;
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				this.setResponseBody(EntityUtils.toString(entity, "UTF-8"));
			}
			this.responseData.setStatusCode(response.getStatusLine().getStatusCode());
			this.responseData.setHeaders(response.getAllHeaders());
		} catch (ParseException e) {
			throw new RuntimeException("Parsing exception. Message: "+e.getMessage());
		} catch (IOException e) {
			throw new RuntimeException("IO exception. Message: "+e.getMessage());
		}
	}

	public String getResponseBody() {
		return responseData.getText();
	}

	private void setResponseBody(String responseBody) {
		this.responseData.setText(responseBody);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		if (url.endsWith("/")) {
			this.url = url.substring(0, url.length()-1);
		}
	}

	public Authentication getAuthentication() {
		return authentication;
	}

	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

	public HttpHost getHttpHost() {
		return httpHost;
	}

	public void setHttpHost(HttpHost httpHost) {
		this.httpHost = httpHost;
	}
	
	public ResponseData getResponseData() {
		return this.responseData;
	}

	public String getVerify() {
		return verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

    /**
     * Get the hostnameVerifier.
     * @return the hostnameVerifier
     */
    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    /**
     * Set the hostnameVerifier.
     * @param hostnameVerifier the hostnameVerifier to set
     */
    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    /**
     * Get the trustStrategy.
     * @return the trustStrategy
     */
    public TrustStrategy getTrustStrategy() {
        return trustStrategy;
    }

    /**
     * Set the trustStrategy.
     * @param trustStrategy the trustStrategy to set
     */
    public void setTrustStrategy(TrustStrategy trustStrategy) {
        this.trustStrategy = trustStrategy;
    }

    /**
     * Get the password.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password.
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
	
	
}
