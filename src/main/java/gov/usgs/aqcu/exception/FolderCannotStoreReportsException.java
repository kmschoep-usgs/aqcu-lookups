package gov.usgs.aqcu.exception;

public class FolderCannotStoreReportsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FolderCannotStoreReportsException(String group, String folder) {
		super(String.format("Specified folder '%1$s' within group '%2$s' has report storage disabled.", folder, group));
	}
}