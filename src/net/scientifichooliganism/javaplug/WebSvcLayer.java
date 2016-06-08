package net.scientifichooliganism.javaplug;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class WebSvcLayer {

	private WebSvcLayer() {
		//
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
		response.setContentType("text/plain");
		PrintWriter pwResponse = response.getWriter();

		try {
			response.setStatus(HttpServletResponse.SC_OK);
			pwResponse.println("request.getPathInfo(): " + request.getPathInfo());
			Map parameters = request.getParameterMap();

			if ((parameters != null) && (parameters.size() > 0)) {
				for (Object obj : parameters.keySet()) {
					pwResponse.println(((String)obj) + ": " + ((String)parameters.get(obj)));
				}
			}
		}
		catch (Exception exc) {
			exc.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			pwResponse.println("ERROR");
		}
	}
}