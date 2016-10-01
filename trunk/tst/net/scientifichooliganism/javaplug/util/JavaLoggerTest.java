package net.scientifichooliganism.javaplug.util;

public class JavaLoggerTest {
	public static void main(String args[]){
		LumberJack logger = LumberJack.getInstanceForContext(JavaLoggerTest.class.getName());
		logger.setLogLevel(SpringBoard.INFO);
		logger.config("config");
		logger.info("info");
		logger.log("log");
		logger.warn("warn");
		logger.error("error");
	}
}
