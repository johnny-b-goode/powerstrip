package net.scientifichooliganism.javaplug;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.CharBuffer;
import java.util.*;

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

            if(pathInfo.length >= 2 && pathInfo[1] != null) {
				plugin = pathInfo[1];
			}
			if(pathInfo.length >= 3 && pathInfo[2] != null){
				action = pathInfo[2];
			}

			if(plugin == null){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Plugin not specified in URL.");
			}

			System.out.println("request.getPathInfo(): " + request.getPathInfo());
			System.out.println("context path: " + request.getContextPath());
			System.out.println("plugin: " + plugin);
			System.out.println("action: " + action);

			Map<String, String[]> parameters = request.getParameterMap();

			Object result = null;
			if(plugin.toLowerCase().equals("data")){
				switch(action.toLowerCase()){
					case "query":
						if(parameters.containsKey("query")) {
							String query = parameters.get("query")[0];
							result = dl.query(ac, query);
						} else {
							response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No query specified");
						}
						break;
					case "persist":
					    String json = null;
						Object object = null;
						if(parameters.containsKey("object")){
							json = parameters.get("object")[0];
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
                Map<String, String> paramOptions = getParameterMap(actionInfo, parameters.keySet());
				if (actionInfo == null) {
                    String pluginPath = ac.plugins.get(plugin);
                    String requestPath = request.getPathInfo();
					System.out.println(requestPath);

                    int index = requestPath.indexOf("/", 1);
					if(index != -1) {
						requestPath = requestPath.substring(index + 1);
					} else {
						requestPath = "";
					}

					String filePath = pluginPath + "/static/" + requestPath;
					System.out.println(filePath);
					File requestFile = new File(filePath);

					if(requestFile.exists()){
					    if(requestFile.isDirectory()) {
					        File files[] = requestFile.listFiles(new FilenameFilter() {
								@Override
								public boolean accept(File dir, String name) {
									return name.startsWith("index");
								}
							});

							requestFile = null;
							for(int i = 0; i < files.length && requestFile == null; i++){
								if(files[i].isFile()){
									requestFile = files[i];
								}
							}

							if(requestFile == null) {
								response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Resource requested does not exist");
							}
						}
						try (BufferedReader reader = new BufferedReader(new FileReader(requestFile))) {
							System.out.println("Max String Length : " + Integer.MAX_VALUE);
							String line = null;

							while ((line = reader.readLine()) != null) {
								pwResponse.println(line);
							}
							reader.close();
						}
					} else {
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Resource requested does not exist");
					}

				} else {
					String paramString = (String)((paramOptions.keySet().toArray())[0]);
				    String[] paramOrder = paramString.split(",");
                    String methodSig = (String)((paramOptions.keySet().toArray())[0]);
					System.out.println("Signature: " + methodSig);
					String typesString = methodSig.substring(methodSig.indexOf("(") + 1, methodSig.lastIndexOf(")"));
					String[] paramTypes = typesString.split(",");
                 	Object[] args = new Object[paramOrder.length];

                    for(int i = 0; i < paramOrder.length; i++) {
                        String param = paramOrder[i];
                        Class paramClass = null;
						try {
							paramClass = Class.forName(paramTypes[i]);
						} catch (ClassNotFoundException exc){
							exc.printStackTrace();
						}

						if(paramClass == null) {
							throw new RuntimeException("Class type " + paramTypes[i] + "not known");
                        }

						Object paramObject;

						if(paramClass.isPrimitive()) {
							paramObject = stringToPrimitive(paramClass.getName(), param);
						} else if (paramClass.equals(String.class)) {
							paramObject = param;
						} else if (parameters.containsKey(param)) {

							if (contentType != null && contentType.contains("xml")) {
								paramObject = ac.performAction("XMLPlugin",
										"net.scientifichooliganism.xmlplugin.XMLPlugin",
										"objectFromString", new Object[]{param});
							} else {
								// Assume JSON
								paramObject = ac.performAction("JSONPlugin",
										"net.scientifichooliganism.jsonplugin.JSONPlugin",
										"objectFromJson", new Object[]{param});
							}
						} else {
							paramObject = null;
						}
						args[i] = paramObject;
					}
					result = sendToPlugin(plugin, action, args);
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

	private Map<String, String> getParameterMap(String[] action, Set<String> givenParameters){
		System.out.println("WebSvcLayer.getParameterMap(String[], Set<String>)");
		System.out.println("    Action: ");
		for(String item : action){
			System.out.println("        " + item);
		}
		System.out.println("    Parameters: ");
     	for(String parameter : givenParameters){
     		System.out.println("        " + parameter);
		}



		Map<String, String> ret = new TreeMap<>();
		Map<String, String> mappings = ac.getParameterMap(action);
		Map<String, Integer> paramScores = new TreeMap<>();

		// Score the parameters based on number of matching parameters
		// and number of parameters total
		for(String key : mappings.keySet()){
			Vector<String> paramOption = new Vector(Arrays.asList(key.split(",")));
			for(String givenParam : givenParameters){
			    if(paramOption.contains(givenParam)){
			    	if(paramScores.containsKey(key)){
			    		Integer old = paramScores.get(key);
						paramScores.replace(key, old + 1);
					} else{
						paramScores.put(key, 1);
					}
				}
			}
			if(paramOption.size() == givenParameters.size()){
				if(paramScores.containsKey(key)){
					Integer old = paramScores.get(key);
					paramScores.replace(key, old + 1);
				} else{
					paramScores.put(key, 1);
				}
			}
		}

		// return all best scores
		int max = 0;
		for(String key : paramScores.keySet()){
			int score = paramScores.get(key);
			if(score == max){
				ret.put(mappings.get(key), key);
			} else if (score > max){
				max = score;
				ret.clear();
				ret.put(mappings.get(key), key);
			}
		}

		return ret;
	}

	private Object stringToPrimitive(String className, String value){
		Class klass = null;
		try{
			klass = Class.forName(className);
		} catch (ClassNotFoundException exc){
			exc.printStackTrace();
		}

		if(klass != null){
			if(Boolean.class == klass || Boolean.TYPE == klass) {
				return Boolean.parseBoolean(value);
			}
			if(Byte.class == klass || Byte.TYPE == klass) {
				return Byte.parseByte(value);
			}
			if(Short.class == klass || Short.TYPE == klass) {
				return Short.parseShort(value);
			}
			if(Integer.class == klass || Integer.TYPE == klass) {
				return Integer.parseInt(value);
			}
			if(Long.class == klass || Long.TYPE == klass) {
				return Long.parseLong(value);
			}
			if(Float.class == klass || Float.TYPE == klass) {
				return Float.parseFloat(value);
			}
			if(Double.class == klass || Double.TYPE == klass) {
				return Double.parseDouble(value);
			}
		}

		return null;
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