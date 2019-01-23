package gov.cdc.helper;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class IndexingHelper extends AbstractHelper {

	private static IndexingHelper instance;

	private static String INDEXING_SERVER_URL;
	private static String CREATE_INDEX_PATH;
	private static String DELETE_INDEX_PATH;
	private static String INDEX_OBJECT_PATH;
	private static String INDEX_BULK_OBJECTS_PATH;
	private static String GET_INDEX_PATH;
	private static String SEARCH_PATH;
	private static String SCROLL_PATH;
	private static String DELETE_SCROLL_INDEX_PATH;
	private static String TYPE;
	private static String DELETE_CONFIG_PATH;
	private static String CREATE_OR_UPDATE_CONFIG_PATH;

	/**
	 * If authorizationHeader isn't null and if provided header starts with 'Bearer',
	 * constructs new instance of IndexingHelper class and sets the authorization header to the provided value.
	 * If authorizationHeader is null or doesn't start with 'Bearer', returns singleton instance of IndexingHelper.
	 *
	 * @param authorizationHeader
	 * @return
	 * @throws IOException
	 */
	public static IndexingHelper getInstance(String authorizationHeader) throws IOException {
		if (authorizationHeader != null && (authorizationHeader.startsWith("Bearer") || authorizationHeader.startsWith("bearer")))
			return (IndexingHelper) createNew().setAuthorizationHeader(authorizationHeader);
		else
			return getInstance();
	}

	/**
	 * IndexingHelper singleton constructor
	 *
	 * @return
	 * @throws IOException
	 */
	public static IndexingHelper getInstance() throws IOException {
		if (instance == null) {
			instance = createNew();
		}
		return instance;
	}

	private static IndexingHelper createNew() throws IOException {
		IndexingHelper helper = new IndexingHelper();

		INDEXING_SERVER_URL = ResourceHelper.getSysEnvProperty(ResourceHelper.CONST_ENV_VAR_INDEXING_URL, true);
		if (!INDEXING_SERVER_URL.endsWith("/"))
			INDEXING_SERVER_URL += "/";
		CREATE_INDEX_PATH = ResourceHelper.getProperty("indexing.createIndex");
		DELETE_INDEX_PATH = ResourceHelper.getProperty("indexing.deleteIndex");
		INDEX_OBJECT_PATH = ResourceHelper.getProperty("indexing.indexObject");
		INDEX_BULK_OBJECTS_PATH = ResourceHelper.getProperty("indexing.indexBulkObjects");
		GET_INDEX_PATH = ResourceHelper.getProperty("indexing.getIndex");
		SEARCH_PATH = ResourceHelper.getProperty("indexing.search");
		SCROLL_PATH = ResourceHelper.getProperty("indexing.scroll");
		DELETE_SCROLL_INDEX_PATH = ResourceHelper.getProperty("indexing.deleteScrollIndex");
		TYPE = ResourceHelper.getProperty("indexing.type");
		DELETE_CONFIG_PATH = ResourceHelper.getProperty("indexing.deleteConfig");
		CREATE_OR_UPDATE_CONFIG_PATH = ResourceHelper.getProperty("indexing.createOrUpdateConfig");

		return helper;
	}

	/**
	 * Call Indexing Service to create index using type defined in config-services.properties
	 *
	 * @see IndexingHelper#createIndex(String)
	 *
	 * @return response from indexing service
	 */
	public JSONObject createIndex() {
		return createIndex(TYPE);
	}

	/**
	 * Call Indexing Service to create index for specified config
	 *
	 * @param type config to create index for
	 * @return response from indexing service
	 */
	public JSONObject createIndex(String type) {
		String url = INDEXING_SERVER_URL + CREATE_INDEX_PATH;
		url = url.replace("{type}", type);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePut(url, null);
		return new JSONObject(response.getBody());
	}

	/**
	 * Call Indexing Service to delete index using type defined in config-services.properties
	 *
	 * @see IndexingHelper#deleteIndex(String)
	 *
	 * @return response from indexing service
	 */
	public JSONObject deleteIndex() {
		return deleteIndex(TYPE);
	}

	/**
	 * Call Indexing Service to delete index for specified config
	 *
	 * @param type config to delete index for
	 * @return response from indexing service
	 */
	public JSONObject deleteIndex(String type) {
		String url = INDEXING_SERVER_URL + DELETE_INDEX_PATH;
		url = url.replace("{type}", type);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeDelete(url);
		return new JSONObject(response.getBody());
	}

	/**
	 * Call Indexing Service to index existing stored object using type(config) defined in config-service.properties
	 *
	 * @see IndexingHelper#indexObject(String, String)
	 *
	 * @param objectId id of object to index
	 * @return response from indexing service
	 */
	public JSONObject indexObject(String objectId) {
		return indexObject(TYPE, objectId);
	}

	/**
	 * Call Indexing Service to index existing stored object
	 *
	 * @param type config name
	 * @param objectId id of object to index
	 * @return response from indexing service
	 */
	public JSONObject indexObject(String type, String objectId) {
		String url = INDEXING_SERVER_URL + INDEX_OBJECT_PATH;
		url = url.replace("{type}", type);
		url = url.replace("{id}", objectId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url);
		return new JSONObject(response.getBody());
	}


	/**
	 * Call Indexing Service to index a list of objects
	 *
	 * @param type config name
	 * @param objectIds ids of objects to index
	 * @return response from indexing service
	 */
	public JSONObject indexBulkObjects(String type, JSONArray objectIds) {
		String url = INDEXING_SERVER_URL + INDEX_BULK_OBJECTS_PATH;
		url = url.replace("{type}", type);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, objectIds.toString(), MediaType.APPLICATION_JSON);
		return new JSONObject(response.getBody());
	}

	/**
	 * Call Indexing Service to get an indexed object using type(config) defined in config-service.properties
	 *
	 * @see IndexingHelper#getIndex(String, String)
	 *
	 * @param objectId id of object
	 * @return response from indexing service
	 */
	public JSONObject getIndex(String objectId) {
		return getIndex(TYPE, objectId);
	}

	/**
	 * Call Indexing Service to get an indexed object
	 *
	 * @param type config name
	 * @param objectId id of object
	 * @return response from indexing service
	 */
	public JSONObject getIndex(String type, String objectId) {
		String url = INDEXING_SERVER_URL + GET_INDEX_PATH;
		url = url.replace("{type}", type);
		url = url.replace("{id}", objectId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeGet(url);
		return new JSONObject(response.getBody());
	}

	/**
	 * Call Indexing Service to search indexed object
	 *
	 * @param type config name
	 * @param query search query
	 * @param hydrate whether or not to replace elastic search source with mongo document
	 * @param from query start position
	 * @param size number of results
	 * @return response from indexing service
	 */
	public JSONObject search(String type, String query, boolean hydrate, int from, int size) {
		return search(type, query, hydrate, from, size, null);
	}

	/**
	 * Call Indexing Service to search indexed object
	 *
	 * @param type config name
	 * @param query search query
	 * @param hydrate whether or not to replace elastic search source with mongo document
	 * @param from query start position
	 * @param size number of results
	 * @param scroll scroll live time (ex: 1m)
	 * @return response from indexing service
	 */
	public JSONObject search(String type, String query, boolean hydrate, int from, int size, String scroll) {
		String url = INDEXING_SERVER_URL + SEARCH_PATH;
		url = url.replace("{type}", type);
		url = url.replace("{query}", query);
		url = url.replace("{hydrate}", Boolean.toString(hydrate));
		url = url.replace("{from}", Integer.toString(from));
		url = url.replace("{size}", Integer.toString(size));
		if (scroll != null && !scroll.isEmpty())
			url = url.replace("{scroll}", scroll);
		else
			url = url.replace("&scroll={scroll}", "");

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url);
		return new JSONObject(response.getBody());
	}

	/**
	 * Call Indexing Service to get scroll search result
	 *
	 * @param type config name
	 * @param scroll scroll live time (ex: 1m)
	 * @param scrollId scroll identifier
	 * @param hydrate whether or not to replace elastic search source with mongo document
	 * @return response from indexing service
	 */
	public JSONObject scroll(String type, String scroll, String scrollId, boolean hydrate) {
		String url = INDEXING_SERVER_URL + SCROLL_PATH;
		url = url.replace("{type}", type);
		url = url.replace("{scroll}", scroll);
		url = url.replace("{scrollId}", scrollId);
		url = url.replace("{hydrate}", Boolean.toString(hydrate));

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url);
		return new JSONObject(response.getBody());
	}

	/**
	 * Call Indexing Service to delete scroll index
	 *
	 * @param scrollId scroll identifier
	 * @return response from indexing service
	 */
	public JSONObject deleteScrollIndex(String scrollId) {
		String url = INDEXING_SERVER_URL + DELETE_SCROLL_INDEX_PATH;
		url = url.replace("{scrollId}", scrollId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeDelete(url);
		return new JSONObject(response.getBody());
	}

	/**
	 * Call Indexing Service to create or update configuration
	 *
	 * @param configName configuration name
	 * @param config new configuration
	 * @return response from indexing service
	 */
	public JSONObject createOrUpdateConfig(String configName, JSONObject config) {
		String url = INDEXING_SERVER_URL + CREATE_OR_UPDATE_CONFIG_PATH;
		url = url.replace("{configName}", configName);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, config.toString(), MediaType.APPLICATION_JSON);
		return new JSONObject(response.getBody());
	}

	/**
	 * Call Indexing Service to delete configuration
	 *
	 * @param configName configuration name
	 * @return response from indexing service
	 */
	public JSONObject deleteConfig(String configName) {
		String url = INDEXING_SERVER_URL + DELETE_CONFIG_PATH;
		url = url.replace("{configName}", configName);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeDelete(url);
		return new JSONObject(response.getBody());
	}
}
