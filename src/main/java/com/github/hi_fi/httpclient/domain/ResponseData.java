package com.github.hi_fi.httpclient.domain;

import com.github.hi_fi.httpclient.cookie.Cookies;
import com.google.gson.Gson;
import org.apache.http.Header;

import java.util.HashMap;
import java.util.Map;

public class ResponseData {

    public int status_code;
    public String text;
    public String content;
    public Object json;
    public Map<String, String> headers = new HashMap<String, String>();
    public Cookies cookies = new Cookies();

    public void setHeaders(Header[] headerArray) {
        for (Header header : headerArray) {
            if ("Set-Cookie".equalsIgnoreCase(header.getName())) {
                cookies.add(header.getValue());
            } else {
                this.headers.put(header.getName(), header.getValue());
            }
        }
    }

    public int getStatusCode() {
        return status_code;
    }


    public void setStatusCode(int status_code) {
        this.status_code = status_code;
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
        this.content = text;
        try {
            this.json = new Gson().fromJson(text.replace("u'", "'"), Object.class);
        } catch (Exception e) {
        }
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
