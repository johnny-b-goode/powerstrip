package net.scientifichooliganism.javaplug;

import net.scientifichooliganism.javaplug.interfaces.Action;
import net.scientifichooliganism.javaplug.interfaces.Configuration;
import net.scientifichooliganism.javaplug.util.Logger;

import java.io.*;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

/**
* This class does stuff, maybe.
*/
public class PluginLoader {
	private static ClassLoader defaultClassLoader = ClassLoader.getSystemClassLoader();
	/**
	* The default constructor.
	*/
	public PluginLoader() {
		//
	}

	public static String extractPlugin (String pluginPath) throws IllegalArgumentException {
		if (pluginPath == null) {
			throw new IllegalArgumentException("extractPlugin (String) was called with null");
		}

		if (pluginPath.length() == 0) {
			throw new IllegalArgumentException("extractPlugin (String) was passed an empty string");
		}

		return extractPlugin(new File(pluginPath));
	}

	public static String extractPlugin (File pluginArchive) throws IllegalArgumentException {
		String strRet = null;

		if (pluginArchive == null) {
			throw new IllegalArgumentException("extractPlugin(File) was called with null");
		}

		try {
			String pluginDirectory = pluginArchive.getCanonicalPath();
			ZipInputStream zipIn = new ZipInputStream(new FileInputStream(pluginArchive));

			if (pluginDirectory == null) {
				throw new RuntimeException("getCanonicalPath() returned null");
			}

			pluginDirectory = pluginDirectory.substring(0, pluginDirectory.lastIndexOf("."));

			if (new File(pluginDirectory).mkdir() == false) {
				throw new IOException("unable to create directory " + pluginDirectory);
			}
			else {
				strRet = pluginDirectory;
			}

			try {
				ZipEntry item = zipIn.getNextEntry();

				while (item != null) {
					String itemPath = pluginDirectory + File.separator + item.getName().trim();

					if (item.isDirectory()) {
						if (new File(itemPath).mkdir() == false) {
							throw new Exception("unable to create directory " + pluginDirectory);
						}
					}
					else {
						byte [] buffer = new byte[4096];
						int bytesRead = zipIn.read(buffer);
						//System.out.println("bytesRead: " + String.valueOf(bytesRead));
						BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(itemPath));

						while (bytesRead != -1) {
							fileOut.write(buffer, 0, bytesRead);
							bytesRead = zipIn.read(buffer);
						}

						fileOut.close();
					}

					item = zipIn.getNextEntry();
				}
			}
			catch (ZipException zxc) {
				Logger.log(zxc.getMessage());
				zxc.printStackTrace();
			}
			catch (IOException iox) {
				Logger.log(iox.getMessage());
				iox.printStackTrace();
			}
			finally {
				try {
					zipIn.close();
				}
				catch (Exception exc) {
					Logger.log(exc.getMessage());
					//exc.printStackTrace();
				}
			}

			pluginArchive.delete();
		}
		catch (Exception exc) {
			Logger.log(exc.getMessage());
			exc.printStackTrace();
		}

		return strRet;
	}

	public static ConcurrentHashMap<String, String> findPlugins () {
		return findPlugins("..", "PLUGINS");
	}

	public static ConcurrentHashMap<String, String> findPlugins (String startingPath, String pluginsDirectoryName) {
//		System.out.println("findPlugins(String, String) called with " + startingPath + ", " + pluginsDirectoryName);
		ConcurrentHashMap<String, String> pluginMap = new ConcurrentHashMap<String, String>();

		try {
			File currentDir = new File(startingPath);

			for (File temp : currentDir.listFiles()) {
//				System.out.println("	temp.getName(): " + temp.getName());
//				System.out.println("	temp.getCanonicalPath(): " + temp.getCanonicalPath());
//				System.out.println("	temp.isDirectory(): " + String.valueOf(temp.isDirectory()));

				if (temp.isDirectory()) {
					if (temp.getName().toUpperCase().equals(pluginsDirectoryName.toUpperCase())) {
						//this will be treated differently because the contents of this path should be directories representing plugins,
						//and / or archives that should be expanded into directories representing plugins
//						System.out.println("	Found plugins directory");

						for (File plugin : temp.listFiles()) {
							if (pluginMap.get(plugin.getName().trim()) != null) {
								throw new RuntimeException("a key containing the value " + plugin.getName().trim() + " already exists");
							}

//							System.out.println("    Adding plugin: " + plugin.getName() + " at: " + plugin.getCanonicalPath().trim());
							pluginMap.putIfAbsent(plugin.getName().trim(), plugin.getCanonicalPath().trim());
						}
					}
					else {
						pluginMap.putAll(findPlugins(temp.getAbsolutePath(), pluginsDirectoryName));
					}
				}
			}
		}
		catch (Exception exc) {
			Logger.log(exc.getMessage());
			exc.printStackTrace();
		}

		return pluginMap;
	}

	/**.*/
	public static ActionCatalog loadActionCatalog () {
		ActionCatalog ac = ActionCatalog.getInstance();

		try {
			ConcurrentHashMap<String, String> plugins = findPlugins();
			
			for (String key : plugins.keySet()) {
//				System.out.println("		key: " + key + ", value: " + String.valueOf(plugins.get(key)));
				File pluginFile = new File(plugins.get(key));

				if (pluginFile.isFile()) {
					//then it is an archive, extract it
					try {
						String strKeyTemp = key.substring(0, key.lastIndexOf("."));
						String strPathTemp = extractPlugin(pluginFile);
//						System.out.println("		strKeyTemp: " + strKeyTemp + ", strPathTemp: " + strPathTemp);

						if ((strPathTemp != null) && (strPathTemp.length() > 0)) {
							if (ac.containsPlugin(strKeyTemp)) {
								throw new RuntimeException("a key containing the value " + strKeyTemp + " already exists");
							}

							ac.addPlugin(strKeyTemp, strPathTemp);
							ac.setPluginActive(strKeyTemp, true);
						}
					}
					catch (Exception exc) {
						Logger.log(exc.getMessage());
						exc.printStackTrace();
					}
				}
				else {
					ac.addPlugin(key, plugins.get(key));
					ac.setPluginActive(key, true);
				}
			}

			try {
				Method mthd = (URLClassLoader.class).getDeclaredMethod("addURL", new Class[]{URL.class});
				mthd.setAccessible(true);

				for (String plugin : ac.keySet()) {
//					System.out.println("	plugin: " + plugin);
//					System.out.println("	pluginPath: " + ac.getPluginPath(plugin));
					URL url = null;

					try {
						url = (new File(ac.getPluginPath(plugin))).toURI().toURL();
					}
					catch (MalformedURLException mue) {
						Logger.log(mue.getMessage());
						mue.printStackTrace();
					}

					if (url == null) {
						throw new RuntimeException("url is null");
					}

					if (url.toString().length() == 0) {
						throw new RuntimeException("url is empty");
					}

//					System.out.println("	attempting to load " + String.valueOf(url));
					mthd.invoke((defaultClassLoader), new Object[]{url});
				}

				String thisPath = PluginLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				URL thisURL = (new File(thisPath)).toURI().toURL();

				mthd.invoke(defaultClassLoader, new Object[]{thisURL});
			}
			catch (Exception exc) {
				Logger.log(exc.getMessage());
//				System.out.println("messin' with the classloader failed:");
				exc.printStackTrace();
			}
		}
		catch (Exception exc) {
			Logger.log(exc.getMessage());
			exc.printStackTrace();
		}

		return ac;
	}

	public static void bootstrap() {
		bootstrap(null);
	}

	public static void bootstrap (ClassLoader classLoader) {
		try {
			if(classLoader != null){
				PluginLoader.defaultClassLoader = classLoader;
			}

			ActionCatalog ac = loadActionCatalog();
			//load xml plugin
			ac.addAction("XMLPlugin", "net.scientifichooliganism.xmlplugin.XMLPlugin", "objectFromNode");
			ac.setPluginActive("XMLPlugin", true);

			ac.addAction("XMLDataStorePlugin", "net.scientifichooliganism.xmldatastore.XMLDataStorePlugin", "addResource");
			ac.addAction("XMLDataStorePlugin", "net.scientifichooliganism.xmldatastore.XMLDataStorePlugin", "query");
			ac.setPluginActive("XMLDataStorePlugin", true);
			ac.setPluginStorage("XMLDataStorePlugin", true);

			for (String plugin : ac.keySet()) {
				ac.performAction("XMLDataStorePlugin", "net.scientifichooliganism.xmldatastore.XMLDataStorePlugin", "addResource", new Object[]{ac.getPluginPath(plugin)});
			}
//			System.out.println("Finished Performing actions!");
			//Initialize a data directory that is sibling to plugins for the initial default

			//System.out.println(String.valueOf(ac.isPluginActive("XMLDataStorePlugin")));
			//System.out.println(String.valueOf(ac.isPluginStorage("XMLDataStorePlugin")));

			//ac.findAction("query");
			//ac.findAction("XMLDataStorePlugin query");
			//ac.findAction("XMLDataStorePlugin net.scientifichooliganism.xmldatastore.XMLDataStorePlugin query");

			DataLayer dl = DataLayer.getInstance();
			//read plugin data from xml
			Vector<Action> actions = (Vector<Action>)dl.query(ac, "SELECT action FROM plugin");
			Vector<Configuration> configs = (Vector<Configuration>)dl.query(ac, "SELECT config FROM plugin");

			/*
			All plugins will be disabled
			All actions will be removed from AC
			Actions in the actions vector will be added to AC
			Plugins will be configured - being enabled if appropriate
			*/

			for (String plugin : ac.keySet()) {
				String path = ac.getPluginPath(plugin);
				/*This will remove the plugin, all actions associated with it, and
				whether or not it is enabled and / or a storage plugin.
				*/
				ac.removePlugin(plugin);
				ac.addPlugin(plugin, path);
			}

//			System.out.println("actions:");
			if (actions != null) {
				if (actions.size() > 0) {
					for (Action act : actions) {
//						System.out.println("	module: " + act.getModule());
//						System.out.println("	class: " + act.getKlass());
//						System.out.println("	method: " + act.getMethod());
						ac.addAction(act.getModule(), act.getKlass(), act.getMethod());
					}
				}
			}

			if (configs != null) {
				if (configs.size() > 0) {
					for (Configuration conf : configs) {
						if (conf.getKey().toLowerCase().trim().equals("active")) {
							ac.setPluginActive(conf.getModule(), conf.getValue());
						}
						else if (conf.getKey().toLowerCase().trim().equals("storage")) {
							ac.setPluginStorage(conf.getModule(), conf.getValue());
						}
					}

					//this is done in a second pass to ensure the plugins have been activated so that the proper action can be called.
					for (Configuration conf : configs) {
						if ((! conf.getKey().toLowerCase().trim().equals("active")) && (! conf.getKey().toLowerCase().trim().equals("storage"))) {
							try {
								String [] action = ac.findAction(conf.getModule() + ".setProperty");

								if ((action != null) && (action.length > 0)) {
									ac.performAction(action[0], action[1], action[2], new Object[]{conf.getValue()});
								}
							}
							catch (Exception exc) {
								Logger.log(exc.getMessage());
								exc.printStackTrace();
							}
						}
					}

					for(Configuration config : configs){
						if(config.getKey().toLowerCase().equals("depends")){
							String pluginName = config.getValue();
							if(!(ac.containsPlugin(pluginName) && ac.isPluginActive(pluginName))){
								ac.setPluginActive(config.getModule(), false);
								String message = config.getModule() + " depends on " + pluginName +
										" which was either not added or not enabled.";
								Logger.warn(message);
							}
						}
					}
				}
			}
		}
		catch (Exception exc) {
			Logger.log(exc.getMessage());
			exc.printStackTrace();
		}
	}

	public static void main(String [] args){
		bootstrap();
	}
}
