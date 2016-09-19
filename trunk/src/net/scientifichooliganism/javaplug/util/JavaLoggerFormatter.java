package net.scientifichooliganism.javaplug.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class JavaLoggerFormatter extends Formatter {
	@Override
	public String format(LogRecord record) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
		Date time = new Date(record.getMillis());
		Level level = record.getLevel();
		String levelString = "NULL";
		if(level == Level.SEVERE) {
			levelString = "ERROR";
		} else if(level == Level.WARNING){
			levelString = "WARN";
		} else if(level == Level.INFO){
			levelString = "LOG";
		} else if(level == Level.CONFIG){
			levelString ="INFO";
		} else if(level == Level.FINE) {
			levelString = "CONFIG";
		}

		return sdf.format(time) + " [" + levelString + "]: " + record.getMessage() + "\n";
	}
}
