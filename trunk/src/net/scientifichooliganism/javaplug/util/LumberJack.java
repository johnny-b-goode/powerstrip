package net.scientifichooliganism.javaplug.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class LumberJack {
	private static Map<String, LumberJack> loggers = new ConcurrentHashMap<>();
	private static LumberJack logger = new JavaLogger(LumberJack.class.getName());

	public static synchronized LumberJack getInstanceForContext(String context, String classname){
		LumberJack instance;
		if(loggers.containsKey(context)){
			instance = loggers.get(context);
		} else {
			instance = createInstance(context, classname);
			loggers.put(context, instance);
		}

		return instance;
	}

	public static synchronized LumberJack getInstanceForContext(String context){
		return getInstanceForContext(context, JavaLogger.class.getName());
	}

	// TODO: Do we want this? does this give too much power to plugins with access to this class?
	public static void setGlobalLogLevel(SpringBoard level){
		for(LumberJack logger : loggers.values()){
			logger.setLogLevel(level);
		}
	}

	private static synchronized LumberJack createInstance(String context, String classname){
		LumberJack instance;
		try {
			Class klass = Class.forName(classname);
			if(LumberJack.class.isAssignableFrom(klass)) {
				try {
					Constructor constructor = klass.getConstructor(String.class);
					instance = (LumberJack) constructor.newInstance(context);
					return instance;
				} catch (NoSuchMethodException exc) {
					logger.log("LumberJack could not find constructor with String arg for "
							+ classname + " attempting to find default constructor");
				} catch (Exception exc) {
					logger.error("LumberJack could not instantiate " + classname + "with String constructor");
					logger.logException(exc, SpringBoard.ERROR);
					return null;
				}

				try {
					Constructor constructor = klass.getConstructor();
					instance = (LumberJack) constructor.newInstance();
					return instance;
				} catch(NoSuchMethodException exc){
					logger.log("LumberJack could not find default constructor for " + classname +
						" attempting to find getInstance method.");
				} catch(Exception exc){
					logger.error("LumberJack could not instantiate " + classname + " with default constructor");
					logger.logException(exc, SpringBoard.ERROR);
					return null;
				}

				try {
					Method singletonMethod = klass.getMethod("getInstance", String.class);
					instance = (LumberJack)singletonMethod.invoke(null, context);
					return instance;
				} catch(NoSuchMethodException exc){
					logger.log("LumberJack could not find \"getInstance(String)\" method for "
							+ classname + " attempting to find \"getInstance()\" method");
				} catch(Exception exc){
					logger.error("LumberJack could not call getInstance(String) on " + classname);
					logger.logException(exc, SpringBoard.ERROR);
				}

				try {
					Method singletonMethod = klass.getMethod("getInstance");
					instance = (LumberJack)singletonMethod.invoke(null);
					return instance;
				} catch(Exception exc){
					logger.error("LumberJack could not find a way to instantiate " + classname);
					logger.logException(exc, SpringBoard.ERROR);
					return null;
				}
			} else {
				logger.error("LumberJack cannot assign LumberJack from " + classname);
				instance = null;
			}
		} catch(ClassNotFoundException exc){
			instance = null;
			logger.error("LumberJack could not find class: " + classname);
			logger.logException(exc, SpringBoard.ERROR);
		}

		return instance;
	}

	final public void config(String msg){
		logMessage(msg, SpringBoard.CONFIG);
	}
	final public void info(String msg) {
		logMessage(msg, SpringBoard.INFO);
	}
	final public void log(String msg) {
		logMessage(msg, SpringBoard.LOG);
	}
	final public void warn(String msg) {
		logMessage(msg, SpringBoard.WARN);
	}
	final public void error(String msg) {
		logMessage(msg, SpringBoard.ERROR);
	}
	abstract public void setLogLevel(SpringBoard level);
	abstract public void logMessage(String msg, SpringBoard level);
	abstract public void logException(Exception exc, SpringBoard level);
}
