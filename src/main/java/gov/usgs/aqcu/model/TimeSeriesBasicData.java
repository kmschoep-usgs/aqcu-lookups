package gov.usgs.aqcu.model;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import gov.usgs.aqcu.util.TimeSeriesUtils;

public class TimeSeriesBasicData {
	private String identifier;
	private String uniqueId;
	private String period;
	private String parameter;
	private String description;
	private String timeSeriesType;
	private String units;
	private String computation;
	private Boolean publish;
	private Boolean primary;

	public TimeSeriesBasicData(TimeSeriesDescription desc) {
		this.identifier = desc.getIdentifier();
		this.uniqueId = desc.getUniqueId();
		this.period = desc.getComputationPeriodIdentifier();
		this.parameter = desc.getParameter();
		this.description = desc.getDescription();
		this.timeSeriesType = desc.getTimeSeriesType();
		this.units = desc.getUnit();
		this.computation = desc.getComputationIdentifier();
		this.publish = desc.isPublish();
		this.primary = TimeSeriesUtils.isPrimaryTimeSeries(desc);
	}
	
	public String getIdentifier() {
		return identifier;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public String getPeriod() {
		return period;
	}
	public String getParameter() {
		return parameter;
	}
	public String getDescription() {
		return description;
	}
	public String getTimeSeriesType() {
		return timeSeriesType;
	}
	public String getUnits() {
		return units;
	}
	public String getComputation() {
		return computation;
	}
	public Boolean getPublish() {
		return publish;
	}
	public Boolean getPrimary() {
		return primary;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setTimeSeriesType(String timeSeriesType) {
		this.timeSeriesType = timeSeriesType;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public void setComputation(String computation) {
		this.computation = computation;
	}
	public void setPublish(Boolean publish) {
		this.publish = publish;
	}
	public void setPrimary(Boolean primary) {
		this.primary = primary;
	}
}