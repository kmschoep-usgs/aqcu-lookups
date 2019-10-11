package gov.usgs.aqcu.exception;

public class FolderAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FolderAlreadyExistsException(String group, String folder) {
		super(String.format("Specified folder '%1$s' already exists within group '%2$s'", folder, group));
	}
}