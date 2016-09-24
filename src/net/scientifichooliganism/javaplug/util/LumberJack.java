package net.scientifichooliganism.javaplug.util;

public abstract class LumberJack {
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
