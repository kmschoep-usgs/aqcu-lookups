package gov.usgs.aqcu.controller;

import gov.usgs.aqcu.exception.FolderAlreadyExistsException;
import gov.usgs.aqcu.exception.FolderDoesNotExistException;
import gov.usgs.aqcu.exception.GroupAlreadyExistsException;
import gov.usgs.aqcu.exception.GroupDoesNotExistException;
import gov.usgs.aqcu.exception.InvalidFolderNameException;
import gov.usgs.aqcu.exception.ReportAlreadyExistsException;
import gov.usgs.aqcu.exception.ReportDoesNotExistException;
import gov.usgs.aqcu.model.config.FolderData;
import gov.usgs.aqcu.model.config.GroupData;
import gov.usgs.aqcu.model.report.SavedReportConfiguration;
import gov.usgs.aqcu.reports.ReportConfigsService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import javax.validation.constraints.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger LOG = LoggerFactory.getLogger(ConfigController.class);
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
		try {
			return new ResponseEntity<List<String>>(configsService.getAllGroups(), new HttpHeaders(), HttpStatus.OK);
		} catch(Exception e) {
			LOG.error("An error occurred while interacting with S3. Error: ", e);
			return ResponseEntity.status(500).body("An error occurred while reading all groups.");
		}
	}
	
	@PostMapping(value="/groups/", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> createGroup(@RequestParam @Pattern(regexp = GROUP_NAME_REGEX) String groupName) throws Exception {
		try {
			configsService.createGroup(groupName);
			return new ResponseEntity<String>("Group created.", new HttpHeaders(), HttpStatus.CREATED);
		} catch(GroupAlreadyExistsException | InvalidFolderNameException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		} catch(Exception e) {
			LOG.error("An error occurred while interacting with S3. Error: ", e);
			return ResponseEntity.status(500).body("An error occurred while creating the group.");
		}
	}

	@GetMapping(value="/groups/{groupName}", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getGroup(@PathVariable("groupName") @Pattern(regexp = GROUP_NAME_REGEX) String groupName) throws Exception {
		try {
			return new ResponseEntity<GroupData>(configsService.getGroupData(groupName), new HttpHeaders(), HttpStatus.OK);
		} catch(GroupDoesNotExistException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		} catch(Exception e) {
			LOG.error("An error occurred while interacting with S3. Error: ", e);
			return ResponseEntity.status(500).body("An error occurred while reading the group.");
		}
	}
	
	@DeleteMapping(value="/groups/{groupName}")
	public ResponseEntity<?> deleteGroup(@PathVariable("groupName") @Pattern(regexp = GROUP_NAME_REGEX) String groupName) throws Exception {
		try {
			configsService.deleteGroup(groupName);
			return ResponseEntity.ok("Group deleted.");
		} catch(GroupDoesNotExistException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		} catch(Exception e) {
			LOG.error("An error occurred while interacting with S3. Error: ", e);
			return ResponseEntity.status(500).body("An error occurred while deleting the group.");
		}
	}

	// Folders
	@PostMapping(value="/groups/{groupName}/folders")
	public ResponseEntity<?> createFolder(@PathVariable("groupName") @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath) throws Exception {
		try {
			configsService.createFolder(groupName, folderPath);
			return new ResponseEntity<String>("Folder created.", new HttpHeaders(), HttpStatus.CREATED);
		} catch(GroupDoesNotExistException | FolderDoesNotExistException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		} catch(FolderAlreadyExistsException | InvalidFolderNameException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		} catch(Exception e) {
			LOG.error("An error occurred while interacting with S3. Error: ", e);
			return ResponseEntity.status(500).body("An error occurred while creating the folder.");
		}
	}

	@GetMapping(value="/groups/{groupName}/folders", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getFolder(@PathVariable("groupName") @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath) throws Exception {
		try {
			return new ResponseEntity<FolderData>(configsService.getFolderData(groupName, folderPath), new HttpHeaders(), HttpStatus.OK);
		} catch(GroupDoesNotExistException | FolderDoesNotExistException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		} catch(Exception e) {
			LOG.error("An error occurred while interacting with S3. Error: ", e);
			return ResponseEntity.status(500).body("An error occurred while reading the folder.");
		}
	}

	@DeleteMapping(value="/groups/{groupName}/folders")
	public ResponseEntity<?> deleteFolder(@PathVariable("groupName") @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath) throws Exception {
		try {
			configsService.deleteFolder(groupName, folderPath);
			return ResponseEntity.ok("Folder deleted.");
		} catch(GroupDoesNotExistException | FolderDoesNotExistException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		} catch(Exception e) {
			LOG.error("An error occurred while interacting with S3. Error: ", e);
			return ResponseEntity.status(500).body("An error occurred while deleting the folder.");
		}
	}

	// Reports
	@PostMapping(value="/groups/{groupName}/reports")
	public ResponseEntity<String> createReport(@RequestBody SavedReportConfiguration newReport, @PathVariable("groupName") @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath) {
		try {
			newReport.setId(UUID.randomUUID().toString());
			configsService.saveReport(groupName, folderPath, newReport, false);
			return new ResponseEntity<String>("Report created.", new HttpHeaders(), HttpStatus.CREATED);
		} catch(GroupDoesNotExistException | FolderDoesNotExistException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		} catch(ReportAlreadyExistsException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		} catch(Exception e) {
			LOG.error("An error occurred while interacting with S3. Error: ", e);
			return ResponseEntity.status(500).body("An error occurred while creating the report.");
		}
	}
	
	@PutMapping(value="/groups/{groupName}/reports")
	public ResponseEntity<String> updateReport(@RequestBody SavedReportConfiguration updatedReport, @PathVariable("groupName") @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath, @RequestParam String reportId) {
		try {
			updatedReport.setId(reportId);
			configsService.saveReport(groupName, folderPath, updatedReport, true);
			return ResponseEntity.ok("Report updated.");
		} catch(GroupDoesNotExistException | FolderDoesNotExistException | ReportDoesNotExistException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		} catch(Exception e) {
			LOG.error("An error occurred while interacting with S3. Error: ", e);
			return ResponseEntity.status(500).body("An error occurred while updating the report.");
		}
	}

	@DeleteMapping(value="/groups/{groupName}/reports")
	public ResponseEntity<String> deleteReport(@PathVariable("groupName") @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath, @RequestParam String reportId) {
		try {
			configsService.deleteReport(groupName, folderPath, reportId);
			return ResponseEntity.ok("Report deleted.");
		} catch(GroupDoesNotExistException | FolderDoesNotExistException | ReportDoesNotExistException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		} catch(Exception e) {
			LOG.error("An error occurred while interacting with S3. Error: ", e);
			return ResponseEntity.status(500).body("An error occurred while deleting the report.");
		}
	}

	// Handle Validation Exceptions
	@ExceptionHandler(ValidationException.class)
    public void constraintValidationException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}