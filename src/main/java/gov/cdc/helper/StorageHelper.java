package gov.cdc.helper;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import gov.cdc.helper.common.StreamProcessor;

public class StorageHelper extends AbstractHelper {

	private static StorageHelper instance;

	private static String STORAGE_SERVER_URL;
	private static String GET_DRAWERS_PATH;
	private static String CREATE_DRAWER_PATH;
	private static String DELETE_DRAWER_PATH;
	private static String GET_DRAWER_PATH;
	private static String LIST_NODES_PATH;
	private static String CREATE_NODE_PATH;
	private static String UPDATE_NODE_PATH;
	private static String GET_NODE_PATH;
	private static String DELETE_NODE_PATH;
	private static String DOWNLOAD_NODE_PATH;
	private static String COPY_NODE_PATH;
	private static String DRAWER_NAME;

	/**
	 * If authorizationHeader isn't null and if provided header starts with 'Bearer',
	 * constructs new instance of StorageHelper class and sets the authorization header to the provided value.
	 * If authorizationHeader is null or doesn't start with 'Bearer', returns singleton instance of StorageHelper.
	 *
	 * @param authorizationHeader
	 * @return
	 * @throws IOException
	 */
	public static StorageHelper getInstance(String authorizationHeader) throws IOException {
		if (authorizationHeader != null && (authorizationHeader.startsWith("Bearer") || authorizationHeader.startsWith("bearer")))
			return (StorageHelper) createNew().setAuthorizationHeader(authorizationHeader);
		else
			return getInstance();
	}

	/**
	 * StorageHelper singleton constructor
	 * @return
	 * @throws IOException
	 */
	public static StorageHelper getInstance() throws IOException {
		if (instance == null) {
			instance = createNew();
		}
		return instance;
	}

	private static StorageHelper createNew() throws IOException {
		StorageHelper helper = new StorageHelper();

		STORAGE_SERVER_URL = ResourceHelper.getSysEnvProperty(ResourceHelper.CONST_ENV_VAR_STORAGE_URL, true);
		if (!STORAGE_SERVER_URL.endsWith("/"))
			STORAGE_SERVER_URL += "/";
		GET_DRAWERS_PATH = ResourceHelper.getProperty("storage.getDrawers");
		CREATE_DRAWER_PATH = ResourceHelper.getProperty("storage.createDrawer");
		DELETE_DRAWER_PATH = ResourceHelper.getProperty("storage.deleteDrawer");
		GET_DRAWER_PATH = ResourceHelper.getProperty("storage.getDrawer");
		LIST_NODES_PATH = ResourceHelper.getProperty("storage.listNodes");
		CREATE_NODE_PATH = ResourceHelper.getProperty("storage.createNode");
		UPDATE_NODE_PATH = ResourceHelper.getProperty("storage.updateNode");
		GET_NODE_PATH = ResourceHelper.getProperty("storage.getNode");
		DELETE_NODE_PATH = ResourceHelper.getProperty("storage.deleteNode");
		DOWNLOAD_NODE_PATH = ResourceHelper.getProperty("storage.downloadNode");
		COPY_NODE_PATH = ResourceHelper.getProperty("storage.copyNode");
		DRAWER_NAME = ResourceHelper.getProperty("storage.drawer");

		return helper;
	}

	/**
	 * Returns true if drawer defined in config-services.properties exists
	 *
	 * @return true if drawer defined in config-services.properties exists
	 */
	public boolean exists() {
		return exists(DRAWER_NAME);
	}

	/**
	 * Returns true if drawer exists
	 *
	 * @param name drawer name
	 * @return true if drawer exists
	 */
	public boolean exists(String name) {
		boolean exists = false;
		JSONArray arr = getDrawers();
		for (int i = 0; i < arr.length(); i++)
			exists = exists || arr.getJSONObject(i).getString("name").equals(name);

		return exists;
	}

	/**
	 * Get all drawers from Storage Service
	 *
	 * @return all drawers from storage service
	 */
	public JSONArray getDrawers() {
		String url = STORAGE_SERVER_URL + GET_DRAWERS_PATH;
		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeGet(url);
		return new JSONArray(response.getBody());
	}

	/**
	 * Create drawer defined in config-services.properties
	 *
	 * @return http response from storage service
	 */
	public JSONObject createDrawer() {
		return createDrawer(DRAWER_NAME);
	}

	/**
	 * Create drawer
	 *
	 * @param name drawer name
	 * @return http response from storage service
	 */
	public JSONObject createDrawer(String name) {
		String url = STORAGE_SERVER_URL + CREATE_DRAWER_PATH;
		url = url.replace("{name}", name);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePut(url, null);
		return new JSONObject(response.getBody());
	}

	/**
	 * Delete drawer defined in config-services.properties
	 *
	 * @return http response from storage service
	 */
	public JSONObject deleteDrawer() {
		return deleteDrawer(DRAWER_NAME);
	}

	/**
	 * Delete drawer
	 *
	 * @param name drawer name
	 * @return http response from storage service
	 */
	public JSONObject deleteDrawer(String name) {
		String url = STORAGE_SERVER_URL + DELETE_DRAWER_PATH;
		url = url.replace("{name}", name);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeDelete(url);
		return new JSONObject(response.getBody());
	}

	/**
	 * Get drawer defined in config-services.properties
	 *
	 * @return drawer data from storage service
	 */
	public JSONObject getDrawer() {
		return getDrawer(DRAWER_NAME);
	}

	/**
	 * Get drawer from storage service
	 *
	 * @param name drawer name
	 * @return drawer data from storage service
	 */
	public JSONObject getDrawer(String name) {
		String url = STORAGE_SERVER_URL + GET_DRAWER_PATH;
		url = url.replace("{name}", name);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeGet(url);
		return new JSONObject(response.getBody());
	}

	/**
	 * List all nodes in drawer defined in config-services.properties
	 *
	 * @return http response from storage service with all nodes in drawer
	 */
	public JSONArray listNodes() {
		return listNodes(DRAWER_NAME, "");
	}

	/**
	 * List all nodes in drawer defined in config-services.properties
	 *
	 * @param prefix limit response to keys that begin with this prefix
	 * @return http response from storage service with all nodes beginning with prefix
	 */
	public JSONArray listNodes(String prefix) {
		return listNodes(DRAWER_NAME, prefix);
	}

	/**
	 * List all nodes in drawer
	 *
	 * @param name drawer name
	 * @param prefix limit response to keys that begin with this prefix
	 * @return http response from storage service with all nodes beginning with prefix
	 */
	public JSONArray listNodes(String name, String prefix) {
		String url = STORAGE_SERVER_URL + LIST_NODES_PATH;
		url = url.replace("{name}", name);
		if (prefix != null)
			url = url.replace("{prefix}", prefix);
		else
			url = url.replace("{prefix}", "");

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeGet(url);
		return new JSONArray(response.getBody());
	}

	/**
	 * Create node defined in config-service.properties
	 *
	 * @param filename node id
	 * @param data payload
	 * @param generateStruct generate date/time structure if asked
	 * @param generateId generate node id if asked
	 * @return http response from storage service with details of created node
	 */
	public JSONObject createNode(String filename, byte[] data, boolean generateStruct, boolean generateId) {
		return createNode(DRAWER_NAME, filename, data, generateStruct, generateId, false);
	}

	/**
	 * Create node defined in config-service.properties
	 *
	 * @param filename node id
	 * @param data payload
	 * @param generateStruct generate date/time structure if asked
	 * @param generateId generate node id if asked
	 * @return http response from storage service with details of created node
	 */
	public JSONObject createNode(String filename, String data, boolean generateStruct, boolean generateId) {
		return createNode(DRAWER_NAME, filename, data, generateStruct, generateId);
	}

	/**
	 * Create node defined in config-service.properties
	 *
	 * @param filename node id
	 * @param data payload
	 * @param generateStruct generate date/time structure if asked
	 * @param generateId generate node id if asked
	 * @param replace replace if existing
	 * @return http response from storage service with details of created node
	 */
	public JSONObject createNode(String filename, String data, boolean generateStruct, boolean generateId, boolean replace) {
		return createNode(DRAWER_NAME, filename, data, generateStruct, generateId, replace);
	}

	/**
	 * Create node
	 *
	 * @param drawerName  drawer name
	 * @param filename node id
	 * @param data payload
	 * @param generateStruct date/time structure if asked
	 * @param generateId generate node id if asked
	 * @return http response from storage service with details of created node
	 */
	public JSONObject createNode(String drawerName, String filename, String data, boolean generateStruct, boolean generateId) {
		return createNode(drawerName, filename, data.getBytes(), generateStruct, generateId, false);
	}

	/**
	 * Create node
	 *
	 * @param drawerName  drawer name
	 * @param filename node id
	 * @param data payload
	 * @param generateStruct date/time structure if asked
	 * @param generateId generate node id if asked
	 * @param replace replace if existing
	 * @return http response from storage service with details of created node
	 */
	public JSONObject createNode(String drawerName, String filename, String data, boolean generateStruct, boolean generateId, boolean replace) {
		return createNode(drawerName, filename, data.getBytes(), generateStruct, generateId, replace);
	}

	/**
	 * Create node
	 *
	 * @param drawerName  drawer name
	 * @param filename node id
	 * @param data payload
	 * @param generateStruct date/time structure if asked
	 * @param generateId generate node id if asked
	 * @param replace replace if existing
	 * @return http response from storage service with details of created node
	 */
	public JSONObject createNode(String drawerName, String filename, byte[] data, boolean generateStruct, boolean generateId, boolean replace) {
		String url = STORAGE_SERVER_URL + CREATE_NODE_PATH;
		url = url.replace("{name}", drawerName);
		url = url.replace("{generateStruct}", Boolean.toString(generateStruct));
		url = url.replace("{generateId}", Boolean.toString(generateId));
		url = url.replace("{replace}", Boolean.toString(replace));
		if (generateId)
			url = url.replace("&id={id}", "");
		else
			url = url.replace("{id}", filename);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeMultipartPost(url, "file", filename, data);
		return new JSONObject(response.getBody());
	}

	/**
	 * Create node with generated date/time structure and generated id
	 *
	 * @param filename node id
	 * @param data payload
	 * @return http response from storage service with details of created node
	 */
	public JSONObject createNode(String filename, String data) {
		return createNode(filename, data, true, true);
	}

	/**
	 * Create node with generated date/time structure and generated id
	 *
	 * @param drawerName drawer name
	 * @param filename node id
	 * @param data payload
	 * @return http response from storage service with details of created node
	 */
	public JSONObject createNode(String drawerName, String filename, String data) {
		return createNode(drawerName, filename, data, true, true);
	}

	/**
	 * Update node
	 *
	 * @param drawerName drawer name
	 * @param nodeId node id
	 * @param data payload
	 * @return http response from storage service with success boolean
	 */
	public JSONObject updateNode(String drawerName, String nodeId, String data) {
		String url = STORAGE_SERVER_URL + UPDATE_NODE_PATH;
		url = url.replace("{name}", drawerName);
		url = url.replace("{id}", nodeId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeMultipartPut(url, "file", nodeId, data);
		return new JSONObject(response.getBody());
	}

	/**
	 * Delete node defined in config-services.properties
	 *
	 * @param nodeId node id
	 * @return http response from storage service with success boolean
	 */
	public JSONObject deleteNode(String nodeId) {
		return deleteNode(DRAWER_NAME, nodeId);
	}

	/**
	 * Delete node
	 *
	 * @param drawerName drawer name
	 * @param nodeId node id
	 * @return http response from storage service with success boolean
	 */
	public JSONObject deleteNode(String drawerName, String nodeId) {
		String url = STORAGE_SERVER_URL + DELETE_NODE_PATH;
		url = url.replace("{name}", drawerName);
		url = url.replace("{id}", nodeId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeDelete(url);
		return new JSONObject(response.getBody());
	}

	/**
	 * Get node defined in config-services.properties
	 *
	 * @param nodeId node id
	 * @return http response from storage service with node details
	 */
	public JSONObject getNode(String nodeId) {
		return getNode(DRAWER_NAME, nodeId);
	}

	/**
	 * Get node from storage service
	 *
	 * @param drawerName drawer name
	 * @param nodeId node id
	 * @return http response from storage service with node details
	 */
	public JSONObject getNode(String drawerName, String nodeId) {
		String url = STORAGE_SERVER_URL + GET_NODE_PATH;
		url = url.replace("{name}", drawerName);
		url = url.replace("{id}", nodeId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeGet(url);
		return new JSONObject(response.getBody());
	}

	/**
	 * Download node from storage service
	 *
	 * @param drawerName drawer name
	 * @param nodeId node id
	 * @return node from storage service
	 */
	public byte[] dowloadNode(String drawerName, String nodeId) {
		String url = STORAGE_SERVER_URL + DOWNLOAD_NODE_PATH;
		url = url.replace("{name}", drawerName);
		url = url.replace("{id}", nodeId);

		ResponseEntity<byte[]> response = RequestHelper.getInstance(getAuthorizationHeader()).downloadBinary(url, HttpMethod.GET);
		return response.getBody();
	}

	/**
	 * Download node from storage service defined in config-services.properties
	 *
	 * @param nodeId node id
	 * @return node from storage service
	 */
	public byte[] downloadNode(String nodeId) {
		return dowloadNode(DRAWER_NAME, nodeId);
	}

	/**
	 * Stream node defined in config-services.properties
	 *
	 * @param nodeId node id
	 * @param processor
	 */
	public void streamNode(String nodeId, StreamProcessor processor) {
		String url = STORAGE_SERVER_URL + DOWNLOAD_NODE_PATH;
		url = url.replace("{name}", DRAWER_NAME);
		url = url.replace("{id}", nodeId);

		RequestHelper.getInstance(getAuthorizationHeader()).stream(url, HttpMethod.GET, processor);
	}

	/**
	 * Copy node defined in config-services.properties
	 *
	 * @param nodeId node id
	 * @param targetDrawerName target drawer name
	 * @param generateStruct generate date/time structure if asked
	 * @param generateId generate node id if asked
	 * @param deleteOriginal delete original
	 * @return http response from storage service with success boolean
	 */
	public JSONObject copyNode(String nodeId, String targetDrawerName, boolean generateStruct, boolean generateId, boolean deleteOriginal) {
		return copyNode(DRAWER_NAME, nodeId, targetDrawerName, generateStruct, generateId, deleteOriginal);
	}

	/**
	 * Copy node
	 *
	 * @param drawerName drawer name
	 * @param nodeId node id
	 * @param targetDrawerName target drawer name
	 * @param generateStruct generate date/time structure if asked
	 * @param generateId generate node id if asked
	 * @param deleteOriginal delete original
	 * @return http response from storage service with success boolean
	 */
	public JSONObject copyNode(String drawerName, String nodeId, String targetDrawerName, boolean generateStruct, boolean generateId, boolean deleteOriginal) {
		String url = STORAGE_SERVER_URL + COPY_NODE_PATH;
		url = url.replace("{source}", drawerName);
		url = url.replace("{target}", targetDrawerName);
		url = url.replace("{generateStruct}", Boolean.toString(generateStruct));
		url = url.replace("{generateId}", Boolean.toString(generateId));
		url = url.replace("{deleteOriginal}", Boolean.toString(deleteOriginal));
		if (generateId)
			url = url.replace("&targetId={id}", "");
		else
			url = url.replace("{id}", nodeId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePut(url, null);
		return new JSONObject(response.getBody());
	}

}
