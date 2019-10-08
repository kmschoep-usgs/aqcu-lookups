package gov.usgs.aqcu.model.config;

import java.util.List;

public class FolderData {
	private String groupName;
	private String currentPath;
	private List<String> folders;
	private ReportsConfig reports;

	public String getCurrentPath() {
		return currentPath;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public ReportsConfig getReports() {
		return reports;
	}

	public void setReports(ReportsConfig reports) {
		this.reports = reports;
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
}