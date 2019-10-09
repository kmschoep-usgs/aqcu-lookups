package gov.usgs.aqcu.model.config;

import java.util.ArrayList;
import java.util.List;

public class GroupData {
	private String groupName;
	private List<String> folders;
	private GroupConfig config;

	public GroupData() {
		folders = new ArrayList<>();
	}

	public List<String> getFolders() {
		return folders;
	}

	public GroupConfig getConfig() {
		return config;
	}

	public void setConfig(GroupConfig config) {
		this.config = config;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setFolders(List<String> folders) {
		this.folders = folders;
	}
}