package net.scientifichooliganism.javaplug;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
		PrintWriter pwResponse = response.getWriter();
		String contentType = null;

		if(request.getHeader("Accept") != null){
			contentType = request.getHeader("Accept");
			response.setContentType(contentType);
			System.out.println("Response type is " + contentType);
		}


		try {
			response.setStatus(HttpServletResponse.SC_OK);
			String[] pathInfo = request.getPathInfo().split("/");
			String plugin = null;
			String action = null;

			if(pathInfo.length > 2) {
				plugin = pathInfo[pathInfo.length - 2];
				action = pathInfo[pathInfo.length - 1];
			}

			if(plugin == null || action == null){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Either Plugin or Action not specified in URL.");
			}

			System.out.println("request.getPathInfo(): " + request.getPathInfo());
			System.out.println("context path: " + request.getContextPath());
			System.out.println("plugin: " + plugin);
			System.out.println("action: " + action);

			Map parameters = request.getParameterMap();

//			if ((parameters != null) && (parameters.size() > 0)) {
//				System.out.println(parameters.keySet());
//				for (Object obj : parameters.keySet()) {
//					System.out.println(obj + ": " + ((String[])parameters.get(obj))[0]);
//				}
//			}

			if(plugin.toLowerCase().equals("data")){
				Object result = null;
				switch(action.toLowerCase()){
					case "query":
						if(parameters.containsKey("query")) {
							String query = ((String[]) parameters.get("query"))[0];
							result = DataLayer.getInstance().query(actionCatalog, query);
						} else {
							response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No query specified");
						}
						break;
					case "persist":
						throw new NotImplementedException();
						// break;
					case "remove":
						break;
					default:
						response.sendError(HttpServletResponse.SC_NOT_FOUND, "Action specified not found for " + plugin + ".");
						break;
				}

				switch(contentType){
					case "text/json":
					case "application/json":
					default:
						String json = (String)actionCatalog.performAction("JSONPlugin",
							"net.scientifichooliganism.jsonplugin.JSONPlugin",
							"jsonFromObject", new Object[]{result});
						pwResponse.println(json);
						break;
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