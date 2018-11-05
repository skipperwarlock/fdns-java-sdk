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

	public static ObjectHelper getInstance(String authorizationHeader) throws IOException {
		if (authorizationHeader != null && (authorizationHeader.startsWith("Bearer") || authorizationHeader.startsWith("bearer")))
			return (ObjectHelper) createNew().setAuthorizationHeader(authorizationHeader);
		else
			return getInstance();
	}

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

	public JSONObject getObject(String objectId) {
		return getObject(objectId, DB, COLLECTION);
	}

	public JSONObject getObject(String objectId, String db, String collection) {
		String url = OBJECT_SERVER_URL + GET_OBJECT_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);
		url = url.replace("{id}", objectId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeGet(url);

		// Some micro services are storing a json object with $ and . in keys,
		// but it's not supported
		String body = response.getBody();
		body = body.replaceAll("__DOLLAR__", "\\$");
		body = body.replaceAll("__DOT__", "\\.");

		return new JSONObject(body);
	}

	public boolean exists(String objectId) {
		return exists(objectId, DB, COLLECTION);
	}

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

	public JSONObject bulkImport(String data) {
		return bulkImport(data, DB, COLLECTION);
	}

	public JSONObject bulkImport(String data, String db, String collection) {
		String url = OBJECT_SERVER_URL + BULK_IMPORT_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeMultipartPost(url, "csv", "data.csv", data);
		return new JSONObject(response.getBody());
	}

	public JSONObject createObject(JSONObject json) {
		return createObject(json, DB, COLLECTION);
	}

	public JSONObject createObject(JSONObject json, String db, String collection) {
		return createObject(json, "", db, collection);
	}

	public JSONObject createObject(JSONObject json, String id) {
		return createObject(json, id, DB, COLLECTION);
	}

	public JSONObject createObject(JSONObject json, String id, String db, String collection) {
		String url = OBJECT_SERVER_URL + CREATE_OBJECT_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);
		url = url.replace("{id}", id);

		// Some micro services are storing a json object with $ and . in keys,
		// but it's not supported
		String payloadAsString = json.toString();
		payloadAsString = payloadAsString.replaceAll("\\$", "__DOLLAR__");
		payloadAsString = payloadAsString.replaceAll("\\.", "__DOT__");

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, payloadAsString, MediaType.APPLICATION_JSON);
		return new JSONObject(response.getBody());
	}

	public JSONObject countObjects(JSONObject json) {
		return countObjects(json, DB, COLLECTION);
	}

	public JSONObject countObjects(JSONObject json, String db, String collection) {
		String url = OBJECT_SERVER_URL + COUNT_OBJECTS_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, json.toString(), MediaType.APPLICATION_JSON);
		return new JSONObject(response.getBody());
	}

	public JSONObject aggregate(JSONArray json) {
		return aggregate(json, DB, COLLECTION);
	}

	public JSONObject aggregate(JSONArray json, String db, String collection) {
		String url = OBJECT_SERVER_URL + AGGREGATE_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, json.toString(), MediaType.APPLICATION_JSON);
		return new JSONObject(response.getBody());
	}

	public JSONArray distinct(JSONObject json, String field) {
		return distinct(json, field, DB, COLLECTION);
	}

	public JSONArray distinct(JSONObject json, String field, String db, String collection) {
		String url = OBJECT_SERVER_URL + DISTINCT_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);
		url = url.replace("{field}", field);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, json.toString(), MediaType.APPLICATION_JSON);
		return new JSONArray(response.getBody());
	}

	public JSONObject find(JSONObject json) {
		return find(json, DB, COLLECTION);
	}

	public JSONObject find(JSONObject json, String db, String collection) {
		return find(json, db, collection, -1, -1);
	}

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

	public JSONObject search(String qs) {
		return search(qs, DB, COLLECTION);
	}

	public JSONObject search(String qs, String db, String collection) {
		return search(qs, db, collection, -1, -1);
	}

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

	public JSONObject deleteObject(String objectId) {
		return deleteObject(objectId, DB, COLLECTION);
	}

	public JSONObject deleteObject(String objectId, String db, String collection) {
		String url = OBJECT_SERVER_URL + DELETE_OBJECT_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);
		url = url.replace("{id}", objectId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeDelete(url);
		return new JSONObject(response.getBody());
	}

	public JSONObject updateObject(String objectId, JSONObject command) {
		return updateObject(objectId, command, DB, COLLECTION);
	}

	public JSONObject updateObject(String objectId, JSONObject json, String db, String collection) {
		String url = OBJECT_SERVER_URL + UPDATE_OBJECT_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);
		url = url.replace("{id}", objectId);

		// Some micro services are storing a json object with $ and . in keys,
		// but it's not supported
		String payloadAsString = json.toString();
		payloadAsString = payloadAsString.replaceAll("\\$", "__DOLLAR__");
		payloadAsString = payloadAsString.replaceAll("\\.", "__DOT__");
		
		// But reapply the _id if it has been changed
		if (json.has("_id")) {
			JSONObject fixedJson = new JSONObject(payloadAsString);
			fixedJson.put("_id", json.get("_id"));
			payloadAsString = fixedJson.toString();
		}

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePut(url, payloadAsString, MediaType.APPLICATION_JSON);
		return new JSONObject(response.getBody());
	}

	public JSONObject deleteCollection() {
		return deleteCollection(DB, COLLECTION);
	}

	public JSONObject deleteCollection(String db, String collection) {
		String url = OBJECT_SERVER_URL + DELETE_COLLECTION_PATH;
		url = url.replace("{db}", db);
		url = url.replace("{collection}", collection);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeDelete(url);
		return new JSONObject(response.getBody());
	}

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
