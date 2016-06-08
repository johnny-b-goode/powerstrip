package net.scientifichooliganism.javaplug;

import java.io.IOException;
//import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class WebSvcLayer {
	private static WebSvcLayer instance;

	private WebSvcLayer() {
		//
	}

	public static WebSvcLayer getInstance () {
		if (instance == null) {
			instance = new WebSvcLayer();
		}

		return instance;
	}

	public static void main (String [] args) {
		try {
			//
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public void doGet (HttpServletRequest request, HttpServletResponse response) throws
		ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/plain");
		PrintWriter pwResponse = response.getWriter();

		try {
			String operation = request.getPathInfo();
			String operands[] = request.getParameterValues("operand");
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}