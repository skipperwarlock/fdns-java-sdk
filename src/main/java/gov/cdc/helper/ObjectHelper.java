package gov.cdc.helper;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

public class ObjectHelper extends AbstractHelper {

	public final static String CONST_STATUS = "status";
	public final static String CONST_STATUS_PARSED = "parsed";
	public final static String CONST_STATUS_PROCESSED = "processed";
	public final static String CONST_STATUS_INDEXED = "indexed";

	private static ObjectHelper instance;
	
	private static final Logger logger = Logger.getLogger(ObjectHelper.class);

	private static String OBJECT_SERVER_URL;
	private static String CREATE_OBJECT_PATH;
	private static String BULK_IMPORT_PATH;
	private static String GET_OBJECT_PATH;
	private static String UPDATE_OBJECT_PATH;
	private static String COUNT_OBJECTS_PATH;
	private static String AGGREGATE_PATH;
	private static String DISTINCT_PATH;
	private static String FIND_PATH;
	private static String SEARCH_PATH;
	private static String DELETE_OBJECT_PATH;
	private static String DELETE_COLLECTION_PATH;
	private static String DB;
	private static String COLLECTION;

	/**
	 * If authorizationHeader isn't null and if provided header starts with 'Bearer',
	 * constructs new instance of ObjectHelper class and sets the authorization header to the provided value.
	 * If authorizationHeader is null or doesn't start with 'Bearer', returns singleton instance of ObjectHelper.
	 *
	 * @param authorizationHeader
	 * @return
	 * @throws IOException
	 */
	public static ObjectHelper getInstance(String authorizationHeader) throws IOException {
		if (authorizationHeader != null && (authorizationHeader.startsWith("Bearer") || authorizationHeader.startsWith("bearer")))
			return (ObjectHelper) createNew().setAuthorizationHeader(authorizationHeader);
		else
			return getInstance();
	}

	/**
	 * ObjectHelper singleton constructor
	 * @return
	 * @throws IOException
	 */
	public static ObjectHelper getInstance() throws IOException {
		if (instance == null) {
			instance = createNew();
		}
		return instance;
	}

	private static ObjectHelper createNew() throws IOException {
		ObjectHelper helper = new ObjectHelper();

		OBJECT_SERVER_URL = ResourceHelper.getSysEnvProperty(ResourceHelper.CONST_ENV_VAR_OBJECT_URL, true);
		if (!OBJECT_SERVER_URL.endsWith("/"))
			OBJECT_SERVER_URL += "/";
		CREATE_OBJECT_PATH = ResourceHelper.getProperty("object.createObject");
		BULK_IMPORT_PATH = ResourceHelper.getProperty("object.bulkImport");
		GET_OBJECT_PATH = ResourceHelper.getProperty("object.getObject");
		UPDATE_OBJECT_PATH = ResourceHelper.getProperty("object.updateObject");
		COUNT_OBJECTS_PATH = ResourceHelper.getProperty("object.countObjects");
		AGGREGATE_PATH = ResourceHelper.getProperty("object.aggregate");
		DISTINCT_PATH = ResourceHelper.getProperty("object.distinct");
		FIND_PATH = ResourceHelper.getProperty("object.find");
		SEARCH_PATH = ResourceHelper.getProperty("object.search");
		DELETE_OBJECT_PATH = ResourceHelper.getProperty("object.deleteObject");
		DELETE_COLLECTION_PATH = ResourceHelper.getProperty("object.deleteCollection");
		DB = ResourceHelper.getProperty("object.db");
		COLLECTION = ResourceHelper.getProperty("object.collection");

		return helper;
	}

	/**
	 * Call Object Service to get object from database using database and collection defined in config-services.properties
	 *
	 * @see ObjectHelper#getObject(String, String, String)
	 *
	 * @param objectId object ID
	 * @return response from Object Service containing object details
	 */
	public JSONObject getObject(String objectId) {
		return getObject(objectId, DB, COLLECTION);
	}

	/**
	 * Call Object Service to get object from database
	 *
	 * @param objectId object ID
	 * @param db database name
	 * @param collection collection name
	 * @return response from Object Service containing object details
	 */
	public JSONObject getObject(String objectId, String db, String collection) {
		String url = OBJECT_SERVER_URL + GET_OBJECT_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);
		url = url.replace("{id}", objectId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeGet(url);

		String body = response.getBody();

		return new JSONObject(body);
	}

	/**
	 * Returns true if object exists in database and collection defined in config-services.properties
	 *
	 * @see ObjectHelper#exists(String, String, String)
	 *
	 * @param objectId object ID
	 * @return true if object exists
	 */
	public boolean exists(String objectId) {
		return exists(objectId, DB, COLLECTION);
	}


	/**
	 * Returns true if object exists
	 *
	 * @param objectId object ID
	 * @param db database name
	 * @param collection collection name
	 * @return true if object exists
	 */
	public boolean exists(String objectId, String db, String collection) {
		String url = OBJECT_SERVER_URL + GET_OBJECT_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);
		url = url.replace("{id}", objectId);

		try {
			RequestHelper.getInstance(getAuthorizationHeader()).executeGet(url);
		} catch (HttpClientErrorException e) {
			logger.debug("URL: " + url);
			logger.error("Method: ObjectHelper.exists", e);
			return false;
		}
		return true;
	}

	/**
	 * Call Object Service to bulk import objects using csv formatted data. Database and Collection values
	 * defined in config-services.properties
	 *
	 * @see ObjectHelper#bulkImport(String, String, String)
	 *
	 * @param data csv formatted objects
	 * @return response from object service
	 */
	public JSONObject bulkImport(String data) {
		return bulkImport(data, DB, COLLECTION);
	}

	/**
	 * Call Object Service to bulk import objects using csv formatted data.
	 *
	 * @param data csv formatted objects
	 * @return response from object service
	 */
	public JSONObject bulkImport(String data, String db, String collection) {
		String url = OBJECT_SERVER_URL + BULK_IMPORT_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeMultipartPost(url, "csv", "data.csv", data);
		return new JSONObject(response.getBody());
	}

	/**
	 * Call Object Service to create an object. Database and Collection values defined in config-services.properties
	 *
	 * @see ObjectHelper#createObject(JSONObject, String)
	 * @see ObjectHelper#createObject(JSONObject, String, String)
	 * @see ObjectHelper#createObject(JSONObject, String, String, String)
	 *
	 * @param json object data
	 * @return response from object service with object details
	 */
	public JSONObject createObject(JSONObject json) {
		return createObject(json, DB, COLLECTION);
	}

	/**
	 * Call Object Service to create an object.
	 *
	 * @see ObjectHelper#createObject(JSONObject)
	 * @see ObjectHelper#createObject(JSONObject, String)
	 * @see ObjectHelper#createObject(JSONObject, String, String, String)
	 *
	 * @param json object data
	 * @param db database name
	 * @param collection collection name
	 * @return response from object service with object details
	 */
	public JSONObject createObject(JSONObject json, String db, String collection) {
		return createObject(json, "", db, collection);
	}

	/**
	 * Call Object Service to create an object.
	 *
	 * @see ObjectHelper#createObject(JSONObject)
	 * @see ObjectHelper#createObject(JSONObject, String, String)
	 * @see ObjectHelper#createObject(JSONObject, String, String, String)
	 *
	 * @param json object data
	 * @param id object id
	 * @return response from object service with object details
	 */
	public JSONObject createObject(JSONObject json, String id) {
		return createObject(json, id, DB, COLLECTION);
	}

	/**
	 * Call Object Service to create an object
	 *
	 * @param json object data
	 * @param id object id
	 * @param db database name
	 * @param collection collection name
	 * @return response from object service with object details
	 */
	public JSONObject createObject(JSONObject json, String id, String db, String collection) {
		String url = OBJECT_SERVER_URL + CREATE_OBJECT_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);
		url = url.replace("{id}", id);

		String payloadAsString = json.toString();

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, payloadAsString, MediaType.APPLICATION_JSON);
		return new JSONObject(response.getBody());
	}

	/**
	 * Call Object Service to count number of objects in a collection.
	 * Database and Collection values defined in config-services.properties.
	 *
	 * @see ObjectHelper#createObject(JSONObject, String, String)
	 *
	 * @param json search query
	 * @return response from object service with number of objects matching query
	 */
	public JSONObject countObjects(JSONObject json) {
		return countObjects(json, DB, COLLECTION);
	}

	/**
	 * Call Object Service to count number of objects in a collection.
	 *
	 * @param json search query
	 * @param db database name
	 * @param collection collection name
	 * @return response from object service with number of objects matching query
	 */
	public JSONObject countObjects(JSONObject json, String db, String collection) {
		String url = OBJECT_SERVER_URL + COUNT_OBJECTS_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, json.toString(), MediaType.APPLICATION_JSON);
		return new JSONObject(response.getBody());
	}

	/**
	 * Call Object Service to return aggregate values in a collection
	 *
	 * @see ObjectHelper#aggregate(JSONArray, String, String)
	 *
	 * @param json search query
	 * @return object service response containing aggregate values
	 */
	public JSONObject aggregate(JSONArray json) {
		return aggregate(json, DB, COLLECTION);
	}

	/**
	 * Call Object Service to return aggregate values in a collection
	 *
	 * @param json search query
	 * @param db database name
	 * @param collection collection name
	 * @return object service response containing aggregate values
	 */
	public JSONObject aggregate(JSONArray json, String db, String collection) {
		String url = OBJECT_SERVER_URL + AGGREGATE_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, json.toString(), MediaType.APPLICATION_JSON);
		return new JSONObject(response.getBody());
	}

	/**
	 * Call Object Service to get distinct values for a specified field across a single collection.
	 * Database and Collection values defined in config-services.properties.
	 *
	 * @see ObjectHelper#distinct(JSONObject, String, String, String)
	 *
	 * @param json search query
	 * @param field field name
	 * @return response from object service with distinct values for specified field
	 */
	public JSONArray distinct(JSONObject json, String field) {
		return distinct(json, field, DB, COLLECTION);
	}

	/**
	 * Call Object Service to get distinct values for a specified field across a single collection.
	 *
	 * @see ObjectHelper#distinct(JSONObject, String, String, String)
	 *
	 * @param json search query
	 * @param field field name
	 * @param db database name
	 * @param collection collection name
	 * @return response from object service with distinct values for specified field
	 */
	public JSONArray distinct(JSONObject json, String field, String db, String collection) {
		String url = OBJECT_SERVER_URL + DISTINCT_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);
		url = url.replace("{field}", field);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, json.toString(), MediaType.APPLICATION_JSON);
		return new JSONArray(response.getBody());
	}

	/**
	 * Call Object Service to find objects
	 * Database and Collection values defined in config-services.properties.
	 *
	 * @see ObjectHelper#find(JSONObject, String, String)
	 * @see ObjectHelper#find(JSONObject, String, String, int, int)
	 *
	 * @param json search query
	 * @return response from object service with matching objects
	 */
	public JSONObject find(JSONObject json) {
		return find(json, DB, COLLECTION);
	}

	/**
	 * Call Object Service to find objects
	 *
	 * @see ObjectHelper#find(JSONObject)
	 * @see ObjectHelper#find(JSONObject, String, String, int, int)
	 *
	 * @param json search query
	 * @param db database name
	 * @param collection collection name
	 * @return response from object service with matching objects
	 */
	public JSONObject find(JSONObject json, String db, String collection) {
		return find(json, db, collection, -1, -1);
	}

	/**
	 * Call Object Service to find objects
	 *
	 * @see ObjectHelper#find(JSONObject)
	 * @see ObjectHelper#find(JSONObject, String, String)
	 *
	 * @param json search query
	 * @param db database name
	 * @param collection collection name
	 * @param from starting point for result set
	 * @param size limit number of objects to return
	 * @return response from object service with matching objects
	 */
	public JSONObject find(JSONObject json, String db, String collection, int from, int size) {
		String url = OBJECT_SERVER_URL + FIND_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);

		if (from != -1)
			url = url.replace("{from}", Integer.toString(from));
		else
			url = url.replace("from={from}", "");

		if (size != -1)
			url = url.replace("{size}", Integer.toString(size));
		else
			url = url.replace("size={size}", "");

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, json.toString(), MediaType.APPLICATION_JSON);
		return new JSONObject(response.getBody());
	}

	/**
	 * Call Object Service to search objects in a specific collection using url parameters instead of json payload
	 * Database and Collection values defined in config-services.properties.
	 *
	 * @see ObjectHelper#search(String, String, String)
	 * @see ObjectHelper#search(String, String, String, int, int)
	 *
	 * @param qs search query
	 * @return response from object service with matching objects
	 */
	public JSONObject search(String qs) {
		return search(qs, DB, COLLECTION);
	}

	/**
	 * Call Object Service to search objects in a specific collection using url parameters instead of json payload
	 *
	 * @see ObjectHelper#search(String)
	 * @see ObjectHelper#search(String, String, String, int, int)
	 *
	 * @param qs search query
	 * @param db database name
	 * @param collection collection name
	 * @return response from object service with matching objects
	 */
	public JSONObject search(String qs, String db, String collection) {
		return search(qs, db, collection, -1, -1);
	}

	/**
	 * Call Object Service to search objects in a specific collection using url parameters instead of json payload
	 *
	 * @see ObjectHelper#search(String)
	 * @see ObjectHelper#search(String, String, String)
	 *
	 * @param qs search query
	 * @param db database name
	 * @param collection collection name
	 * @param from starting poi9nt of result set
	 * @param size limit number of objects to return
	 * @return response from object service with matching objects
	 */
	public JSONObject search(String qs, String db, String collection, int from, int size) {
		String url = OBJECT_SERVER_URL + SEARCH_PATH;
		url = url.replace("{qs}", qs);
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);

		if (from != -1)
			url = url.replace("{from}", Integer.toString(from));
		else
			url = url.replace("from={from}", "");

		if (size != -1)
			url = url.replace("{size}", Integer.toString(size));
		else
			url = url.replace("size={size}", "");

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeGet(url);
		return new JSONObject(response.getBody());
	}

	/**
	 * Call Object Service to delete object
	 * Database and Collection values defined in config-services.properties.
	 *
	 * @see ObjectHelper#deleteObject(String, String, String)
	 *
	 * @param objectId object id
	 * @return response from object service
	 */
	public JSONObject deleteObject(String objectId) {
		return deleteObject(objectId, DB, COLLECTION);
	}

	/**
	 * Call Object Service to delete object
	 *
	 * @param objectId object id
	 * @param db database name
	 * @param collection collection name
	 * @return response from object service
	 */
	public JSONObject deleteObject(String objectId, String db, String collection) {
		String url = OBJECT_SERVER_URL + DELETE_OBJECT_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);
		url = url.replace("{id}", objectId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeDelete(url);
		return new JSONObject(response.getBody());
	}

	/**
	 * Call object Service to update object
	 * Database and Collection values defined in config-services.properties.
	 *
	 * @see ObjectHelper#updateObject(String, JSONObject, String, String)
	 *
	 * @param objectId object id
	 * @param command new object data
	 * @return response from object service
	 */
	public JSONObject updateObject(String objectId, JSONObject command) {
		return updateObject(objectId, command, DB, COLLECTION);
	}

	/**
	 * Call Object Service to update object
	 *
	 * @param objectId object id
	 * @param json new object data
	 * @param db database name
	 * @param collection collection name
	 * @return response from object service
	 */
	public JSONObject updateObject(String objectId, JSONObject json, String db, String collection) {
		String url = OBJECT_SERVER_URL + UPDATE_OBJECT_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);
		url = url.replace("{id}", objectId);

		String payloadAsString = json.toString();

		// But reapply the _id if it has been changed
		if (json.has("_id")) {
			JSONObject fixedJson = new JSONObject(payloadAsString);
			fixedJson.put("_id", json.get("_id"));
			payloadAsString = fixedJson.toString();
		}

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePut(url, payloadAsString, MediaType.APPLICATION_JSON);
		return new JSONObject(response.getBody());
	}

	/**
	 * Call Object Service to delete collection
	 * Database and Collection values defined in config-services.properties.
	 *
	 * @see ObjectHelper#deleteCollection(String, String)
	 * @return response from object service with success boolean
	 */
	public JSONObject deleteCollection() {
		return deleteCollection(DB, COLLECTION);
	}

	/**
	 * Call Object Service to delete collection
	 *
	 * @param db database name
	 * @param collection collection name
	 * @return response from object service with success boolean
	 */
	public JSONObject deleteCollection(String db, String collection) {
		String url = OBJECT_SERVER_URL + DELETE_COLLECTION_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeDelete(url);
		return new JSONObject(response.getBody());
	}

	/**
	 * Merge JSONObjects
	 *
	 * @param o1 base object
	 * @param o2 object to merge into o1
	 * @return object with values from o2 merged into o1
	 */
	public JSONObject merge(JSONObject o1, JSONObject o2) {
		JSONObject o = new JSONObject(o1.toString());

		for (Object key : o2.keySet()) {
			if (!o1.has(key.toString())) {
				o.put((String) key, o2.get(key.toString()));
			}
		}

		return o;
	}

}
