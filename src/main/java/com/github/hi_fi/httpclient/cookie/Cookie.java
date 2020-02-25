package com.github.hi_fi.httpclient.cookie;

import java.util.LinkedHashMap;

public class Cookie extends LinkedHashMap<String, String> {

    private static final long serialVersionUID = -5706229460116409983L;

    public Cookie(String setCookieValue) {
        parse(setCookieValue);
    }

    public String name() {
        return size() == 0 ? "" : entrySet().iterator().next().getKey();
    }

    public String value() {
        return size() == 0 ? "" : entrySet().iterator().next().getValue();
    }

    protected void parse(String setCookieValue) {
        String[] pairs = setCookieValue.split("; ");
        for (String pair : pairs) {
            String[] parts = pair.split("=", 2);
            String name = parts[0];
            String value = parts.length == 2 ? parts[1] : "";
            put(name, value);
        }
    }

}
