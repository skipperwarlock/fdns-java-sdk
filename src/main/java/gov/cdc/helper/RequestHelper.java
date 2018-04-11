package gov.cdc.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import gov.cdc.helper.common.FileMessageResource;
import gov.cdc.helper.common.StreamProcessor;
import gov.cdc.helper.common.StringResponseExtractor;

public class RequestHelper extends AbstractHelper {

	private static RequestHelper instance;

	public static RequestHelper getInstance(String username, String password) {
		return (RequestHelper) createNew().setAuthorizationHeader("Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));
	}

	public static RequestHelper getInstance(String authorizationHeader) {
		if (authorizationHeader != null && (authorizationHeader.startsWith("Bearer") || authorizationHeader.startsWith("bearer")))
			return (RequestHelper) createNew().setAuthorizationHeader(authorizationHeader);
		else
			return getInstance();
	}

	public static RequestHelper getInstance() {
		if (instance == null) {
			instance = createNew();
		}
		return instance;
	}

	private static RequestHelper createNew() {
		return new RequestHelper();
	}

	private static final Logger logger = Logger.getLogger(RequestHelper.class);

	private static final RestTemplate rt = new RestTemplate();

	public ResponseEntity<String> executeGet(String url) {
		return execute(url, HttpMethod.GET, MediaType.APPLICATION_JSON, null);
	}

	public ResponseEntity<String> executePost(String url) {
		return execute(url, HttpMethod.POST, MediaType.TEXT_PLAIN, null);
	}

	public ResponseEntity<String> executePost(String url, String data) {
		return execute(url, HttpMethod.POST, MediaType.TEXT_PLAIN, data);
	}

	public ResponseEntity<String> executePost(String url, MultiValueMap<String, String> data) {
		return execute(url, HttpMethod.POST, data);
	}

	public ResponseEntity<String> executePost(String url, String data, MediaType mediaType) {
		return execute(url, HttpMethod.POST, mediaType, data);
	}

	public ResponseEntity<String> executePut(String url, String data) {
		return execute(url, HttpMethod.PUT, MediaType.TEXT_PLAIN, data);
	}

	public ResponseEntity<String> executePut(String url, String data, MediaType mediaType) {
		return execute(url, HttpMethod.PUT, mediaType, data);
	}

	public ResponseEntity<String> execute(String url, HttpMethod method, MediaType contentType, String data) {
		logger.debug("URL: " + url);
		logger.debug("Method: " + method);
		logger.debug("Content Type: " + contentType);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(contentType);
		if (isValidAuthorizationHeader())
			headers.set("Authorization", getAuthorizationHeader());
		HttpEntity<String> request = new HttpEntity<String>(data, headers);

		return rt.exchange(url, method, request, String.class);
	}

	public ResponseEntity<String> execute(String url, HttpMethod method, MultiValueMap<String, String> data) {
		logger.debug("URL: " + url);
		logger.debug("Method: " + method);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		if (isValidAuthorizationHeader())
			headers.set("Authorization", getAuthorizationHeader());
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(data, headers);

		return rt.exchange(url, method, request, String.class);
	}

	public ResponseEntity<String> executeMultipartPost(String url, String fieldName, String filename, byte[] data) {
		return executeMultipart(url, HttpMethod.POST, fieldName, filename, data);
	}

	public ResponseEntity<String> executeMultipartPost(String url, String fieldName, String filename, String data) {
		return executeMultipart(url, HttpMethod.POST, fieldName, filename, data);
	}

	public ResponseEntity<String> executeMultipartPut(String url, String fieldName, String filename, String data) {
		return executeMultipart(url, HttpMethod.PUT, fieldName, filename, data);
	}

	public ResponseEntity<String> executeMultipart(String url, HttpMethod method, String fieldName, String filename, String data) {
		return executeMultipart(url, method, fieldName, filename, data.getBytes());
	}

	public ResponseEntity<String> executeMultipart(String url, HttpMethod method, String fieldName, String filename, byte[] data) {
		logger.debug("URL: " + url);
		logger.debug("Method: " + method);
		logger.debug("Content Type: " + MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> multipartMap = new LinkedMultiValueMap<String, Object>();
		multipartMap.add(fieldName, new FileMessageResource(data, filename));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		if (isValidAuthorizationHeader())
			headers.set("Authorization", getAuthorizationHeader());
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(multipartMap, headers);

		return rt.exchange(url, method, request, String.class);
	}

	public ResponseEntity<byte[]> executeMultipartAndGetBytes(String url, HttpMethod method, String fieldName, String filename, byte[] data) {
		logger.debug("URL: " + url);
		logger.debug("Method: " + method);
		logger.debug("Content Type: " + MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> multipartMap = new LinkedMultiValueMap<String, Object>();
		multipartMap.add(fieldName, new FileMessageResource(data, filename));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		if (isValidAuthorizationHeader())
			headers.set("Authorization", getAuthorizationHeader());
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(multipartMap, headers);

		return rt.exchange(url, method, request, byte[].class);
	}

	public ResponseEntity<String> executeMultipart(String url, HttpMethod method, String fieldName, List<FileMessageResource> resources) {
		logger.debug("URL: " + url);
		logger.debug("Method: " + method);
		logger.debug("Content Type: " + MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> multipartMap = new LinkedMultiValueMap<String, Object>();
		for (FileMessageResource fileMessageResource : resources) {
			multipartMap.add(fieldName, fileMessageResource);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		if (isValidAuthorizationHeader())
			headers.set("Authorization", getAuthorizationHeader());
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(multipartMap, headers);

		return rt.exchange(url, method, request, String.class);
	}

	public ResponseEntity<byte[]> downloadBinary(String url, HttpMethod method) {
		logger.debug("URL: " + url);
		logger.debug("Method: " + method);

		rt.getMessageConverters().add(new ByteArrayHttpMessageConverter());

		HttpHeaders headers = new HttpHeaders();
		if (isValidAuthorizationHeader())
			headers.set("Authorization", getAuthorizationHeader());

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		return rt.exchange(url, method, entity, byte[].class);
	}

	public void stream(String url, HttpMethod method, StreamProcessor processor) {
		logger.debug("URL: " + url);
		logger.debug("Method: " + method);
		StringResponseExtractor extractor = new StringResponseExtractor(processor);

		HttpHeaders headers = new HttpHeaders();
		if (isValidAuthorizationHeader())
			headers.set("Authorization", getAuthorizationHeader());

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		HttpEntityRequestCallback requestCallback = new HttpEntityRequestCallback(entity, String.class);

		rt.execute(url, method, requestCallback, extractor);
	}

	public ResponseEntity<String> executeDelete(String url) {
		return execute(url, HttpMethod.DELETE, MediaType.TEXT_PLAIN, null);
	}
	
	/**
	 * Request callback implementation that prepares the request's accept headers.
	 */
	private class AcceptHeaderRequestCallback implements RequestCallback {

		private final Class<?> responseType;

		private AcceptHeaderRequestCallback(Class<?> responseType) {
			this.responseType = responseType;
		}

		@SuppressWarnings("unchecked")
		public void doWithRequest(ClientHttpRequest request) throws IOException {
			if (responseType != null) {
				List<MediaType> allSupportedMediaTypes = new ArrayList<MediaType>();
				for (HttpMessageConverter<?> messageConverter : rt.getMessageConverters()) {
					if (messageConverter.canRead(responseType, null)) {
						List<MediaType> supportedMediaTypes = messageConverter.getSupportedMediaTypes();
						for (MediaType supportedMediaType : supportedMediaTypes) {
							if (supportedMediaType.getCharSet() != null) {
								supportedMediaType =
										new MediaType(supportedMediaType.getType(), supportedMediaType.getSubtype());
							}
							allSupportedMediaTypes.add(supportedMediaType);
						}
					}
				}
				if (!allSupportedMediaTypes.isEmpty()) {
					MediaType.sortBySpecificity(allSupportedMediaTypes);
					if (logger.isDebugEnabled()) {
						logger.debug("Setting request Accept header to " + allSupportedMediaTypes);
					}
					request.getHeaders().setAccept(allSupportedMediaTypes);
				}
			}
		}
	}


	/**
	 * Request callback implementation that writes the given object to the
	 * request stream.
	 */
	private class HttpEntityRequestCallback extends AcceptHeaderRequestCallback {

		private final HttpEntity requestEntity;

		private HttpEntityRequestCallback(Object requestBody) {
			this(requestBody, null);
		}

		@SuppressWarnings("unchecked")
		private HttpEntityRequestCallback(Object requestBody, Class<?> responseType) {
			super(responseType);
			if (requestBody instanceof HttpEntity) {
				this.requestEntity = (HttpEntity) requestBody;
			} else if (requestBody != null) {
				this.requestEntity = new HttpEntity(requestBody);
			} else {
				this.requestEntity = HttpEntity.EMPTY;
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public void doWithRequest(ClientHttpRequest httpRequest) throws IOException {
			super.doWithRequest(httpRequest);
			if (!requestEntity.hasBody()) {
				HttpHeaders httpHeaders = httpRequest.getHeaders();
				HttpHeaders requestHeaders = requestEntity.getHeaders();
				if (!requestHeaders.isEmpty()) {
					httpHeaders.putAll(requestHeaders);
				}
				if (httpHeaders.getContentLength() == -1) {
					httpHeaders.setContentLength(0L);
				}
			} else {
				Object requestBody = requestEntity.getBody();
				Class<?> requestType = requestBody.getClass();
				HttpHeaders requestHeaders = requestEntity.getHeaders();
				MediaType requestContentType = requestHeaders.getContentType();
				for (HttpMessageConverter messageConverter : rt.getMessageConverters()) {
					if (messageConverter.canWrite(requestType, requestContentType)) {
						if (!requestHeaders.isEmpty()) {
							httpRequest.getHeaders().putAll(requestHeaders);
						}
						if (logger.isDebugEnabled()) {
							if (requestContentType != null) {
								logger.debug("Writing [" + requestBody + "] as \"" + requestContentType + "\" using [" + messageConverter + "]");
							} else {
								logger.debug("Writing [" + requestBody + "] using [" + messageConverter + "]");
							}

						}
						messageConverter.write(requestBody, requestContentType, httpRequest);
						return;
					}
				}
				String message = "Could not write request: no suitable HttpMessageConverter found for request type [" + requestType.getName() + "]";
				if (requestContentType != null) {
					message += " and content type [" + requestContentType + "]";
				}
				throw new RestClientException(message);
			}
		}
	}

}
