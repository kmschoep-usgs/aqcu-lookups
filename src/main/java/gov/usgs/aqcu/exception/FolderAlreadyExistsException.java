package gov.usgs.aqcu.exception;

public class FolderAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FolderAlreadyExistsException(String group, String folder) {
		super("Specified folder '" + folder + "' alread exists within group '" + group + "'");
	}
}