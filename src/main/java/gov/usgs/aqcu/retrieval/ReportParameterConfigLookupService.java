package gov.usgs.aqcu.retrieval;

import org.springframework.stereotype.Repository;
import gov.usgs.aqcu.model.report.ReportParameterConfig;

@Repository
public class ReportParameterConfigLookupService {

	public ReportParameterConfig getByReportType(String reportType){
		return ReportParameterConfig.getByReportType(reportType);
	}
}
