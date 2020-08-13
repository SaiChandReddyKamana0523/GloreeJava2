package com.gloree.utils;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



public class SecurityUtil {

	// takes the response object as a param 
	// and if the user is not logged in, forward the user
	// to the start page.
	public static boolean authenticationPassed(HttpServletRequest request, HttpServletResponse response )
		throws ServletException, IOException {

		// get the session object.
		HttpSession session = request.getSession(false);
		
		if (session == null) {

			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/WebSite/startPage.jsp");
			dispatcher.forward(request, response);
			return false;
		}
		
		// if the session has been invalidated, lets forward to re-log in.
		if (!(request.isRequestedSessionIdValid())){

			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/WebSite/startPage.jsp");
			dispatcher.forward(request, response);
			return false;
		}
		
		
		String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
		if ((isLoggedIn == null) || (isLoggedIn.equals(""))){
			// not logged in . so redirect.

			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/WebSite/startPage.jsp");
			dispatcher.forward(request, response);
			return false;
		}
		
		return true;
	}
}
