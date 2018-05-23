package gov.usgs.aqcu.model;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescription;

public class LocationBasicData {
	private String siteNumber;
	private String siteName;

	public LocationBasicData(LocationDescription desc) {
		this.siteNumber = desc.getIdentifier();
		this.siteName = desc.getName();
	}
	
	public String getSiteNumber() {
		return siteNumber;
	}
	public String getSiteName() {
		return siteName;
	}

	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
}