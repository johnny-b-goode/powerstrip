package net.scientifichooliganism.javaplug.vo;

import net.scientifichooliganism.javaplug.interfaces.Application;

public class BaseApplication extends BaseValueObject implements Application {
	private String name;
	private String description;

	public BaseApplication() {
		super();
		name = null;
		description = null;
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
}
