package com.github.hi_fi.httpclient.security;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;

import com.github.hi_fi.httpclient.domain.Authentication;

public class Auth {
	
	public AuthCache getAuthCache(Authentication auth, HttpHost target) {
		AuthCache authCache = new BasicAuthCache();
		AuthScheme authScheme = null;
		switch (auth.getType()) {
		case BASIC:
			authScheme = new BasicScheme();
			break;
		case DIGEST:
			authScheme = new DigestScheme();
			break;
		case NTLM:
			break;
		}

        authCache.put(target, authScheme);
        return authCache;
	}
	
	public CredentialsProvider getCredentialsProvider(Authentication auth, HttpHost target) {
		CredentialsProvider credsProvider = new BasicCredentialsProvider();

        credsProvider.setCredentials(
        		new AuthScope(target.getHostName(), target.getPort()),
                new UsernamePasswordCredentials(auth.getUsername(), auth.getPassword()));
        return credsProvider;
	}

}
