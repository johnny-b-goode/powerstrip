package net.scientifichooliganism.javaplug.interfaces;

import java.util.Collection;

public interface Store {
	public static boolean isStore() {
		return false;
	}

	public void addResource (Object resource) throws IllegalArgumentException;
	public Collection getResources () throws IllegalArgumentException;
	public void removeResource (Object resource) throws IllegalArgumentException;
	public void persist (Object in) throws IllegalArgumentException;
	public Collection query (String query) throws IllegalArgumentException;
}