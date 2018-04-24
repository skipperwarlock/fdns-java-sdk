package gov.cdc.helper;

import java.util.ArrayList;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import gov.cdc.helper.common.FileMessageResource;

public class CombinerHelper extends AbstractHelper {

	private static CombinerHelper instance;

	private static String COMBINER_SERVER_URL;
	private static String FLATTEN_PATH;
	private static String COMBINE_PATH;
	private static String DELETE_CONFIG_PATH;
	private static String CREATE_OR_UPDATE_CONFIG_PATH;

	public static CombinerHelper getInstance(String authorizationHeader) throws IOException {
		if (authorizationHeader != null && (authorizationHeader.startsWith("Bearer") || authorizationHeader.startsWith("bearer")))
			return (CombinerHelper) createNew().setAuthorizationHeader(authorizationHeader);
		else
			return getInstance();
	}

	public static CombinerHelper getInstance() throws IOException {
		if (instance == null) {
			instance = createNew();
		}
		return instance;
	}

	private static CombinerHelper createNew() throws IOException {
		CombinerHelper helper = new CombinerHelper();

		COMBINER_SERVER_URL = ResourceHelper.getSysEnvProperty(ResourceHelper.CONST_ENV_VAR_COMBINER_URL, true);
		if (!COMBINER_SERVER_URL.endsWith("/"))
			COMBINER_SERVER_URL += "/";
		FLATTEN_PATH = ResourceHelper.getProperty("combiner.flatten");
		COMBINE_PATH = ResourceHelper.getProperty("combiner.combine");
		DELETE_CONFIG_PATH = ResourceHelper.getProperty("combiner.deleteConfig");
		CREATE_OR_UPDATE_CONFIG_PATH = ResourceHelper.getProperty("combiner.createOrUpdateConfig");

		return helper;
	}

	public byte[] flatten(String targetType, JSONObject json) {
		String url = COMBINER_SERVER_URL + FLATTEN_PATH;
		url = url.replace("{targetType}", targetType);

		String filename;
		if (json.has("_id")) {
			if (json.get("_id") instanceof String)
				filename = json.getString("_id") + ".json";
			else if (json.get("_id") instanceof JSONObject && json.getJSONObject("_id").has("$oid"))
				filename = json.getJSONObject("_id").getString("$oid") + ".json";
			else
				filename = UUID.randomUUID() + ".json";
		} else
			filename = UUID.randomUUID() + ".json";

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeMultipartPost(url, "file", filename, json.toString());
		return response.getBody().getBytes();
	}

	public byte[] combine(String targetType, String configName, List<JSONObject> jsons, String orientation, boolean includeHeader) {
		String url = COMBINER_SERVER_URL + COMBINE_PATH;
		url = url.replace("{targetType}", targetType);
		url = url.replace("{configName}", configName);
		url = url.replace("{includeHeader}", Boolean.toString(includeHeader));
		if (orientation != null && !orientation.isEmpty())
			url = url.replace("{orientation}", orientation);
		else
			url = url.replace("?orientation={orientation}", "");

		List<FileMessageResource> resources = new ArrayList<FileMessageResource>();
		for (JSONObject json : jsons) {
			String filename;
			if (json.has("_id")) {
				if (json.get("_id") instanceof String)
					filename = json.getString("_id") + ".json";
				else if (json.get("_id") instanceof JSONObject && json.getJSONObject("_id").has("$oid"))
					filename = json.getJSONObject("_id").getString("$oid") + ".json";
				else
					filename = UUID.randomUUID() + ".json";
			} else
				filename = UUID.randomUUID() + ".json";
			resources.add(new FileMessageResource(json.toString().getBytes(), filename));
		}

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeMultipart(url, HttpMethod.POST, "file", resources);
		return response.getBody().getBytes();
	}

	public JSONObject createOrUpdateConfig(String configName, JSONObject config) {
		String url = COMBINER_SERVER_URL + CREATE_OR_UPDATE_CONFIG_PATH;
		url = url.replace("{configName}", configName);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, config.toString(), MediaType.APPLICATION_JSON);
		return new JSONObject(response.getBody());
	}

	public JSONObject deleteConfig(String configName) {
		String url = COMBINER_SERVER_URL + DELETE_CONFIG_PATH;
		url = url.replace("{configName}", configName);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeDelete(url);
		return new JSONObject(response.getBody());
	}
}
