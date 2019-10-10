package gov.usgs.aqcu.exception;

public class ReportDoesNotExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ReportDoesNotExistException(String group, String folder, String reportId) {
		super(String.format("Specified report with ID '%1$s' does not exist within folder '%2$s' of group '%3$s'", reportId, folder, group));
	}
}