package gov.cdc.helper;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.client.HttpClientErrorException;

import com.google.gson.Gson;

import gov.cdc.helper.common.ServiceException;

public class AbstractMessageHelper {

	public static final String CONST_METHOD = "method";
	public static final String CONST_SUCCESS = "success";
	public static final String CONST_ERROR = "error";
	public static final String CONST_CAUSE = "cause";
	public static final String CONST_REASON = "reason";
	public static final String CONST_TRACE = "trace";
	public static final String CONST_MESSAGE = "message";

	public static void append(Map<String, Object> log, Exception e) {
		log.put(AbstractMessageHelper.CONST_SUCCESS, false);
		log.put(AbstractMessageHelper.CONST_ERROR, e.getMessage());
		log.put(AbstractMessageHelper.CONST_CAUSE, e.getCause());
		Gson gson = new Gson();
		JSONObject trace = new JSONObject();
		trace.put("stackTrace", new JSONArray(gson.toJson(e.getStackTrace())));
		if (e instanceof HttpClientErrorException) {
			trace.put("rawStatusCode", ((HttpClientErrorException) e).getStatusCode().value());
			trace.put("statusCode", ((HttpClientErrorException) e).getStatusText());
		}
		if (e instanceof ServiceException && ((ServiceException) e).getObj() != null)
			log.put(AbstractMessageHelper.CONST_REASON, ((ServiceException) e).getObj());
		
		log.put(AbstractMessageHelper.CONST_TRACE, trace);
	}
}
