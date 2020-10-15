package gov.usgs.aqcu.model.config;

import java.util.ArrayList;
import java.util.List;

import gov.usgs.aqcu.model.config.persist.FolderProperties;
import gov.usgs.aqcu.model.config.persist.SavedReportConfiguration;

public class FolderData {
	private String groupName;
	private String folderName;
	private String currentPath;
	private List<String> folders;
	private FolderProperties properties;
	private List<SavedReportConfiguration> reports;

	public FolderData() {
		folders = new ArrayList<>();
		reports = new ArrayList<>();
	}

	public FolderProperties getProperties() {
		return properties;
	}

	public void setProperties(FolderProperties properties) {
		this.properties = properties;
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