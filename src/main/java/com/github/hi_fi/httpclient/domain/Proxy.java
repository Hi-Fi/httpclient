package com.github.hi_fi.httpclient.domain;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;

public class Proxy {

	private final Log logger = LogFactory.getLog(Proxy.class);
	
	private String host;
	private int port;
	private Authentication auth;
	private boolean authenticable = false;
	private boolean use = false;

	public Proxy(Map<String, String> robotDictionary) {
		if (robotDictionary.size() > 0) {
			this.host = robotDictionary.get("host");
			this.port = Integer.parseInt(robotDictionary.get("port"));
			logger.debug(String.format("Created proxy through %s:%s", this.host, this.port));
			if (robotDictionary.get("username") != null && robotDictionary.get("password") != null) {
				this.authenticable = true;
				this.auth = Authentication.getAuthentication(Arrays.asList(robotDictionary.get("username"), robotDictionary.get("password")));
				logger.debug(String.format("Proxy authentication with user %s", this.auth.getUsername()));
			}
			
		}
	}

	public String getHost() {
		return host;
	}
	
	public HttpHost getHttpHost() {
		if (this.port != 0) {
			return new HttpHost(this.host, this.port);
		} else {
			return new HttpHost(this.host);
		}
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Authentication getAuth() {
		return auth;
	}

	public void setAuth(Authentication auth) {
		this.auth = auth;
		this.authenticable = true;
	}
	
	public boolean isAuthenticable() {
		return this.authenticable;
	}
	
	public boolean isInUse() {
		return this.use;
	}
	
	public String toString() {
		return String.format("Proxy set through %s:%s", this.host, this.port);
		
	}
}
