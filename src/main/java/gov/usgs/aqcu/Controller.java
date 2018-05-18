package gov.usgs.aqcu;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


@RestController
@RequestMapping("/lookup")
public class Controller {
	private static final Logger LOG = LoggerFactory.getLogger(Controller.class);
	private Gson gson;
	@Autowired
	public Controller(Gson gson) {
		this.gson = gson;
	}

	@GetMapping(value="/timeseries/identifiers", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getTimeSeriesIdentifiers() throws Exception {
		
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
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
	public ResponseEntity<?> getProcessorTypes() throws Exception {
		
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/sites", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getSites() throws Exception {
		
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/controlConditions", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getControlConditions() throws Exception {
		
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/computations", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getComputations() throws Exception {
		
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/periods", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getPeriods() throws Exception {
		
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/field-visit-dates", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getFieldVisitDates() throws Exception {
		
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/units", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getUnits() throws Exception {
		
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/sitefile", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getSitefile() throws Exception {
		
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value="/report/types", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getReportTypes() throws Exception {
		
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}

	String getRequestingUser() {
		//Pull Requesting User From SecurityContext
		return "testUser";
	}
}
