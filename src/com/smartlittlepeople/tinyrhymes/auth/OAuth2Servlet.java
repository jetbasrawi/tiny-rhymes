package com.smartlittlepeople.tinyrhymes.auth;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.smartlittlepeople.common.URLUtils;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

@SuppressWarnings("serial")
public class OAuth2Servlet extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		if (!hasError(req, res)) {
			res.sendRedirect(doAuth(req));
		}
	}

	private boolean hasError(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		String error = req.getParameter("error");
		if (error != null) {
			res.getWriter().write("Sorry, auth failed because: " + error);
			return true;
		}
		return false;
	}

	private String doAuth(HttpServletRequest req) throws IOException {
		String authCode = req.getParameter("code");

		String callbackUri = URLUtils.fullUrl(req, AuthUtils.OAUTH2_PATH);

		// We need a flow no matter what to either redirect or extract
		// information
		AuthorizationCodeFlow flow = AuthUtils.buildCodeFlow();

		// Without a response code, redirect to Google's authorization URI
		if (authCode == null) {
			return flow.newAuthorizationUrl().setRedirectUri(callbackUri)
					.build();
		}

		GoogleTokenResponse tokenRes = getTokenRes(flow, authCode, callbackUri);

		String userId = getUserId(tokenRes);

		SessionUtils.setUserId(req, userId);

		flow.createAndStoreCredential(tokenRes, userId);

		//MirrorUtils.subscribeToTimelineUpdates(req, userId);

		return "/";
		// ENDT:subscribe
	}

	/**
	 * Makes a remote call to the Google Auth server to authorize the grant
	 * code, in order to issue a request token.
	 * 
	 * @param flow
	 * @param code
	 * @param callbackUri
	 * @return
	 * @throws IOException
	 */
	private GoogleTokenResponse getTokenRes(AuthorizationCodeFlow flow,
			String code, String callbackUri) throws IOException {
		AuthorizationCodeTokenRequest tokenReq = flow.newTokenRequest(code)
				.setRedirectUri(callbackUri);

		TokenResponse tokenRes = tokenReq.execute();

		return (GoogleTokenResponse) tokenRes;
	}

	/**
	 * Extract the Google user ID from the ID token in the auth response
	 */
	private String getUserId(GoogleTokenResponse tokenRes) throws IOException {
		return tokenRes.parseIdToken().getPayload().getUserId();
	}
}