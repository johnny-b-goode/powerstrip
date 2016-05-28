package net.scientifichooliganism.javaplug;

import java.io.File;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

import java.nio.file.Files;

import java.util.concurrent.ConcurrentHashMap;

import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

/**
* This class does stuff, maybe.
*/
public class PluginLoader {
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
				zxc.printStackTrace();
			}
			catch (IOException iox) {
				iox.printStackTrace();
			}
			finally {
				try {
					zipIn.close();
				}
				catch (Exception exc) {
					//exc.printStackTrace();
				}
			}

			pluginArchive.delete();
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}

		return strRet;
	}

	public static ConcurrentHashMap<String, String> findPlugins () {
		return findPlugins("..", "PLUGINS");
	}

	public static ConcurrentHashMap<String, String> findPlugins (String startingPath, String pluginsDirectoryName) {
		//System.out.println("findPlugins(String, String) called with " + startingPath + ", " + pluginsDirectoryName);
		ConcurrentHashMap<String, String> pluginMap = new ConcurrentHashMap<String, String>();

		try {
			File currentDir = new File(startingPath);

			for (File temp : currentDir.listFiles()) {
				//System.out.println("	temp.getName(): " + temp.getName());
				//System.out.println("	temp.getCanonicalPath(): " + temp.getCanonicalPath());
				//System.out.println("	temp.isDirectory(): " + String.valueOf(temp.isDirectory()));

				if (temp.isDirectory()) {
					if (temp.getName().toUpperCase().equals(pluginsDirectoryName.toUpperCase())) {
						//this will be treated differently because the contents of this path should be directories representing plugins,
						//and / or archives that should be expanded into directories representing plugins
						//System.out.println("	Found plugins directory");

						for (File plugin : temp.listFiles()) {
							if (pluginMap.get(plugin.getName().trim()) != null) {
								throw new RuntimeException("a key containing the value " + plugin.getName().trim() + " already exists");
							}

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
			exc.printStackTrace();
		}

		return pluginMap;
	}

	/**.*/
	public static ActionCatalog loadActionCatalog () {
		ActionCatalog ac = new ActionCatalog();

		try {
			ConcurrentHashMap<String, String> plugins = findPlugins();
			
			for (String key : plugins.keySet()) {
				//System.out.println("		key: " + key + ", value: " + String.valueOf(plugins.get(key)));
				File pluginFile = new File(plugins.get(key));

				if (pluginFile.isFile()) {
					//then it is an archive, extract it
					try {
						String strKeyTemp = key.substring(0, key.lastIndexOf("."));
						String strPathTemp = extractPlugin(pluginFile);
						//System.out.println("		strKeyTemp: " + strKeyTemp + ", strPathTemp: " + strPathTemp);

						if ((strPathTemp != null) && (strPathTemp.length() > 0)) {
							if (ac.containsPlugin(strKeyTemp)) {
								throw new RuntimeException("a key containing the value " + strKeyTemp + " already exists");
							}

							ac.addPlugin(strKeyTemp, strPathTemp);
							ac.setPluginActive(strKeyTemp, true);
						}
					}
					catch (Exception exc) {
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
					//System.out.println("	plugin: " + plugin);
					//System.out.println("	pluginPath: " + ac.getPluginPath(plugin));
					URL url = null;

					try {
						url = (new File(ac.getPluginPath(plugin))).toURI().toURL();
					}
					catch (MalformedURLException mue) {
						mue.printStackTrace();
					}

					if (url == null) {
						throw new RuntimeException("url is null");
					}

					if (url.toString().length() == 0) {
						throw new RuntimeException("url is empty");
					}

					//System.out.println("	attempting to load " + String.valueOf(url));
					mthd.invoke(((URLClassLoader)ClassLoader.getSystemClassLoader()), new Object[]{url});
				}
			}
			catch (Exception exc) {
				System.out.println("messin' with the classloader failed:");
				exc.printStackTrace();
			}

			//add actions

			//System.out.println("============================================================");
				//for every plugin
					//find the class that implements the plugin interface
					//register the classes and methods from the class that implements the plugin interface
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}

		return ac;
	}

	public static void main (String [] args) {
		ActionCatalog ac = loadActionCatalog();

		//

		try {
			String strPluginClass = "net.scientifichooliganism.helloworldplugin.HelloWorldPlugin";
			String strPluginMethod = "printMessage";
			Class klass = Class.forName(strPluginClass);
			Method [] methods = klass.getDeclaredMethods();
			Method objectMethod = null;

			if (methods.length > 0) {
				for (Method m : methods) {
					if (m.getName().equals(strPluginMethod)) {
						objectMethod = m;
					}
				}
			}

			if (objectMethod != null) {
				Object objectInstance = null;

				if (Modifier.isStatic(objectMethod.getModifiers())) {
					//System.out.println("Yeaup, it's static alright.");
				}

				objectMethod.invoke(null, null);
			}
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
		finally {
			System.exit(0);
		}
	}
}
