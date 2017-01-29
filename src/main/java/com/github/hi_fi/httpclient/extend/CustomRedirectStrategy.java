package com.github.hi_fi.httpclient.extend;

import org.apache.http.annotation.Immutable;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultRedirectStrategy;

/**
 * Custom {@link org.apache.http.client.RedirectStrategy} implementation
 * that automatically redirects all HEAD, GET, PUT, POST, DELETE, PATCH and OPTIONS requests.
 */
@Immutable
public class CustomRedirectStrategy extends DefaultRedirectStrategy {

    /**
     * Redirectable methods.
     */
    private static final String[] REDIRECT_METHODS = new String[] {
        HttpGet.METHOD_NAME,
        HttpPost.METHOD_NAME,
        HttpHead.METHOD_NAME,
        HttpDelete.METHOD_NAME,
        HttpPut.METHOD_NAME,
        HttpPatch.METHOD_NAME,
        HttpOptions.METHOD_NAME
    };

    @Override
    protected boolean isRedirectable(final String method) {
        for (final String m: REDIRECT_METHODS) {
            if (m.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }

}

