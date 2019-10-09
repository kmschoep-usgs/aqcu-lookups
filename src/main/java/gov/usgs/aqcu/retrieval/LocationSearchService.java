package gov.usgs.aqcu.retrieval;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.usgs.aqcu.aquarius.Apps.SearchLocationsServiceRequest;
import gov.usgs.aqcu.aquarius.Apps.SearchLocationsServiceResponse;
import gov.usgs.aqcu.model.lookup.LocationBasicData;

import gov.usgs.aqcu.util.LogExecutionTime;

@Repository
public class LocationSearchService {
	private AquariusAppsRetrievalService aquariusAppsRetrievalService;

	@Autowired
	public LocationSearchService(AquariusAppsRetrievalService aquariusAppsRetrievalService) {
		this.aquariusAppsRetrievalService = aquariusAppsRetrievalService;
	}

        @LogExecutionTime
	protected SearchLocationsServiceResponse getRawResponse(String queryString, Integer pageSize) {
		SearchLocationsServiceRequest request = new SearchLocationsServiceRequest()
            .setQueryString(queryString)
            .setPageSize(pageSize);
        SearchLocationsServiceResponse locationData = aquariusAppsRetrievalService.executeAppsApiRequest(request);
		return locationData;
    }
    
    @LogExecutionTime
    public List<LocationBasicData> searchSites(String queryString, Integer pageSize) {
        return getRawResponse(queryString, pageSize).getResults().stream()
            .map(r -> new LocationBasicData(r))
            .collect(Collectors.toList());
    }
}
