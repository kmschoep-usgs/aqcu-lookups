package gov.usgs.aqcu.model.report;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class SavedReportConfiguration {

	private String id;

	@NotBlank
	private String reportName;

	@NotBlank
	private String reportType;
	private String lastModifiedUser;
	private String createdUser;

	@NotBlank
	private String primaryParameter;

	@NotEmpty
	private Map<String, String> parameterValues;

	public String getReportName() {
		return reportName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrimaryParameter() {
		return primaryParameter;
	}

	public void setPrimaryParameter(String primaryParameter) {
		this.primaryParameter = primaryParameter;
	}

	public Map<String, String> getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(Map<String, String> parameterValues) {
		this.parameterValues = parameterValues;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public String getLastModifiedUser() {
		return lastModifiedUser;
	}

	public void setLastModifiedUser(String lastModifiedUser) {
		this.lastModifiedUser = lastModifiedUser;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

}