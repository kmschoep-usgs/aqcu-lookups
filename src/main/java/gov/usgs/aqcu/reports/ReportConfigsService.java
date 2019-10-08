package gov.usgs.aqcu.reports;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gov.usgs.aqcu.aws.S3Service;
import gov.usgs.aqcu.exception.FolderAlreadyExistsException;
import gov.usgs.aqcu.exception.FolderDoesNotExistException;
import gov.usgs.aqcu.exception.GroupAlreadyExistsException;
import gov.usgs.aqcu.exception.GroupDoesNotExistException;
import gov.usgs.aqcu.model.report.SavedReportConfiguration;
import gov.usgs.aqcu.model.config.GroupConfig;
import gov.usgs.aqcu.model.config.GroupData;
import gov.usgs.aqcu.model.config.FolderData;
import gov.usgs.aqcu.model.config.ReportsConfig;
import gov.usgs.aqcu.util.PathUtils;

@Service
public class ReportConfigsService {	
	private static final String REPORT_CONFIG_FILE_NAME = "reports.json";
	private static final String GROUP_CONFIG_FILE_NAME = "config.json";

	@Value("${s3.bucket}")
	private String S3_BUCKET;

	private S3Service s3Service;

	@Autowired
	public ReportConfigsService(S3Service s3Service) {
		this.s3Service = s3Service;
	}

	// Groups
	public GroupData getGroupData(String groupNameString) throws IOException {
		final String groupName = PathUtils.trimPath(groupNameString);

		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		GroupData result = new GroupData();
		result.setGroupName(groupName);
		result.setFolders(s3Service.getFolderSubPaths(groupName));
		result.setConfig(loadGroupConfig(groupName));

		return result;
	}

	public void createGroup(String groupNameString) throws IOException {
		final String groupName = PathUtils.trimPath(groupNameString);

		if(doesGroupExist(groupName)) {
			throw new GroupAlreadyExistsException(groupName);
		}

		saveGroupConfig(groupName, new GroupConfig());
	}

	public void deleteGroup(String groupNameString) {
		final String groupName = PathUtils.trimPath(groupNameString);

		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		s3Service.deleteFolder(groupName);
	}

	public List<String> getAllGroups() {
		return s3Service.getFolderSubPaths("");
	}

	private void saveGroupConfig(String groupName, GroupConfig groupConfig) throws IOException {
		s3Service.saveJsonString(
			groupName + GROUP_CONFIG_FILE_NAME, 
			new ObjectMapper().writeValueAsString(groupConfig)
		);
	}

	private GroupConfig loadGroupConfig(String groupName) throws IOException {
		String groupConfigFileString = s3Service.getFileAsString(groupName + "/" + GROUP_CONFIG_FILE_NAME);
		return new ObjectMapper().readValue(groupConfigFileString, GroupConfig.class);
	}

	private Boolean doesGroupExist(String groupName) {
		return s3Service.doesFileExist(groupName + GROUP_CONFIG_FILE_NAME);
	}

	// Folders
	public FolderData getFolderData(String groupNameString, String folderPathString) throws IOException {
		final String groupName = PathUtils.trimPath(groupNameString);
		final String folderPath = PathUtils.trimPath(folderPathString);

		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		if(!doesFolderExist(groupName, folderPath)) {
			throw new FolderDoesNotExistException(groupName, folderPath);
		}

		FolderData result = new FolderData();
		result.setCurrentPath(folderPath);
		result.setFolders(s3Service.getFolderSubPaths(PathUtils.mergePaths(groupName, folderPath)));
		result.setReports(loadFolderReportsConfig(groupName, folderPath));

		return result;
	}

	public void createFolder(String groupNameString, String folderPathString) throws IOException {
		final String groupName = PathUtils.trimPath(groupNameString);
		final String folderPath = PathUtils.trimPath(folderPathString);

		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		if(doesFolderExist(groupName, folderPath)) {
			throw new FolderAlreadyExistsException(groupName, folderPath);
		}

		String parentPath = PathUtils.getParentPath(folderPath);
		if(!parentPath.isEmpty() && !doesFolderExist(groupName, parentPath)) {
			throw new FolderDoesNotExistException(groupName, parentPath);
		}
		
		saveFolderReportsConfig(groupName, folderPath, new ReportsConfig());
	}

	public void deleteFolder(String groupNameString, String folderPathString) {
		final String groupName = PathUtils.trimPath(groupNameString);
		final String folderPath = PathUtils.trimPath(folderPathString);

		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		if(!doesFolderExist(groupName, folderPath)) {
			throw new FolderDoesNotExistException(groupName, folderPath);
		}

		s3Service.deleteFolder(PathUtils.mergePaths(groupName, folderPath));
	}

	private void saveFolderReportsConfig(String groupName, String folderPath, ReportsConfig reportsConfig) throws IOException {
		s3Service.saveJsonString(
			PathUtils.mergePaths(groupName, folderPath) + REPORT_CONFIG_FILE_NAME, 
			new ObjectMapper().writeValueAsString(reportsConfig)
		);
	}

	private ReportsConfig loadFolderReportsConfig(String groupName, String folderPath) throws IOException {
		String folderReportsConfigString = s3Service.getFileAsString(PathUtils.mergePaths(groupName, folderPath) + REPORT_CONFIG_FILE_NAME);
		return new ObjectMapper().readValue(folderReportsConfigString, ReportsConfig.class);
	}

	private Boolean doesFolderExist(String groupName, String folderPath) {
		return s3Service.doesFileExist(PathUtils.mergePaths(groupName, folderPath) + REPORT_CONFIG_FILE_NAME);
	}

	// Reports
	public void saveReport(String groupNameString, String folderPathString, SavedReportConfiguration newReport) throws IOException {
		final String groupName = PathUtils.trimPath(groupNameString);
		final String folderPath = PathUtils.trimPath(folderPathString);

		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		if(!doesFolderExist(groupName, folderPath)) {
			throw new FolderDoesNotExistException(groupName, folderPath);
		}

		ReportsConfig reportsConfig = loadFolderReportsConfig(groupName, folderPath);

		reportsConfig.saveReport(newReport);

		saveFolderReportsConfig(groupName, folderPath, reportsConfig);
	}
}