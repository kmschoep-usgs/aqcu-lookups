package gov.usgs.aqcu.model;

import java.util.List;

public class ReportParameterConfig {
	private String reportType;
	private List<ReportBasicParameter> parameters;

	public ReportParameterConfig(){}
	
	public ReportParameterConfig(String reportType, List<ReportBasicParameter> parameters) {
		this.setReportType(reportType);
		this.setParameters(parameters);
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public List<ReportBasicParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<ReportBasicParameter> parameters) {
		this.parameters = parameters;
	}

		
	
}