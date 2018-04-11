package gov.cdc.helper;

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

	public static StorageHelper getInstance(String authorizationHeader) throws Exception {
		if (authorizationHeader != null && (authorizationHeader.startsWith("Bearer") || authorizationHeader.startsWith("bearer")))
			return (StorageHelper) createNew().setAuthorizationHeader(authorizationHeader);
		else
			return getInstance();
	}

	public static StorageHelper getInstance() throws Exception {
		if (instance == null) {
			instance = createNew();
		}
		return instance;
	}

	private static StorageHelper createNew() throws Exception {
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

	public boolean exists() {
		return exists(DRAWER_NAME);
	}

	public boolean exists(String name) {
		boolean exists = false;
		JSONArray arr = getDrawers();
		for (int i = 0; i < arr.length(); i++)
			exists = exists || arr.getJSONObject(i).getString("name").equals(name);

		return exists;
	}

	public JSONArray getDrawers() {
		String url = STORAGE_SERVER_URL + GET_DRAWERS_PATH;
		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeGet(url);
		return new JSONArray(response.getBody());
	}

	public JSONObject createDrawer() {
		return createDrawer(DRAWER_NAME);
	}

	public JSONObject createDrawer(String name) {
		String url = STORAGE_SERVER_URL + CREATE_DRAWER_PATH;
		url = url.replace("{name}", name);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePut(url, null);
		return new JSONObject(response.getBody());
	}

	public JSONObject deleteDrawer() {
		return deleteDrawer(DRAWER_NAME);
	}

	public JSONObject deleteDrawer(String name) {
		String url = STORAGE_SERVER_URL + DELETE_DRAWER_PATH;
		url = url.replace("{name}", name);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeDelete(url);
		return new JSONObject(response.getBody());
	}

	public JSONObject getDrawer() {
		return getDrawer(DRAWER_NAME);
	}

	public JSONObject getDrawer(String name) {
		String url = STORAGE_SERVER_URL + GET_DRAWER_PATH;
		url = url.replace("{name}", name);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeGet(url);
		return new JSONObject(response.getBody());
	}

	public JSONArray listNodes() {
		return listNodes(DRAWER_NAME, "");
	}

	public JSONArray listNodes(String prefix) {
		return listNodes(DRAWER_NAME, prefix);
	}

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

	public JSONObject createNode(String filename, byte[] data, boolean generateStruct, boolean generateId) {
		return createNode(DRAWER_NAME, filename, data, generateStruct, generateId, false);
	}

	public JSONObject createNode(String filename, String data, boolean generateStruct, boolean generateId) {
		return createNode(DRAWER_NAME, filename, data, generateStruct, generateId);
	}

	public JSONObject createNode(String filename, String data, boolean generateStruct, boolean generateId, boolean replace) {
		return createNode(DRAWER_NAME, filename, data, generateStruct, generateId, replace);
	}

	public JSONObject createNode(String drawerName, String filename, String data, boolean generateStruct, boolean generateId) {
		return createNode(drawerName, filename, data.getBytes(), generateStruct, generateId, false);
	}

	public JSONObject createNode(String drawerName, String filename, String data, boolean generateStruct, boolean generateId, boolean replace) {
		return createNode(drawerName, filename, data.getBytes(), generateStruct, generateId, replace);
	}

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

	public JSONObject createNode(String filename, String data) {
		return createNode(filename, data, true, true);
	}

	public JSONObject createNode(String drawerName, String filename, String data) {
		return createNode(drawerName, filename, data, true, true);
	}

	public JSONObject updateNode(String drawerName, String nodeId, String data) {
		String url = STORAGE_SERVER_URL + UPDATE_NODE_PATH;
		url = url.replace("{name}", drawerName);
		url = url.replace("{id}", nodeId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeMultipartPut(url, "file", nodeId, data);
		return new JSONObject(response.getBody());
	}

	public JSONObject deleteNode(String nodeId) {
		return deleteNode(DRAWER_NAME, nodeId);
	}

	public JSONObject deleteNode(String drawerName, String nodeId) {
		String url = STORAGE_SERVER_URL + DELETE_NODE_PATH;
		url = url.replace("{name}", drawerName);
		url = url.replace("{id}", nodeId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeDelete(url);
		return new JSONObject(response.getBody());
	}

	public JSONObject getNode(String nodeId) {
		return getNode(DRAWER_NAME, nodeId);
	}

	public JSONObject getNode(String drawerName, String nodeId) {
		String url = STORAGE_SERVER_URL + GET_NODE_PATH;
		url = url.replace("{name}", drawerName);
		url = url.replace("{id}", nodeId);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeGet(url);
		return new JSONObject(response.getBody());
	}

	public byte[] dowloadNode(String drawerName, String nodeId) {
		String url = STORAGE_SERVER_URL + DOWNLOAD_NODE_PATH;
		url = url.replace("{name}", drawerName);
		url = url.replace("{id}", nodeId);

		ResponseEntity<byte[]> response = RequestHelper.getInstance(getAuthorizationHeader()).downloadBinary(url, HttpMethod.GET);
		return response.getBody();
	}

	public byte[] dowloadNode(String nodeId) {
		return dowloadNode(DRAWER_NAME, nodeId);
	}

	public void streamNode(String nodeId, StreamProcessor processor) {
		String url = STORAGE_SERVER_URL + DOWNLOAD_NODE_PATH;
		url = url.replace("{name}", DRAWER_NAME);
		url = url.replace("{id}", nodeId);

		RequestHelper.getInstance(getAuthorizationHeader()).stream(url, HttpMethod.GET, processor);
	}

	public JSONObject copyNode(String nodeId, String targetDrawerName, boolean generateStruct, boolean generateId, boolean deleteOriginal) {
		return copyNode(DRAWER_NAME, nodeId, targetDrawerName, generateStruct, generateId, deleteOriginal);
	}

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
