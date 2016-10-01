package net.scientifichooliganism.javaplug;

import net.scientifichooliganism.javaplug.query.Query;
import net.scientifichooliganism.javaplug.query.QueryNode;
import net.scientifichooliganism.javaplug.util.LumberJack;
import net.scientifichooliganism.javaplug.util.SpringBoard;
import net.scientifichooliganism.javaplug.vo.*;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class DataLayer {
	private static DataLayer instance;
	private Vector<String> stores;
	private Map<String, Vector<String>> storeMap;
	private boolean configuringId = false;
	private Configuration lastId = null;
	private Configuration shutdownStatus = null;
	private int sequenceCount = 0;
	private int sequenceReset = -1;
	private String defaultStore;
    private LumberJack logger;

	/**
	* The default constructor.
	*/
	private DataLayer() {
		stores = new Vector<>();
		defaultStore = null;
		storeMap = new TreeMap<>();
		logger = LumberJack.getInstanceForContext(this.getClass().getName());
	}

	public static DataLayer getInstance () {
		if (instance == null) {
			instance = new DataLayer();
		}

		return instance;
	}

	private String getDefaultStore(){
//		System.out.println("DataLayer.getDefaultStore()");
		if(defaultStore == null) {
//			System.out.println("    querying for default store");
			Vector<Configuration> configs = (Vector<Configuration>) query("Configuration");
//			System.out.println("    found " + configs.size() + " configs");
			for (Configuration config : configs) {
				if (config.getKey().equals("default_store")) {
					defaultStore = config.getValue();
//					System.out.println("    found defaultStore " + defaultStore);
				}
			}
		}

		return defaultStore;
	}

	/**a bunch of tests, I mean, a main method*/
	public static void main (String [] args) {
		try {
			PluginLoader.bootstrap();
			DataLayer dl = DataLayer.getInstance();

			dl.query("WHERE Object.Test == \"test\"");

			Action action = new Action();
			action.setName("My Action Name");
			action.setMethod("New method");
			action.setURL("google.com");

			action.setModule("Module");
			action.setDescription("description");
			action.setKlass("action");

			dl.persist(action);

			Collection actions = dl.query("Action");

			Action changeAction = (Action)actions.iterator().next();
			changeAction.setName("NEW NAME!!");
			dl.persist(changeAction);
		}
		catch (Exception exc) {
            exc.printStackTrace();
		}
	}

	private void configureLastID(){
		if (!configuringId && (lastId == null || sequenceReset == -1 || shutdownStatus == null)) {
			configuringId = true;
			Vector<Configuration> configs = (Vector<Configuration>) query("Configuration");
			boolean dirtyStartup = false;
			for (Configuration config : configs) {
				if (config.getKey().equals("lastID")) {
					lastId = config;
					if(lastId.getID() == null) {
						lastId.setID("0");
					}
				} else if (config.getKey().equals("seq_length")) {
					sequenceReset = Integer.parseInt(config.getValue());
				} else if (config.getKey().equals("shutdown_state")) {
					shutdownStatus = config;
					if(shutdownStatus.getID() == null) {
						shutdownStatus.setID("1");
					}
					if (config.getValue().equals("dirty")) {
						dirtyStartup = true;
					} else {
						dirtyStartup = false;
					}
				}
			}
//			// Set defaults if nothing was initialized from xml
//			if (lastId == null) {
//				lastId = new BaseConfiguration();
//				lastId.setKey("lastID");
//				lastId.setValue("0");
//			}
//
//			if (sequenceReset == -1) {
//				sequenceReset = 5;
//			}
//
//			if (shutdownStatus == null) {
//				shutdownStatus = new BaseConfiguration();
//				shutdownStatus.setKey("shutdown_state");
//				shutdownStatus.setValue("dirty");
//			}
//			if (dirtyStartup) {
//				lastId.setValue(new BigInteger(lastId.getValue()).add(new BigInteger(String.valueOf(sequenceReset))).toString());
//			}

			configuringId = false;
		}
	}

	public String getUniqueID(){
		String newId = null;
		if (!configuringId && (lastId == null || sequenceReset == -1 || shutdownStatus == null)) {
			configureLastID();
		}
		if(!configuringId){
			newId = (new BigInteger(lastId.getValue())).add(BigInteger.ONE).toString();
			lastId.setValue(newId);

			// TODO: Assume dirty
			if (sequenceCount == 0) {
				sequenceCount = sequenceReset;
				shutdownStatus.setValue("clean");
				persist(lastId);
				persist(shutdownStatus);
                shutdownStatus = (Configuration)(query("Configuration WHERE Configuration.Key == \"shutdown_state\"").iterator().next());
			} else if (sequenceCount == sequenceReset) {
				shutdownStatus.setValue("dirty");
				sequenceCount--;
				persist(shutdownStatus);
				shutdownStatus = (Configuration)(query("Configuration WHERE Configuration.Key == \"shutdown_state\"").iterator().next());
			} else {
				sequenceCount--;
			}
}
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
        logger.info("DataLayer.query(String) with [" + query + "]");
		return query(ActionCatalog.getInstance(), query);
	}
	public Collection query(ActionCatalog ac, String queryStr) throws IllegalArgumentException, RuntimeException {
		return query(ac, new Query(queryStr));
	}
	public Collection query (ActionCatalog ac, Query query) throws IllegalArgumentException, RuntimeException {
        logger.info("DataLayer.query(ActionCatalog, String)");

		if (ac == null) {
			throw new RuntimeException("query(ActionCatalog, String) ActionCatalog is null");
		}

		if (stores == null) {
			throw new RuntimeException("query(ActionCatalog, String) stores is null");
		}

		final CopyOnWriteArrayList results = new CopyOnWriteArrayList();
		// Query translation for plugins
		final Query translatedQuery = new Query();

		try {
			translatedQuery.copy(translateQuery(query));
		} catch (Exception exc){
			logger.logException(exc, SpringBoard.ERROR);
		}

		if (stores.size() <= 0) {
			//TODO: It is probably not appropriate to use queryWithoutStores here
		    results.addAll(queryWithoutStores(ac, translatedQuery));
		} else {

			Vector<String> queryStores = new Vector<>();

			if (translatedQuery.getFromValues() != null && translatedQuery.getFromValues().length > 0) {
				for (int i = 0; i < translatedQuery.getFromValues().length; i++) {
					queryStores.add(translatedQuery.getFromValues()[i]);
				}
			} else {
				queryStores = stores;
			}

			ExecutorService executorService = Executors.newFixedThreadPool(queryStores.size());

			// Query and aggregate plugins
			for (String store : queryStores) {
				//TODO: Only query stores that provide the object type(s) being requested or MetaData
				try {
					Runnable r = new Runnable() {
						@Override
						public void run() {
							results.addAll(queryPlugin(ac, store, translatedQuery));
						}
					};

					executorService.execute(r);
				}
				catch (Exception exc) {
                    logger.logException(exc, SpringBoard.ERROR);
				}
			}

			executorService.shutdown();

            // TODO: add timeout to config, exit method in catch
			try{
				executorService.awaitTermination(5, TimeUnit.MINUTES);
			} catch (InterruptedException exc){
                logger.logException(exc, SpringBoard.ERROR);
                return null;
			}
		}


		Vector ret = new Vector();
		// Correlate results
		if(query.getWherePrefix() != null && query.getWherePrefix().length > 0) {
			ret.addAll(correlateData(results, query));
		} else {
			ret.addAll(results);
		}

		return ret;
	}

	private Collection queryPlugin (ActionCatalog ac, String plugin, Query query){
		logger.info("DataLayer.queryPlugin(ActionCatalog, String, Query) with [_, " + plugin + ", " + query.toString() + "]");

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
						(obj).setLabel(plugin + "|" + (obj).getLabel());
					}
				}
			} catch(Exception exc){
                logger.logException(exc, SpringBoard.ERROR);
			}
		}

		return ret;
	}

	private Set correlateData(Iterable data, Query query){
		Set ret = new HashSet();
		QueryNode tree = query.buildTree();

		// Sort data by class type
		Map<Class, Collection> sortedData = new HashMap<>();
		sortedData.put(Action.class, new ArrayList());
		sortedData.put(Application.class, new ArrayList());
		sortedData.put(Block.class, new ArrayList());
		sortedData.put(Configuration.class, new ArrayList());
		sortedData.put(Environment.class, new ArrayList());
		sortedData.put(Event.class, new ArrayList());
		sortedData.put(Release.class, new ArrayList());
		sortedData.put(Task.class, new ArrayList());
		sortedData.put(TaskCategory.class, new ArrayList());
		sortedData.put(Transaction.class, new ArrayList());

		for(Object o : data){
		    for(Class key : sortedData.keySet()){
		    	if(key.isInstance(o)) {
					sortedData.get(key).add(o);
				}
			}
		}

		// correlate data
		// This could probably be parallelized one day, using the Fork/Join construct here may be good.
		for(String type : query.getSelectValues()){
			Set<String> qualifiedTypes = resolveQualifiedClassName(type);
			for(Class dataType : sortedData.keySet()){
			    for(String queryType : qualifiedTypes) {
			    	try {
						if (Class.forName(queryType).isAssignableFrom(dataType)) {
							for (Object object : sortedData.get(dataType)) {
								if (checkDataAgainstQuery(object, tree, sortedData)) {
									ret.add(object);
								}
							}
						}
					} catch (ClassNotFoundException exc){
						// If this gets called something probably went
						// terribly wrong
                        logger.logException(exc, SpringBoard.ERROR);
					}
				}
			}
		}

		return ret;
	}

	private boolean checkDataAgainstQuery(Object member, QueryNode node, Map<Class, Collection> data){
		String leftProperty = null, rightProperty = null;
		Boolean leftResult = null, rightResult = null;
		Vector<Object> leftValues = new Vector<>(), rightValues = new Vector<>();
		QueryNode leftChild = node.getLeftChild(), rightChild = node.getRightChild();

		if(leftChild != null) {
			if (leftChild.isProperty()){
				leftProperty = leftChild.getValue();
			} else if (leftChild.isLiteral()){
				String valueString = leftChild.getValue();
				leftValues.add(valueString.substring(1, valueString.length() - 1));
			} else if (node.getLeftChild().isOperator()) {
				leftResult = checkDataAgainstQuery(member, leftChild, data);
			} else {
				throw new RuntimeException("checkDataAgainstQuery: where tree could not be evaluated, node type unknown");
			}
		}

		if(rightChild != null) {
			if (rightChild.isProperty()){
				rightProperty = rightChild.getValue();
			} else if (rightChild.isLiteral()){
				String valueString = rightChild.getValue();
				rightValues.add(valueString.substring(1, valueString.length() - 1));
			} else if (rightChild.isOperator()){
				rightResult = checkDataAgainstQuery(member, rightChild, data);
			} else {
				throw new RuntimeException("checkDataAgainstQuery: where tree could not be evaluated, node type unknown");
			}
		}

		if(leftProperty != null){
			Set<String> leftPropertyTypes = resolveQualifiedClassName(leftProperty);
            boolean memberIsType = false;
			try {
				for(String type : leftPropertyTypes){
					Class klass = Class.forName(type);
					if(klass.isInstance(member)){
						memberIsType = true;
					}
				}
			} catch (ClassNotFoundException exc){
			    // Something went terribly wrong!
                logger.logException(exc, SpringBoard.ERROR);
			}

			if(memberIsType){
				leftValues.add(evaluateProperty(member, leftProperty));
			} else {
				for(String type : leftPropertyTypes) {
					for(Class classType : data.keySet()){
						if(classType.getName().equals(type)) {
							for(Object item : data.get(classType)) {
								leftValues.add(evaluateProperty(item, leftProperty));
							}
						}
					}
				}
			}
		}

		if(rightProperty != null){
			Set<String> rightPropertyTypes = resolveQualifiedClassName(rightProperty);
			boolean memberIsType = false;
			try {
				for(String type : rightPropertyTypes){
					Class klass = Class.forName(type);
					if(klass.isInstance(member)){
						memberIsType = true;
					}
				}
			} catch (ClassNotFoundException exc){
			    // Something went terribly wrong!
                logger.logException(exc, SpringBoard.ERROR);
			}

			if(memberIsType){
				rightValues.add(evaluateProperty(member, rightProperty));
			} else {
				for(String type : rightPropertyTypes){
					for(Class classType : data.keySet()){
						if(classType.getName().equals(type)) {
							for(Object item : data.get(classType)) {
								rightValues.add(evaluateProperty(item, rightProperty));
							}
						}
					}
				}
			}
		}

		if(leftResult != null) {
			return node.getOperator().evaluate(leftResult, rightResult);
		}

		for(Object leftValue : leftValues){
			for(Object rightValue : rightValues){
				if(node.getOperator().evaluate(leftValue, rightValue)){
					return true;
				}
			}
		}

		return false;
	}

	private Object evaluateProperty(Object object, String propertyStr){
		Vector<String> properties = new Vector<>();
		if(propertyStr.contains(object.getClass().getName())){
			propertyStr = propertyStr.replace(object.getClass().getName(), "");
			for(String item : propertyStr.split("\\.")){
				if(!item.isEmpty()){
					properties.add(item);
				}
			}
		} else {
			String propertyArray[] = propertyStr.split("\\.");
			for(String item : propertyArray){
				if(!item.isEmpty()) {
					properties.add(item);
				}
			}
		}

		Object result = object;
		for(String property : properties) {
			Method methods[] = result.getClass().getMethods();
			Method getter = null;
			for(Method method : methods){
				if(method.getName().equals("get" + property)){
					getter = method;
				}
			}
			if(getter != null){
				try {
					result = getter.invoke(result);
				} catch (Exception exc){
					logger.logException(exc, SpringBoard.ERROR);
					throw new RuntimeException("Could not find " + propertyStr + " on object " + object.getClass().getName());
				}
			}
		}
		return result;
	}

	// Finds a fully qualified last name from an "unqualified name" which is in the form
	// Object.Property.Property (up to 2 properties, can be changed by variable TO_REMOVE)
	private Set<String> resolveQualifiedClassName(String unqualifiedName){
		Set<String> ret = new TreeSet<>();
		int TO_REMOVE = 2;
		String properties[] = new String[TO_REMOVE];

		for(String type : storeMap.keySet()) {
			if (type.toLowerCase().contains(unqualifiedName.toLowerCase())) {
				ret.add(type);
			}
		}

		for(int i = 0; i < TO_REMOVE; i++){
			if(unqualifiedName.contains(".")) {
				int propertyIndex = unqualifiedName.lastIndexOf(".");
				properties[i] = unqualifiedName.substring(propertyIndex + 1);
				unqualifiedName = unqualifiedName.substring(0, propertyIndex);
			}
			for (String type : storeMap.keySet()) {
				if (type.contains(unqualifiedName)) {
					try {
						Class klass = Class.forName(type);
						// Check all properties exist
						boolean found = false;
						for (int j = i; j >= 0; j--) {
							Method methods[] = klass.getMethods();
							for (Method method : methods) {
								if (method.getName().equals("get" + properties[j])) {
									klass = method.getReturnType();
									if (j == 0) {
										found = true;
									}
								}
							}
						}
						if (found) {
							ret.add(type);
						}
					} catch (ClassNotFoundException exc) {
						// Suppress, we expect this exception at times
					}
				}
			}
		}

		return ret;
	}

	//TODO: Modify this method to take a plugin or plugin name as a second argument
	//TODO: Determine whether or not to move this elsewhere
	// Method adjusts query object for specific plugin, to ensure proper correlation and
	// aggregation between data stores
	private Query translateQuery(Query query){
//		System.out.println("Query.translateQuery(Query)");
		if (query == null) {
			throw new RuntimeException("Query.translatedQuery(Query) query is null");
		}

		Query translatedQuery = new Query(query);

		if (translatedQuery == null) {
			throw new RuntimeException("Query.translatedQuery(Query) translatedQuery is null");
		}

		if (translatedQuery.getQueryString() != null && !translatedQuery.getQueryString().isEmpty()) {
			// If we only have one store to query, then no modification is needed
			if(query.getFromValues() != null && query.getFromValues().length != 1){
				Vector<String> types = new Vector<>();
				QueryNode tree = query.buildTree();
				boolean hasRelation = false;

				if(tree != null) {
					hasRelation = checkRelationGetTypes(tree, types);
				}

				if(hasRelation){
					int numStores = 0;
					for(int i = 0; i < types.size() && numStores < 2; i++){
						numStores += resolveQualifiedClassName(types.elementAt(i)).size();
					}

					//TODO: This does not seem appropriate. Look into this later. - JFT
					// We now know we are selecting relational data between
					// multiple data stores, so the entire aggregation and
					// correlation should happen in the DataLayer, remove query clause

					if(numStores > 1){
						translatedQuery.setWherePrefix(null);
					}
				}
			}
		}
		return translatedQuery;
	}

	private boolean checkRelationGetTypes(QueryNode node, List<String> types){
		boolean hasRelation = false, leftRelation = false, rightRelation = false;
		QueryNode leftChild = node.getLeftChild(), rightChild = node.getRightChild();

		if(node.isProperty()){
			types.add(node.getValue());
		}

		if(leftChild != null && rightChild != null) {
			hasRelation = node.getLeftChild().isProperty() && node.getRightChild().isProperty();
		}
		if(leftChild != null){
			leftRelation = checkRelationGetTypes(leftChild, types);
		}
		if(rightChild != null){
			rightRelation = checkRelationGetTypes(rightChild, types);
		}

		return hasRelation || leftRelation || rightRelation;
	}

	/**This method is an internal method used to load configurations and actions during initialization.*/
	private Collection queryWithoutStores (ActionCatalog ac, Query query) throws IllegalArgumentException {
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

	public void persist (Object obj) throws IllegalArgumentException {
//		System.out.println("DataLayer.persist(Object)");
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
			persist(getDefaultStore(), obj);
		} else {
			// Assume store is first part of label
			String store = vo.getLabel().split("\\|")[0];

			if(!stores.contains(store)){
//				System.out.println("Stores are: ");
//				for(String storeStr : stores){
//					System.out.println(storeStr);
//				}

				throw new IllegalArgumentException("persist(Object) was called on an object whose store (" + store + ") does not exist");
			}

			// Clear off plugin part of label, will rebuild on way back out of storage location
			vo.setLabel(vo.getLabel().replaceFirst(store, ""));
            if(vo.getLabel().indexOf('|') == 0){
            	vo.setLabel(vo.getLabel().substring(1));
			}
			System.out.println("    Starting persisting on store " + store);
			persist(store, obj);
			System.out.println("    Finished persisting on store " + store);
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

	public void remove (Object obj) {
		if (obj == null){
			throw new IllegalArgumentException("remove(Object) was called with a null object");
		}

		ValueObject vo = (ValueObject)obj;

		if (vo.getID() == null) {
			throw new IllegalArgumentException("remove(Object) called on an object with a null ID");
		}

		if(vo.getLabel() != null && !vo.getLabel().isEmpty()){
			String store = vo.getLabel().split("\\|")[0];

			if (!stores.contains(store)) {
				throw new IllegalArgumentException("remove(Object) called on an object whose store does not exist");
			}

			vo.setLabel(vo.getLabel().replaceFirst(store, ""));
            if(vo.getLabel().indexOf("|") == 0){
            	vo.setLabel(vo.getLabel().substring(1));
			}
            remove(store, vo);
		} else {
			for(String store : stores){
				remove(store, obj);
			}
		}
	}

	public void remove (String store, Object obj) {
		if (store == null) {
			throw new IllegalArgumentException("remove(String, Object) was called with a null string");
		}

		if (store.isEmpty()) {
			throw new IllegalArgumentException("remove(String, Object) was called with an empty string");
		}

		if(obj == null){
			throw new IllegalArgumentException("remove(String, Object) was called with a null object");
		}

		ActionCatalog ac = ActionCatalog.getInstance();
        String action[] = ac.findAction(store + " remove");
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
			throw new IllegalArgumentException("addStore(String) stores (" + store + ") already contains an object with the value passed");
		}

		if (! ac.isPluginStorage(store)) {
			throw new IllegalArgumentException("addStore(String) the specified plugin is not a store");
		}

		stores.add(store);

		// Determine what types are supported by the new store and add them to the store map
		Collection<Configuration> configurations = (Collection<Configuration>)query("Configuration FROM " + store);
		for(Configuration config : configurations){
		    if(config.getKey().equals("provides")) {
				if (!storeMap.containsKey(config.getValue())) {
					storeMap.put(config.getValue(), new Vector<>());
				} else {
					storeMap.get(config.getValue()).add(config.getModule());
				}
			}
		}
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
