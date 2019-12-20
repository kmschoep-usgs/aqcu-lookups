package gov.usgs.aqcu.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import gov.usgs.aqcu.parameter.FieldVisitDatesRequestParameters;
import gov.usgs.aqcu.parameter.FindInDerivationChainRequestParameters;
import gov.usgs.aqcu.parameter.GetUpchainRatingModelsRequestParameters;
import gov.usgs.aqcu.parameter.ProcessorTypesRequestParameters;
import gov.usgs.aqcu.parameter.SiteSearchRequestParameters;
import gov.usgs.aqcu.parameter.TimeSeriesIdentifiersRequestParameters;
import gov.usgs.aqcu.config.AquariusReferenceListProperties;
import gov.usgs.aqcu.lookup.LookupsService;
import gov.usgs.aqcu.model.lookup.LocationBasicData;
import gov.usgs.aqcu.model.lookup.TimeSeriesBasicData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/lookup")
public class LookupController {
	private LookupsService lookupsService;
	private AquariusReferenceListProperties aquariusReferenceListProperties;

	@Autowired
	public LookupController(
		LookupsService lookupsService,
		AquariusReferenceListProperties aquariusReferenceListProperties
	) {
		this.lookupsService = lookupsService;
		this.aquariusReferenceListProperties = aquariusReferenceListProperties;
	}

	@GetMapping(value="/timeseries/identifiers", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getTimeSeriesDescriptions(@Validated TimeSeriesIdentifiersRequestParameters params) throws Exception {
		return new ResponseEntity<Map<String,TimeSeriesBasicData>>(lookupsService.getTimeSeriesDescriptions(params), new HttpHeaders(), HttpStatus.OK);
	}

	@GetMapping(value="/timeseries/identifiers/list", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getTimeSeriesDescriptions(@RequestParam("identifier") String[] uniqueIds) throws Exception {
		return new ResponseEntity<Map<String,TimeSeriesBasicData>>(lookupsService.getTimeSeriesDescriptionsForUniqueIds(Arrays.asList(uniqueIds)), new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/derivationChain/find", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> searchDerivationChain(@Validated FindInDerivationChainRequestParameters params) throws Exception {
		return new ResponseEntity<List<String>>(lookupsService.searchDerivationChain(params), new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/derivationChain/ratingModel", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getRatingModel(@Validated GetUpchainRatingModelsRequestParameters params) throws Exception {
		return new ResponseEntity<List<String>>(lookupsService.getRatingModel(params), new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/timeseries/processorTypes", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getProcessorTypes(@Validated ProcessorTypesRequestParameters params) throws Exception {
		return new ResponseEntity<Map<String, List<String>>>(lookupsService.getProcessorTypes(params), new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/sites", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getSites(@Validated SiteSearchRequestParameters params) throws Exception {
		return new ResponseEntity<List<LocationBasicData>>(lookupsService.searchSites(params), new HttpHeaders(), HttpStatus.OK);
	}

	@GetMapping(value="/sites/list", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getSites(@RequestParam("identifier") String[] identifiers) throws Exception {
		return new ResponseEntity<List<LocationBasicData>>(lookupsService.getSiteDataForIdentifiers(Arrays.asList(identifiers)), new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/field-visit-dates", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getFieldVisitDates(@Validated FieldVisitDatesRequestParameters params) throws Exception {
		return new ResponseEntity<List<String>>(lookupsService.getFieldVisitDates(params), new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/controlConditions", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getControlConditions(WebRequest req) throws Exception {
		if(req.checkNotModified(getReferenceListsLastModified())) {
			return null;
		}
		HttpHeaders head = new HttpHeaders();
		head.setLastModified(getReferenceListsLastModified());
		return new ResponseEntity<List<String>>(lookupsService.getControlConditions(), head, HttpStatus.OK);
	}
	
	@GetMapping(value="/computations", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getComputations(WebRequest req) throws Exception {
		if(req.checkNotModified(getReferenceListsLastModified())) {
			return null;
		}
		HttpHeaders head = new HttpHeaders();
		head.setLastModified(getReferenceListsLastModified());
		return new ResponseEntity<List<String>>(lookupsService.getComputations(), head, HttpStatus.OK);
	}
	
	@GetMapping(value="/periods", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getPeriods(WebRequest req) throws Exception {
		if(req.checkNotModified(getReferenceListsLastModified())) {
			return null;
		}
		HttpHeaders head = new HttpHeaders();
		head.setLastModified(getReferenceListsLastModified());
		return new ResponseEntity<List<String>>(lookupsService.getPeriods(), head, HttpStatus.OK);
	}
	
	@GetMapping(value="/units", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getUnits() throws Exception {
		return new ResponseEntity<List<String>>(lookupsService.getUnits(), new HttpHeaders(), HttpStatus.OK);
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

	long getReferenceListsLastModified() {
		return aquariusReferenceListProperties.getLastModifiedInstant().toEpochMilli();
	}
}