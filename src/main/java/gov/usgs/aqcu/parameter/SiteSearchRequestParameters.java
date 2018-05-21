package gov.usgs.aqcu.parameter;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class SiteSearchRequestParameters {
	protected Integer pageSize;
	@NotBlank
	@Size(min=3, message="You must specify at least 3 characters in your search.")
	protected String siteNumber;

	public static Integer DEFAULT_PAGE_SIZE = 10;

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getPageSize() {
		return pageSize != null ? pageSize : DEFAULT_PAGE_SIZE;
	}
	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber.trim();
	}
	public String getSiteNumber() {
		return siteNumber;
	}
}
