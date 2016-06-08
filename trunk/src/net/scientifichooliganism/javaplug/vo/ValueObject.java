package net.scientifichooliganism.javaplug.vo;

public class ValueObject {
	private int id;
	private String label;

	public ValueObject () {
		id = -1;
		label = null;
	}

	public int getID () {
		return id;
	}

	public void setID (int in) throws IllegalArgumentException, RuntimeException {
		if (in < 0) {
			throw new IllegalArgumentException("setID(int) was called with a value less than zero");
		}

		/**This is intentional. This property is only intended to be set once, ever, and by the data layer at that.*/
		if (id != -1) {
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