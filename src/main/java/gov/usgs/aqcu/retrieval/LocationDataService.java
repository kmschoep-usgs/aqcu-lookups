package gov.usgs.aqcu.retrieval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceResponse;

@Repository
public class LocationDataService {
	private static final Logger LOG = LoggerFactory.getLogger(LocationDataService.class);	

	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public LocationDataService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

	public LocationDataServiceResponse getRawResponse(String locationIdentifier) {
		LocationDataServiceRequest request = new LocationDataServiceRequest()
				.setLocationIdentifier(locationIdentifier);
        LocationDataServiceResponse locationData = aquariusRetrievalService.executePublishApiRequest(request);
		return locationData;
	}
}
