package gov.usgs.aqcu.exception;

public class GroupAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GroupAlreadyExistsException(String folder) {
		super("Specified group '" + folder + "' alread exists");
	}
}