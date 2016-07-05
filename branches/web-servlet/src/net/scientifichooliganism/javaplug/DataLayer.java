package net.scientifichooliganism.javaplug;

import net.scientifichooliganism.javaplug.interfaces.Action;
import net.scientifichooliganism.javaplug.interfaces.Configuration;
import net.scientifichooliganism.javaplug.interfaces.ValueObject;
import net.scientifichooliganism.javaplug.util.Logger;
import net.scientifichooliganism.javaplug.vo.BaseAction;
import net.scientifichooliganism.javaplug.vo.BaseConfiguration;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Vector;

public final class DataLayer {
	private String defaultStore;
	private static DataLayer instance;
	private Vector<String> stores;
	private BigInteger currentId;
	Configuration currentIdConfig;

	/**
	* The default constructor.
	*/
	private DataLayer() {
		stores = new Vector();
		defaultStore = null;

		currentIdConfig = (Configuration)query("SELECT configuration FROM data");
		if(currentIdConfig == null) {
			currentIdConfig = new BaseConfiguration();
			currentIdConfig.setKey("current_id");
			currentIdConfig.setValue("0");
		}
		currentId = new BigInteger(currentIdConfig.getValue());


	}

	public static DataLayer getInstance () {
		if (instance == null) {
			instance = new DataLayer();
		}

		return instance;
	}

	/**a bunch of tests, I mean, a main method*/
	public static void main (String [] args) {
		try {
			PluginLoader.bootstrap();
			DataLayer dl = DataLayer.getInstance();
			dl.setDefaultStore("XMLDataStorePlugin");

			Action action = new BaseAction();
			action.setName("My Action Name");
			action.setMethod("New method");
			action.setURL("google.com");

			action.setModule("Module");
			action.setDescription("description");
			action.setKlass("action");

			dl.persist(action);

			Collection actions = dl.query("SELECT action FROM data");

			Action changeAction = (Action)actions.iterator().next();
			changeAction.setName("NEW NAME!!");
			dl.persist(changeAction);
		}
		catch (Exception exc) {
//			Logger.log(exc.getMessage());
			exc.printStackTrace();
		}
	}

	public String getUniqueID(){
		String newId = currentId.toString();
		currentId.add(BigInteger.ONE);
		currentIdConfig.setValue(currentId.toString());
		persist(currentIdConfig);
		return newId;
	}



	/*I need to figure out a proper solution for this because I really do not think I should be implementing
	my own query language or a parser for it. HOWEVER, whatever solution is used needs to be able to function
	with non-homogoneous abstracted data stores. I am inclined to use something SQL like due to:
		widespread use of SQL
		can be (relatively) easily translated from an HTTP query string.

	Also, if I ever find an instance where some goober implements a storage plugin that uses non-standard SQL
	and then other things end up querying based on that crap I will beat a dev to death with the ban-hammer.

	I think the smart thing to do here is to hand the query to the plugin and let the plugin figure out what
	to do with it.*/
	public Collection query (String query) throws IllegalArgumentException, RuntimeException {
//		System.out.println("query(String)");
		return query(ActionCatalog.getInstance(), query);
	}

	public Collection query (ActionCatalog ac, String query) throws IllegalArgumentException, RuntimeException {
//		System.out.println("DataLayer.query(ActionCatalog, String)");
		Vector ret = new Vector();

		if (ac == null) {
			throw new RuntimeException("query(ActionCatalog, String) ActionCatalog is null");
		}

		if (stores == null) {
			throw new RuntimeException("query(ActionCatalog, String) stores is null");
		}

		validateQuery(query);

		if (stores.size() <= 0) {
			return queryWithoutStores(ac, query);
		}

		for (String store : stores) {
			ret.addAll(queryPlugin(ac, store, query));
		}

		return ret;
	}

	private Collection queryPlugin (ActionCatalog ac, String plugin, String query) {
//		System.out.println("DataLayer.queryPlugin(ActionCatalog, String, String)");
		Vector<? extends ValueObject> ret = null;

		if (ac.isPluginActive(plugin)) {
			/*this is a bit of a special case because we already know what the method is called
			and the return type does not need to be specified (because we know the return type
			is going to be some form of java.util.Collection)*/
			String action[] = ac.findAction(plugin + " " + "query");
//			System.out.println("	action: ");
//			System.out.println("		" + action[0]);
//			System.out.println("		" + action[1]);
//			System.out.println("		" + action[2]);
			ret = (Vector)ac.performAction(action[0], action[1], action[2], new Object[]{query});

			try{
				if (ret != null && ret.size() > 0) {
					for (ValueObject obj: ret) {
						(obj).setLabel(action[0] + "|" + (obj).getLabel());
					}
				}
			} catch(Exception exc){
				Logger.log(exc.getMessage());
				exc.printStackTrace();
			}
		}

		return ret;
	}

	/**This method is an internal method used to load configurations and actions during initialization.*/
	private Collection queryWithoutStores (ActionCatalog ac, String query) throws IllegalArgumentException {
//		System.out.println("DataLayer.queryWithoutStores(ActionCatalog, String)");
		if (ac == null) {
			throw new IllegalArgumentException("query(ActionCatalog, String) was called with a null ActionCatalog");
		}

		Vector ret = new Vector();

		for (String plugin : ac.keySet()) {
			if ((ac.isPluginStorage(plugin)) && (ac.isPluginActive(plugin))) {
//				System.out.println("    plugin: " + plugin);
//				System.out.println("    query: " + query);
				ret.addAll(queryPlugin(ac, plugin, query));
			}
		}

		return ret;
	}

	public void validateQuery (String query) throws IllegalArgumentException {
		if (query == null) {
			throw new IllegalArgumentException("validateQuery(String) String is null");
		}

		if (query.trim().length() <= 0) {
			throw new IllegalArgumentException("validateQuery(String) String is empty");
		}
	}

	public void persist (Object obj) throws IllegalArgumentException {
		if (obj == null) {
			throw new IllegalArgumentException("persist(Object) was called with a null object");
		}

		/*This is where the magic with ID's needs to occur. In database terms, the ValueObject.id is
		the primary key. This is done intentionally to adhere to good database design, and it makes
		things much easier with certain persistence libraries / frameworks. This is particularly true
		when working with joins.

		The magic ends up being a potential need for IDs to be unique across many data stores. In
		reality the label mechanism should prevent this need explicitly, but I think synchronization
		of IDs should still be done for the following reasons:
			I think it's a good practice
			it would make moving data between stores easy
			it would prevent the need for managing IDs in each plugin, in particular keeping sequences and
				such out of the databases
			it would make IDs consistent regardless of which plugin generated the object the ID belongs to

		One might then think the label mechanism is unnecessary, but it would still be needed for items
		that have been defined in xml without an ID and have not been re-persisted (ie, plugin configurations).

		The idea then is simple, just have a hash table storing the object type and the id associated with it.
		Any time an id is used, the value in the hash table is incremented. The fun part is initialization.
		*/

		//deconstruct the label to determine where to persist an object
		//	this is where the default data store comes into play - new objects go here

		ValueObject vo = (ValueObject)obj;
		if(vo.getLabel() == null || vo.getLabel().isEmpty()){
			persist(defaultStore, obj);
		} else {
			// Assume store is first part of label
			String store = vo.getLabel().split("\\|")[0];

			if(!stores.contains(store)){
				throw new RuntimeException("persist(Object) was called on an object whose store does not exist");
			}

			// Clear off plugin part of label, will rebuild on way back out of storage location
			vo.setLabel(vo.getLabel().replace(store + "|", ""));
			persist(store, obj);
		}
	}

	public void persist (String store, Object obj) throws IllegalArgumentException {
		if (store == null) {
			throw new IllegalArgumentException("persist(String, Object) was called with a null string");
		}

		if (store.length() <= 0) {
			throw new IllegalArgumentException("persist(String, Object) was called with an empty string");
		}

		if (obj == null) {
			throw new IllegalArgumentException("persist(String, Object) was called with a null object");
		}

		ActionCatalog ac = ActionCatalog.getInstance();
		String action[] = ac.findAction(store + " persist");
		ac.performAction(action[0], action[1], action[2], new Object[]{obj});
	}

	public void addStore (String store) throws IllegalArgumentException {
		//theoretically this should be safe
		addStore(ActionCatalog.getInstance(), store);
	}

	public void addStore (ActionCatalog ac, String store) throws IllegalArgumentException {
		if (store == null) {
			throw new IllegalArgumentException("addStore(String) was called with a null string");
		}

		if (store.length() <= 0) {
			throw new IllegalArgumentException("addStore(String) was called with an empty string");
		}

		if (! ac.containsPlugin(store)) {
			throw new IllegalArgumentException("addStore(String) was called for a plugin that does not exist in the catalog");
		}

		if (stores.contains(store)) {
			throw new IllegalArgumentException("addStore(String) stores already contains an object with the value passed");
		}

		if (! ac.isPluginStorage(store)) {
			throw new IllegalArgumentException("addStore(String) the specified plugin is not a store");
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

	public String getDefaultStore () {
		return defaultStore;
	}

	public void setDefaultStore (String in) throws IllegalArgumentException {
		if (in == null) {
			throw new IllegalArgumentException("setDefaultStore(String) was called with a null string");
		}

		if (in.length() <= 0) {
			throw new IllegalArgumentException("setDefaultStore(String) was called with an empty string");
		}

		defaultStore = in;
		stores.add(in);
	}
}