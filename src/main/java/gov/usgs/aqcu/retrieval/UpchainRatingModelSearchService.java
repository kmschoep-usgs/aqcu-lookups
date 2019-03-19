package gov.usgs.aqcu.retrieval;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.usgs.aqcu.util.LogExecutionTime;

@Repository
public class UpchainRatingModelSearchService {
	private UpchainProcessorListService upchainProcessorListService;

	@Autowired
	public UpchainRatingModelSearchService(UpchainProcessorListService upchainProcessorListService) {
		this.upchainProcessorListService = upchainProcessorListService;
	}

        @LogExecutionTime
	public List<String> getRatingModelsUpchain(String timeSeriesIdentifier, Instant startDate, Instant endDate, Boolean fullChain) {
		List<Processor> upProcs = upchainProcessorListService.getRawResponse(timeSeriesIdentifier, startDate, endDate).getProcessors();
		List<String> ratingModelIds = new ArrayList<>();

		if(!upProcs.isEmpty()) {
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
		}

		return ratingModelIds;
	}
}
