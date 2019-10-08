package gov.usgs.aqcu.model.report;

import java.util.Arrays;
import java.util.List;

public enum ReportParameterConfig {

	// Surface Water
	CORRECTIONS_AT_A_GLANCE("correctionsataglance", null),
	DERIVATION_CHAIN("derivationchain", null),
	DV_HYDROGRAPH("dvhydrograph", null),
	EXTREMES("extremes", null),
	FIVE_YEAR_GW_SUMMARY("fiveyeargwsum", null),
	SENSOR_READING_SUMMARY("sensorreadingsummary", null),
	SITE_VISIT_PEAK("sitevisitpeak", null),
	TIME_SERIES_SUMMARY("timeseriessummary", null),
	UV_HYDROGRAPH("uvhydrograph", null),
	V_DIAGRAM("vdiagram", null),

	// Ground Water
	GW_VISIT_REVIEW_STATUS("gwvisitreviewstatus", Arrays.asList(
		new ReportBasicParameter("locationIdentifier", "Primary Location", "location")
	)),
	GW_VISIT_READING_EXCEEDS_THRESHOLDS("gwvret", null),
	GW_VISIT_EXTREMES("gwvext", null);

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