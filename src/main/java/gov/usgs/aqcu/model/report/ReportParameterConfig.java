package gov.usgs.aqcu.model.report;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ReportParameterConfig {
    
    GW_VRSTAT("gwvrstatreport", Arrays.asList(
    	new ReportBasicParameter("locationIdentifier", "Primary Location", "location")
    ));

	private String reportType;
	private List<ReportBasicParameter> parameters;
	
    ReportParameterConfig(String reportType, List<ReportBasicParameter> parameters) {
        this.reportType = reportType;
        this.parameters = parameters;
    }

    public String getReportType() {
    	return this.reportType;
    }
    
    public List<ReportBasicParameter> getParameters() {
    	return this.parameters;
    }

    public static ReportParameterConfig getByReportType(String reportType) {
        for(ReportParameterConfig config : values()) {
            if(config.getReportType().equals(reportType)) return config; 
        }
        return null;
    }
}