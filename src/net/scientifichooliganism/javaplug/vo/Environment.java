package net.scientifichooliganism.javaplug.vo;

public class Environment extends ValueObject {
	private String name;
	private String description;

	public Environment() {
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