package gov.usgs.aqcu;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.aqcu.parameter.FieldVisitDatesRequestParameters;
import gov.usgs.aqcu.parameter.FindInDerivationChainRequestParameters;
import gov.usgs.aqcu.parameter.GetUpchainRatingModelsRequestParameters;
import gov.usgs.aqcu.parameter.ProcessorTypesRequestParameters;
import gov.usgs.aqcu.parameter.SiteSearchRequestParameters;
import gov.usgs.aqcu.parameter.TimeSeriesIdentifiersRequestParameters;
import gov.usgs.aqcu.lookup.LookupsService;
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
	private LookupsService lookupsService;

	@Autowired
	public Controller(LookupsService lookupsService) {
		this.lookupsService = lookupsService;
	}

	@GetMapping(value="/timeseries/identifiers", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getTimeSeriesDescriptions(@Validated TimeSeriesIdentifiersRequestParameters params) throws Exception {
		return new ResponseEntity<Map<String,TimeSeriesBasicData>>(lookupsService.getTimeSeriesDescriptions(params), new HttpHeaders(), HttpStatus.OK);
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
	
	@GetMapping(value="/field-visit-dates", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getFieldVisitDates(@Validated FieldVisitDatesRequestParameters params) throws Exception {
		return new ResponseEntity<List<String>>(lookupsService.getFieldVisitDates(params), new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/controlConditions", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getControlConditions() throws Exception {
		return new ResponseEntity<List<Map<String,String>>>(lookupsService.getControlConditions(), new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/computations", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getComputations() throws Exception {
		return new ResponseEntity<List<String>>(lookupsService.getComputations(), new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/periods", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getPeriods() throws Exception {
		return new ResponseEntity<List<String>>(lookupsService.getPeriods(), new HttpHeaders(), HttpStatus.OK);
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
	
	/*
		Does not appear to be used in the UI anywhere, need to check with AutoFF. Deprecating.
	*/
	@Deprecated
	@GetMapping(value="/report/types", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getReportTypes() throws Exception {
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.GONE);
	}

	String getRequestingUser() {
		//TODO: Pull Requesting User From SecurityContext
		return "testUser";
	}
}
