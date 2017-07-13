package com.github.hi_fi.httpclient;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.hi_fi.httpclient.domain.Authentication;
import com.github.hi_fi.httpclient.domain.Proxy;
import com.github.hi_fi.httpclient.domain.Session;

public class RestClientIT {

	static RestClient rc;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		rc = new RestClient();
		Map<String, String> robotDictionary = new HashMap<String, String>();
		robotDictionary.put("host", "127.0.0.1");
		robotDictionary.put("port", "1234");
		Proxy proxy = new Proxy(robotDictionary);
		rc.createSession("test", "http://www.google.com", new HashMap<String, String>(),
				Authentication.getAuthentication(new ArrayList<String>()), "false", true, proxy);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = RuntimeException.class)
	public void testMakeGetRequest() {
		rc.makeGetRequest("test", "/", new HashMap<String, String>(), new HashMap<String, String>(), true);
	}

}
