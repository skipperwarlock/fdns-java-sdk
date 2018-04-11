package gov.cdc.helper;

import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class MicrosoftHelper extends AbstractHelper {

	private static MicrosoftHelper instance;

	private static String MICROSOFT_UTILS_SERVER_URL;
	private static String XLSX_EXTRACT_CSV_PATH;
	private static String XLSX_EXTRACT_JSON_PATH;
	private static String XLSX_GET_SHEETS_PATH;
	private static String XLSX_CONVERT_CSV_PATH;
	private static String DOCX_EXTRACT_PATH;

	public static MicrosoftHelper getInstance(String authorizationHeader) throws Exception {
		if (authorizationHeader != null && (authorizationHeader.startsWith("Bearer") || authorizationHeader.startsWith("bearer")))
			return (MicrosoftHelper) createNew().setAuthorizationHeader(authorizationHeader);
		else
			return getInstance();
	}

	public static MicrosoftHelper getInstance() throws Exception {
		if (instance == null) {
			instance = createNew();
		}
		return instance;
	}

	private static MicrosoftHelper createNew() throws Exception {
		MicrosoftHelper helper = new MicrosoftHelper();

		MICROSOFT_UTILS_SERVER_URL = ResourceHelper.getSysEnvProperty(ResourceHelper.CONST_ENV_VAR_MICROSOFT_UTILS_URL, true);
		if (!MICROSOFT_UTILS_SERVER_URL.endsWith("/"))
			MICROSOFT_UTILS_SERVER_URL += "/";
		XLSX_EXTRACT_CSV_PATH = ResourceHelper.getProperty("msft_utils.xlsx.extractCsv");
		XLSX_EXTRACT_JSON_PATH = ResourceHelper.getProperty("msft_utils.xlsx.extractJson");
		XLSX_GET_SHEETS_PATH = ResourceHelper.getProperty("msft_utils.xlsx.getSheets");
		XLSX_CONVERT_CSV_PATH = ResourceHelper.getProperty("msft_utils.xlsx.convertCSVToXLSX");
		DOCX_EXTRACT_PATH = ResourceHelper.getProperty("msft_utils.docx.extract");

		return helper;
	}

	public JSONObject getSheets(String filename, byte[] data) {
		String url = MICROSOFT_UTILS_SERVER_URL + XLSX_GET_SHEETS_PATH;
		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeMultipart(url, HttpMethod.POST, "file", filename, data);
		return new JSONObject(response.getBody());
	}

	public String extractXlsxToCsv(String filename, byte[] data, String sheetName, String sheetRange, String orientation) {
		String url = MICROSOFT_UTILS_SERVER_URL + XLSX_EXTRACT_CSV_PATH;
		url = url.replace("{sheetName}", sheetName);
		url = url.replace("{sheetRange}", sheetRange);
		if (orientation != null)
			url = url.replace("{orientation}", orientation);
		else
			url = url.replace("{orientation}", "portrait");
		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeMultipart(url, HttpMethod.POST, "file", filename, data);
		return response.getBody();
	}

	public JSONObject extractXlsxToJson(String filename, byte[] data, String sheetName, String sheetRange, String orientation) {
		String url = MICROSOFT_UTILS_SERVER_URL + XLSX_EXTRACT_JSON_PATH;
		url = url.replace("{sheetName}", sheetName);
		url = url.replace("{sheetRange}", sheetRange);
		if (orientation != null)
			url = url.replace("{orientation}", orientation);
		else
			url = url.replace("{orientation}", "portrait");
		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeMultipart(url, HttpMethod.POST, "file", filename, data);
		return new JSONObject(response.getBody());
	}

	public String extractDocxToTxt(String filename, byte[] data) {
		String url = MICROSOFT_UTILS_SERVER_URL + DOCX_EXTRACT_PATH;
		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeMultipart(url, HttpMethod.POST, "file", filename, data);
		return response.getBody();
	}

	public byte[] convertCSVToXLSX(String filename, byte[] data) {
		String url = MICROSOFT_UTILS_SERVER_URL + XLSX_CONVERT_CSV_PATH;
		ResponseEntity<byte[]> response = RequestHelper.getInstance(getAuthorizationHeader()).executeMultipartAndGetBytes(url, HttpMethod.POST, "file", filename, data);
		return response.getBody();
	}

}
