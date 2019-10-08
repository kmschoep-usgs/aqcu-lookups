package gov.usgs.aqcu.exception;

public class InvalidFolderNameException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public InvalidFolderNameException(String folderName) {
		super("'" + folderName + "' is not a valid folder name. Folder names cannot be empty, contain multiple '/' characters, or contain any of the following characters: \\ ' \" . , * & % $");
	}

}