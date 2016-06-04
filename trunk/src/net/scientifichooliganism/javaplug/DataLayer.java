package net.scientifichooliganism.javaplug;

import java.util.Collection;
import java.util.Vector;

import net.scientifichooliganism.javaplug.interfaces.Store;
import net.scientifichooliganism.javaplug.vo.ValueObject;

public final class DataLayer {
	private static DataLayer instance;
	private Vector<String> stores;
	/**
	* The default constructor.
	*/
	private DataLayer() {
		stores = new Vector();
	}

	public static DataLayer getInstance () {
		if (instance == null) {
			instance = new DataLayer();
		}

		return instance;
	}

	//

	/**a bunch of tests, I mean, a main method*/
	public static void main (String [] args) {
		try {
			DataLayer dl = DataLayer.getInstance();
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public Collection query(ActionCatalog ac, String query) throws IllegalArgumentException {
		if (ac == null) {
			throw new IllegalArgumentException("query(ActionCatalog, String) was called with a null ActionCatalog");
		}

		/*I need to figure out a proper solution for this because I really do not think I should be implementing
		my own query language or a parser for it. HOWEVER, whatever solution is used needs to be able to function
		with non-homogoneous abstracted data stores. I am inclined to use something SQL like due to:
			widespread use of SQL
			XPath is SQL like(ish) which should make translation / transition to XML based stores relatively easy
		Another requirement is that it should be easily translated from an HTTP query string.

		Also, if I ever find an instance where some goober implements a storage plugin that uses non-standard SQL
		and then other things end up querying based on that crap I will beat a dev to death with the ban-hammer.

		I think the smart thing to do here is to hand the query to the plugin and let the plugin figure out what
		to do with it.*/

		Vector ret = new Vector();
		for (String plugin : ac.keySet()) {
			if ((ac.isPluginStorage(plugin)) && (ac.isPluginActive(plugin))) {
				/*this is a bit of a special case because we already know what the method is called
				and the return type does not need to be specified (because we know the return type
				is going to be some form of java.util.Collection)*/
				String action[] = ac.findAction(plugin + " " + "query");
				System.out.println("	action: ");
				System.out.println("		" + action[0]);
				System.out.println("		" + action[1]);
				System.out.println("		" + action[2]);
				Vector temp = (Vector)ac.performAction(action[0], action[1], action[2], new Object[]{query});

				if (temp != null) {
					if (temp.size() > 0) {
						for (Object obj: temp) {
							((ValueObject)obj).setLabel(action[0] + "|" + ((ValueObject)obj).getLabel());
						}
					}
				}

				ret.addAll(temp);
			}
		}

		//dump collection to console
		System.out.println("	contents of ret: ");
		if (ret != null) {
			if (ret.size() > 0) {
				for (Object obj: ret) {
					System.out.println(String.valueOf(obj));
				}
			}
		}

		return null;
	}

	public void addStore(String store) throws IllegalArgumentException {
		if (store == null) {
			throw new IllegalArgumentException("addStore(String) was called with a null string");
		}

		if (store.length() <= 0) {
			throw new IllegalArgumentException("addStore(String) was called with an empty string");
		}

		if (stores.contains(store)) {
			throw new IllegalArgumentException("addStore(String) stores already contains an object with the value passed");
		}

		stores.add(store);
	}

	public void removeStore(String store) throws IllegalArgumentException {
		if (store == null) {
			throw new IllegalArgumentException("removeStore(String) was called with a null string");
		}

		if (store.length() <= 0) {
			throw new IllegalArgumentException("removeStore(String) was called with an empty string");
		}

		if (stores.contains(store)) {
			stores.remove(store);
		}
	}
}