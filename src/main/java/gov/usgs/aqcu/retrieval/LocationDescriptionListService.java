package gov.usgs.aqcu.retrieval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescriptionListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescriptionListServiceResponse;

@Repository
public class LocationDescriptionListService {
	private static final Logger LOG = LoggerFactory.getLogger(LocationDescriptionListService.class);	

	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public LocationDescriptionListService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

	public LocationDescriptionListServiceResponse getRawResponse(String locationName, String locationIdentifier) {
		LocationDescriptionListServiceRequest request = new LocationDescriptionListServiceRequest()
				.setLocationName(locationName)
				.setLocationIdentifier(locationIdentifier);
		LocationDescriptionListServiceResponse locationList = aquariusRetrievalService.executePublishApiRequest(request);
		return locationList;
	}

	public List<LocationDescription> searchSites(String searchString, Integer maxResults) {
		if(searchString != null) {
			Set<LocationDescription> locationSet = new HashSet<>();

			//Add partial name matching charatcers to the start and end of the trimmed search string
			searchString = "*" + searchString.trim() + "*";

			//1. Search by location identifier first
			locationSet.addAll(getRawResponse(null, searchString).getLocationDescriptions());

			//2. If we haven't reached max results then search by location name
			if(locationSet.size() < maxResults) {
				locationSet.addAll(getRawResponse(searchString, null).getLocationDescriptions());
			}

			//Return up to maxResults results
			List<LocationDescription> locationSetList = new ArrayList<>(locationSet);
			if(locationSetList.size() <= maxResults) {
				return locationSetList;
			} else {
				return locationSetList.subList(0, maxResults);
			}
		}

		return new ArrayList<>();
	}
}
