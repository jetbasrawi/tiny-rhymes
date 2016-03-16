package com.smartlittlepeople.common;

import javax.servlet.http.HttpServletRequest;

import com.google.api.client.http.GenericUrl;



public class URLUtils {

	public static String fullUrl(HttpServletRequest req, String rawPath) {
		
		GenericUrl url = new GenericUrl(new String(req.getRequestURL()));
		url.setRawPath(rawPath);
		return url.build();
		
	}
}
