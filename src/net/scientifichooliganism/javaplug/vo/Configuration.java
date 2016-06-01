package net.scientifichooliganism.javaplug.vo;

public class Configuration extends ValueObject {
	private int id;
	private String module;
	private int sequence;
	private String key;
	private String value;

	public Configuration () {
		super();
		id = -1;
		module = null;
		sequence = -1;
		key = null;
		value = null;
	}

	public static void main (String [] args) {
		try {
			//
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public int getID () {
		return id;
	}

	public void setID (int in) throws IllegalArgumentException {
		if (in < 0) {
			throw new IllegalArgumentException("setID(int) was called with a value less than zero");
		}

		id = in;
	}

	public String getModule () {
		return module;
	}

	public void setModule (String in) throws IllegalArgumentException {
		if (in == null) {
			throw new IllegalArgumentException("setModule(String) was called with a null string");
		}

		if (in.length() <= 0) {
			throw new IllegalArgumentException("setModule(String) was called with an empty string");
		}

		module = in;
	}

	public int getSequence () {
		return sequence;
	}

	public void setSequence (int in) throws IllegalArgumentException {
		if (in < 0) {
			throw new IllegalArgumentException("setSequence(int) was called with a value less than zero");
		}

		sequence = in;
	}

	public String getKey () {
		return key;
	}

	public void setKey (String in) throws IllegalArgumentException {
		if (in == null) {
			throw new IllegalArgumentException("setKey(String) was called with a null string");
		}

		if (in.length() <= 0) {
			throw new IllegalArgumentException("setKey(String) was called with an empty string");
		}

		key = in;
	}

	public String getValue () {
		return value;
	}

	public void setValue (String in) throws IllegalArgumentException {
		if (in == null) {
			throw new IllegalArgumentException("setValue(String) was called with a null string");
		}

		if (in.length() <= 0) {
			throw new IllegalArgumentException("setValue(String) was called with an empty string");
		}

		value = in;
	}
}