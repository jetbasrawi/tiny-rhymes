package com.smartlittlepeople.tinyrhymes.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.smartlittlepeople.tinyrhymes.domain.TinyRhymesService;
import com.smartlittlepeople.tinyrhymes.auth.SessionUtils;

@SuppressWarnings("serial")
public class TinyRhymesServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(TinyRhymesServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		String userId = SessionUtils.getUserId(req);
		
		ServletContext ctx = getServletContext();
		TinyRhymesService.insertSongBundle(ctx, req, userId);
		

	}
}
