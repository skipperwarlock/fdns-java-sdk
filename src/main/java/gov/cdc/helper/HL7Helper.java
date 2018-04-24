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

	public static HL7Helper getInstance(String authorizationHeader) throws IOException {
		if (authorizationHeader != null && (authorizationHeader.startsWith("Bearer") || authorizationHeader.startsWith("bearer")))
			return (HL7Helper) createNew().setAuthorizationHeader(authorizationHeader);
		else
			return getInstance();
	}

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

	public Document parseToXML(String hl7_message) throws ParserConfigurationException, SAXException, IOException {
		String url = HL7_SERVER_URL + PARSE_HL7_TO_XML_PATH;

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, hl7_message);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.parse(new ByteArrayInputStream(response.getBody().getBytes()));
	}

	public JSONObject parse(String hl7_message) {
		return parse(hl7_message, HL7_SPEC);
	}

	public JSONObject parse(String hl7_message, String spec) {
		String url = HL7_SERVER_URL + PARSE_HL7_PATH;
		url = url.replace("{spec}", spec);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, hl7_message);
		return new JSONObject(response.getBody());
	}

	public JSONObject parse(String hl7_message, String spec, String profile) {
		String url = HL7_SERVER_URL + PARSE_HL7_PROFILE_PATH;
		url = url.replace("{spec}", spec);
		url = url.replace("{profile}", profile);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, hl7_message);
		return new JSONObject(response.getBody());
	}

	public JSONObject getCaseId(String hl7_message) {
		return getCaseId(hl7_message, HL7_SPEC);
	}

	public JSONObject getCaseId(String hl7_message, String spec) {
		String url = HL7_SERVER_URL + GET_CASE_ID_PATH;
		url = url.replace("{spec}", spec);

		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executePost(url, hl7_message);
		return new JSONObject(response.getBody());
	}

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
