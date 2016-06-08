package net.scientifichooliganism.javaplug.vo;

public class Configuration extends ValueObject {
	private String module;
	private int sequence;
	private String key;
	private String value;

	public Configuration () {
		super();
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

	public String toString() {
		String ret = super.toString();
		ret = ret + "module: " + String.valueOf(module) + "\n";
		ret = ret + "sequence: " + String.valueOf(sequence) + "\n";
		ret = ret + "key: " + String.valueOf(key) + "\n";
		ret = ret + "value: " + String.valueOf(value) + "\n";
		return ret;
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