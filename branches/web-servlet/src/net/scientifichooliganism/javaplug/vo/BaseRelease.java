package net.scientifichooliganism.javaplug.vo;

import net.scientifichooliganism.javaplug.interfaces.Release;
import java.util.Date;

public class BaseRelease extends BaseValueObject implements Release{
	private String application;
	private String name;
	private String description;
	private Date dueDate;
	private Date releaseDate;

	public BaseRelease() {
		super();
		application = null;
		name = null;
		description = null;
		dueDate = null;
		releaseDate = null;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String in) {
		application = in;
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

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date in) {
		dueDate = in;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date in) {
		releaseDate = in;
	}
}