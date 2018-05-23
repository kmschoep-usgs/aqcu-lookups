package gov.usgs.aqcu.retrieval;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Processor;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.usgs.aqcu.util.AqcuTimeUtils;
import gov.usgs.aqcu.util.TimeSeriesUtils;

@Repository
public class DerivationChainSearchService {
	private static final Logger LOG = LoggerFactory.getLogger(DerivationChainSearchService.class);	
	
	private UpchainProcessorListService upchainProcessorListService;
	private DownchainProcessorListService downchainProcessorListService;
	private TimeSeriesDescriptionListService timeSeriesDescriptionListService;

	public static final String DERIVATION_CHAIN_UP_DIRECTION = "upchain";
	public static final String DERIVATION_CHAIN_DOWN_DIRECTION = "downchain";

	@Autowired
	public DerivationChainSearchService(
		UpchainProcessorListService upchainProcessorListService,
		DownchainProcessorListService downchainProcessorListService,
		TimeSeriesDescriptionListService timeSeriesDescriptionListService
	) {
		this.upchainProcessorListService = upchainProcessorListService;
		this.downchainProcessorListService = downchainProcessorListService;
		this.timeSeriesDescriptionListService = timeSeriesDescriptionListService;
	}

	public List<TimeSeriesDescription> findTimeSeriesInDerivationChain(String timeSeriesIdentifier, String direction, Boolean primary, Boolean publish, String parameter,
			String computationIdentifier, String computationPeriodIdentifier, Instant startDate, Instant endDate, Boolean fullChain) {
		List<String> fullTimeSeriesList = null;
		List<TimeSeriesDescription> filteredTimeSeriesList = new ArrayList<>();

		//Get list of related time series that are within the requested time range
		if(direction != null && DERIVATION_CHAIN_UP_DIRECTION.equals(direction.trim().toLowerCase())) {
			List<Processor> procList = upchainProcessorListService.getRawResponse(timeSeriesIdentifier, startDate, endDate).getProcessors();
			fullTimeSeriesList = upchainProcessorListService.getInputTimeSeriesUniqueIdList(procList);
		} else if(direction != null && DERIVATION_CHAIN_DOWN_DIRECTION.equals(direction.trim().toLowerCase())) {
			List<Processor> procList = downchainProcessorListService.getRawResponse(timeSeriesIdentifier, startDate, endDate).getProcessors();
			fullTimeSeriesList = downchainProcessorListService.getOutputTimeSeriesUniqueIdList(procList);
		} else {
			LOG.error("Invalid direction. Expected one of: " + DERIVATION_CHAIN_UP_DIRECTION + " or " + DERIVATION_CHAIN_DOWN_DIRECTION);
		}

		//Fetch descriptions for each found time series and see if it matches the filter criteria.
		if(fullTimeSeriesList != null && !fullTimeSeriesList.isEmpty()) {
			List<TimeSeriesDescription> tsDescList = timeSeriesDescriptionListService.getTimeSeriesDescriptionList(fullTimeSeriesList);
			filteredTimeSeriesList.addAll(tsDescList.stream()
				.filter(t -> timeSeriesMatchesFilterCriteria(t, primary, publish, parameter, computationIdentifier, computationPeriodIdentifier, startDate, endDate))
				.collect(Collectors.toList()));
			
			//If we're searching the entire chain then continue recursively
			if(fullChain) {
				for(String tsUid : fullTimeSeriesList) {
					filteredTimeSeriesList.addAll(findTimeSeriesInDerivationChain(tsUid, direction, primary, publish, parameter, computationIdentifier, computationPeriodIdentifier, startDate, endDate, fullChain));
				}
			}
		}

		return filteredTimeSeriesList;
	}

	public boolean timeSeriesMatchesFilterCriteria(TimeSeriesDescription tsDesc, Boolean primary, Boolean publish, String parameter,
	String computationIdentifier, String computationPeriodIdentifier, Instant startDate, Instant endDate) {
		if(tsDesc == null) {
			return false;
		} else if(publish != null && publish != tsDesc.isPublish()) {
			return false;
		} else if(primary != null && primary != TimeSeriesUtils.isPrimaryTimeSeries(tsDesc)) {
			return false;
		} else if(parameter != null && !parameter.equals(tsDesc.getParameter())) {
			return false;
		} else if(computationIdentifier != null && !computationIdentifier.equals(tsDesc.getComputationIdentifier())) {
			return false;
		} else if(computationPeriodIdentifier != null && !computationPeriodIdentifier.equals(tsDesc.getComputationPeriodIdentifier())) {
			return false;
		} else if(startDate != null && endDate != null && !AqcuTimeUtils.doesTimeRangeOverlap(tsDesc.getRawStartTime(), tsDesc.getRawEndTime(), startDate, endDate)) {
			return false;
		}

		return true;
	}
}