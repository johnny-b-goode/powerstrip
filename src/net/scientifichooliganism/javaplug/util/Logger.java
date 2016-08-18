package net.scientifichooliganism.javaplug.util;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;

public class Logger {
    static private volatile java.util.logging.Logger logger = null;

    // Make initializing logger thread-safe
    static private java.util.logging.Logger getLogger(){
        if(logger == null) {
            synchronized (Logger.class) {
                try {
                    if (logger == null) {
                        initLogger();
                    }
                } catch (IOException exc){
                    Logger.error(exc.getMessage());
                }
            }
        }
        return logger;
    }

    private static void initLogger() throws IOException{
        String logFileDir = "C:\\";
        String logFileName = "JavaPlugLog.txt";
        String logFilePath = logFileDir + logFileName;

        File logDir = new File(logFileDir);
        if(!logDir.exists()){
            logDir.mkdir();
        }


        FileHandler fileHandler = new FileHandler(logFilePath);
        ConsoleHandler consoleHandler = new ConsoleHandler();

        Formatter formatter = new LogFormatter();
        fileHandler.setFormatter(formatter);
        consoleHandler.setFormatter(formatter);

        logger = java.util.logging.Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
        logger.addHandler(fileHandler);
        logger.addHandler(consoleHandler);

        logger.setLevel(Level.ALL);
    }

    public static void config(String message) {
        getLogger().log(Level.CONFIG, message);
    }

    public static void config(String message, Object[] params) {
        getLogger().log(Level.CONFIG, message, params);
    }

    public static void info(String message) {
        getLogger().log(Level.INFO, message);
    }

    public static void info(String message, Object[] params) {
        getLogger().log(Level.INFO, message, params);
    }

    public static void log(String message) {
        // Using fine for now to debug
        getLogger().log(Level.FINE, message);
    }

    public static void log(String message, Object[] params) {
        getLogger().log(Level.FINE, message, params);
    }

    public static void warn(String message) {
        getLogger().log(Level.WARNING, message);
    }

    public static void warn(String message, Object[] params) {
        getLogger().log(Level.WARNING, message, params);
    }

    public static void error(String message) {
        getLogger().log(Level.SEVERE, message);
    }

    public static void error(String message, Object[] params){
        getLogger().log(Level.SEVERE, message, params);
    }

    static private String getTime(){
        DateFormat secondFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        Date date = new Date();
        return secondFormat.format(date);
    }

    public static void main(String args[]){
        Logger.error("Test logging methods!");
        Logger.error("An example with %1$s", new Object[]{"parameters"});
    }
}
