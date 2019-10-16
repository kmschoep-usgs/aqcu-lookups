package gov.usgs.aqcu.model.report;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ReportParameterConfig {

    GW_VRSTAT("gwvisitreviewstatus", "GW Visit Review Status", ServiceType.LAMBDA,
            Arrays.asList(new ReportBasicParameter("locationIdentifier", "Primary Location", "location")));

    private String reportType;
    private String reportTypeDisplayName;
    private ServiceType serviceType;
    private List<ReportBasicParameter> parameters;

    ReportParameterConfig(String reportType, String reportTypeDisplayName, ServiceType serviceType,
            List<ReportBasicParameter> parameters) {
        this.reportType = reportType;
        this.reportTypeDisplayName = reportTypeDisplayName;
        this.parameters = parameters;
        this.serviceType = serviceType;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public String getReportType() {
    	return this.reportType;
    }
    
    public List<ReportBasicParameter> getParameters() {
    	return this.parameters;
    }

    public String getReportTypeDisplayName() {
		return reportTypeDisplayName;
	}

	public static ReportParameterConfig getByReportType(String reportType) {
        for(ReportParameterConfig config : values()) {
            if(config.getReportType().equals(reportType)) return config; 
        }
        return null;
    }
	
	public static String getReportTypes() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(ReportParameterConfig.values());
    }
    
    public enum ServiceType {
        LAMBDA, SERVICE;
    }
}
