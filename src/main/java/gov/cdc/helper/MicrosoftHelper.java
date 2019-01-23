package gov.cdc.helper;

import java.io.IOException;
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

	/**
	 * If authorizationHeader isn't null and if provided header starts with 'Bearer',
	 * constructs new instance of MicrosoftHelper class and sets the authorization header to the provided value.
	 * If authorizationHeader is null or doesn't start with 'Bearer', returns singleton instance of MicrosoftHelper.
	 *
	 * @param authorizationHeader
	 * @return
	 * @throws IOException
	 */
	public static MicrosoftHelper getInstance(String authorizationHeader) throws IOException {
		if (authorizationHeader != null && (authorizationHeader.startsWith("Bearer") || authorizationHeader.startsWith("bearer")))
			return (MicrosoftHelper) createNew().setAuthorizationHeader(authorizationHeader);
		else
			return getInstance();
	}

	/**
	 * MicrosoftHelper singleton constructor
	 *
	 * @return
	 * @throws IOException
	 */
	public static MicrosoftHelper getInstance() throws IOException {
		if (instance == null) {
			instance = createNew();
		}
		return instance;
	}

	private static MicrosoftHelper createNew() throws IOException {
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

	/**
	 * Call Microsoft Utilities Service to get list of sheets of a xlsx file.
	 *
	 * @param filename expected filename
	 * @param data xlsx file
	 * @return response from msft utilities service with sheet names and indexes
	 */
	public JSONObject getSheets(String filename, byte[] data) {
		String url = MICROSOFT_UTILS_SERVER_URL + XLSX_GET_SHEETS_PATH;
		ResponseEntity<String> response = RequestHelper.getInstance(getAuthorizationHeader()).executeMultipart(url, HttpMethod.POST, "file", filename, data);
		return new JSONObject(response.getBody());
	}


	/**
	 * Call Microsoft Utilities Service to extract data from xlsx file and return it in csv format
	 *
	 * @param filename expected filename
	 * @param data xlsx file
	 * @param sheetName name of xlsx sheet to read
	 * @param sheetRange cell range on sheet (Ex: A1:D1 or A2:A10)
	 * @param orientation portrait or landscape
	 * @return response from msft utilities service with transformed data in csv format
	 */
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

	/**
	 * Call Microsoft Utilities Service to extract data from xlsx file and return it in json format
	 *
	 * @param filename expected filename
	 * @param data xlsx file
	 * @param sheetName name of xlsx sheet to read
	 * @param sheetRange cell range on sheet (Ex: A1:D1 or A2:A10)
	 * @param orientation portrait or landscape
	 * @return response from msft utilities service with transformed data in json format
	 */
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

	/**
	 * Call Microsoft Utilities Service to extract text from docx file
	 *
	 * @param filename expected filename
	 * @param data docx file
	 * @return response from msft utilities service with text data from docx file
	 */
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
