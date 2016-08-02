package net.scientifichooliganism.javaplug.vo;

import net.scientifichooliganism.javaplug.interfaces.Environment;

public class BaseEnvironment extends BaseValueObject implements Environment{
	private String name;
	private String description;

	public BaseEnvironment() {
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
}