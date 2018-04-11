package gov.cdc.helper;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class OAuthHelper extends AbstractHelper {

	private static OAuthHelper instance;

	private static String OAUTH_SERVER_URL;
	private static String REQUEST_TOKEN;
	private static String SCOPES;

	public static OAuthHelper getInstance() throws Exception {
		if (instance == null) {
			instance = createNew();
		}
		return instance;
	}

	private static OAuthHelper createNew() throws Exception {
		OAuthHelper helper = new OAuthHelper();

		OAUTH_SERVER_URL = ResourceHelper.getSysEnvProperty(ResourceHelper.CONST_ENV_VAR_OAUTH_URL, true);
		if (!OAUTH_SERVER_URL.endsWith("/"))
			OAUTH_SERVER_URL += "/";
		REQUEST_TOKEN = ResourceHelper.getProperty("oauth.requestToken");
		SCOPES = ResourceHelper.getSysEnvProperty(ResourceHelper.CONST_ENV_VAR_OAUTH_SCOPES, false);

		return helper;
	}

	public String getToken() throws Exception {
		if (SCOPES != null && !SCOPES.isEmpty())
			return getToken(Arrays.asList(SCOPES.split(" ")));
		else
			return getToken(null);
	}

	public String getToken(List<String> scopes) throws Exception {
		String token = null;

		String url = OAUTH_SERVER_URL + REQUEST_TOKEN;

		MultiValueMap<String, String> data = new LinkedMultiValueMap<String, String>();
		data.add("grant_type", "client_credentials");
		if (scopes != null && !scopes.isEmpty())
			data.add("scope", String.join(" ", scopes));

		String clientId = ResourceHelper.getSysEnvProperty(ResourceHelper.CONST_ENV_VAR_OAUTH_CLIENTID, true);
		String clientSecret = ResourceHelper.getSysEnvProperty(ResourceHelper.CONST_ENV_VAR_OAUTH_CLIENTSECRET, true);
		JSONObject json = new JSONObject(RequestHelper.getInstance(clientId, clientSecret).executePost(url, data).getBody());
		token = json.getString("access_token");

		return token;
	}

}
