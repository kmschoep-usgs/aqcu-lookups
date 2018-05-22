package gov.usgs.aqcu.retrieval;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.usgs.aqcu.util.TimeSeriesUtils;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescriptionListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescriptionListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescription;

@Repository
public class FieldVisitDescriptionListService {
	private static final Logger LOG = LoggerFactory.getLogger(FieldVisitDescriptionListService.class);	

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

	protected FieldVisitDescriptionListServiceResponse getRawResponse(String locationIdentifier) {
		FieldVisitDescriptionListServiceRequest request = new FieldVisitDescriptionListServiceRequest()
			.setLocationIdentifier(locationIdentifier)
			.setIncludeInvalidFieldVisits(false);
        FieldVisitDescriptionListServiceResponse unitList = aquariusRetrievalService.executePublishApiRequest(request);
		return unitList;
    }

    public List<String> getFieldVisitDates(String locationIdentifier) {
		ZoneOffset primaryZoneOffset = TimeSeriesUtils.getZoneOffset(locationDataService.getRawResponse(locationIdentifier));
		List<FieldVisitDescription> descList = getRawResponse(locationIdentifier).getFieldVisitDescriptions();
		List<LocalDateTime> fieldVisitDates = descList.stream().map(f -> LocalDateTime.ofInstant(f.getStartTime(), primaryZoneOffset)).collect(Collectors.toList());	
		return fieldVisitDates.stream().map(l -> DateTimeFormatter.ofPattern("yyyy-MM-dd").format(l)).collect(Collectors.toList());
	}
}
