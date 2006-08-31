/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.views.util;

import com.mockobjects.dynamic.Mock;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.config.Settings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;


/**
 * Test case for UrlHelper.
 * 
 */
public class UrlHelperTest extends StrutsTestCase {
	
	
	
	public void testForceAddSchemeHostAndPort() throws Exception {
		String expectedUrl = "http://localhost/contextPath/path1/path2/myAction.action";
		
		Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
		mockHttpServletRequest.expectAndReturn("getScheme", "http");
		mockHttpServletRequest.expectAndReturn("getServerName", "localhost");
        mockHttpServletRequest.expectAndReturn("getContextPath", "/contextPath");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedUrl, expectedUrl);
		
		String result = UrlHelper.buildUrl("/path1/path2/myAction.action", (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse)mockHttpServletResponse.proxy(), null, "http", true, true, true);
		assertEquals(expectedUrl, result);
		mockHttpServletRequest.verify();
	}
	
	public void testDoNotForceAddSchemeHostAndPort() throws Exception {
		String expectedUrl = "/contextPath/path1/path2/myAction.action";
		
		Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
		mockHttpServletRequest.expectAndReturn("getScheme", "http");
		mockHttpServletRequest.expectAndReturn("getServerName", "localhost");
        mockHttpServletRequest.expectAndReturn("getContextPath", "/contextPath");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedUrl, expectedUrl);
		
		String result = UrlHelper.buildUrl("/path1/path2/myAction.action", (HttpServletRequest)mockHttpServletRequest.proxy(), (HttpServletResponse)mockHttpServletResponse.proxy(), null, "http", true, true, false);
		
		assertEquals(expectedUrl, result);
	}
	
	
	public void testBuildParametersStringWithUrlHavingSomeExistingParameters() throws Exception {
		String expectedUrl = "http://localhost:8080/myContext/myPage.jsp?initParam=initValue&amp;param1=value1&amp;param2=value2";
		
		Map parameters = new LinkedHashMap();
		parameters.put("param1", "value1");
		parameters.put("param2", "value2");
		
		StringBuffer url = new StringBuffer("http://localhost:8080/myContext/myPage.jsp?initParam=initValue");
		
		UrlHelper.buildParametersString(parameters, url);
		
		assertEquals(
		   expectedUrl, url.toString());
	}
	
	

    public void testBuildWithRootContext() {
        String expectedUrl = "/MyAction.action";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getContextPath", "/");
        mockHttpServletRequest.expectAndReturn("getScheme", "http");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedUrl, expectedUrl);

        String actualUrl = UrlHelper.buildUrl(expectedUrl, (HttpServletRequest) mockHttpServletRequest.proxy(),
                (HttpServletResponse) mockHttpServletResponse.proxy(), new HashMap());
        assertEquals(expectedUrl, actualUrl);
    }

    /**
     * just one &, not &amp;
     */
    public void testBuildUrlCorrectlyAddsAmp() {
        String expectedString = "my.actionName?foo=bar&amp;hello=world";
        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "my.actionName";
        TreeMap params = new TreeMap();
        params.put("hello", "world");
        params.put("foo", "bar");

        String urlString = UrlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params);
        assertEquals(expectedString, urlString);
    }

    public void testBuildUrlWithStringArray() {
        String expectedString = "my.actionName?foo=bar&amp;hello=earth&amp;hello=mars";
        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "my.actionName";
        TreeMap params = new TreeMap();
        params.put("hello", new String[]{"earth", "mars"});
        params.put("foo", "bar");

        String urlString = UrlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params);
        assertEquals(expectedString, urlString);
    }

    /**
     * The UrlHelper should build a URL that starts with "https" followed by the server name when the scheme of the
     * current request is "http" and the port for the "https" scheme is 443.
     */
    public void testSwitchToHttpsScheme() {
        String expectedString = "https://www.mydomain.com/mywebapp/MyAction.action?foo=bar&amp;hello=earth&amp;hello=mars";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getServerName", "www.mydomain.com");
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        mockHttpServletRequest.expectAndReturn("getServerPort", 80);
        mockHttpServletRequest.expectAndReturn("getContextPath", "/mywebapp");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "/MyAction.action";
        TreeMap params = new TreeMap();
        params.put("hello", new String[]{"earth", "mars"});
        params.put("foo", "bar");

        String urlString = UrlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, "https", true, true);
        assertEquals(expectedString, urlString);
    }

    /**
     * The UrlHelper should build a URL that starts with "http" followed by the server name when the scheme of the
     * current request is "https" and the port for the "http" scheme is 80.
     */
    public void testSwitchToHttpScheme() {
        String expectedString = "http://www.mydomain.com/mywebapp/MyAction.action?foo=bar&amp;hello=earth&amp;hello=mars";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getServerName", "www.mydomain.com");
        mockHttpServletRequest.expectAndReturn("getScheme", "https");
        mockHttpServletRequest.expectAndReturn("getServerPort", 443);
        mockHttpServletRequest.expectAndReturn("getContextPath", "/mywebapp");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "/MyAction.action";
        TreeMap params = new TreeMap();
        params.put("hello", new String[]{"earth", "mars"});
        params.put("foo", "bar");

        String urlString = UrlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, "http", true, true);
        assertEquals(expectedString, urlString);
    }

    /**
     * This test is similar to {@link #testSwitchToHttpsScheme()} with the HTTP port equal to 7001 and the HTTPS port
     * equal to 7002.
     */
    public void testSwitchToHttpsNonDefaultPort() {

        String expectedString = "https://www.mydomain.com:7002/mywebapp/MyAction.action?foo=bar&amp;hello=earth&amp;hello=mars";

        Settings.set(StrutsConstants.STRUTS_URL_HTTP_PORT, "7001");
        Settings.set(StrutsConstants.STRUTS_URL_HTTPS_PORT, "7002");

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getServerName", "www.mydomain.com");
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        mockHttpServletRequest.expectAndReturn("getServerPort", 7001);
        mockHttpServletRequest.expectAndReturn("getContextPath", "/mywebapp");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "/MyAction.action";
        TreeMap params = new TreeMap();
        params.put("hello", new String[]{"earth", "mars"});
        params.put("foo", "bar");

        String urlString = UrlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, "https", true, true);
        assertEquals(expectedString, urlString);
    }

    /**
     * This test is similar to {@link #testSwitchToHttpScheme()} with the HTTP port equal to 7001 and the HTTPS port
     * equal to port 7002.
     */
    public void testSwitchToHttpNonDefaultPort() {

        String expectedString = "http://www.mydomain.com:7001/mywebapp/MyAction.action?foo=bar&amp;hello=earth&amp;hello=mars";

        Settings.set(StrutsConstants.STRUTS_URL_HTTP_PORT, "7001");
        Settings.set(StrutsConstants.STRUTS_URL_HTTPS_PORT, "7002");

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getServerName", "www.mydomain.com");
        mockHttpServletRequest.expectAndReturn("getScheme", "https");
        mockHttpServletRequest.expectAndReturn("getServerPort", 7002);
        mockHttpServletRequest.expectAndReturn("getContextPath", "/mywebapp");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "/MyAction.action";
        TreeMap params = new TreeMap();
        params.put("hello", new String[]{"earth", "mars"});
        params.put("foo", "bar");

        String urlString = UrlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, "http", true, true);
        assertEquals(expectedString, urlString);
    }

    /**
     * A check to verify that the scheme, server, and port number are omitted when the scheme of the current request
     * matches the scheme supplied when building the URL.
     */
    public void testBuildWithSameScheme() {
        String expectedString = "/mywebapp/MyAction.action?foo=bar&amp;hello=earth&amp;hello=mars";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getServerName", "www.mydomain.com");
        mockHttpServletRequest.expectAndReturn("getScheme", "https");
        mockHttpServletRequest.expectAndReturn("getServerPort", 443);
        mockHttpServletRequest.expectAndReturn("getContextPath", "/mywebapp");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "/MyAction.action";
        TreeMap params = new TreeMap();
        params.put("hello", new String[]{"earth", "mars"});
        params.put("foo", "bar");

        String urlString = UrlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, "https", true, true);
        assertEquals(expectedString, urlString);
    }
    
    
    public void testParseQuery() throws Exception {
    	Map result = UrlHelper.parseQueryString("aaa=aaaval&bbb=bbbval&ccc=");
    	
    	assertEquals(result.get("aaa"), "aaaval");
    	assertEquals(result.get("bbb"), "bbbval");
    	assertEquals(result.get("ccc"), "");
    }
    
    public void testParseEmptyQuery() throws Exception {
    	Map result = UrlHelper.parseQueryString("");
    	
    	assertNotNull(result);
    	assertEquals(result.size(), 0);
    }
    
    public void testParseNullQuery() throws Exception {
    	Map result = UrlHelper.parseQueryString(null);
    	
    	assertNotNull(result);
    	assertEquals(result.size(), 0);
    }
    
    
    public void testTranslateAndEncode() throws Exception {
    	String defaultI18nEncoding = Settings.get(StrutsConstants.STRUTS_I18N_ENCODING);
    	try {
    		Settings.set(StrutsConstants.STRUTS_I18N_ENCODING, "UTF-8");
    		String result = UrlHelper.translateAndEncode("\u65b0\u805e");
    		String expectedResult = "%E6%96%B0%E8%81%9E";
    	
    		assertEquals(result, expectedResult);
    	}
    	finally {
    		Settings.set(StrutsConstants.STRUTS_I18N_ENCODING, defaultI18nEncoding);
    	}
    }
    
    public void testTranslateAndDecode() throws Exception {
    	String defaultI18nEncoding = Settings.get(StrutsConstants.STRUTS_I18N_ENCODING);
    	try {
    		Settings.set(StrutsConstants.STRUTS_I18N_ENCODING, "UTF-8");
    		String result = UrlHelper.translateAndDecode("%E6%96%B0%E8%81%9E");
    		String expectedResult = "\u65b0\u805e";
    	
    		assertEquals(result, expectedResult);
    	}
    	finally {
    		Settings.set(StrutsConstants.STRUTS_I18N_ENCODING, defaultI18nEncoding);
    	}
    }
}
