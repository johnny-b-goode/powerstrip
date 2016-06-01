package net.scientifichooliganism.javaplug;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.concurrent.ConcurrentHashMap;

/**
* This class is a bit beastly. It uses a combination of hashmaps and a two dimensional array
of strings to keep track of plugins and their associated actions. ActionCatalog is also meant
to serve as the primary cache of Objects and Methods in an effort to speed up the process of
invoking the various methods (performing actions).

The plugins hashmap is used to map plugins to filesystem paths
The pluginsEnabled hashmap is used to determine whether or not a plugin is enabled
The objects and methods hashmaps store instantiated Objects (for instance methods) and
Methods (for both instance and static methods).
actions[][] stores three values: the plugin name, a class name that exists within the plugin,
and a method name that can be called as an action. These values are used to cross-reference the
hashmaps.
*/
public final class ActionCatalog {
	private static ActionCatalog instance;
	/*Plugin Name | isActive | Class Name | Method Name*/
	String actions[][];
	ConcurrentHashMap<String, String> plugins;
	ConcurrentHashMap<String, Boolean> pluginsEnabled;
	ConcurrentHashMap<String, Boolean> pluginsStorage;
	ConcurrentHashMap<String, Object> objects;
	ConcurrentHashMap<String, Method> methods;

	/**
	* The default constructor.
	*/
	private ActionCatalog () {
		try {
			actions = new String[1][3];
			plugins = new ConcurrentHashMap<String, String>();
			pluginsEnabled = new ConcurrentHashMap<String, Boolean>();
			pluginsStorage = new ConcurrentHashMap<String, Boolean>();
			objects = new ConcurrentHashMap<String, Object>();
			methods = new ConcurrentHashMap<String, Method>();
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	/**The method to get an instance of the ActionCatalog class*/
	/*As it stands this method is not threadsafe, so that should be addressed.*/
	public static ActionCatalog getInstance () {
		if (instance == null) {
			instance = new ActionCatalog();
		}

		return instance;
	}

	/**add a plugin*/
	public void addPlugin (String pluginName, String pluginPath) throws IllegalArgumentException {
		if (pluginName == null) {
			throw new IllegalArgumentException("addPlugin (String, String) was called with a null string");
		}

		if (pluginName.length() == 0) {
			throw new IllegalArgumentException("addPlugin (String, String) was called with an empty string");
		}

		if (plugins.containsKey(pluginName)) {
			throw new IllegalArgumentException("addPlugin (String, String) was called for a plugin that already exists in the catalog");
		}

		if (pluginPath == null) {
			throw new IllegalArgumentException("addPlugin (String, String) was called with a null string");
		}

		if (pluginPath.length() == 0) {
			throw new IllegalArgumentException("addPlugin (String, String) was called with an empty string");
		}

		try {
			plugins.putIfAbsent(pluginName, pluginPath);
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	/**add an action*/
	public void addAction (String pluginName, String className, String methodName) throws IllegalArgumentException {
		if (pluginName == null) {
			throw new IllegalArgumentException("addAction(String, String, String, String) was called with a null string");
		}

		if (pluginName.length() == 0) {
			throw new IllegalArgumentException("addAction(String, String, String, String) was called with an empty string");
		}

		if (! plugins.containsKey(pluginName)) {
			throw new IllegalArgumentException("addAction(String, String, String, String) was called for a plugin that does not exist in the catalog");
		}

		if (className == null) {
			throw new IllegalArgumentException("addAction(String, String, String, String) was called with a null string");
		}

		if (className.length() == 0) {
			throw new IllegalArgumentException("addAction(String, String, String, String) was called with an empty string");
		}

		if (methodName == null) {
			throw new IllegalArgumentException("addAction(String, String, String, String) was called with a null string");
		}

		if (methodName.length() == 0) {
			throw new IllegalArgumentException("addAction(String, String, String, String) was called with an empty string");
		}

		//if the first entry is null then nothing has been added to the list of actions
		if (actions[0][0] == null) {
			actions[0][0] = pluginName;
			actions[0][1] = className;
			actions[0][2] = methodName;
		}
		else {
			String newActions[][] = new String[actions.length + 1][actions[0].length];

			for (int i = 0; i < actions.length; i++) {
				for (int j = 0; j < actions[0].length; j++) {
					newActions[i][j] = actions[i][j];
				}
			}

			newActions[actions.length][0] = pluginName;
			newActions[actions.length][1] = className;
			newActions[actions.length][2] = methodName;
			actions = newActions;
		}

		//System.out.println("addAction(String, String, String)");
		//for (int i = 0; i < actions.length; i++) {
		//	for (int j = 0; j < actions[0].length; j++) {
		//		System.out.println("	" + actions[i][j]);
		//	}
		//}
	}

	public String[] findAction (String query) throws IllegalArgumentException {
		if (query == null) {
			throw new IllegalArgumentException("findAction(String) was called with a null string");
		}

		if ((query.length() == 0) || (query.toLowerCase().matches("^\\s*$"))) {
			throw new IllegalArgumentException("findAction(String) was called with an empty string");
		}

		System.out.println("ActionCatalog.findAction(String, String)");
		query = query.trim();
		String ret[] = null;
		String queryMethod = null;
		String queryClass = null;

		if (query.contains("(")) {
			query = query.substring(0, query.indexOf("("));
		}

		if (query.contains(".")) {
			queryMethod = query.substring(query.lastIndexOf("."), query.length());
			queryClass = query.substring(0, query.lastIndexOf("."));
		}
		else if (query.contains(" ")) {
			queryMethod = query.substring(query.lastIndexOf(" "), query.length()).trim();
			queryClass = query.substring(0, query.lastIndexOf(" ")).trim();
		}
		else {
			queryMethod = query;
		}

		if ((queryClass != null) && (queryClass.length() <= 0)) {
			queryClass = null;
		}

		//System.out.println("	queryMethod: " + String.valueOf(queryMethod));
		//System.out.println("	queryClass: " + String.valueOf(queryClass));

		for (String [] action : actions) {
			if (action[2].equals(queryMethod)) {
				//System.out.println("	possible match found...");
				if ((queryClass == null) || (queryClass.length() <= 0)) {
					//System.out.println("	action positively matched");
					return action;
				}
				else {
					if ((queryClass.contains(action[0])) || (queryClass.contains(action[1]))) {
						//System.out.println("	continuing evaluation...");
						if ((queryClass.contains(action[0])) && (queryClass.contains(action[1]))) {
							//System.out.println("	action positively matched");
							return action;
						}
						else if (ret != null) {
							if ((queryClass.contains(ret[0])) && (queryClass.contains(action[1]))) {
								//System.out.println("	possible match found");
								ret = action;
							}
						}
						else {
							//System.out.println("	possible match found");
							ret = action;
						}
					}
				}
			}
		}

		return ret;
	}

	/**.*/
	public boolean containsPlugin (String pluginName) throws IllegalArgumentException {
		if (pluginName == null) {
			throw new IllegalArgumentException("containsPlugin(String) was called with a null string");
		}

		if (pluginName.length() == 0) {
			throw new IllegalArgumentException("containsPlugin(String) was called with an empty string");
		}

		return plugins.containsKey(pluginName);
	}

	/**.*/
	public String getPluginPath (String pluginName) throws IllegalArgumentException {
		if (pluginName == null) {
			throw new IllegalArgumentException("getPluginPath(String) was called with a null string");
		}

		if (pluginName.length() == 0) {
			throw new IllegalArgumentException("getPluginPath(String) was called with an empty string");
		}

		return plugins.get(pluginName);
	}

	/**provides access to the keyset of the underlying ConcurrentHashMap plugins*/
	public ConcurrentHashMap.KeySetView<String, String> keySet () {
		return plugins.keySet();
	}

	/**remove a plugin from the catalog*/
	public void removePlugin (String pluginName) throws IllegalArgumentException {
		if (pluginName == null) {
			throw new IllegalArgumentException("removePlugin(String) was called with a null string");
		}

		if (pluginName.length() == 0) {
			throw new IllegalArgumentException("removePlugin(String) was called with an empty string");
		}

		if (plugins.containsKey(pluginName)) {
			plugins.remove(pluginName);
		}

		if (pluginsEnabled.containsKey(pluginName)) {
			pluginsEnabled.remove(pluginName);
		}

		for (String[] action : actions) {
			System.out.println("	" + action[0]);
			if (action[0].equals(pluginName)) {
				removeAction(action[0], action[1], action[2]);
			}
		}
	}

	/**remove an action from the catalog*/
	public void removeAction (String pluginName, String className, String methodName) throws IllegalArgumentException {
		if (pluginName == null) {
			throw new IllegalArgumentException("removeAction(String, String, String) was called with a null string");
		}

		if (pluginName.length() == 0) {
			throw new IllegalArgumentException("removeAction(String, String, String) was called with an empty string");
		}

		if (className == null) {
			throw new IllegalArgumentException("removeAction(String, String, String) was called with a null string");
		}

		if (className.length() == 0) {
			throw new IllegalArgumentException("removeAction(String, String, String) was called with an empty string");
		}

		if (methodName == null) {
			throw new IllegalArgumentException("removeAction(String, String, String) was called with a null string");
		}

		if (methodName.length() == 0) {
			throw new IllegalArgumentException("removeAction(String, String, String) was called with an empty string");
		}

		String newActions[][] = new String[actions.length - 1][actions[0].length];
		boolean found = false;

		for (int i = 0; i < actions.length; i++) {
			if ((actions[i][0].equals(pluginName)) && (actions[i][1].equals(className)) && (actions[i][2].equals(methodName))) {
				found = true;
			}
			else {
				if (found) {
					for (int j = 0; j < actions[0].length; j++) {
						newActions[i - 1][j] = actions[i][j];
					}
				}
				else {
					if (i == newActions.length) {
						return;
					}

					for (int j = 0; j < actions[0].length; j++) {
						newActions[i][j] = actions[i][j];
					}
				}
			}
		}

		actions = newActions;
		/*we only want to remove class and method objects if there are no more references to them*/
		boolean classFound = false;
		boolean methodFound = false;

		for (int i = 0; i < actions.length; i++) {
			if (actions[i][1].equals(className)) {
				classFound = true;
			}

			if (actions[i][2].equals(methodName)) {
				methodFound = true;
			}

			if (classFound && methodFound) {
				i = actions.length;
			}
		}

		if (classFound == false) {
			if (objects.contains(className)) {
				objects.remove(className);
			}
		}

		if (methodFound == false) {
			if (methods.contains(methodName)) {
				methods.remove(methodName);
			}
		}

		System.out.println("removeAction(String, String, String)");

		for (int i = 0; i < actions.length; i++) {
			for (int j = 0; j < actions[0].length; j++) {
				System.out.println("	" + actions[i][j]);
			}
		}
	}

	/**set the active state on a plugin*/
	public void setPluginActive (String pluginName, String pluginState) throws IllegalArgumentException {
		if (pluginState == null) {
			throw new IllegalArgumentException("setPluginActive (String, String) was called with a null string");
		}

		if (pluginState.length() == 0) {
			throw new IllegalArgumentException("setPluginActive (String, String) was called with an empty string");
		}

		if (! pluginState.toLowerCase().matches("(^true$)|(^false$)")) {
			throw new IllegalArgumentException("setPluginActive (String, String) was called with a value other than \"true\" or \"false\"");
		}

		pluginState = pluginState.toLowerCase();
		setPluginActive(pluginName, Boolean.parseBoolean(pluginState));
	}

	/**set the active state on a plugin*/
	public void setPluginActive (String pluginName, boolean pluginState) throws IllegalArgumentException {
		if (pluginName == null) {
			throw new IllegalArgumentException("setPluginActive (String, boolean) was called with a null string");
		}

		if (pluginName.length() == 0) {
			throw new IllegalArgumentException("setPluginActive (String, boolean) was called with an empty string");
		}

		if (! plugins.containsKey(pluginName)) {
			throw new IllegalArgumentException("setPluginActive(String, boolean) was called for a plugin that does not exist in the catalog");
		}

		if (pluginsEnabled.contains(pluginName)) {
			pluginsEnabled.replace(pluginName, pluginState);
		}
		else {
			pluginsEnabled.put(pluginName, pluginState);
		}
	}

	/**get whether or not the specified plugin is active (enabled).
	*
	* This defaults to false, which prevents actions for the plugin being used until this
	has been explicitly set to true (setPluginActive([plugin name], true)). While it is
	not really necessary it is a measure that will help prevent incorrectly configured
	plugins from being used. This also allows plugins intended to provide common access to
	libraries to contain minimum configuration. Such plugins do not require any
	explicit action configuration.*/
	public boolean isPluginActive (String pluginName) throws IllegalArgumentException {
		if (pluginName == null) {
			throw new IllegalArgumentException("isPluginActive (String) was called with a null string");
		}

		if (pluginName.length() == 0) {
			throw new IllegalArgumentException("isPluginActive (String) was called with an empty string");
		}

		if (! plugins.containsKey(pluginName)) {
			throw new IllegalArgumentException("isPluginActive(String) was called for a plugin that does not exist in the catalog");
		}

		//System.out.println("isPluginActive (String)");
		boolean ret = false;

		if (pluginsEnabled.containsKey(pluginName)) {
			ret = pluginsEnabled.get(pluginName);
		}

		return ret;
	}

	/**flag the plugin as a storage plugin*/
	public void setPluginStorage (String pluginName, String pluginState) throws IllegalArgumentException {
		if (pluginState == null) {
			throw new IllegalArgumentException("setPluginStorage (String, String) was called with a null string");
		}

		if (pluginState.length() == 0) {
			throw new IllegalArgumentException("setPluginStorage (String, String) was called with an empty string");
		}

		if (! pluginState.toLowerCase().matches("(^true$)|(^false$)")) {
			throw new IllegalArgumentException("setPluginStorage (String, String) was called with a value other than \"true\" or \"false\"");
		}

		pluginState = pluginState.toLowerCase();
		setPluginStorage(pluginName, Boolean.parseBoolean(pluginState));
	}

	/**flag the plugin as a storage plugin
	* This will cause the plugin's functionality to be useable only by the DataLayer object,
	which will prevent the actions associated with the plugin being automatically exposed
	through the web service layer.*/
	public void setPluginStorage (String pluginName, boolean pluginState) throws IllegalArgumentException {
		if (pluginName == null) {
			throw new IllegalArgumentException("setPluginActive (String, boolean) was called with a null string");
		}

		if (pluginName.length() == 0) {
			throw new IllegalArgumentException("setPluginActive (String, boolean) was called with an empty string");
		}

		if (! plugins.containsKey(pluginName)) {
			throw new IllegalArgumentException("setPluginStorage(String, boolean) was called for a plugin that does not exist in the catalog");
		}

		/*Actions associated with storage plugins are not allowed to be directly executed.
		As a result, any currently available actions will be removed prior to setting the
		plugin as a storage plugin.*/
		//or not
		/*
		for (String[] action : actions) {
			//System.out.println("	" + action[0]);
			if (action[0].equals(pluginName)) {
				removeAction(action[0], action[1], action[2]);
			}
		}
		*/

		System.out.println("setPluginStorage(String, boolean)");
		System.out.println("	pluginName: " + pluginName);
		System.out.println("	pluginState: " + pluginState);
		if (pluginsStorage.contains(pluginName)) {
			System.out.println("	1");
			pluginsStorage.replace(pluginName, pluginState);
		}
		else {
			System.out.println("	2");
			pluginsStorage.put(pluginName, pluginState);
		}
	}

	/**get whether or not the specified plugin is a storage plugin.*/
	public boolean isPluginStorage (String pluginName) throws IllegalArgumentException {
		if (pluginName == null) {
			throw new IllegalArgumentException("isPluginStorage (String) was called with a null string");
		}

		if (pluginName.length() == 0) {
			throw new IllegalArgumentException("isPluginStorage (String) was called with an empty string");
		}

		if (! plugins.containsKey(pluginName)) {
			throw new IllegalArgumentException("isPluginStorage(String) was called for a plugin that does not exist in the catalog");
		}

		//System.out.println("isPluginStorage(String)");
		boolean ret = false;

		if (pluginsStorage.containsKey(pluginName)) {
			ret = pluginsStorage.get(pluginName);
		}

		return ret;
	}

////////////////////////////////////////////////////////////////////////////////
//Probably end up moving this out of this class because I do not think this
//should be in this class
////////////////////////////////////////////////////////////////////////////////
	//public Object performAction(String pluginName, String className, String methodName) {
	//	return performAction(pluginName, className, methodName, null);
	//}

	/*I don't need to know the return type because it's not part of the method signature*/
	public Object performAction(String pluginName, String className, String methodName, Object[] arguments) throws IllegalArgumentException {
		if (pluginName == null) {
			throw new IllegalArgumentException("performAction(String, String, String, Object[]) was called with a null string");
		}

		if (pluginName.length() == 0) {
			throw new IllegalArgumentException("performAction(String, String, String, Object[]) was called with an empty string");
		}

		if (! plugins.containsKey(pluginName)) {
			throw new IllegalArgumentException("performAction(String, String, String, Object[]) was called for a plugin that does not exist in the catalog");
		}

		if (className == null) {
			throw new IllegalArgumentException("performAction(String, String, String, Object[]) was called with a null string");
		}

		if (className.length() == 0) {
			throw new IllegalArgumentException("performAction(String, String, String, Object[]) was called with an empty string");
		}

		if (methodName == null) {
			throw new IllegalArgumentException("performAction(String, String, String, Object[]) was called with a null string");
		}

		if (methodName.length() == 0) {
			throw new IllegalArgumentException("performAction(String, String, String, Object[]) was called with an empty string");
		}

		System.out.println("performAction(String, String, String, Object[])");
		Object ret = null;

		if (isPluginActive(pluginName)) {
			try {
				int action = -1;

				for (int i = 0; i < actions.length; i++) {
					//System.out.println("	" + actions[i][0] + ", " + actions[i][1] + ", " + actions[i][2]);
					if ((actions[i][0].equals(pluginName)) && (actions[i][1].equals(className)) && (actions[i][2].equals(methodName))) {
							action = i;
					}
				}

				if (action < 0) {
					throw new RuntimeException("performAction(String, String, String, Object[]) invalid index in action");
				}

				String methodKey = actions[action][2] + "(";

				for (Object obj : arguments) {
					if (obj != null) {
						methodKey = methodKey + obj.getClass().getName() + ",";
					}
				}

				methodKey = methodKey.substring(0, methodKey.lastIndexOf(",")) + ")";
				//System.out.println("	methodKey: " + methodKey);
				Object objectInstance = null;
				Method objectMethod = null;

				if (objects.containsKey(actions[action][1])) {
					//System.out.println("	found cached object");
					objectInstance = objects.get(actions[action][1]);
				}
				else {
					//This can be a bit complicated because at this point
					//I need to know how to instantiate the object.
					//if there is a public constructor call it
					//System.out.println("	didn't find a cached object");
					Class klass = Class.forName(actions[action][1]);

					for (Constructor c : klass.getConstructors()) {
						if (c.getParameterCount() == 0) {
							//System.out.println("	found a public constructor...");
							objectInstance = c.newInstance(null);
						}
					}

					//if there is not a public constructor but there is
					//a public getInstance method call it
					//
					if (objectInstance == null) {
						//System.out.println("	didn't find a public constructor...");
						for (Method m : klass.getDeclaredMethods()) {
							//System.out.println("		evaluating " + m.getName());
							if ((m.getName().equals("getInstance")) && (m.getParameterCount() == 0) && (Modifier.isStatic(m.getModifiers()))) {
								//System.out.println("	found a static getInstance method");
								objectInstance = m.invoke(null, null);
								objects.putIfAbsent(actions[action][1], objectInstance);
							}
						}
					}
				}

				if (methods.containsKey(methodKey)) {
					System.out.println("	found cached method");
					objectMethod = methods.get(methodKey);
				}
				else {
					//if the method isn't in the cache, add it
					Class klass = Class.forName(actions[action][1]);
					Class args[] = null;

					if (arguments.length > 0) {
						args = new Class[arguments.length];

						for (int i = 0; i < arguments.length; i++) {
							args[i] = arguments[i].getClass();
						}
					}

					objectMethod = klass.getMethod(actions[action][2], args);

					if (objectMethod != null) {
						methods.putIfAbsent(methodKey, objectMethod);
					}
				}

				if (objectMethod != null) {
					//System.out.println("	objectMethod: " + objectMethod.getName());
					if (Modifier.isStatic(objectMethod.getModifiers())) {
						System.out.println("	" + objectMethod.getName() + " is static");
						ret = objectMethod.invoke(null, arguments);
					}
					else {
						if (objectInstance != null) {
							System.out.println("	objectInstance: " + objectInstance.getClass().getName());
							ret = objectMethod.invoke(objectInstance, arguments);
						}
					}
				}
			}
			catch (Exception exc) {
				exc.printStackTrace();
			}
		}

		return ret;
	}
////////////////////////////////////////////////////////////////////////////////

	/**a bunch of tests, I mean, a main method*/
	public static void main (String [] args) {
		try {
			ActionCatalog ac = new ActionCatalog();
			/*
			ac.setPluginActive("HelloWorldPlugin", true);
			ac.setPluginActive("HelloWorldPlugin", "true");
			ac.setPluginActive("HelloWorldPlugin", "TRUE");
			ac.setPluginActive("HelloWorldPlugin", "tRuE");
			ac.setPluginActive("HelloWorldPlugin", false);
			ac.setPluginActive("HelloWorldPlugin", "false");
			ac.setPluginActive("HelloWorldPlugin", "FALSE");
			ac.setPluginActive("HelloWorldPlugin", "FaLsE");
			*/
			//ac.setPluginActive("HelloWorldPlugin", "");
			//ac.setPluginActive("HelloWorldPlugin", " ");
			//ac.setPluginActive("HelloWorldPlugin", "wut");
			//ac.setPluginActive("HelloWorldPlugin", "adfkjew");

			ac.addPlugin("HelloWorldPugin", "C:\\core\\plugins\\HelloWorldPlugin");
			ac.addPlugin("HelloWorldPugin2", "C:\\core\\plugins\\HelloWorldPlugin2");
			ac.setPluginActive("HelloWorldPugin", true);
			ac.setPluginActive("HelloWorldPugin2", "true");
			ac.addAction("HelloWorldPugin", "ClassName", "MethodName");
			ac.addAction("HelloWorldPugin2", "ClassName2", "MethodName2");

			ac.removeAction("HelloWorldPugin", "ClassName", "MethodName");
			ac.removePlugin("HelloWorldPugin2");
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}