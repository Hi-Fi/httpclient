package com.github.hi_fi.httpclient;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.hi_fi.httpclient.domain.Authentication;
import com.github.hi_fi.httpclient.domain.Proxy;
import com.github.hi_fi.httpclient.domain.Session;

public class RestClientTest {

	static RestClient rc;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		rc = new RestClient();
		Map<String, String> robotDictionary = new HashMap<String, String>();
		robotDictionary.put("host", "localhost");
		robotDictionary.put("port", "8080");
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

	@Test
	public void verifySessionContent() {
		Session session = rc.getSession("test");
		Proxy proxy = session.getProxy();
		assert proxy.isInUse();
		assert proxy.getPort() == 8080;
		assert proxy.getHost().equals("localhost");
		
	}

}
