package gov.usgs.aqcu.exception;

public class ReportDoesNotExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ReportDoesNotExistException(String group, String folder, String reportId) {
		super("Report with ID '" + reportId + "' does not exist within folder '" + folder + "' of group '" + group + "'");
	}
}