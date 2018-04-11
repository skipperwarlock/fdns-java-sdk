package gov.cdc.helper;

public class AbstractHelper {

	private String authorizationHeader = null;

	public String getAuthorizationHeader() {
		return authorizationHeader;
	}

	public AbstractHelper setAuthorizationHeader(String authorizationHeader) {
		this.authorizationHeader = authorizationHeader;
		return this;
	}
	
	public boolean isValidAuthorizationHeader() {
		return authorizationHeader != null && (authorizationHeader.startsWith("Bearer") || authorizationHeader.startsWith("bearer") || authorizationHeader.startsWith("basic") || authorizationHeader.startsWith("Basic"));
	}
	
	
}
