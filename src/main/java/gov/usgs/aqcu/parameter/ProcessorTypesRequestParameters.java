package gov.usgs.aqcu.parameter;

import javax.validation.constraints.NotBlank;

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
