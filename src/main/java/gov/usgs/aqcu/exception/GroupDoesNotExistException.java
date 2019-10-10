package gov.usgs.aqcu.exception;

public class GroupDoesNotExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GroupDoesNotExistException(String group) {
		super(String.format("Specified group '%1$s' does not exist.", group));
	}
}