package gov.usgs.aqcu.controller;

import gov.usgs.aqcu.config.Roles;
import gov.usgs.aqcu.exception.FolderAlreadyExistsException;
import gov.usgs.aqcu.exception.FolderCannotStoreReportsException;
import gov.usgs.aqcu.exception.FolderDoesNotExistException;
import gov.usgs.aqcu.exception.GroupAlreadyExistsException;
import gov.usgs.aqcu.exception.GroupDoesNotExistException;
import gov.usgs.aqcu.exception.ReportAlreadyExistsException;
import gov.usgs.aqcu.exception.ReportDoesNotExistException;
import gov.usgs.aqcu.model.config.FolderData;
import gov.usgs.aqcu.model.config.GroupData;
import gov.usgs.aqcu.model.config.persist.FolderProperties;
import gov.usgs.aqcu.model.config.persist.SavedReportConfiguration;
import gov.usgs.aqcu.reports.ReportConfigsService;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;

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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@RestController
@RequestMapping("/config")
@Validated
public class ConfigController {
	public static final String UNKNOWN_USERNAME = "unknown";
	public static final String GROUP_NAME_REGEX = "^[\\s]*[a-zA-Z0-9-_]+[\\s]*$";
	public static final String FOLDER_PATH_REGEX = "^[\\s]*[\\/]?[a-zA-Z0-9-_]+(?:\\/[a-zA-Z0-9-_]+)*[\\/]?[\\s]*$";
	private static final String GROUPS_CONTEXT_PATH = "/groups";
	private static final String SINGLE_GROUP_CONTEXT_PATH = GROUPS_CONTEXT_PATH + "/{groupName}";
	private static final String FOLDERS_CONTEXT_PATH = SINGLE_GROUP_CONTEXT_PATH + "/folders";
        private static final String ROOT_FOLDERS_CONTEXT_PATH = FOLDERS_CONTEXT_PATH + "/{rootFolder}";
        private static final String SUBFOLDERS_CONTEXT_PATH = ROOT_FOLDERS_CONTEXT_PATH + "/{*subfolder}";
	private static final String REPORTS_CONTEXT_PATH = SINGLE_GROUP_CONTEXT_PATH + "/reports";
	
	private ReportConfigsService configsService;
	private Clock clock;

	@Autowired
	public ConfigController(ReportConfigsService configsService, Clock clock) {
		this.configsService = configsService;
		this.clock = clock;
	}
	
	// Groups
	@GetMapping(value=GROUPS_CONTEXT_PATH, produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getAllGroups() throws Exception {
		return new ResponseEntity<List<String>>(configsService.getAllGroups(), new HttpHeaders(), HttpStatus.OK);
	}
	
	@PostMapping(value=SINGLE_GROUP_CONTEXT_PATH)
        @RolesAllowed({Roles.NATIONAL_ADMIN})
	public ResponseEntity<?> createGroup(@PathVariable @NotBlank @Pattern(regexp = GROUP_NAME_REGEX) String groupName) throws Exception {
		configsService.createGroup(groupName.toLowerCase().trim());
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.CREATED);
	}

	@GetMapping(value=SINGLE_GROUP_CONTEXT_PATH, produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getGroup(@PathVariable("groupName") @NotBlank @Pattern(regexp = GROUP_NAME_REGEX) String groupName) throws Exception {
		return new ResponseEntity<GroupData>(configsService.getGroupData(groupName.toLowerCase().trim()), new HttpHeaders(), HttpStatus.OK);
	}
	
	@DeleteMapping(value=SINGLE_GROUP_CONTEXT_PATH)
        @RolesAllowed({Roles.NATIONAL_ADMIN})
	public ResponseEntity<?> deleteGroup(@PathVariable("groupName") @NotBlank @Pattern(regexp = GROUP_NAME_REGEX) String groupName) throws Exception {
		configsService.deleteGroup(groupName.toLowerCase().trim());
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}

	// Folders
	@PostMapping(value=ROOT_FOLDERS_CONTEXT_PATH)
    @RolesAllowed({Roles.NATIONAL_ADMIN})
	public ResponseEntity<?> createRootFolder(
        @PathVariable("groupName") @NotBlank @Pattern(regexp = GROUP_NAME_REGEX) String groupName,
        @PathVariable @NotBlank @Pattern(regexp = FOLDER_PATH_REGEX) String rootFolder
    ) throws Exception {
		configsService.createFolder(groupName.toLowerCase().trim(), rootFolder.toLowerCase().trim());
		return new ResponseEntity<FolderData>(configsService.getFolderData(groupName.toLowerCase().trim(), rootFolder.toLowerCase().trim()), new HttpHeaders(), HttpStatus.CREATED);
	}
        
	@PostMapping(value=SUBFOLDERS_CONTEXT_PATH)
        @RolesAllowed({Roles.LOCAL_DATA_MANAGER, Roles.NATIONAL_ADMIN})
	public ResponseEntity<?> createSubFolder(
                @PathVariable("groupName") @NotBlank @Pattern(regexp = GROUP_NAME_REGEX) String groupName,
                @PathVariable(name = "rootFolder") @NotBlank @Pattern(regexp = FOLDER_PATH_REGEX) String rootFolder,
                @PathVariable(name = "subfolder") @NotBlank @Pattern(regexp = FOLDER_PATH_REGEX) String subfolder
        ) throws Exception {
                String fullFolder = rootFolder.toLowerCase().trim() + subfolder.toLowerCase().trim();
		configsService.createFolder(groupName.toLowerCase().trim(), fullFolder);
		return new ResponseEntity<FolderData>(configsService.getFolderData(groupName.toLowerCase().trim(), fullFolder), new HttpHeaders(), HttpStatus.CREATED);
	}

	@PutMapping(value=FOLDERS_CONTEXT_PATH)
	public ResponseEntity<?> updateFolder(@RequestBody @Valid FolderProperties folderProperties, @PathVariable("groupName") @NotBlank @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @NotBlank @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath) throws Exception {
		configsService.updateFolder(groupName.toLowerCase().trim(), folderPath.toLowerCase().trim(), folderProperties);
		return new ResponseEntity<FolderData>(configsService.getFolderData(groupName.toLowerCase().trim(), folderPath.toLowerCase().trim()), new HttpHeaders(), HttpStatus.OK);
	}

	@GetMapping(value=FOLDERS_CONTEXT_PATH, produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getFolder(@PathVariable("groupName") @NotBlank @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @NotBlank @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath) throws Exception {
            return new ResponseEntity<FolderData>(configsService.getFolderData(groupName.toLowerCase().trim(), folderPath.toLowerCase().trim()), new HttpHeaders(), HttpStatus.OK);
	}

	@DeleteMapping(value=ROOT_FOLDERS_CONTEXT_PATH)
    @RolesAllowed({Roles.NATIONAL_ADMIN})
	public ResponseEntity<?> deleteRootFolder(
        @PathVariable("groupName") @NotBlank @Pattern(regexp = GROUP_NAME_REGEX) String groupName,
        @PathVariable @NotBlank @Pattern(regexp = FOLDER_PATH_REGEX) String rootFolder
    ) throws Exception {
		configsService.deleteFolder(groupName.toLowerCase().trim(), rootFolder.toLowerCase().trim());
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}

    @DeleteMapping(value=SUBFOLDERS_CONTEXT_PATH)
    @RolesAllowed({Roles.LOCAL_DATA_MANAGER, Roles.NATIONAL_ADMIN})
	public ResponseEntity<?> deleteSubFolder(
        @PathVariable("groupName") @NotBlank @Pattern(regexp = GROUP_NAME_REGEX) String groupName,
        @PathVariable(name = "rootFolder") @NotBlank @Pattern(regexp = FOLDER_PATH_REGEX) String rootFolder,
        @PathVariable(name = "subfolder") @NotBlank @Pattern(regexp = FOLDER_PATH_REGEX) String subfolder
    ) throws Exception {
        String fullFolder = rootFolder.toLowerCase().trim() + subfolder.toLowerCase().trim();
		configsService.deleteFolder(groupName.toLowerCase().trim(), fullFolder);
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}
    
	// Reports
	@PostMapping(value=REPORTS_CONTEXT_PATH)
	public ResponseEntity<?> createReport(@RequestBody @Valid SavedReportConfiguration newReport, @PathVariable("groupName") @NotBlank @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @NotBlank @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath) throws Exception {
		newReport.setId(UUID.randomUUID().toString());
		newReport.setCreatedUser(getRequestingUser());
		newReport.setLastModifiedUser(getRequestingUser());
		newReport.setCreatedDate(Instant.now(clock));
		newReport.setLastModifiedDate(Instant.now(clock));
		configsService.saveReport(groupName.toLowerCase().trim(), folderPath.toLowerCase().trim(), newReport, false);
		return new ResponseEntity<SavedReportConfiguration>(newReport, new HttpHeaders(), HttpStatus.CREATED);
	}
	
	@PutMapping(value=REPORTS_CONTEXT_PATH)
	public ResponseEntity<?> updateReport(@RequestBody @Valid SavedReportConfiguration updatedReport, @PathVariable("groupName") @NotBlank @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @NotBlank @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath, @RequestParam String reportId) throws Exception {
		updatedReport.setId(reportId);
		updatedReport.setLastModifiedUser(getRequestingUser());
		updatedReport.setLastModifiedDate(Instant.now(clock));
		configsService.saveReport(groupName.toLowerCase().trim(), folderPath.toLowerCase().trim(), updatedReport, true);
		return new ResponseEntity<SavedReportConfiguration>(updatedReport, new HttpHeaders(), HttpStatus.OK);
	}

	@DeleteMapping(value=REPORTS_CONTEXT_PATH)
	public ResponseEntity<String> deleteReport(@PathVariable("groupName") @NotBlank @Pattern(regexp = GROUP_NAME_REGEX) String groupName, @RequestParam @NotBlank @Pattern(regexp = FOLDER_PATH_REGEX) String folderPath, @RequestParam String reportId) throws Exception {
		configsService.deleteReport(groupName.toLowerCase().trim(), folderPath.toLowerCase().trim(), reportId);
		return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.OK);
	}

	// Handle Validation Exceptions
	@ExceptionHandler(ValidationException.class)
    public void constraintValidationExceptionHandler(HttpServletResponse response) throws Exception {
        response.sendError(HttpStatus.BAD_REQUEST.value());
	}
	
	// Handle NotFound Exceptions
	@ExceptionHandler({GroupDoesNotExistException.class,FolderDoesNotExistException.class,ReportDoesNotExistException.class})
    public void doesNotExistExceptionHandler(Exception exception, HttpServletResponse response) throws Exception {
        response.sendError(HttpStatus.NOT_FOUND.value(), exception.getMessage());
	}
	
	// Handle BadRequest Exceptions
	@ExceptionHandler({GroupAlreadyExistsException.class,FolderAlreadyExistsException.class,ReportAlreadyExistsException.class,FolderCannotStoreReportsException.class})
    public void alreadyExistsExceptionHandler(Exception exception, HttpServletResponse response) throws Exception {
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
	}

	// Handle JSON Exceptions
	@ExceptionHandler({JsonProcessingException.class})
	public void jsonProcessingExceptionHandler(HttpServletResponse response) throws Exception {
		response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to load folder data from S3.");
	}
	
	String getRequestingUser() {
		String username = UNKNOWN_USERNAME;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(null != authentication && !(authentication instanceof AnonymousAuthenticationToken)) {
			username = authentication.getName();
		}

		return username;

	}
}