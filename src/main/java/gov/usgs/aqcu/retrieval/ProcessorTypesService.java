package gov.usgs.aqcu.retrieval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.usgs.aqcu.parameter.ProcessorTypesRequestParameters;
import gov.usgs.aqcu.util.TimeSeriesUtils;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Processor;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;

@Repository
public class ProcessorTypesService {
	private static final Logger LOG = LoggerFactory.getLogger(ProcessorTypesService.class);	

	private DownchainProcessorListService downchainProcessorListService;
	private UpchainProcessorListService upchainProcessorListService;
	private TimeSeriesDescriptionListService timeSeriesDescriptionListService;

	@Autowired
	public ProcessorTypesService(
		TimeSeriesDescriptionListService timeSeriesDescriptionListService,
		DownchainProcessorListService downchainProcessorListService,
		UpchainProcessorListService upchainProcessorListService
	) {
		this.timeSeriesDescriptionListService = timeSeriesDescriptionListService;
		this.downchainProcessorListService = downchainProcessorListService;
		this.upchainProcessorListService = upchainProcessorListService;
	}

	public Map<String, List<String>> getProcessorTypes(ProcessorTypesRequestParameters params) {
		Map<String, List<String>> processorMap = new HashMap<>();
		TimeSeriesDescription primaryDescription = timeSeriesDescriptionListService.getTimeSeriesDescription(params.getTimeSeriesIdentifier());
		ZoneOffset primaryZoneOffset = TimeSeriesUtils.getZoneOffset(primaryDescription);
		List<Processor> upProcessors = upchainProcessorListService.getRawResponse(params.getTimeSeriesIdentifier(), params.getStartInstant(primaryZoneOffset), params.getEndInstant(primaryZoneOffset)).getProcessors();
		List<Processor> downProcessors = downchainProcessorListService.getRawResponse(params.getTimeSeriesIdentifier(), params.getStartInstant(primaryZoneOffset), params.getEndInstant(primaryZoneOffset)).getProcessors();
		
		processorMap.put("upChain", new ArrayList<>(upProcessors.stream().map(p -> p.getProcessorType()).collect(Collectors.toSet())));
		processorMap.put("downChain", new ArrayList<>(downProcessors.stream().map(p -> p.getProcessorType()).collect(Collectors.toSet())));

		return processorMap;
	}
}
