package net.scientifichooliganism.javaplug;

import java.lang.reflect.Method;

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
public class ActionCatalog {
	/*Plugin Name | isActive | Class Name | Method Name*/
	String actions[][];
	ConcurrentHashMap<String, String> plugins;
	ConcurrentHashMap<String, Boolean> pluginsEnabled;
	ConcurrentHashMap<String, Object> objects;
	ConcurrentHashMap<String, Method> methods;

	/**
	* The default constructor.
	*/
	public ActionCatalog () {
		try {
			actions = new String[1][3];
			plugins = new ConcurrentHashMap<String, String>();
			pluginsEnabled = new ConcurrentHashMap<String, Boolean>();
			objects = new ConcurrentHashMap<String, Object>();
			methods = new ConcurrentHashMap<String, Method>();
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
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
			throw new IllegalArgumentException("addAction(String, String, String) was called with a null string");
		}

		if (pluginName.length() == 0) {
			throw new IllegalArgumentException("addAction(String, String, String) was called with an empty string");
		}

		if (! plugins.containsKey(pluginName)) {
			throw new IllegalArgumentException("addAction(String, String, String) was called for a plugin that does not exist in the catalog");
		}

		if (className == null) {
			throw new IllegalArgumentException("addAction(String, String, String) was called with a null string");
		}

		if (className.length() == 0) {
			throw new IllegalArgumentException("addAction(String, String, String) was called with an empty string");
		}

		if (methodName == null) {
			throw new IllegalArgumentException("addAction(String, String, String) was called with a null string");
		}

		if (methodName.length() == 0) {
			throw new IllegalArgumentException("addAction(String, String, String) was called with an empty string");
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

		System.out.println("addAction(String, String, String)");
		for (int i = 0; i < actions.length; i++) {
			for (int j = 0; j < actions[0].length; j++) {
				System.out.println("	" + actions[i][j]);
			}
		}
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
			throw new IllegalArgumentException("setPluginActive (String, String) was called with a null string");
		}

		if (pluginName.length() == 0) {
			throw new IllegalArgumentException("setPluginActive (String, String) was called with an empty string");
		}

		if (! plugins.containsKey(pluginName)) {
			throw new IllegalArgumentException("addAction(String, String, String) was called for a plugin that does not exist in the catalog");
		}

		if (pluginsEnabled.contains(pluginName)) {
			pluginsEnabled.replace(pluginName, pluginState);
		}
		else {
			pluginsEnabled.put(pluginName, pluginState);
		}
	}

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