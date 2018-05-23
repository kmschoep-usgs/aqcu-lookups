package gov.usgs.aqcu.parameter;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

public class FindInDerivationChainRequestParameters extends DateRangeRequestParameters {
	@NotBlank
	protected String timeSeriesIdentifier;
	@NotBlank
	@Pattern(regexp = "upchain|downchain", flags = Pattern.Flag.CASE_INSENSITIVE)
	protected String direction;
	protected String parameter;
	protected Boolean primary;
	protected String computationIdentifier;
	protected String computationPeriodIdentifier;
	protected Boolean fullChain;

	public static final Boolean DEFAULT_FULL_CHAIN = false;

	public String getTimeSeriesIdentifier() {
		return timeSeriesIdentifier;
	}
	public void setTimeSeriesIdentifier(String timeSeriesIdentifier) {
		this.timeSeriesIdentifier = timeSeriesIdentifier;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public Boolean getFullChain() {
		return fullChain != null ? fullChain : DEFAULT_FULL_CHAIN;
	}
	public void setFullChain(Boolean fullChain) {
		this.fullChain = fullChain;
	}
	public Boolean getPrimary() {
		return primary;
	}
	public void setPrimary(Boolean primary) {
		this.primary = primary;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public String getComputationIdentifier() {
		return computationIdentifier;
	}
	public void setComputationIdentifier(String computationIdentifier) {
		this.computationIdentifier = computationIdentifier;
	}
	public String getComputationPeriodIdentifier() {
		return computationPeriodIdentifier;
	}
	public void setComputationPeriodIdentifier(String computationPeriodIdentifier) {
		this.computationPeriodIdentifier = computationPeriodIdentifier;
	}
}
