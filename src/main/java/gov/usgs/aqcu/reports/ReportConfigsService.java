package gov.usgs.aqcu.reports;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.usgs.aqcu.aws.S3Service;
import gov.usgs.aqcu.exception.FolderAlreadyExistsException;
import gov.usgs.aqcu.exception.FolderDoesNotExistException;
import gov.usgs.aqcu.exception.GroupAlreadyExistsException;
import gov.usgs.aqcu.exception.GroupDoesNotExistException;
import gov.usgs.aqcu.exception.ReportAlreadyExistsException;
import gov.usgs.aqcu.exception.ReportDoesNotExistException;
import gov.usgs.aqcu.model.report.SavedReportConfiguration;
import gov.usgs.aqcu.model.config.GroupConfig;
import gov.usgs.aqcu.model.config.GroupData;
import gov.usgs.aqcu.model.config.FolderData;
import gov.usgs.aqcu.model.config.ReportsConfig;

@Service
public class ReportConfigsService {	
	public static final String REPORT_CONFIG_FILE_NAME = "reports.json";
	public static final String GROUP_CONFIG_FILE_NAME = "config.json";

	private S3Service s3Service;

	@Autowired
	public ReportConfigsService(S3Service s3Service) {
		this.s3Service = s3Service;
	}

	// Groups
	public GroupData getGroupData(String groupName) throws IOException {
		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		GroupData result = new GroupData();
		result.setGroupName(groupName);
		result.setFolders(s3Service.getFolderSubPaths(groupName));
		result.setConfig(loadGroupConfig(groupName));

		return result;
	}

	public void createGroup(String groupName) throws IOException {
		if(doesGroupExist(groupName)) {
			throw new GroupAlreadyExistsException(groupName);
		}

		saveGroupConfig(groupName, new GroupConfig());
	}

	public void deleteGroup(String groupName) {
		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		s3Service.deleteFolder(groupName);
	}

	public List<String> getAllGroups() {
		return s3Service.getFolderSubPaths("").stream().map(g -> g.endsWith("/") ? g.substring(0, g.length()-1) : g).collect(Collectors.toList());
	}

	private void saveGroupConfig(String groupName, GroupConfig groupConfig) throws IOException {
		s3Service.saveJsonString(
			Paths.get(groupName, GROUP_CONFIG_FILE_NAME).toString(), 
			new ObjectMapper().writeValueAsString(groupConfig)
		);
	}

	private GroupConfig loadGroupConfig(String groupName) throws IOException {
		String groupConfigFileString = s3Service.getFileAsString(Paths.get(groupName, GROUP_CONFIG_FILE_NAME).toString());
		return new ObjectMapper().readValue(groupConfigFileString, GroupConfig.class);
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

		ReportsConfig reportsConfig = loadFolderReportsConfig(groupName, folderPath);

		FolderData result = new FolderData();
		result.setGroupName(groupName);
		result.setCurrentPath(folderPath);
		result.setFolders(s3Service.getFolderSubPaths(Paths.get(groupName, folderPath).toString()));
		result.setReports(reportsConfig.getSavedReportsList());
		result.setParameterDefaults(reportsConfig.getParameterDefaults());

		return result;
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
		
		saveFolderReportsConfig(groupName, folderPath, new ReportsConfig());
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

	private void saveFolderReportsConfig(String groupName, String folderPath, ReportsConfig reportsConfig) throws IOException {
		s3Service.saveJsonString(
			Paths.get(groupName, folderPath, REPORT_CONFIG_FILE_NAME).toString(), 
			new ObjectMapper().writeValueAsString(reportsConfig)
		);
	}

	private ReportsConfig loadFolderReportsConfig(String groupName, String folderPath) throws IOException {
		String folderReportsConfigString = s3Service.getFileAsString(Paths.get(groupName, folderPath, REPORT_CONFIG_FILE_NAME).toString());
		return new ObjectMapper().readValue(folderReportsConfigString, ReportsConfig.class);
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

		ReportsConfig reportsConfig = loadFolderReportsConfig(groupName, folderPath);
		Boolean reportExists = reportsConfig.doesReportExist(newReport.getId());


		if(update && !reportExists) {
			throw new ReportDoesNotExistException(groupName, folderPath, newReport.getId());
		} else if(!update && reportExists) {
			throw new ReportAlreadyExistsException(groupName, folderPath, newReport.getId());
		}
		
		reportsConfig.saveReport(newReport);

		saveFolderReportsConfig(groupName, folderPath, reportsConfig);
	}

	public void deleteReport(String groupName, String folderPath, String reportId) throws IOException {
		if(!doesGroupExist(groupName)) {
			throw new GroupDoesNotExistException(groupName);
		}

		if(!doesFolderExist(groupName, folderPath)) {
			throw new FolderDoesNotExistException(groupName, folderPath);
		}

		ReportsConfig reportsConfig = loadFolderReportsConfig(groupName, folderPath);

		if(!reportsConfig.doesReportExist(reportId)) {
			throw new ReportDoesNotExistException(groupName, folderPath, reportId);
		}

		reportsConfig.deleteSavedReportById(reportId);
		saveFolderReportsConfig(groupName, folderPath, reportsConfig);
	}

	public String getParentPath(String path) {
		Path parent = Paths.get(path).getParent();
		return parent == null ? null : parent.toString();
	}
}