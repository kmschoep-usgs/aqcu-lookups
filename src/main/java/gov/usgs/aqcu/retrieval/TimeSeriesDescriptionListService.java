package gov.usgs.aqcu.retrieval;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListByUniqueIdServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListByUniqueIdServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ExtendedAttributeFilter;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;

import gov.usgs.aqcu.exception.AquariusProcessingException;
import gov.usgs.aqcu.util.AquariusRetrievalUtils;
import gov.usgs.aqcu.util.LogExecutionTime;

@Repository
public class TimeSeriesDescriptionListService {
	private static final Logger LOG = LoggerFactory.getLogger(TimeSeriesDescriptionListService.class);	

	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public TimeSeriesDescriptionListService(
		AquariusRetrievalService aquariusRetrievalService
	) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

        @LogExecutionTime
	protected TimeSeriesDescriptionListByUniqueIdServiceResponse getRawResponse(List<String> timeSeriesUniqueIds) {
		TimeSeriesDescriptionListByUniqueIdServiceRequest request = new TimeSeriesDescriptionListByUniqueIdServiceRequest()
				.setTimeSeriesUniqueIds(new ArrayList<>(new HashSet<>(timeSeriesUniqueIds)));
		TimeSeriesDescriptionListByUniqueIdServiceResponse tssDesc = aquariusRetrievalService.executePublishApiRequest(request);
		return tssDesc;
	}

        @LogExecutionTime
	protected TimeSeriesDescriptionListServiceResponse getRawResponse(String computationIdentifier, String computationPeriodIdentifier, 
	String stationId, String parameter, Boolean publish, Boolean primary) {
		ArrayList<ExtendedAttributeFilter> extendedFilters = new ArrayList<>();

		if(primary) {
			extendedFilters.add(AquariusRetrievalUtils.getPrimaryFilter());
		}
		
		TimeSeriesDescriptionServiceRequest request = new TimeSeriesDescriptionServiceRequest()
				.setComputationIdentifier(computationIdentifier)
				.setComputationPeriodIdentifier(computationPeriodIdentifier)
				.setLocationIdentifier(stationId)
				.setParameter(parameter)
				.setPublish(publish)
				.setExtendedFilters(extendedFilters);
		TimeSeriesDescriptionListServiceResponse tssDesc = aquariusRetrievalService.executePublishApiRequest(request);
		return tssDesc;
	}

        @LogExecutionTime
	public List<TimeSeriesDescription> getTimeSeriesDescriptionList(List<String> timeSeriesUniqueIds) {
		LOG.debug("Get time series description list.");
                List<TimeSeriesDescription> descList = getRawResponse(timeSeriesUniqueIds).getTimeSeriesDescriptions();

		if(descList.size() != timeSeriesUniqueIds.size()) {
			String errorString = "Failed to fetch descriptions for all requested Time Series Identifiers: \nRequested: " + timeSeriesUniqueIds.size() + 
				"\nReceived: "  + descList.size();
			LOG.error(errorString);
			throw new AquariusProcessingException(errorString);
		}
		return descList;
	}

        @LogExecutionTime
	public List<TimeSeriesDescription> getTimeSeriesDescriptionList(String computationIdentifier, String computationPeriodIdentifier, 
			String stationId, String parameter, Boolean publish, Boolean primary) {
		return getRawResponse(computationIdentifier, computationPeriodIdentifier, stationId, parameter, publish, primary).getTimeSeriesDescriptions();
	}

        @LogExecutionTime
	public TimeSeriesDescription getTimeSeriesDescription(String timeSeriesUniqueId) {
		return getTimeSeriesDescriptionList(Arrays.asList(timeSeriesUniqueId)).get(0);
	}
}
