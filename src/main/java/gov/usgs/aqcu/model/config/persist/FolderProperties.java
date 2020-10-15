package gov.usgs.aqcu.model.config.persist;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FolderProperties {
    private boolean canStoreReports;
    private Map<String, String> parameterDefaults;

    public FolderProperties() {
        canStoreReports = false;
        parameterDefaults = new HashMap<>();
    }

    public boolean getCanStoreReports() {
        return canStoreReports;
    }

    public Map<String, String> getParameterDefaults() {
        return parameterDefaults;
    }

    public void setParameterDefaults(Map<String, String> parameterDefaults) {
        this.parameterDefaults = parameterDefaults;
    }

    public void setCanStoreReports(boolean canStoreReports) {
        this.canStoreReports = canStoreReports;
    }
}
