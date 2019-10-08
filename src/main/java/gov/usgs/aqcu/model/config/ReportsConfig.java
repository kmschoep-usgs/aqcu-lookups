package gov.usgs.aqcu.model.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

	public List<SavedReportConfiguration> getSavedReportsList() {
		return new ArrayList<>(savedReports.values());
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