package gov.usgs.aqcu.model.lookup;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescription;

import gov.usgs.aqcu.aquarius.Apps.LocationSearchResult;

public class LocationBasicData {
	private String siteNumber;
	private String siteName;

	public LocationBasicData(){};

	public LocationBasicData(String siteNumber, String siteName) {
		this.siteNumber = siteNumber;
		this.siteName = siteName;
	}

	public LocationBasicData(LocationDescription desc) {
		this.siteNumber = desc.getIdentifier();
		this.siteName = desc.getName();
	}

	public LocationBasicData(LocationSearchResult res) {
		this.siteNumber = res.getIdentifier();
		this.siteName = res.getName();
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