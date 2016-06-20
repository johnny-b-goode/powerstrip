package net.scientifichooliganism.javaplug;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public final class WebSvcLayer extends HttpServlet {

	private ActionCatalog actionCatalog = null;

	public WebSvcLayer() {
		//
	}

	@Override
	public void init() throws ServletException {
		super.init();
		PluginLoader.bootstrap(getServletContext().getClassLoader());
		actionCatalog = ActionCatalog.getInstance();
	}

	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws
		ServletException, IOException {
		response.setContentType("text/plain");
		PrintWriter pwResponse = response.getWriter();

		try {
			response.setStatus(HttpServletResponse.SC_OK);
			String[] pathInfo = request.getPathInfo().split("/");
			String plugin = null;
			String action = null;

			// TODO: Fix indexes
			if(pathInfo.length > 1) {
				plugin = pathInfo[1];
			}
			if(pathInfo.length > 2) {
				action = pathInfo[2];
			}

			pwResponse.println("request.getPathInfo(): " + request.getPathInfo());
			pwResponse.println("context path: " + request.getContextPath());
			pwResponse.println("plugin: " + plugin);
			pwResponse.println("action: " + action);

			Map parameters = request.getParameterMap();

			if ((parameters != null) && (parameters.size() > 0)) {
				pwResponse.println(parameters.keySet());
				for (Object obj : parameters.keySet()) {
					pwResponse.println(obj + ": " + ((String[])parameters.get(obj))[0]);
				}
			}

			actionCatalog.performAction(plugin, actionCatalog.plugins.get(plugin), action, new Object[]{"My Message!"});
		}
		catch (Exception exc) {
			exc.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			pwResponse.println("ERROR");
		}
	}
}