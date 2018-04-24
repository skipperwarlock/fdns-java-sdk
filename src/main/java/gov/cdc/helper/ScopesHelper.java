package gov.cdc.helper;

import java.util.List;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

public class ScopesHelper extends AbstractHelper {

	private static ScopesHelper instance;

	private static String SCOPES_SERVER_URL;
	private static String GET_SCOPES_PATH;

	public static ScopesHelper getInstance() throws IOException {
		if (instance == null) {
			instance = createNew();
		}
		return instance;
	}

	private static ScopesHelper createNew() throws IOException {
		ScopesHelper helper = new ScopesHelper();

		SCOPES_SERVER_URL = ResourceHelper.getSysEnvProperty(ResourceHelper.CONST_ENV_VAR_SCOPES_URL, true);
		if (!SCOPES_SERVER_URL.endsWith("/"))
			SCOPES_SERVER_URL += "/";
		GET_SCOPES_PATH = ResourceHelper.getProperty("scopes.getScopes");

		return helper;
	}

	public JSONObject getScopes(List<String> groupNames) {

		String url = SCOPES_SERVER_URL + GET_SCOPES_PATH;
		url = url.replace("{groups}", StringUtils.join(groupNames, ","));

		ResponseEntity<String> response = RequestHelper.getInstance().executeGet(url);

		return new JSONObject(response.getBody());
	}

}
