package gov.usgs.aqcu.retrieval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Processor;

import gov.usgs.aqcu.util.LogExecutionTime;

@Repository
public class ProcessorTypesService {
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

        @LogExecutionTime
	public Map<String, List<String>> getProcessorTypes(String timeSeriesIdentifier, Instant startDate, Instant endDate) {
		Map<String, List<String>> processorMap = new HashMap<>();
		List<Processor> upProcessors = upchainProcessorListService.getRawResponse(timeSeriesIdentifier, startDate, endDate).getProcessors();
		List<Processor> downProcessors = downchainProcessorListService.getRawResponse(timeSeriesIdentifier, startDate, endDate).getProcessors();
		List<String> upTypes = new ArrayList<>();
		List<String> downTypes = new ArrayList<>();
		
		if(!upProcessors.isEmpty()) {
			upTypes = new ArrayList<>(upProcessors.stream().map(p -> p.getProcessorType()).collect(Collectors.toSet()));
		}

		if(!downProcessors.isEmpty()) {
			downTypes = new ArrayList<>(downProcessors.stream().map(p -> p.getProcessorType()).collect(Collectors.toSet()));
		}

		processorMap.put("upChain", upTypes);
		processorMap.put("downChain", downTypes);
		
		return processorMap;
	}
}
