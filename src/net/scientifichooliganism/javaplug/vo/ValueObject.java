package net.scientifichooliganism.javaplug.vo;

public class ValueObject {
	private String label;

	public ValueObject () {
		label = null;
	}

	public String getLabel () {
		return label;
	}

	public void setLabel (String in) throws IllegalArgumentException {
		if (in == null) {
			throw new IllegalArgumentException("setMethod(String) was called with a null string");
		}

		if (in.length() <= 0) {
			throw new IllegalArgumentException("setMethod(String) was called with an empty string");
		}

		label = in;
	}

	public String toString () {
		return "label: " + String.valueOf(label) + "\n";
	}
}