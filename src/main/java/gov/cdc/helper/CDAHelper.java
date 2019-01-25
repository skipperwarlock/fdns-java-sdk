package gov.cdc.helper;

import org.json.JSONObject;
import java.io.IOException;
import org.springframework.http.ResponseEntity;

public class CDAHelper extends AbstractHelper {

	private static CDAHelper instance;

	private static String CDA_SERVER_URL;
	private static String PARSE_CDA;

	/**
	 * If authorizationHeader isn't null and if provided header starts with 'Bearer',
	 * constructs new instance of CDAHelper class and sets the authorization header to the specified value.
	 * If authorizationHeader is null or doesn't start with 'Bearer', returns singleton instance of CDAHelper.
	 *
	 * @param authorizationHeader
	 * @return
	 * @throws IOException
	 */
	public static CDAHelper getInstance(String authorizationHeader) throws IOException {
		if (authorizationHeader != null && (authorizationHeader.startsWith("Bearer") || authorizationHeader.startsWith("bearer")))
			return (CDAHelper) createNew().setAuthorizationHeader(authorizationHeader);
		else
			return getInstance();
	}

	/**
	 * CDAHelper singleton constructor
	 * @return
	 * @throws IOException
	 */
	public static CDAHelper getInstance() throws IOException {
		if (instance == null) {
			instance = createNew();
		}
		return instance;
	}

	private static CDAHelper createNew() throws IOException {
		CDAHelper helper = new CDAHelper();

		CDA_SERVER_URL = ResourceHelper.getSysEnvProperty(ResourceHelper.CONST_ENV_VAR_CDA_UTILS_URL, true);
		if (!CDA_SERVER_URL.endsWith("/"))
			CDA_SERVER_URL += "/";
		PARSE_CDA = ResourceHelper.getProperty("cda_utils.parse_cda");
		
		return helper;
	}

	/**
	 * Send specified CDA message to CDA Utils and return parsed message as JSONObject
	 *
	 * @param cda_message message to be parsed
	 * @return parsed cda message
	 */
	public JSONObject parse(String cda_message) {
		String url = CDA_SERVER_URL + PARSE_CDA;

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, cda_message);
		return new JSONObject(response.getBody());
	}

}
