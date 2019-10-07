package gov.usgs.aqcu.model;

import java.util.List;
import java.util.Map;

public class S3DirectoryConfig {
    private List<String> authorizedUsers;
    private Map<String, String> reportDefaults;

    public List<String> getAuthorizedUsers() {
        return authorizedUsers;
    }

    public Map<String, String> getReportDefaults() {
        return reportDefaults;
    }

    public void setReportDefaults(Map<String, String> reportDefaults) {
        this.reportDefaults = reportDefaults;
    }

    public void setAuthorizedUsers(List<String> authorizedUsers) {
        this.authorizedUsers = authorizedUsers;
    }
}