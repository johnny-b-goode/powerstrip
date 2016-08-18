package net.scientifichooliganism.javaplug.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
    private static final DateFormat dateFormat =
            new SimpleDateFormat("yyyMMdd-hhmmss");
    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append("[")
                .append(getLevelString(record.getLevel()))
                .append("] ")
                .append(dateFormat.format(record.getMillis()))
                .append(" - ")
                .append(String.format(record.getMessage(), record.getParameters()))
                .append("\n");
        return builder.toString();
    }

    private String getLevelString(Level level){
        if(level == Level.SEVERE){
            return "ERROR";
        }
        if(level == Level.WARNING){
            return "WARN";
        }
        if (level == Level.FINE){
            return "LOG";
        }

        return level.toString();
    }
}
