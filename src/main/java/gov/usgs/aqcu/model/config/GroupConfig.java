package gov.usgs.aqcu.model.config;

import java.util.List;

public class GroupConfig {
	private List<String> authorizedUsers;

	public List<String> getAuthorizedUsers() {
		return authorizedUsers;
	}

	public void setAuthorizedUsers(List<String> authorizedUsers) {
		this.authorizedUsers = authorizedUsers;
	}
}