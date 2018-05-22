package gov.usgs.aqcu;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Provisioning.Location;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ControlConditionType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.aqcu.parameter.FieldVisitDatesRequestParameters;
import gov.usgs.aqcu.parameter.ProcessorTypesRequestParameters;
import gov.usgs.aqcu.parameter.SiteSearchRequestParameters;
import gov.usgs.aqcu.parameter.TimeSeriesIdentifiersRequestParameters;
import gov.usgs.aqcu.reference.ComputationReferenceService;
import gov.usgs.aqcu.reference.ControlConditionReferenceService;
import gov.usgs.aqcu.reference.PeriodReferenceService;
import gov.usgs.aqcu.retrieval.FieldVisitDescriptionListService;
import gov.usgs.aqcu.retrieval.LocationDescriptionListService;
import gov.usgs.aqcu.retrieval.ProcessorTypesService;
import gov.usgs.aqcu.retrieval.TimeSeriesDescriptionListService;
import gov.usgs.aqcu.retrieval.UnitsLookupService;
import gov.usgs.aqcu.util.AqcuTimeUtils;
import gov.usgs.aqcu.model.LocationBasicData;
import gov.usgs.aqcu.model.TimeSeriesBasicData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/lookup")
public class Controller {
	private static final Logger LOG = LoggerFactory.getLogger(Controller.class);
	private TimeSeriesDescriptionListService timeSeriesDescriptionListService;
	private ProcessorTypesService processorTypesService;
	private LocationDescriptionListService locationDescriptionListService;
	private ComputationReferenceService computationReferenceService;
	private ControlConditionReferenceService controlConditionReferenceService;
	private PeriodReferenceService periodReferenceService;
	private UnitsLookupService unitsLookupService;
	private FieldVisitDescriptionListService fieldVisitDescriptionListService;

	@Autowired
	public Controller(
		TimeSeriesDescriptionListService timeSeriesDescriptionListService,
		ProcessorTypesService processorTypesService,
		LocationDescriptionListService locationDescriptionListService,
		UnitsLookupService unitsLookupService,
		ComputationReferenceService computationReferenceService,
		ControlConditionReferenceService controlConditionReferenceService,
		PeriodReferenceService periodReferenceService,
		FieldVisitDescriptionListService fieldVisitDescriptionListService) {
			this.timeSeriesDescriptionListService = timeSeriesDescriptionListService;
			this.processorTypesService = processorTypesService;
			this.locationDescriptionListService = locationDescriptionListService;
			this.unitsLookupService = unitsLookupService;
			this.computationReferenceService = computationReferenceService;
			this.controlConditionReferenceService = controlConditionReferenceService;
			this.periodReferenceService = periodReferenceService;
			this.fieldVisitDescriptionListService = fieldVisitDescriptionListService;
	}

	@GetMapping(value="/timeseries/identifiers", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getTimeSeriesDescriptions(@Validated TimeSeriesIdentifiersRequestParameters params) throws Exception {
		List<TimeSeriesDescription> descs = timeSeriesDescriptionListService.getTimeSeriesDescriptionList(params.getComputationIdentifier(), params.getComputationPeriodIdentifier(),
			params.getStationId(), params.getParameter(), params.getPublish(), params.getPrimary());
		Map<String,TimeSeriesBasicData> returnDataMap = descs.stream().map(d -> new TimeSeriesBasicData(d)).collect(Collectors.toMap(TimeSeriesBasicData::getUniqueId, Function.identity()));
		return new ResponseEntity<Map<String,TimeSeriesBasicData>>(returnDataMap, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/derivationChain/find", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> searchDerivationChain() throws Exception {
		
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/derivationChain/ratingModel", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getRatingModel() throws Exception {
		
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/timeseries/processorTypes", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getProcessorTypes(@Validated ProcessorTypesRequestParameters params) throws Exception {
		Map<String, List<String>> processorTypeMap = processorTypesService.getProcessorTypes(params);
		return new ResponseEntity<Map<String, List<String>>>(processorTypeMap, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/sites", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getSites(@Validated SiteSearchRequestParameters params) throws Exception {
		List<LocationDescription> siteDescList = locationDescriptionListService.searchSites(params.getSiteNumber(), params.getPageSize());
		List<LocationBasicData> returnDataList = siteDescList.stream().map(d -> new LocationBasicData(d)).collect(Collectors.toList());
		return new ResponseEntity<List<LocationBasicData>>(returnDataList, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/controlConditions", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getControlConditions() throws Exception {
		List<Map<String,String>> controlConditionList = controlConditionReferenceService.get();
		return new ResponseEntity<List<Map<String,String>>>(controlConditionList, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/computations", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getComputations() throws Exception {
		List<String> computationList = computationReferenceService.get();
		return new ResponseEntity<List<String>>(computationList, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/periods", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getPeriods() throws Exception {
		List<String> periodList = periodReferenceService.get();
		return new ResponseEntity<List<String>>(periodList, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/field-visit-dates", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getFieldVisitDates(@Validated FieldVisitDatesRequestParameters params) throws Exception {
		List<String> fieldVisitDates = fieldVisitDescriptionListService.getFieldVisitDates(params.getSiteNumber());
		return new ResponseEntity<List<String>>(fieldVisitDates, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/units", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getUnits() throws Exception {
		List<UnitMetadata> unitMetadataList = unitsLookupService.getUnits();
		List<String> unitList = unitMetadataList.stream().map(u -> u.getIdentifier()).collect(Collectors.toList());
		return new ResponseEntity<List<String>>(unitList, new HttpHeaders(), HttpStatus.OK);
	}
	
	/*
		Only used in the UI to verify the user is logged in. Can be deprecated when we move to WaterAuth.
		Should not be implemented here, should use the gateway to always route to the old service for this call.
	*/
	@Deprecated
	@GetMapping(value="/sitefile", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getSitefile() throws Exception {
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.GONE);
	}
	
	/*
		Does not appear to be used in the UI anywhere, need to check with AutoFF. Deprecating.
	*/
	@Deprecated
	@GetMapping(value="/report/types", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getReportTypes() throws Exception {
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.GONE);
	}

	String getRequestingUser() {
		//Pull Requesting User From SecurityContext
		return "testUser";
	}
}
