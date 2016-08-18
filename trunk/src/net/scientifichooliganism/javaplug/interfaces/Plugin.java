package net.scientifichooliganism.javaplug.interfaces;

public interface Plugin {
	public String[][] getActions();

	/*default*/
	public default Object getProperty (String key) throws RuntimeException {
		throw new RuntimeException("getProperty(String) default implementation called");
	}

	public default void setProperty (String key, Object value) throws RuntimeException {
		throw new RuntimeException("setProperty(String) default implementation called");
	}

	public default void init () { }
}