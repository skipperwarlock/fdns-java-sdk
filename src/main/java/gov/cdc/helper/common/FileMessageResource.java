package gov.cdc.helper.common;

import org.springframework.core.io.ByteArrayResource;

public class FileMessageResource extends ByteArrayResource {

	/**
	 * The filename to be associated with the {@link MimeMessage} in the form
	 * data.
	 */
	private final String filename;

	/**
	 * Constructs a new {@link FileMessageResource}.
	 * 
	 * @param byteArray
	 *            A byte array containing data from a {@link MimeMessage}.
	 * @param filename
	 *            The filename to be associated with the {@link MimeMessage} in
	 *            the form data.
	 */
	public FileMessageResource(byte[] byteArray, String filename) {
		super(byteArray);
		this.filename = filename;
	}

	/**
	 * Returns filename associated with the {@Link MimeMessage} in the form data
	 * @return filename associated with the {@Link MimeMessage} in the form data
	 */
	@Override
	public String getFilename() {
		return filename;
	}

	/**
	 * @param obj
	 * @return true if obj is FileMessageResource with equal filename (ignoring case) or true if obj is ByteArrayResource with equal byteArray
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FileMessageResource) {
			FileMessageResource objFMR = (FileMessageResource) obj;
			return objFMR.getFilename().equalsIgnoreCase(getFilename());
		} else
			return super.equals(obj);
	}

	/**
	 * @return a hash code value for this object
	 */
	@Override
	public int hashCode() {
		return super.hashCode() + getFilename().hashCode();
	}
}