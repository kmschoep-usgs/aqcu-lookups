package gov.usgs.aqcu.reports;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import gov.usgs.aqcu.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gov.usgs.aqcu.aws.S3Service;
import gov.usgs.aqcu.exception.FolderAlreadyExistsException;
import gov.usgs.aqcu.exception.FolderCannotStoreReportsException;
import gov.usgs.aqcu.exception.FolderDoesNotExistException;
import gov.usgs.aqcu.exception.GroupAlreadyExistsException;
import gov.usgs.aqcu.exception.GroupDoesNotExistException;
import gov.usgs.aqcu.exception.ReportAlreadyExistsException;
import gov.usgs.aqcu.exception.ReportDoesNotExistException;
import gov.usgs.aqcu.model.config.persist.SavedReportConfiguration;
import gov.usgs.aqcu.model.config.persist.GroupConfig;
import gov.usgs.aqcu.model.config.persist.FolderConfig;
import gov.usgs.aqcu.model.config.persist.FolderProperties;
import gov.usgs.aqcu.model.config.GroupData;
import gov.usgs.aqcu.model.config.FolderData;

@Service
public class ReportConfigsService {	
	public static final String REPORT_CONFIG_FILE_NAME = "reports.json";
	public static final String GROUP_CONFIG_FILE_NAME = "config.json";

	private S3Service s3Service;
	private ObjectMapper mapper;
	private List<String> defaultGroupFolders;

	@Autowired
	public ReportConfigsService(S3Service s3Service) {
		this.s3Service = s3Service;
		mapper = new ObjectMapper().registerModule(new JavaTimeModule())
			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	@Value("${saved-configs.groups.default-folders:}")
	public void setDefaultGroupFolders(String defaultGroupFoldersCsv) {
		this.defaultGroupFolders = !StringUtils.isNullOrEmpty(defaultGroupFoldersCsv) ? 
			Arrays.asList(defaultGroupFoldersCsv.split(",")) : new ArrayList<>();
	}

	// Groups
	public GroupData getGroupData(String groupName) throws IOException {
		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		GroupConfig config = loadGroupConfig(groupName);

		GroupData result = new GroupData();
		result.setGroupName(groupName);
		result.setFolders(getFolderSubFolders(groupName, ""));
		result.setProperties(config.getGroupProperties());

		return result;
	}

	public void createGroup(String groupName) throws IOException {
		if(doesGroupExist(groupName)) {
			throw new GroupAlreadyExistsException(groupName);
		}

		saveGroupConfig(groupName, new GroupConfig());

		// Create default folders within group
		if(this.defaultGroupFolders != null && !this.defaultGroupFolders.isEmpty()) {
			for(String folder : this.defaultGroupFolders) {
				saveFolderConfig(groupName, folder, new FolderConfig());
			}
		}
	}

	public void deleteGroup(String groupName) {
		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		s3Service.deleteFolder(groupName);
	}

	public List<String> getAllGroups() {
		return s3Service.getSubFolderNames("").stream().filter(g -> doesGroupExist(g)).collect(Collectors.toList());
	}

	private void saveGroupConfig(String groupName, GroupConfig groupConfig) throws IOException {
		s3Service.saveJsonString(
			Paths.get(groupName, GROUP_CONFIG_FILE_NAME).toString(), 
			mapper.writeValueAsString(groupConfig)
		);
	}

	private GroupConfig loadGroupConfig(String groupName) throws IOException {
		String groupConfigFileString = s3Service.getFileAsString(Paths.get(groupName, GROUP_CONFIG_FILE_NAME).toString());
		return mapper.readValue(groupConfigFileString, GroupConfig.class);
	}

	private Boolean doesGroupExist(String groupName) {
		return s3Service.doesFileExist(Paths.get(groupName, GROUP_CONFIG_FILE_NAME).toString());
	}

	// Folders
	public FolderData getFolderData(String groupName, String folderPath) throws IOException {
		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		if(!doesFolderExist(groupName, folderPath)) {
			throw new FolderDoesNotExistException(groupName, folderPath);
		}

		FolderConfig folderConfig = loadFolderConfig(groupName, folderPath);

		FolderData result = new FolderData();
		result.setGroupName(groupName);
		result.setFolderName(parseFolderName(folderPath));
		result.setCurrentPath(folderPath);
		result.setFolders(getFolderSubFolders(groupName, folderPath));
		result.setReports(new ArrayList<>(folderConfig.getSavedReports().values()));
		result.setProperties(folderConfig.getProperties());

		return result;
	}

	private String parseFolderName (String folderPath) {
		List<String> paths = new ArrayList<String>(Arrays.asList(folderPath.split("/")));
		String folderName = null;
		if (!paths.isEmpty()) {
			folderName = paths.get(paths.size() - 1);
		}
		return folderName;
	}
	
	public void createFolder(String groupName, String folderPath) throws IOException {
		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		if(doesFolderExist(groupName, folderPath)) {
			throw new FolderAlreadyExistsException(groupName, folderPath);
		}

		String parentPath = getParentPath(folderPath);

		if(!StringUtils.isNullOrEmpty(parentPath) && !doesFolderExist(groupName, parentPath)) {
			throw new FolderDoesNotExistException(groupName, parentPath);
		}
		
		saveFolderConfig(groupName, folderPath, new FolderConfig());
	}

	public void updateFolder(String groupName, String folderPath, FolderProperties properties) throws IOException {
		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		if(!doesFolderExist(groupName, folderPath)) {
			throw new FolderDoesNotExistException(groupName, folderPath);
		}

		FolderConfig folderConfig = loadFolderConfig(groupName, folderPath);

		folderConfig.setProperties(properties);
		
		saveFolderConfig(groupName, folderPath, folderConfig);
	}

	public void deleteFolder(String groupName, String folderPath) {
		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		if(!doesFolderExist(groupName, folderPath)) {
			throw new FolderDoesNotExistException(groupName, folderPath);
		}

		s3Service.deleteFolder(Paths.get(groupName, folderPath).toString());
	}

	public List<String> getFolderSubFolders(String groupName, String folderPath) {
		return s3Service.getSubFolderNames(Paths.get(groupName, folderPath).toString()).stream()
				.filter(f -> doesFolderExist(groupName, Paths.get(folderPath, f).toString()))
				.collect(Collectors.toList());
	}

	private void saveFolderConfig(String groupName, String folderPath, FolderConfig folderConfig) throws IOException {
		s3Service.saveJsonString(
			Paths.get(groupName, folderPath, REPORT_CONFIG_FILE_NAME).toString(), 
			mapper.writeValueAsString(folderConfig)
		);
	}

	private FolderConfig loadFolderConfig(String groupName, String folderPath) throws IOException {
		String folderConfigString = s3Service.getFileAsString(Paths.get(groupName, folderPath, REPORT_CONFIG_FILE_NAME).toString());
		return mapper.readValue(folderConfigString, FolderConfig.class);
	}

	private Boolean doesFolderExist(String groupName, String folderPath) {
		return s3Service.doesFileExist(Paths.get(groupName, folderPath, REPORT_CONFIG_FILE_NAME).toString());
	}

	// Reports
	public void saveReport(String groupName, String folderPath, SavedReportConfiguration newReport, Boolean update) throws IOException {
		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		if(!doesFolderExist(groupName, folderPath)) {
			throw new FolderDoesNotExistException(groupName, folderPath);
		}

		FolderConfig folderConfig = loadFolderConfig(groupName, folderPath);

		if(folderConfig.getProperties() == null || !folderConfig.getProperties().getCanStoreReports()) {
			throw new FolderCannotStoreReportsException(groupName, folderPath);
		}

		if(doesReportTypeExists(folderConfig, newReport)){
			throw new ReportTypeAlreadyExistsException(folderPath);
		}
		Boolean reportExists = folderConfig.doesReportExist(newReport.getId());


		if(update && !reportExists) {
			throw new ReportDoesNotExistException(groupName, folderPath, newReport.getId());
		} else if(!update && reportExists) {
			throw new ReportAlreadyExistsException(groupName, folderPath, newReport.getId());
		}
		
		if(update) {
			SavedReportConfiguration existingReport = folderConfig.getSavedReportById(newReport.getId());
			newReport.setCreatedUser(existingReport.getCreatedUser());
			newReport.setCreatedDate(existingReport.getCreatedDate());
		}

		folderConfig.saveReport(newReport);

		saveFolderConfig(groupName, folderPath, folderConfig);  
	}
	
	public void deleteReport(String groupName, String folderPath, String reportId) throws IOException {
		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		if(!doesFolderExist(groupName, folderPath)) {
			throw new FolderDoesNotExistException(groupName, folderPath);
		}

		FolderConfig folderConfig = loadFolderConfig(groupName, folderPath);

		if(!folderConfig.doesReportExist(reportId)) {
			throw new ReportDoesNotExistException(groupName, folderPath, reportId);
		}

		folderConfig.deleteSavedReportById(reportId);
		saveFolderConfig(groupName, folderPath, folderConfig);
	}

	public String getParentPath(String path) {
		Path parent = Paths.get(path).getParent();
		return parent == null ? null : parent.toString();
	}

	public boolean doesReportTypeExists(FolderConfig folderConfig, SavedReportConfiguration newReport) {
		Map<String, SavedReportConfiguration> savedReportByType = folderConfig.getSavedReportByType();
		return savedReportByType.containsKey(newReport.getReportType());
	}


}