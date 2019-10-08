package gov.usgs.aqcu.exception;

public class FolderDoesNotExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FolderDoesNotExistException(String group, String folder) {
		super("Specified folder '" + folder + "' does not exist within group '" + group + "'");
	}
}