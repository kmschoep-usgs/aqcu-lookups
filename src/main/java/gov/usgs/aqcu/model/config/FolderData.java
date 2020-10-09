package gov.usgs.aqcu.model.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.usgs.aqcu.model.config.persist.SavedReportConfiguration;

public class FolderData {
	private String groupName;
	private String folderName;
	private String currentPath;
	private List<String> folders;
	private Map<String, String> parameterDefaults;
	private List<SavedReportConfiguration> reports;

	public FolderData() {
		folders = new ArrayList<>();
		reports = new ArrayList<>();
		parameterDefaults = new HashMap<>();
	}

	public String getCurrentPath() {
		return currentPath;
	}

	public List<SavedReportConfiguration> getReports() {
		return reports;
	}

	public void setReports(List<SavedReportConfiguration> reports) {
		this.reports = reports;
	}

	public Map<String, String> getParameterDefaults() {
		return parameterDefaults;
	}

	public void setParameterDefaults(Map<String, String> parameterDefaults) {
		this.parameterDefaults = parameterDefaults;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<String> getFolders() {
		return folders;
	}

	public void setFolders(List<String> folders) {
		this.folders = folders;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
}