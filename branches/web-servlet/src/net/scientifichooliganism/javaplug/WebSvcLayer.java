package net.scientifichooliganism.javaplug;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.CharBuffer;
import java.util.Map;

public final class WebSvcLayer extends HttpServlet {

	private ActionCatalog ac = null;
	private DataLayer dl = null;

	public WebSvcLayer() {
		//
	}

	@Override
	public void init() throws ServletException {
		super.init();
		PluginLoader.bootstrap(getServletContext().getClassLoader());
		ac = ActionCatalog.getInstance();
        dl = DataLayer.getInstance();
	}

	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws
		ServletException, IOException {
		PrintWriter pwResponse = response.getWriter();
		String requestType = null;
        String contentType = null;
		int contentLength = request.getContentLength();

		if(request.getHeader("Accept") != null){
			requestType = request.getHeader("Accept");
			response.setContentType(requestType);
			System.out.println("Response type is " + requestType);
		}

		if(request.getHeader("Content-Type") != null){
			contentType = request.getHeader("Content-Type");
            System.out.println("Content type is " + contentType);
		} else {
			try(Reader reader = request.getReader()){
				int ch;
				ch = reader.read();
				while(contentType == null && ch != -1){
					System.out.println("Reading content...");
					// Simplistic method for determining whether our
					// content is json or xml
				    if((char)ch == '"' || (char)ch == '{'){
				    	contentType = "json";
					} else if(ch == '<'){
						contentType = "xml";
					}

					ch = reader.read();
				}
			}
		}


		try {
			response.setStatus(HttpServletResponse.SC_OK);
			String[] pathInfo = request.getPathInfo().split("/");
			String plugin = null;
			String action = null;

			if(pathInfo.length > 2) {
				plugin = pathInfo[1];
				action = pathInfo[2];
			}

			if(plugin == null){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Plugin not specified in URL.");
			}

			System.out.println("request.getPathInfo(): " + request.getPathInfo());
			System.out.println("context path: " + request.getContextPath());
			System.out.println("plugin: " + plugin);
			System.out.println("action: " + action);

			Map parameters = request.getParameterMap();

			Object result = null;
			if(plugin.toLowerCase().equals("data")){
				switch(action.toLowerCase()){
					case "query":
						if(parameters.containsKey("query")) {
							String query = ((String[]) parameters.get("query"))[0];
							result = dl.query(ac, query);
						} else {
							response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No query specified");
						}
						break;
					case "persist":
					    String json = null;
						Object object = null;
						if(parameters.containsKey("object")){
							json = ((String[])parameters.get("object"))[0];
						} else {
							response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No object specified");
						}

						if(json != null){
							object = ac.performAction("JSONPlugin",
									"net.scientifichooliganism.jsonplugin.JSONPlugin",
                                    "objectFromJson", new Object[]{json});
						} else {
							response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No json string found");
						}

						if(object != null){
							dl.persist(object);
						} else {
							response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not create object from json");
						}

						break;
					case "remove":
					    throw new NotImplementedException();
					default:
						response.sendError(HttpServletResponse.SC_NOT_FOUND, "Action specified not found for " + plugin + ".");
						break;
				}

			}
			else {
			    String[] actionInfo = ac.findAction(plugin + " " + action);
				if (actionInfo == null) {
				    // Assume static page requested, try to open file.
					System.out.println("attempting static retrieval");
                    System.out.println("Plugins: ");
					for(String name : ac.plugins.keySet()){
						System.out.println(name + " : " + ac.plugins.get(name));
					}
                    String pluginPath = ac.plugins.get(plugin);
                    String requestPath = request.getPathInfo();
					requestPath = requestPath.substring(requestPath.indexOf("/", 1) + 1);
					String filePath = pluginPath + "/static/" + requestPath;
					File requestFile = new File(filePath);

					if(requestFile.exists()){
						BufferedReader reader = new BufferedReader(new FileReader(requestFile));
                        String line = null;

						while((line = reader.readLine()) != null){
							pwResponse.write(line);
						}
						reader.close();
					} else {
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Resource requested does not exist");
					}

				} else {
					result = sendToPlugin(plugin, action, new Object[]{});
				}
			}

			if(result != null){
				sendResponse(response, result, requestType);
			}
		}
		catch (Exception exc) {
			exc.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private Object objectFromContent(Reader content, String contentType, int contentLength){
		CharBuffer buffer = CharBuffer.allocate(contentLength);
		try {
			content.read(buffer);
		} catch(Exception exc){
			exc.printStackTrace();
		}
		Object result = null;
		if (contentType != null && contentType.contains("xml")) {
			result = ac.performAction("XMLPlugin",
					"net.scientifichooliganism.xmlplugin.XMLPlugin",
					"objectFromString", new Object[]{buffer.toString()});
		} else {
			// Assume JSON
			result = ac.performAction("JSONPlugin",
					"net.scientifichooliganism.jsonplugin.JSONPlugin",
					"objectFromJson", new Object[]{buffer.toString()});
		}

		return result;
	}

	private void sendResponse(HttpServletResponse response, Object object, String requestType){
		String result = null;
		if (object instanceof String) {
			result = (String)object;
		} else {
		    if(requestType != null && requestType.toLowerCase().contains("xml")){
				result = (String)ac.performAction("XMLPlugin",
						"net.scientifichooliganism.xmlplugin.XMLPlugin",
						"stringFromObject", new Object[]{object});
			} else {
				// Assume json if XML not requested explicitly
				result = (String)ac.performAction("JSONPlugin",
						"net.scientifichooliganism.jsonplugin.JSONPlugin",
						"jsonFromObject", new Object[]{object});
			}
		}

		if(result != null){
			try{
				response.getWriter().write(result);
			} catch(Exception exc){
				exc.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private Object sendToPlugin(String plugin, String action, Object[] args){
		String[] actionInfo = ac.findAction(plugin + " " + action);
		return ac.performAction(actionInfo[0], actionInfo[1], actionInfo[2], args);
	}

}