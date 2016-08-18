package net.scientifichooliganism.javaplug.interfaces;

import net.scientifichooliganism.javaplug.query.Query;

import java.util.Collection;

public interface Store {
	public static boolean isStore() {
		return false;
	}

	public void addResource (Object resource) throws IllegalArgumentException;
	public Collection getResources () throws IllegalArgumentException;
	public void removeResource (Object resource) throws IllegalArgumentException;
	public void persist (Object in) throws IllegalArgumentException;
	public void remove (Object in) throws IllegalArgumentException;
	public Collection query (Query query) throws IllegalArgumentException;

}