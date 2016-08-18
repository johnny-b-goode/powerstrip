package net.scientifichooliganism.javaplug.vo;

import net.scientifichooliganism.javaplug.interfaces.Environment;

public class BaseEnvironment extends BaseValueObject implements Environment{
	private String name;
	private String description;

	public BaseEnvironment() {
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