package net.scientifichooliganism.javaplug.vo;

import net.scientifichooliganism.javaplug.interfaces.ValueObject;

public class BaseValueObject implements ValueObject {
	private String id;
	private String label;

	public BaseValueObject() {
		id = null;
		label = null;
	}

	public String getID () {
		return id;
	}

	public void setID (String in) throws IllegalArgumentException, RuntimeException {
		/**This is intentional. This property is only intended to be set once, ever, and by the data layer at that.*/
		if (id != null){
			throw new RuntimeException("setID(int) has already been called, it may not be called again.");
		}

		id = in;
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
		String ret = "label: " + String.valueOf(label) + "\n";
		ret = ret + "id: " + String.valueOf(id) + "\n";
		return ret;
	}

}