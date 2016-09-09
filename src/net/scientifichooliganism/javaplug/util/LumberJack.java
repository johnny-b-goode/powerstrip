package net.scientifichooliganism.javaplug.util;

public interface LumberJack {
    default void config(String msg){
        logMessage(msg, SpringBoard.CONFIG);
    }
    default void info(String msg) {
        logMessage(msg, SpringBoard.INFO);
    }
    default void log(String msg) {
        logMessage(msg, SpringBoard.LOG);
    }
    default void warn(String msg) {
        logMessage(msg, SpringBoard.WARN);
    }
    default void error(String msg) {
        logMessage(msg, SpringBoard.ERROR);
    }
    void logMessage(String msg, SpringBoard level);
    void logException(Exception exc, SpringBoard level);
}
