package gov.usgs.aqcu.retrieval;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import gov.usgs.aqcu.model.ReportBasicParameter;
import gov.usgs.aqcu.model.ReportParameterConfig;

@Repository
public class ReportParameterConfigLookupService {

	public ReportParameterConfig getReportParameterConfig(String reportType) {
        ReportParameterConfig reportParameterConfig = new ReportParameterConfig();
        if (reportType == "gw-vrstat") {
        	reportParameterConfig.setReportType(reportType);
        	reportParameterConfig.setParameters(buildGWVRStatParameters());
        }
        
		return reportParameterConfig;
	}
	
	private List<ReportBasicParameter> buildGWVRStatParameters() {
		List<ReportBasicParameter> parameters = new ArrayList<>();
		parameters.add(new ReportBasicParameter("locationIdentifier", "Primary Location", "location"));
		return parameters;
	}
}
