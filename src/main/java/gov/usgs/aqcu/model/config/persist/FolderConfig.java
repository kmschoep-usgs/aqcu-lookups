package gov.usgs.aqcu.model.config.persist;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FolderConfig {
	private FolderProperties properties;
	private Map<String, SavedReportConfiguration> savedReports;

	public FolderConfig() {
		properties = new FolderProperties();
		savedReports = new HashMap<>();
	}

	public FolderProperties getProperties() {
		return properties;
	}

	public void setProperties(FolderProperties properties) {
		this.properties = properties;
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
}