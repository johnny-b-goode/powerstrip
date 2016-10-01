package net.scientifichooliganism.javaplug.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class LumberJack {
	private static ConcurrentMap<String, LumberJack> loggers = new ConcurrentHashMap<String, LumberJack>();

	public static LumberJack getInstanceForContext(String context){
		LumberJack logger;

		if(loggers.containsKey(context)){
			logger = loggers.get(context);
		}
		else {
			logger = new JavaLogger(context);
		}

		return logger;
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

	abstract public void logMessage(String msg);

	abstract public void logMessage(String msg, SpringBoard level);

	abstract public void logException(Exception exc);

	abstract public void logException(Exception exc, SpringBoard level);
}
