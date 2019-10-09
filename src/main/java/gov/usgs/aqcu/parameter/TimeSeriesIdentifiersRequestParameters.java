package gov.usgs.aqcu.parameter;

import javax.validation.constraints.NotBlank;

public class TimeSeriesIdentifiersRequestParameters {
	@NotBlank
	private String stationId;
	
	private Boolean publish;
	private Boolean primary;
	private String parameter;
	private String computationIdentifier;
	private String computationPeriodIdentifier;

	public static Boolean DEFAULT_PUBLISH = false;
	public static Boolean DEFAULT_PRIMARY = false;

	public String getStationId() {
		return stationId;
	}
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	public Boolean getPublish() {
		return publish;
	}
	public void setPublish(Boolean publish) {
		this.publish = publish != null ? publish : DEFAULT_PUBLISH;
	}
	public Boolean getPrimary() {
		return primary != null ? primary : DEFAULT_PRIMARY;
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
