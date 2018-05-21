package gov.usgs.aqcu.parameter;

import org.hibernate.validator.constraints.NotBlank;

public class ProcessorTypesRequestParameters extends DateRangeRequestParameters {
	@NotBlank
	protected String timeSeriesIdentifier;

	public void setTimeSeriesIdentifier(String timeSeriesIdentifier) {
		this.timeSeriesIdentifier = timeSeriesIdentifier;
	}
	public String getTimeSeriesIdentifier() {
		return timeSeriesIdentifier;
	}
}
