package gov.usgs.aqcu.retrieval;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Processor;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.usgs.aqcu.parameter.GetUpchainRatingModelsRequestParameters;
import gov.usgs.aqcu.util.TimeSeriesUtils;

@Repository
public class UpchainRatingModelSearchService {
	private static final Logger LOG = LoggerFactory.getLogger(UpchainRatingModelSearchService.class);	

	private UpchainProcessorListService upchainProcessorListService;
	private TimeSeriesDescriptionListService timeSeriesDescriptionListService;

	@Autowired
	public UpchainRatingModelSearchService(
		UpchainProcessorListService upchainProcessorListService,
		TimeSeriesDescriptionListService timeSeriesDescriptionListService
	) {
		this.upchainProcessorListService = upchainProcessorListService;
		this.timeSeriesDescriptionListService = timeSeriesDescriptionListService;
	}

	public List<String> getRatingModelsUpchain(GetUpchainRatingModelsRequestParameters params) {
		TimeSeriesDescription primaryDescription = timeSeriesDescriptionListService.getTimeSeriesDescription(params.getTimeSeriesIdentifier());
		ZoneOffset primaryZoneOffset = TimeSeriesUtils.getZoneOffset(primaryDescription);
		return getRatingModelsUpchain(params.getTimeSeriesIdentifier(), params.getStartInstant(primaryZoneOffset), params.getEndInstant(primaryZoneOffset), params.getFullChain());
	}

	public List<String> getRatingModelsUpchain(String timeSeriesIdentifier, Instant startDate, Instant endDate, Boolean fullChain) {
		List<Processor> upProcs = upchainProcessorListService.getRawResponse(timeSeriesIdentifier, startDate, endDate).getProcessors();
		List<String> ratingModelIds = new ArrayList<>();
		List<String> inputTsIds = upchainProcessorListService.getInputTimeSeriesUniqueIdList(upProcs);
		ratingModelIds.addAll(upchainProcessorListService.getRatingModelUniqueIdList(upProcs));
		
		if(fullChain && !inputTsIds.isEmpty()) {
			for(String tsId : inputTsIds) {
				List<String> newRatingModels = getRatingModelsUpchain(tsId, startDate, endDate, fullChain);
				if(!newRatingModels.isEmpty()) {
					ratingModelIds.addAll(newRatingModels);
				}
			}
		}

		return ratingModelIds;
	}
}
