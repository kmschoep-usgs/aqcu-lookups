package gov.usgs.aqcu.exception;

public class GroupAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GroupAlreadyExistsException(String group) {
		super(String.format("Specified group '%1$s' already exists.", group));
	}
}