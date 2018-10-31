package gov.cdc.helper;

import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.BsonSerializationException;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.helper.common.ServiceException;

@Configuration
public class ErrorHandler {

	private static final Logger logger = Logger.getLogger(ErrorHandler.class);

	private static ErrorHandler me = null;

	public ResponseEntity<?> handle(HttpStatus status, Map<String,Object> log){
			log.put(AbstractMessageHelper.CONST_SUCCESS, false);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode error = null;
			try{
				error = mapper.readTree(new JSONObject(log).toString());
			} catch(Exception e){
				logger.error(e);
			}
		return ResponseEntity.status(status).body(error);
	}

	public ResponseEntity<?> handle(Exception e, Map<String, Object> log) {
		boolean trace = false;
		try {
			trace = Boolean.parseBoolean(ResourceHelper.getSysEnvProperty(ResourceHelper.CONST_ENV_VAR_INCLUDE_TRACE, true));
		} catch (Exception e1) {
			logger.error(e1);
		}

		if (trace)
			AbstractMessageHelper.append(log, e);
		
		if (e instanceof ServiceException) {
			ServiceException se = (ServiceException) e;
			log.put("cause", se.getObj());
		}
		
		log.put(AbstractMessageHelper.CONST_SUCCESS, false);
		log.put(AbstractMessageHelper.CONST_MESSAGE, e.getMessage());

		ObjectMapper mapper = new ObjectMapper();
		JsonNode error = null;
		try {
			error = mapper.readTree(new JSONObject(log).toString());
		} catch (Exception e2) {
			// Do nothing
			logger.error(e2);
		}
		if(e.getCause() instanceof BsonSerializationException){
			return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}
	}

	public static ErrorHandler getInstance() {
		if (me == null)
			me = new ErrorHandler();
		return me;
	}
}
