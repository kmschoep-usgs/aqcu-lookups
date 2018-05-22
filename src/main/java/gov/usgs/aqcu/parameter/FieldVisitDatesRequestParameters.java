package gov.usgs.aqcu.parameter;

import org.hibernate.validator.constraints.NotBlank;

public class FieldVisitDatesRequestParameters {
	@NotBlank
	protected String siteNumber;

	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
	}
	public String getSiteNumber() {
		return siteNumber;
	}
}
