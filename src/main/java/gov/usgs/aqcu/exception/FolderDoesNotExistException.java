package gov.usgs.aqcu.exception;

public class FolderDoesNotExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FolderDoesNotExistException(String group, String folder) {
		super(String.format("Specified folder '%1$s' does not exist within group '%2$s'", folder, group));
	}
}