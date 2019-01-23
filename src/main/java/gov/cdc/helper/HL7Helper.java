package gov.cdc.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class HL7Helper extends AbstractHelper {

	private static HL7Helper instance;

	private static String HL7_SERVER_URL;
	private static String PARSE_HL7_PATH;
	private static String PARSE_HL7_TO_XML_PATH;
	private static String PARSE_HL7_PROFILE_PATH;
	private static String GET_CASE_ID_PATH;
	private static String GET_MESSAGE_HASH_PATH;
	private static String VALIDATE_WITH_IG_PATH;
	private static String VALIDATE_WITH_RULES_PATH;
	private static String HL7_SPEC;

	/**
	 * If authorizationHeader isn't null and if provided header starts with 'Bearer',
	 * constructs new instance of HL7Helper class and sets the authorization header to the provided value.
	 * If authorizationHeader is null or doesn't start with 'Bearer', returns singleton instance of HL7Helper.
	 *
	 * @param authorizationHeader
	 * @return
	 * @throws IOException
	 */
	public static HL7Helper getInstance(String authorizationHeader) throws IOException {
		if (authorizationHeader != null && (authorizationHeader.startsWith("Bearer") || authorizationHeader.startsWith("bearer")))
			return (HL7Helper) createNew().setAuthorizationHeader(authorizationHeader);
		else
			return getInstance();
	}

	/**
	 * HL7Helper singleton constructor
	 *
	 * @return
	 * @throws IOException
	 */
	public static HL7Helper getInstance() throws IOException {
		if (instance == null) {
			instance = createNew();
		}
		return instance;
	}

	private static HL7Helper createNew() throws IOException {
		HL7Helper helper = new HL7Helper();

		HL7_SERVER_URL = ResourceHelper.getSysEnvProperty(ResourceHelper.CONST_ENV_VAR_HL7_UTILS_URL, true);
		if (!HL7_SERVER_URL.endsWith("/"))
			HL7_SERVER_URL += "/";
		PARSE_HL7_PATH = ResourceHelper.getProperty("hl7_utils.parse_hl7");
		PARSE_HL7_TO_XML_PATH = ResourceHelper.getProperty("hl7_utils.parse_hl7_to_xml");
		PARSE_HL7_PROFILE_PATH = ResourceHelper.getProperty("hl7_utils.parse_hl7_profile");
		GET_CASE_ID_PATH = ResourceHelper.getProperty("hl7_utils.getCaseId");
		GET_MESSAGE_HASH_PATH = ResourceHelper.getProperty("hl7_utils.getMessageHash");
		VALIDATE_WITH_IG_PATH = ResourceHelper.getProperty("hl7_utils.validateWithIG");
		VALIDATE_WITH_RULES_PATH = ResourceHelper.getProperty("hl7_utils.validateWithRules");
		HL7_SPEC = ResourceHelper.getProperty("hl7_utils.spec");

		return helper;
	}

	/**
	 * Execute post call to HL7 Utils to transform an HL7 message to XML document.
	 *
	 * @param hl7_message  HL7 message to transform
	 * @return XML transformation of hl7_message
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document parseToXML(String hl7_message) throws ParserConfigurationException, SAXException, IOException {
		String url = HL7_SERVER_URL + PARSE_HL7_TO_XML_PATH;

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, hl7_message);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.parse(new ByteArrayInputStream(response.getBody().getBytes()));
	}

	/**
	 * Calls HL7 Utils to transform HL7 to JSON with spec defined in config-services.properties
	 * @see HL7Helper#parse(String, String)
	 *
	 * @param hl7_message message to parse
	 * @return
	 */
	public JSONObject parse(String hl7_message) {
		return parse(hl7_message, HL7_SPEC);
	}

	/**
	 * Execute post call to HL7 Utils to transform HL7 to JSON.
	 *
	 * @param hl7_message hl7 message to transform to json
	 * @param spec  hl7 or phinms
	 * @return json transformation of hl7 message
	 */
	public JSONObject parse(String hl7_message, String spec) {
		String url = HL7_SERVER_URL + PARSE_HL7_PATH;
		url = url.replace("{spec}", spec);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, hl7_message);
		return new JSONObject(response.getBody());
	}

	/**
	 * Execute post call to HL7 Utils to transform HL7 to JSON.
	 *
	 * @param hl7_message hl7 message to transform to json
	 * @param spec hl7 or phinms
	 * @param profile 	message profile
	 * @return json transformation of hl7 message
	 */
	public JSONObject parse(String hl7_message, String spec, String profile) {
		String url = HL7_SERVER_URL + PARSE_HL7_PROFILE_PATH;
		url = url.replace("{spec}", spec);
		url = url.replace("{profile}", profile);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, hl7_message);
		return new JSONObject(response.getBody());
	}

	/**
	 * Calls HL7 Utils to get case identifiers for HL7 message with spec defined in config-services.properties
	 * @see HL7Helper#getCaseId(String, String)
	 *
	 * @param hl7_message message from which to read case identifiers
	 * @return case identifier segments
	 */
	public JSONObject getCaseId(String hl7_message) {
		return getCaseId(hl7_message, HL7_SPEC);
	}

	/**
	 * Execute post call to HL7 Utils to get case identifiers from hl7 message
	 *
	 * @param hl7_message message containing Case Ids
	 * @param spec hl7 or phinms
	 * @return case identifier segments
	 */
	public JSONObject getCaseId(String hl7_message, String spec) {
		String url = HL7_SERVER_URL + GET_CASE_ID_PATH;
		url = url.replace("{spec}", spec);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, hl7_message);
		return new JSONObject(response.getBody());
	}

	/**
	 * Execute post call to HL7 Utils to get hash for hl7 message
	 *
	 * @param hl7_message message for which to get hash
	 * @return hash value for message
	 */
	public JSONObject getMessageHash(String hl7_message) {
		String url = HL7_SERVER_URL + GET_MESSAGE_HASH_PATH;

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, hl7_message);
		return new JSONObject(response.getBody());
	}

	public JSONObject validateWithIG(String hl7_message, String profile, String conformanceProfileName, String valueSetName, String conformanceContextNames) {
		String url = HL7_SERVER_URL + VALIDATE_WITH_IG_PATH;
		url = url.replace("{profile}", profile);
		url = url.replace("{conformanceProfileName}", conformanceProfileName);
		url = url.replace("{valueSetName}", valueSetName);
		url = url.replace("{conformanceContextNames}", conformanceContextNames);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, hl7_message);
		return new JSONObject(response.getBody());
	}

	public JSONObject validateWithRules(String hl7_message, String profile, boolean explain, boolean checkPII) {
		String url = HL7_SERVER_URL + VALIDATE_WITH_RULES_PATH;
		url = url.replace("{profile}", profile);
		url = url.replace("{explain}", Boolean.toString(explain));
		url = url.replace("{checkPII}", Boolean.toString(checkPII));

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, hl7_message);
		return new JSONObject(response.getBody());
	}

}
