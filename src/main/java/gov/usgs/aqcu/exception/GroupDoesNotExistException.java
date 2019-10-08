package gov.usgs.aqcu.exception;

public class GroupDoesNotExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GroupDoesNotExistException(String group) {
		super("Specified group '" + group + "' does not exist");
	}
}