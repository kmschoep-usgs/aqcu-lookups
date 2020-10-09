package gov.usgs.aqcu.model.config.persist;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FolderConfig {
	private Map<String, String> parameterDefaults;
	private Map<String, SavedReportConfiguration> savedReports;

	public FolderConfig() {
		parameterDefaults = new HashMap<>();
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

	public Boolean deleteSavedReportById(String id) {
		if(savedReports.containsKey(id)) {
			savedReports.remove(id);
			return true;
		}

		return false;		
	}

	public Boolean doesReportExist(String id) {
		return savedReports.containsKey(id);
	}

	public Map<String, String> getParameterDefaults() {
		return parameterDefaults;
	}

	public void setParameterDefaults(Map<String, String> parameterDefaults) {
		this.parameterDefaults = parameterDefaults;
	}
}