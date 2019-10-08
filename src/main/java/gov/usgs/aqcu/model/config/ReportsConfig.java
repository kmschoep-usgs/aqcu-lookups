package gov.usgs.aqcu.model.config;

import java.util.HashMap;
import java.util.Map;

import gov.usgs.aqcu.model.report.SavedReportConfiguration;

public class ReportsConfig {
	private Map<String, String> parameterDefaults;
	private Map<String, SavedReportConfiguration> savedReports;

	public ReportsConfig() {
		savedReports = new HashMap<>();
	}

	public Map<String, SavedReportConfiguration> getSavedReports() {
		return savedReports;
	}

	public void setSavedReports(Map<String, SavedReportConfiguration> savedReports) {
		this.savedReports = savedReports;
	}

	public void saveReport(SavedReportConfiguration report) {
		savedReports.put(report.getId(), report);
	}

	public SavedReportConfiguration getSavedReportById(String id) {
		return savedReports.get(id);
	}

	public void deleteSavedReportById(String id) {
		savedReports.put(id, null);
	}

	public Map<String, String> getParameterDefaults() {
		return parameterDefaults;
	}

	public void setParameterDefaults(Map<String, String> parameterDefaults) {
		this.parameterDefaults = parameterDefaults;
	}
}