package gov.usgs.aqcu.retrieval;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UpchainRatingModelSearchService {
	private static final Logger LOG = LoggerFactory.getLogger(UpchainRatingModelSearchService.class);	

	private UpchainProcessorListService upchainProcessorListService;

	@Autowired
	public UpchainRatingModelSearchService(UpchainProcessorListService upchainProcessorListService) {
		this.upchainProcessorListService = upchainProcessorListService;
	}

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
