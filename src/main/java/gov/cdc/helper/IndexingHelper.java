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

	public static IndexingHelper getInstance(String authorizationHeader) throws IOException {
		if (authorizationHeader != null && (authorizationHeader.startsWith("Bearer") || authorizationHeader.startsWith("bearer")))
			return (IndexingHelper) createNew().setAuthorizationHeader(authorizationHeader);
		else
			return getInstance();
	}

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

		return helper;
	}

	public JSONObject createIndex() {
		return createIndex(TYPE);
	}

	public JSONObject createIndex(String type) {
		String url = INDEXING_SERVER_URL + CREATE_INDEX_PATH;
		url = url.replace("{type}", type);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePut(url, null);
		return new JSONObject(response.getBody());
	}

	public JSONObject deleteIndex() {
		return deleteIndex(TYPE);
	}

	public JSONObject deleteIndex(String type) {
		String url = INDEXING_SERVER_URL + DELETE_INDEX_PATH;
		url = url.replace("{type}", type);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeDelete(url);
		return new JSONObject(response.getBody());
	}

	public JSONObject indexObject(String objectId) {
		return indexObject(TYPE, objectId);
	}

	public JSONObject indexObject(String type, String objectId) {
		String url = INDEXING_SERVER_URL + INDEX_OBJECT_PATH;
		url = url.replace("{type}", type);
		url = url.replace("{id}", objectId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url);
		return new JSONObject(response.getBody());
	}

	public JSONObject indexBulkObjects(String type, JSONArray objectIds) {
		String url = INDEXING_SERVER_URL + INDEX_BULK_OBJECTS_PATH;
		url = url.replace("{type}", type);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, objectIds.toString(), MediaType.APPLICATION_JSON);
		return new JSONObject(response.getBody());
	}

	public JSONObject getIndex(String objectId) {
		return getIndex(TYPE, objectId);
	}

	public JSONObject getIndex(String type, String objectId) {
		String url = INDEXING_SERVER_URL + GET_INDEX_PATH;
		url = url.replace("{type}", type);
		url = url.replace("{id}", objectId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeGet(url);
		return new JSONObject(response.getBody());
	}

	public JSONObject search(String type, String query, boolean hydrate, int from, int size) {
		return search(type, query, hydrate, from, size, null);
	}

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

	public JSONObject scroll(String type, String scroll, String scrollId, boolean hydrate) {
		String url = INDEXING_SERVER_URL + SCROLL_PATH;
		url = url.replace("{type}", type);
		url = url.replace("{scroll}", scroll);
		url = url.replace("{scrollId}", scrollId);
		url = url.replace("{hydrate}", Boolean.toString(hydrate));

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url);
		return new JSONObject(response.getBody());
	}

	public JSONObject deleteScrollIndex(String scrollId) {
		String url = INDEXING_SERVER_URL + DELETE_SCROLL_INDEX_PATH;
		url = url.replace("{scrollId}", scrollId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeDelete(url);
		return new JSONObject(response.getBody());
	}

}
