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
		name = null;
		description = null;
		concurrent = false;
		exclusive = false;
		scheduledDuration = null;
		startDate = null;
		completedDate = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String in) {
		name = in;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String in) {
		description = in;
	}

	public boolean getConcurrent() {
		return concurrent;
	}

	public void setConcurrent(boolean in) {
		concurrent = in;
	}

	public boolean getExclusive() {
		return exclusive;
	}

	public void setExclusive(boolean in) {
		exclusive = in;
	}

	public Duration getScheduledDuration() {
		return scheduledDuration;
	}

	public void setScheduledDuration(Duration in) {
		scheduledDuration = in;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date in) {
		startDate = in;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date in) {
		completedDate = in;
	}
}