package gov.usgs.aqcu.retrieval;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.usgs.aqcu.model.ReportParameterConfig;

@Repository
public class ReportParameterConfigLookupService {

	public ReportParameterConfig getByReportType(String reportType){
		return ReportParameterConfig.getByReportType(reportType);
	}
	
	public String getReportTypes() throws JsonProcessingException{
		return ReportParameterConfig.getReportTypes();
	}
}
