package net.scientifichooliganism.javaplug.vo;

public class Action extends ValueObject {
	private int id;
	private String name;
	private String description;
	private String module;
	private String klass;
	private String url;
	private String method;
	private boolean active;

	public Action () {
		super();
		id = -1;
		name = null;
		description = null;
		module = null;
		klass = null;
		url = null;
		method = null;
		active = false;
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

	public String getName () {
		return name;
	}

	public void setName (String in) throws IllegalArgumentException {
		if (in == null) {
			throw new IllegalArgumentException("setName(String) was called with a null string");
		}

		if (in.length() <= 0) {
			throw new IllegalArgumentException("setName(String) was called with an empty string");
		}

		name = in;
	}

	public String getDescription () {
		return description;
	}

	/**.*/
	public void setDescription (String in) throws IllegalArgumentException {
		if (in == null) {
			throw new IllegalArgumentException("setDescription(String) was called with a null string");
		}

		if (in.length() <= 0) {
			throw new IllegalArgumentException("setDescription(String) was called with an empty string");
		}

		description = in;
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

	public String getKlass () {
		return klass;
	}

	/*should probably setup mutual exclusion between class / method and URL.*/
	public void setKlass (String in) throws IllegalArgumentException {
		if (in == null) {
			throw new IllegalArgumentException("setKlass(String) was called with a null string");
		}

		if (in.length() <= 0) {
			throw new IllegalArgumentException("setKlass(String) was called with an empty string");
		}

		klass = in;
	}

	public String getURL () {
		return url;
	}

	public void setURL (String in) throws IllegalArgumentException {
		if (in == null) {
			throw new IllegalArgumentException("setURL(String) was called with a null string");
		}

		if (in.length() <= 0) {
			throw new IllegalArgumentException("setURL(String) was called with an empty string");
		}

		url = in;
	}

	public String getMethod () {
		return method;
	}

	public void setMethod (String in) throws IllegalArgumentException {
		if (in == null) {
			throw new IllegalArgumentException("setMethod(String) was called with a null string");
		}

		if (in.length() <= 0) {
			throw new IllegalArgumentException("setMethod(String) was called with an empty string");
		}

		method = in;
	}

	public boolean isActive () {
		return active;
	}

	public void setActive (boolean in) {
		active = in;
	}
}