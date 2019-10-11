package gov.usgs.aqcu.exception;

public class ReportAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ReportAlreadyExistsException(String group, String folder, String reportId) {
		super(String.format("Specified report with ID '%1$s' already exists within folder '%2$s' of group '%3$s'", reportId, folder, group));
	}
}