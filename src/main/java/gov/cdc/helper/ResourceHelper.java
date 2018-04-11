package gov.cdc.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class ResourceHelper {

	private static final Logger logger = Logger.getLogger(ResourceHelper.class);

	private static final String APPLICATION_PROPERTIES_PATH = "/application.properties";
	private static final String CONFIG_PROPERTIES_PATH = "/config.properties";
	private static final String CONFIG_SERVICES_PATH = "/config-services.properties";
	private static Properties properties = null;

	public static final String CONST_ENV_VAR_PROFILE_NAME = "PROFILE_NAME";
	public static final String CONST_ENV_VAR_GROUP_NAME = "GROUP_NAME";
	public static final String CONST_ENV_VAR_INCOMING_TOPIC_NAME = "INCOMING_TOPIC_NAME";
	public static final String CONST_ENV_VAR_OUTGOING_TOPIC_NAME = "OUTGOING_TOPIC_NAME";
	public static final String CONST_ENV_VAR_ERROR_TOPIC_NAME = "ERROR_TOPIC_NAME";
	public static final String CONST_ENV_VAR_KAFKA_BROKERS = "KAFKA_BROKERS";
	public static final String CONST_ENV_VAR_S3_BUCKET_NAME = "S3_BUCKET_NAME";
	public static final String CONST_ENV_VAR_SCHEMA_REGISTRY_URL = "SCHEMA_REGISTRY_URL";
	public static final String CONST_ENV_VAR_HL7_UTILS_URL = "HL7_UTILS_URL";
	public static final String CONST_ENV_VAR_CDA_UTILS_URL = "CDA_UTILS_URL";
	public static final String CONST_ENV_VAR_OBJECT_URL = "OBJECT_URL";
	public static final String CONST_ENV_VAR_STORAGE_URL = "STORAGE_URL";
	public static final String CONST_ENV_VAR_INDEXING_URL = "INDEXING_URL";
	public static final String CONST_ENV_VAR_MICROSOFT_UTILS_URL = "MICROSOFT_UTILS_URL";
	public static final String CONST_ENV_VAR_COMBINER_URL = "COMBINER_URL";
	public static final String CONST_ENV_VAR_SCOPES_URL = "SCOPES_URL";
	public static final String CONST_ENV_VAR_OAUTH_URL = "OAUTH_URL";
	public static final String CONST_ENV_VAR_OAUTH_SCOPES = "OAUTH_SCOPES";
	public static final String CONST_ENV_VAR_OAUTH_CLIENTID = "OAUTH_CLIENTID";
	public static final String CONST_ENV_VAR_OAUTH_CLIENTSECRET = "OAUTH_CLIENTSECRET";
	public static final String CONST_ENV_VAR_INCLUDE_TRACE = "ERROR_INCLUDE_TRACE";

	private ResourceHelper() {
		throw new IllegalAccessError("Helper class");
	}

	public static Properties getProperties() throws IOException {
		if (properties == null) {
			properties = new Properties();
			InputStream is = ResourceHelper.class.getResourceAsStream(APPLICATION_PROPERTIES_PATH);
			if (is != null)
				properties.load(is);
			is = ResourceHelper.class.getResourceAsStream(CONFIG_PROPERTIES_PATH);
			if (is != null)
				properties.load(is);
			is = ResourceHelper.class.getResourceAsStream(CONFIG_SERVICES_PATH);
			if (is != null)
				properties.load(is);
		}
		return properties;
	}

	public static Properties getProperties(String path) throws IOException {
		Properties properties = new Properties();
		properties.load(ResourceHelper.class.getResourceAsStream(path));
		return properties;
	}

	public static Map<String, String> getPropertyMap(String path) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			Properties properties = getProperties(path);
			for (Object key : properties.keySet()) {
				map.put((String) key, properties.getProperty((String) key));
			}
		} catch (IOException e) {
			logger.error(e);
		}
		return map;
	}

	public static String getProperty(String propertyName) throws IOException {
		return getProperties().getProperty(propertyName);
	}

	public static String getSysEnvProperty(String propertyName, boolean required) throws Exception {
		String value = System.getProperty(propertyName);
		if (value == null || value.isEmpty())
			value = System.getenv().get(propertyName);
			
		logger.debug("Env Variable: " + propertyName + " = " + value);
		if (required && StringUtils.isEmpty(value)) {
			throw new Exception("The environment variable `" + propertyName + "` is empty.");
		}
		return value;
	}

}
