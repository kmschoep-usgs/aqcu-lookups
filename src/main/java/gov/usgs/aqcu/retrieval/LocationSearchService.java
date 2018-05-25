package gov.usgs.aqcu.retrieval;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.usgs.aqcu.aquarius.Apps.SearchLocationsServiceRequest;
import gov.usgs.aqcu.aquarius.Apps.SearchLocationsServiceResponse;
import gov.usgs.aqcu.model.LocationBasicData;


@Repository
public class LocationSearchService {
	private static final Logger LOG = LoggerFactory.getLogger(LocationSearchService.class);	

	private AquariusAppsRetrievalService aquariusAppsRetrievalService;

	@Autowired
	public LocationSearchService(AquariusAppsRetrievalService aquariusAppsRetrievalService) {
		this.aquariusAppsRetrievalService = aquariusAppsRetrievalService;
	}

	protected SearchLocationsServiceResponse getRawResponse(String queryString, Integer pageSize) {
		SearchLocationsServiceRequest request = new SearchLocationsServiceRequest()
            .setQueryString(queryString)
            .setPageSize(pageSize);
        SearchLocationsServiceResponse locationData = aquariusAppsRetrievalService.executeAppsApiRequest(request);
		return locationData;
    }
    
    public List<LocationBasicData> searchSites(String queryString, Integer pageSize) {
        return getRawResponse(queryString, pageSize).getResults().stream()
            .map(r -> new LocationBasicData(r))
            .collect(Collectors.toList());
    }
}
