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

	/**
	 * Returns <code>true</code> if authorization header starts with 'bearer' or 'basic'.
	 *
	 * @return <code>true</code> if authorization header starts with 'bearer' or 'basic';
	 *         <code>false</code> otherwise
	 */
	public boolean isValidAuthorizationHeader() {
		return authorizationHeader != null && (authorizationHeader.startsWith("Bearer") || authorizationHeader.startsWith("bearer") || authorizationHeader.startsWith("basic") || authorizationHeader.startsWith("Basic"));
	}


}
