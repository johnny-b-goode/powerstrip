package net.scientifichooliganism.javaplug.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaLogger extends LumberJack{
    public static void main(String args[]){
        LumberJack logger = LumberJack.getInstanceForContext(JavaLogger.class.getName());
        logger.setLogLevel(SpringBoard.INFO);
        logger.config("config");
        logger.info("info");
        logger.log("log");
        logger.warn("warn");
        logger.error("error");
    }

    Logger logger;
    public JavaLogger(String name) {
        logger = Logger.getLogger(name);
        logger.setUseParentHandlers(false);
        for(Handler h : logger.getHandlers()){
            logger.removeHandler(h);
        }
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE);
        consoleHandler.setFormatter(new JavaLoggerFormatter());
        logger.addHandler(consoleHandler);
    }

    @Override
    public void logMessage(String msg, SpringBoard level) {
        switch(level){
            case CONFIG:
                logger.log(Level.FINE, msg);
                break;
            case INFO:
                logger.log(Level.CONFIG, msg);
                break;
            case LOG:
                logger.log(Level.INFO, msg);
                break;
            case WARN:
                logger.log(Level.WARNING, msg);
                break;
            case ERROR:
                logger.log(Level.SEVERE, msg);
                break;
        }
    }

    @Override
    public void logException(Exception exc, SpringBoard level) {
        for(StackTraceElement element : exc.getStackTrace()){
            logMessage("    " + element.toString(), level);
        }
    }

    @Override
    public void setLogLevel(SpringBoard level){
        switch(level){
            case CONFIG:
                logger.setLevel(Level.FINE);
                break;
            case INFO:
                logger.setLevel(Level.CONFIG);
                break;
            case LOG:
                logger.setLevel(Level.INFO);
                break;
            case WARN:
                logger.setLevel(Level.WARNING);
                break;
            case ERROR:
                logger.setLevel(Level.SEVERE);
                break;
            default:
                logger.setLevel(Level.INFO);
        }
    }

    // TODO: Thought, do we need or want to abstract handlers?
    public void addHandler(Handler handler){
        // TODO: Should we be enforcing set formats and levels?
        handler.setFormatter(new JavaLoggerFormatter());
        handler.setLevel(Level.FINE);
        logger.addHandler(handler);
    }
}
