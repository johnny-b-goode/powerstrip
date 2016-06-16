package net.scientifichooliganism.javaplug.vo;

import net.scientifichooliganism.javaplug.interfaces.Action;

public class BaseAction extends BaseValueObject implements Action{
	private int id;
	private String name;
	private String description;
	private String module;
	private String klass;
	private String url;
	private String method;

	public BaseAction() {
		super();
		id = -1;
		name = null;
		description = null;
		module = null;
		klass = null;
		url = null;
		method = null;
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
		ret = ret + "name: " + String.valueOf(name) + "\n";
		ret = ret + "description: " + String.valueOf(description) + "\n";
		ret = ret + "module: " + String.valueOf(module) + "\n";
		ret = ret + "klass: " + String.valueOf(klass) + "\n";
		ret = ret + "url: " + String.valueOf(url) + "\n";
		ret = ret + "method: " + String.valueOf(method) + "\n";
		return ret;
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
}