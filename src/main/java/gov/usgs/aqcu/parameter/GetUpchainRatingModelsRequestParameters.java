package gov.usgs.aqcu.parameter;

import javax.validation.constraints.NotBlank;

public class GetUpchainRatingModelsRequestParameters extends DateRangeRequestParameters {
	@NotBlank
	protected String timeSeriesIdentifier;
	protected Boolean fullChain;

	public static final Boolean DEFAULT_FULL_CHAIN = true;

	public String getTimeSeriesIdentifier() {
		return timeSeriesIdentifier;
	}
	public void setTimeSeriesIdentifier(String timeSeriesIdentifier) {
		this.timeSeriesIdentifier = timeSeriesIdentifier;
	}
	public Boolean getFullChain() {
		return fullChain != null ? fullChain : DEFAULT_FULL_CHAIN;
	}
	public void setFullChain(Boolean fullChain) {
		this.fullChain = fullChain;
	}
}
