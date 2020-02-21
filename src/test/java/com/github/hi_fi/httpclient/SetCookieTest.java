package com.github.hi_fi.httpclient;

import com.github.hi_fi.httpclient.cookie.Cookies;
import com.github.hi_fi.httpclient.domain.Authentication;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class SetCookieTest {

    static RestClient rc;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        rc = new RestClient();
        rc.createSession("test", "http://www.paypal.com", new HashMap<String, String>(),
                Authentication.getAuthentication(new ArrayList<String>()), "false", true);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public void verifyCookieParsing() {
        // Happy path testing
        rc.makeGetRequest("test", "/", new HashMap<String, String>(), new HashMap<String, String>(), true);
        Cookies cookies = rc.getSession("test").getResponseData().cookies;
        assert cookies.asHeaderList.size() > 5;
        assert cookies.asHeaderDictionary.containsKey("cookie_check");
        assert cookies.asList.size() > 5;
        assert cookies.asDictionary.containsKey("enforce_policy");
    }

}
