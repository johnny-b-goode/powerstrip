package net.scientifichooliganism.javaplug;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.CharBuffer;
import java.util.*;

public final class WebSvcLayer extends HttpServlet {

	public ActionCatalog ac = null;
	public DataLayer dl = null;

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

	public String checkHeaders(HttpServletRequest request, HttpServletResponse response){
		if(request.getHeader("Accept") != null){
			String requestType = request.getHeader("Accept");
			response.setContentType(requestType);
			System.out.println("Response type is " + requestType);
		}

		String contentType = null;
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
			catch(Exception exc){
				exc.printStackTrace();
			}
		}
		return contentType;
	}

	public String[] parsePath(HttpServletRequest request){
		String[] pathInfo = request.getPathInfo().split("/");

		int infoCount = 0;
		for(String info : pathInfo){
			if(info != null && !info.isEmpty()){
				infoCount++;
			}
		}

		String ret[] = new String[infoCount];

		int i = 0;
		for(String info : pathInfo){
			if(info != null && !info.isEmpty()){
				ret[i] = info;
				i++;
			}
		}

		return ret;
	}

	public Object doDataOperation(String action, String contentType, Map<String, Object> parameters, HttpServletResponse response){
	    System.out.println("doDataOperation()");
		System.out.println("    parameters are: " );
		for(String key : parameters.keySet()){
			System.out.println("        key: " + key + " : " + parameters.get(key));
		}
		Object result = null;
		try {
			switch (action.toLowerCase()) {
				case "query":
					if (parameters.containsKey("query")) {
						System.out.println("    doing query!");
						String query = (String) parameters.get("query");
						result = dl.query(ac, query);
					} else {
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No query specified");
					}
					return result;
				case "persist":
					System.out.println("    starting persist!");
					String json = null;
					String xml = null;
					Object object = null;

					if (parameters.containsKey("object")) {
						System.out.println("    getting string content");
						if (contentType.contains("xml")) {
							xml = (String) parameters.get("object");
						} else {
							json = (String) parameters.get("object");
						}
					} else {
						System.out.println("Sending error - \"No object specified\"");
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No object specified");
					}

					if (json != null) {
						System.out.println("    creating object from string");
						object = ac.performAction("JSONPlugin",
								"net.scientifichooliganism.jsonplugin.JSONPlugin",
								"objectFromJson", new Object[]{json});
					} else if(xml != null){
						object = ac.performAction("XMLPlugin",
								"net.scientifichooliganism.xmlplugin.XMLPlugin",
								"objectFromString", new Object[]{xml});
					} else {
						System.out.println("Sending error - \"No json string found \"");
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No json string found");
					}

					if (object != null) {
						System.out.println("    persisting object");
						dl.persist(object);
					} else {
						System.out.println("Sending error - \"Could not create object from json\"");
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not create object from json");
					}

					break;
				case "remove":
					System.out.println("    doing remove!");
					xml = null;
					json = null;
                    if(parameters.containsKey("object")){
						if(contentType.contains("xml")){
							xml = (String) parameters.get("object");
						} else {
							json = (String) parameters.get("object");
						}
					} else {
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No object specified");
					}

					object = null;
					if (json != null) {
						object = ac.performAction("JSONPlugin",
								"net.scientifichooliganism.jsonplugin.JSONPlugin",
								"objectFromJson", new Object[]{json});
					} else if(xml != null){
						object = ac.performAction("XMLPlugin",
								"net.scientifichooliganism.xmlplugin.XMLPlugin",
								"objectFromString", new Object[]{xml});
					} else {
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No json or xml string found");
					}

					if (object != null) {
						dl.remove(object);
					} else {
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not create object from string");
					}
				default:
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "Action specified not found for DataLayer.");
					break;
			}
		} catch (IOException exc){
			exc.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		return null;
	}

	public Map<String, Object> parseArgs(Map<String, String> stringArgs, String plugin, String action, String contentType) {
		System.out.println("WebSvcLayer.parseArgs(Map<String, String>, String, String, String)");
		String[] actionInfo = ac.findAction(plugin + " " + action);
		Map<String, Object> results = new TreeMap<>();

		if (plugin.toLowerCase().equals("data")) {
		    for(String key : stringArgs.keySet()){
		    	results.put(key, stringArgs.get(key));
			}
			return results;
		}

		if (actionInfo != null) {
			Map<String, String> parameterMap = getParameterMap(actionInfo, stringArgs.keySet());
			// Parse out param types
			String[] paramKeys = parseParamKeys((String) parameterMap.keySet().toArray()[0]);
			String[] argTypes = parseArgumentTypes((String) parameterMap.values().toArray()[0]);

			for (int i = 0; i < paramKeys.length; i++) {
				String param = paramKeys[i];
				Class paramClass = null;
				try {
					paramClass = Class.forName(argTypes[i]);
				} catch (ClassNotFoundException exc) {
					exc.printStackTrace();
				}

				if (paramClass == null) {
					throw new RuntimeException("Class type " + argTypes[i] + "not known");
				}

				Object paramObject;

				if (paramClass.isPrimitive()) {
					paramObject = stringToPrimitive(paramClass.getName(), param);
				} else if (paramClass.equals(String.class)) {
					paramObject = param;
				} else if (stringArgs.containsKey(param)) {

					if (contentType != null && contentType.contains("xml")) {
						paramObject = ac.performAction("XMLPlugin",
								"net.scientifichooliganism.xmlplugin.XMLPlugin",
								"objectFromString", new Object[]{stringArgs.get(param)});
					} else {
						// Assume JSON
						paramObject = ac.performAction("JSONPlugin",
								"net.scientifichooliganism.jsonplugin.JSONPlugin",
								"objectFromJson", new Object[]{stringArgs.get(param)});
					}
				} else {
					paramObject = null;
				}

				results.put(param, paramObject);
			}
		} else {
			return null;
		}

		return results;
	}

	public void serveStaticPage(String plugin, HttpServletRequest request, HttpServletResponse response) {
		System.out.println("serveStaticPage(String, HttpServletRequest, HttpServletResponse)");
		try {
			String pluginPath = ac.plugins.get(plugin);
			String requestPath = request.getPathInfo();
			PrintWriter pwResponse = response.getWriter();
			System.out.println(requestPath);

			int index = requestPath.indexOf("/", 1);
			if (index != -1) {
				requestPath = requestPath.substring(index + 1);
			} else {
				requestPath = "";
			}

			String filePath = pluginPath + "/static/" + requestPath;
//					System.out.println(filePath);
			File requestFile = new File(filePath);

			if (requestFile.exists()) {
				if (requestFile.isDirectory()) {
					File files[] = requestFile.listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.startsWith("index");
						}
					});

					requestFile = null;
					for (int i = 0; i < files.length && requestFile == null; i++) {
						if (files[i].isFile()) {
							requestFile = files[i];
						}
					}

					if (requestFile == null) {
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
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Resource requested does not exist");
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public Object doAction(String plugin, String action, String contentType, Map<String, Object> parameters, HttpServletResponse response) {
		System.out.println("doAction()");
		Object result = null;
		if(plugin.toLowerCase().equals("data")){
			System.out.println("    Action is data!");
			result = doDataOperation(action, contentType, parameters, response);

		} else {
			System.out.println("    Action is " + action + "!");
			result = sendToPlugin(plugin, action, parameters.values().toArray());
		}


		return result;
	}

	@Override
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws
			ServletException, IOException {
	    doGet(request, response);
	}

	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws
			ServletException, IOException {
		System.out.println("doGet()");
		String requestType = null;
		String contentType = null;
		String pluginName = null;
		String actionName = null;
		contentType = checkHeaders(request, response);
		requestType = response.getContentType();
		String[] path = parsePath(request);

		if(path.length >= 1){
			pluginName = path[0];
		}

		if(path.length >= 2){
			actionName = path[1];
		}

		response.setStatus(HttpServletResponse.SC_OK);

		//TODO: Verify that calling response.sendError() breaks out of doGet.
		if(pluginName == null || pluginName.isEmpty()){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Plugin not specified in URL.");
		}

		Map<String, String> parameterStrings = new TreeMap<>();

		for(String key : request.getParameterMap().keySet()){
			System.out.println("    putting " + key + " with " + request.getParameterMap().get(key)[0]);
			parameterStrings.put(key, request.getParameterMap().get(key)[0]);
		}

		Map<String, Object> parameters = parseArgs(parameterStrings, pluginName, actionName, contentType);

        Object result = null;
		if(parameters != null){
			result = doAction(pluginName, actionName, contentType, parameters, response);
		} else {
			serveStaticPage(pluginName, request, response);
		}

		if(result != null){
			sendResponse(response, result, requestType);
		}
	}

//TODO: The following two methods have been modified to use positive qualification
//to fix a bug. Positive qualification sucks, so FIX THIS!
	public String[] parseParamKeys(String paramString){
		if ((paramString != null) && (paramString.length() > 0)) {
			return paramString.split(",");
		}
		else {
			return new String[0];
		}
	}

	public String[] parseArgumentTypes(String methodSignature){
		String typesString = "";

		if ((methodSignature != null) && (methodSignature.length() > 0)) {
			typesString = methodSignature.substring(methodSignature.indexOf("(") + 1, methodSignature.lastIndexOf(")"));
		}

		return typesString.split(",");
	}

	public Map<String, String> getParameterMap(String[] action, Set<String> givenParameters){
		System.out.println("WebSvcLayer.getParameterMap(String[], Set<String>)");
		System.out.println("    Action: ");

		for(String item : action){
			System.out.println("        " + item);
		}

		if ((givenParameters != null) && (givenParameters.size() > 0)) {
			System.out.println("    Parameters: ");

			for(String parameter : givenParameters){
				System.out.println("        " + parameter);
			}
		}

		Map<String, String> ret = new TreeMap<>();
		Map<String, String> mappings = ac.getParameterMap(action);
		Map<String, Integer> paramScores = new TreeMap<>();

		System.out.println("	mappings.size(): " + mappings.size());

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

	public Object stringToPrimitive(String className, String value){
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

	public Object objectFromContent(Reader content, String contentType, int contentLength){
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

	public void sendResponse(HttpServletResponse response, Object object, String requestType){
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

	public Object sendToPlugin(String plugin, String action, Object[] args){
		String[] actionInfo = ac.findAction(plugin + " " + action);
		return ac.performAction(actionInfo[0], actionInfo[1], actionInfo[2], args);
	}

}