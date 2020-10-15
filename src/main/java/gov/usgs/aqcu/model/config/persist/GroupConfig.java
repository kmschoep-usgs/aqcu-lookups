package gov.usgs.aqcu.model.config.persist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupConfig {
	private GroupProperties groupProperties;

	public GroupConfig() {
		setGroupProperties(new GroupProperties());
	}

	public GroupProperties getGroupProperties() {
		return groupProperties;
	}

	public void setGroupProperties(GroupProperties groupProperties) {
		this.groupProperties = groupProperties;
	}
}