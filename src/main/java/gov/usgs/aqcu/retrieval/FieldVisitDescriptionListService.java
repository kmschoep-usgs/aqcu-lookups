package gov.usgs.aqcu.retrieval;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.usgs.aqcu.util.TimeSeriesUtils;
import gov.usgs.aqcu.util.LogExecutionTime;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescriptionListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescriptionListServiceResponse;

@Repository
public class FieldVisitDescriptionListService {
	private AquariusRetrievalService aquariusRetrievalService;
	private LocationDataService locationDataService;

	@Autowired
	public FieldVisitDescriptionListService(
		AquariusRetrievalService aquariusRetrievalService,
		LocationDataService locationDataService
	) {
		this.aquariusRetrievalService = aquariusRetrievalService;
		this.locationDataService = locationDataService;
	}

        @LogExecutionTime
	protected FieldVisitDescriptionListServiceResponse getRawResponse(String locationIdentifier) {
		FieldVisitDescriptionListServiceRequest request = new FieldVisitDescriptionListServiceRequest()
			.setLocationIdentifier(locationIdentifier)
			.setIncludeInvalidFieldVisits(false);
		FieldVisitDescriptionListServiceResponse unitList = aquariusRetrievalService.executePublishApiRequest(request);
		return unitList;
	}

        @LogExecutionTime
	public List<String> getFieldVisitDates(String locationIdentifier) {
		ZoneOffset primaryZoneOffset = TimeSeriesUtils.getZoneOffset(locationDataService.getRawResponse(locationIdentifier));
		List<FieldVisitDescription> descList = getRawResponse(locationIdentifier).getFieldVisitDescriptions();
		List<LocalDateTime> fieldVisitDates = descList.stream().map(f -> LocalDateTime.ofInstant(f.getStartTime(), primaryZoneOffset)).collect(Collectors.toList());	
		return fieldVisitDates.stream().map(l -> DateTimeFormatter.ofPattern("yyyy-MM-dd").format(l)).collect(Collectors.toList());
	}
}
