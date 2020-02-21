package com.github.hi_fi.httpclient.cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cookies {

    public List<String> asHeaderList = new ArrayList<String>();
    public Map<String, String> asHeaderDictionary = new HashMap<String, String>();
    public List<Cookie> asList = new ArrayList<Cookie>();
    public Map<String, Cookie> asDictionary = new HashMap<String, Cookie>();

    public void add(String setCookieValue) {
        Cookie cookie = new Cookie(setCookieValue);
        asList.add(cookie);
        asDictionary.put(cookie.name(), cookie);
        asHeaderList.add(setCookieValue);
        asHeaderDictionary.put(cookie.name(), setCookieValue);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append('[');
        boolean first = true;
        for (String value : asHeaderList) {
            if (!first) {
                res.append(',');
            } else {
                first = false;
            }
            res.append('\"');
            res.append(value);
            res.append('\"');
        }
        res.append(']');
        return res.toString();
    }

}
