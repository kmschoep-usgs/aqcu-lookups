package gov.usgs.aqcu.exception;

public class ReportAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ReportAlreadyExistsException(String group, String folder, String reportId) {
		super("Report with ID '" + reportId + "' already exists within folder '" + folder + "' of group '" + group + "'");
	}
}