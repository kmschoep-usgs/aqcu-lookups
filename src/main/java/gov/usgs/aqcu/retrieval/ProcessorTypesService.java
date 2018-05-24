package gov.usgs.aqcu.retrieval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Instant;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Processor;

@Repository
public class ProcessorTypesService {
	private static final Logger LOG = LoggerFactory.getLogger(ProcessorTypesService.class);	

	private DownchainProcessorListService downchainProcessorListService;
	private UpchainProcessorListService upchainProcessorListService;

	@Autowired
	public ProcessorTypesService(
		DownchainProcessorListService downchainProcessorListService,
		UpchainProcessorListService upchainProcessorListService
	) {
		this.downchainProcessorListService = downchainProcessorListService;
		this.upchainProcessorListService = upchainProcessorListService;
	}

	public Map<String, List<String>> getProcessorTypes(String timeSeriesIdentifier, Instant startDate, Instant endDate) {
		Map<String, List<String>> processorMap = new HashMap<>();
		List<Processor> upProcessors = upchainProcessorListService.getRawResponse(timeSeriesIdentifier, startDate, endDate).getProcessors();
		List<Processor> downProcessors = downchainProcessorListService.getRawResponse(timeSeriesIdentifier, startDate, endDate).getProcessors();
		
		if(!upProcessors.isEmpty()) {
			processorMap.put("upChain", new ArrayList<>(upProcessors.stream().map(p -> p.getProcessorType()).collect(Collectors.toSet())));
		}

		if(!downProcessors.isEmpty()) {
			processorMap.put("downChain", new ArrayList<>(downProcessors.stream().map(p -> p.getProcessorType()).collect(Collectors.toSet())));
		}
		
		return processorMap;
	}
}
