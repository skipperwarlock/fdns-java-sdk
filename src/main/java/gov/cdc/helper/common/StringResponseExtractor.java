package gov.cdc.helper.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

public class StringResponseExtractor implements ResponseExtractor<String> {

	private static final Logger logger = Logger.getLogger(StringResponseExtractor.class);
	
	private StreamProcessor processor;

	/**
	 * Constructs a new instance of the StringResponseExtractor class
	 * @param processor
	 */
	public StringResponseExtractor(StreamProcessor processor) {
		this.processor = processor;
	}

	/**
	 * Reads response body into processor and returns null
	 * @param response client response to http request
	 * @return null
	 * @throws IOException
	 */
	public String extractData(ClientHttpResponse response) throws IOException {
		InputStream is = response.getBody();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		while (reader.ready()) {
			String line = reader.readLine();
			logger.debug(line);
			processor.append(line.replaceAll("\\uFEFF", ""));
		}
		return null;
	}

}
