package gov.usgs.aqcu.controller;

import gov.usgs.aqcu.exception.FolderAlreadyExistsException;
import gov.usgs.aqcu.exception.FolderDoesNotExistException;
import gov.usgs.aqcu.exception.GroupAlreadyExistsException;
import gov.usgs.aqcu.exception.GroupDoesNotExistException;
import gov.usgs.aqcu.exception.ReportAlreadyExistsException;
import gov.usgs.aqcu.exception.ReportDoesNotExistException;
import gov.usgs.aqcu.model.config.FolderData;
import gov.usgs.aqcu.model.config.GroupData;
import gov.usgs.aqcu.model.report.SavedReportConfiguration;
import gov.usgs.aqcu.reports.ReportConfigsService;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/config")
@Validated
public class ConfigController {
	public static final String GROUP_NAME_REGEX = "^[\\s]*[\\/]?[a-zA-Z0-9-_]+[\\/]?[\\s]*$";
	public static final String FOLDER_PATH_REGEX = "^[\\s]*[\\/]?[a-zA-Z0-9-_]+(?:\\/[a-zA-Z0-9-_]+)*[\\/]?[\\s]*$";
	
	private ReportConfigsService configsService;
	
	@Autowired
	public ConfigController(ReportConfigsService configsService) {
		this.configsService = configsService;
	}
	
	// Groups
	@GetMapping(value="/groups/", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getAllGroups() throws Exception {
		return new ResponseEntity<List<String>>(configsService.getAllGroups(), new HttpHeaders(), HttpStatus.OK);
	}
	
	@PostMapping(value="/groups/", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> createGroup(@RequestParam @Pattern(regexp = GROUP_NAME_REGEX) String groupName) throws Exception {
		configsService.createGroup(groupName);
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.CREATED);
	}

	@GetMapping(value="/groups/{groupName}", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getGroup(@PathVariable("groupName") @Pattern(regexp = GROUP_NAME_REGEX) String groupName) throws Exception {
		return new ResponseEntity<GroupData>(configsService.getGroupData(groupName), new HttpHeaders(), HttpStatus.OK);
	}
	
	@DeleteMapping(value="/groups/{groupName}")
	public ResponseEntity<?> deleteGroup(@PathVariable("groupName") @Pattern(regexp = GROUP_NAME_REGEX) String groupName) throws Exception {
		configsService.deleteGroup(groupName);
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}

	// Folders
	@PostMapping(value="/groups/{groupName}/folders")
	public ResponseEntity<?> createFolder(@PathVariable("groupName") @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath) throws Exception {
		configsService.createFolder(groupName, folderPath);
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.CREATED);
	}

	@GetMapping(value="/groups/{groupName}/folders", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getFolder(@PathVariable("groupName") @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath) throws Exception {
		return new ResponseEntity<FolderData>(configsService.getFolderData(groupName, folderPath), new HttpHeaders(), HttpStatus.OK);
	}

	@DeleteMapping(value="/groups/{groupName}/folders")
	public ResponseEntity<?> deleteFolder(@PathVariable("groupName") @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath) throws Exception {
		configsService.deleteFolder(groupName, folderPath);
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}

	// Reports
	@PostMapping(value="/groups/{groupName}/reports")
	public ResponseEntity<String> createReport(@RequestBody SavedReportConfiguration newReport, @PathVariable("groupName") @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath) throws Exception {
		newReport.setId(UUID.randomUUID().toString());
		configsService.saveReport(groupName, folderPath, newReport, false);
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.CREATED);
	}
	
	@PutMapping(value="/groups/{groupName}/reports")
	public ResponseEntity<String> updateReport(@RequestBody SavedReportConfiguration updatedReport, @PathVariable("groupName") @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath, @RequestParam String reportId) throws Exception {
		updatedReport.setId(reportId);
		configsService.saveReport(groupName, folderPath, updatedReport, true);
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}

	@DeleteMapping(value="/groups/{groupName}/reports")
	public ResponseEntity<String> deleteReport(@PathVariable("groupName") @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath, @RequestParam String reportId) throws Exception {
		configsService.deleteReport(groupName, folderPath, reportId);
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}

	// Handle Validation Exceptions
	@ExceptionHandler(ValidationException.class)
    public void constraintValidationExceptionHandler(HttpServletResponse response) throws Exception {
        response.sendError(HttpStatus.BAD_REQUEST.value());
	}
	
	// Handle NotExist Exceptions
	@ExceptionHandler({GroupDoesNotExistException.class,FolderDoesNotExistException.class,ReportDoesNotExistException.class})
    public void doesNotExistExceptionHandler(HttpServletResponse response) throws Exception {
        response.sendError(HttpStatus.NOT_FOUND.value());
	}
	
	// Handle AlreadyExists Exceptions
	@ExceptionHandler({GroupAlreadyExistsException.class,FolderAlreadyExistsException.class,ReportAlreadyExistsException.class})
    public void alreadyExistsExceptionHandler(HttpServletResponse response) throws Exception {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}