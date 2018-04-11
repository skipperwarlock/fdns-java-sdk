package gov.cdc.helper.common;

import org.json.JSONException;
import org.json.JSONObject;

public class ServiceException extends Exception {

	private static final long serialVersionUID = 3638768641142369946L;

	private final String obj;

	public ServiceException(String message) {
		super(message);
		obj = null;
	}

	public ServiceException(Exception e) {
		super(e);
		obj = null;
	}

	public ServiceException(JSONObject obj) {
		this.obj = obj.toString();
	}

	public JSONObject getObj() throws JSONException {
		return obj != null ? new JSONObject(obj) : null;
	}

}
