package net.scientifichooliganism.javaplug.vo;

import net.scientifichooliganism.javaplug.interfaces.Task;
import java.util.Date;
import java.time.Duration;

public class BaseTask extends BaseValueObject implements Task {
    private String name;
    private String description;
    private boolean concurrent;
    private boolean exclusive;
    private Duration scheduledDuration;
    private Date startDate;
    private Date completedDate;

	public BaseTask() {
		super();
	}

	public static void main (String [] args) {
		try {
			//
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}

    String getName() {
        return name;
    }

    void setName(String in) {
        name = in;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String in) {
        description = in;
    }

    boolean getConcurrent() {
        return concurrent;
    }

    void setConcurrent(boolean in) {
        concurrent = in;
    }

    boolean getExclusive() {
        return exclusive;
    }

    void setExclusive(boolean in) {
        exclusive = in;
    }

    Duration getScheduledDuration() {
        return scheduledDuration;
    }
    void setScheduledDuration(Duration in) {
        scheduledDuration = in;
    }

    Date getStartDate() {
        return startDate;
    }
    void setStartDate(Date in) {
        startDate = in;
    }

    Date getCompletedDate() {
        return completedDate;
    }

    void setCompletedDate(Date in) {
        completedDate = in;
    }
}