package com.github.hi_fi.httpclient.domain;

import java.util.Arrays;
import java.util.Map;

import org.apache.http.HttpHost;

public class Proxy {

	private Protocol protocol;
	private String host;
	private int port;
	private Authentication auth;
	private boolean authenticable = false;
	private boolean use = false;

	public enum Protocol {
		HTTP, HTTPS
	}
	
	public Proxy(Map<String, String> robotDictionary) {
		if (robotDictionary.size() > 0) {
			try {
				this.protocol = Protocol.valueOf(robotDictionary.get("protocol"));
			} catch (Exception e) {
				throw new RuntimeException("Incorrect or unsupported protocol ("+robotDictionary.get("protocol")+") given. Message: "+e.getMessage());
			}
			this.host = robotDictionary.get("host");
			this.port = Integer.parseInt(robotDictionary.get("port"));
			if (robotDictionary.get("username") != null && robotDictionary.get("password") != null) {
				this.authenticable = true;
				this.auth = Authentication.getAuthentication(Arrays.asList(robotDictionary.get("username"), robotDictionary.get("password")));
			}
		}
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = Protocol.valueOf(protocol.toUpperCase());
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
}
