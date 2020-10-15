package gov.usgs.aqcu.model.config;

import java.util.ArrayList;
import java.util.List;

import gov.usgs.aqcu.model.config.persist.GroupProperties;

public class GroupData {
	private String groupName;
	private List<String> folders;
	private GroupProperties properties;

	public GroupData() {
		folders = new ArrayList<>();
	}

	public List<String> getFolders() {
		return folders;
	}

	public GroupProperties getProperties() {
		return properties;
	}

	public void setProperties(GroupProperties properties) {
		this.properties = properties;
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