package gov.usgs.aqcu.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import gov.usgs.aqcu.aws.S3Service;
import gov.usgs.aqcu.exception.FolderAlreadyExistsException;
import gov.usgs.aqcu.exception.FolderDoesNotExistException;
import gov.usgs.aqcu.exception.GroupAlreadyExistsException;
import gov.usgs.aqcu.exception.GroupDoesNotExistException;
import gov.usgs.aqcu.exception.ReportAlreadyExistsException;
import gov.usgs.aqcu.exception.ReportDoesNotExistException;
import gov.usgs.aqcu.model.config.FolderData;
import gov.usgs.aqcu.model.config.GroupConfig;
import gov.usgs.aqcu.model.config.GroupData;
import gov.usgs.aqcu.model.config.ReportsConfig;
import gov.usgs.aqcu.model.report.SavedReportConfiguration;
import gov.usgs.aqcu.reports.ReportConfigsService;

@RunWith(SpringRunner.class)
public class ReportConfigsServiceTest {	
    @MockBean
    S3Service s3Service;

    ReportConfigsService service;

    private final String TEST_GROUP_NAME = "test_group/";
    private final String TEST_FOLDER_NAME = "test_folder/";
    private final String TEST_SUB_FOLDER_NAME = "test_sub_folder/";

    @Before
    public void setup() {
        service = new ReportConfigsService(s3Service);
    }

    @Test
    public void getGroupDataBasicTest() throws Exception {
        GroupConfig basicConfig = new GroupConfig();
        basicConfig.setAuthorizedUsers(Arrays.asList("user_1"));
        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.getFolderSubPaths(TEST_GROUP_NAME)).willReturn(Arrays.asList("folder_1", "folder_2"));
        given(s3Service.getFileAsString(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(new ObjectMapper().writeValueAsString(basicConfig));

        GroupData result = service.getGroupData(TEST_GROUP_NAME);
        assertThat(result.getConfig().getAuthorizedUsers(), containsInAnyOrder("user_1"));
        assertThat(result.getFolders(), containsInAnyOrder("folder_1", "folder_2"));
        assertEquals(TEST_GROUP_NAME, result.getGroupName());

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.getFolderSubPaths(TEST_GROUP_NAME)).willReturn(new ArrayList<>());
        given(s3Service.getFileAsString(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(new ObjectMapper().writeValueAsString(new GroupConfig()));

        result = service.getGroupData(TEST_GROUP_NAME);
        assertTrue(result.getConfig().getAuthorizedUsers().isEmpty());
        assertTrue(result.getFolders().isEmpty());
        assertEquals(TEST_GROUP_NAME, result.getGroupName());
    }

    @Test
    public void getGroupDataNotExistTest() {
        GroupConfig basicConfig = new GroupConfig();
        basicConfig.setAuthorizedUsers(Arrays.asList("user_1"));
        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(false);

        try {
            service.getGroupData(TEST_GROUP_NAME);
            fail("Expected GroupDoesNotExistException but got no exception.");
        } catch(GroupDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }
    }

    @Test
    public void createGroupBasicTest() throws Exception {
        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(false);
        doNothing().when(s3Service).saveJsonString(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME, new ObjectMapper().writeValueAsString(new GroupConfig()));
        service.createGroup(TEST_GROUP_NAME);
    }
    
    @Test
    public void createGroupAlreadyExistsTest() throws Exception {
        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);

        try {
            service.createGroup(TEST_GROUP_NAME);
            fail("Expected GroupAlreadyExistsException but got no exception.");
        } catch(GroupAlreadyExistsException e) {
            // Success
        } catch(Exception e) {
            fail("Expected GroupAlreadyExistsException but got " + e.getClass().getName());
        }
    }

    @Test
    public void deleteGroupBasicTest() {
        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        doNothing().when(s3Service).deleteFolder(TEST_GROUP_NAME);
        service.deleteGroup(TEST_GROUP_NAME);
    }

    @Test
    public void deleteGroupNotExistTest() {
        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(false);
        
        try {
            service.deleteGroup(TEST_GROUP_NAME);
            fail("Expected GroupDoesNotExistException but got no exception.");
        } catch(GroupDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }
    }
    
    @Test
    public void getFolderDataBasicTest() throws Exception {
        HashMap<String, String> basicDefaults = new HashMap<>();
        basicDefaults.put("test_key", "test_value");
        SavedReportConfiguration basicReport = new SavedReportConfiguration();
        basicReport.setCreatedUser("test_user");
        basicReport.setId("test_report");
        basicReport.setLastModifiedUser("test_user");
        basicReport.setParameterValues(basicDefaults);
        basicReport.setReportName("test_name");
        basicReport.setReportType("test_type");
        HashMap<String, SavedReportConfiguration> basicReports = new HashMap<>();
        basicReports.put("test_report", basicReport);
        ReportsConfig basicConfig = new ReportsConfig();
        basicConfig.setParameterDefaults(basicDefaults);
        basicConfig.setSavedReports(basicReports);

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.getFileAsString(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(new ObjectMapper().writeValueAsString(basicConfig));
        given(s3Service.getFolderSubPaths(TEST_GROUP_NAME + TEST_FOLDER_NAME)).willReturn(Arrays.asList("folder_1", "folder_2"));

        FolderData result = service.getFolderData(TEST_GROUP_NAME, TEST_FOLDER_NAME);
        assertEquals(TEST_FOLDER_NAME, result.getCurrentPath());
        assertEquals(TEST_GROUP_NAME, result.getGroupName());
        assertEquals(1, result.getReports().size());
        assertTrue(jsonEqual(result.getReports().get(0), basicReport));
        assertTrue(jsonEqual(basicDefaults, result.getParameterDefaults()));
        assertThat(result.getFolders(), containsInAnyOrder("folder_1", "folder_2"));

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.getFileAsString(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(new ObjectMapper().writeValueAsString(new ReportsConfig()));
        given(s3Service.getFolderSubPaths(TEST_GROUP_NAME + TEST_FOLDER_NAME)).willReturn(new ArrayList<>());

        result = service.getFolderData(TEST_GROUP_NAME, TEST_FOLDER_NAME);
        assertEquals(TEST_FOLDER_NAME, result.getCurrentPath());
        assertEquals(TEST_GROUP_NAME, result.getGroupName());
        assertTrue(result.getReports().isEmpty());
        assertTrue(result.getParameterDefaults().isEmpty());
        assertTrue(result.getFolders().isEmpty());
    }

    @Test
    public void getFolderDataNotExistTest() {
        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(false);
        try {
            service.getFolderData(TEST_GROUP_NAME, TEST_FOLDER_NAME);
            fail("Expected FolderDoesNotExistException but got no exception.");
        } catch(FolderDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected FolderDoesNotExistException but got " + e.getClass().getName());
        }

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(false);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(true);
        try {
            service.getFolderData(TEST_GROUP_NAME, TEST_FOLDER_NAME);
            fail("Expected GroupDoesNotExistException but got no exception.");
        } catch(GroupDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(false);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(false);
        try {
            service.getFolderData(TEST_GROUP_NAME, TEST_FOLDER_NAME);
            fail("Expected GroupDoesNotExistException but got no exception.");
        } catch(GroupDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }
    }

    @Test
    public void createFolderBasicTest() throws Exception {
        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(false);
        doNothing().when(s3Service).saveJsonString(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME, new ObjectMapper().writeValueAsString(new ReportsConfig()));

        service.createFolder(TEST_GROUP_NAME, TEST_FOLDER_NAME);
    }

    @Test
    public void createFolderNestedTest() throws Exception {
        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + TEST_SUB_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(false);
        doNothing().when(s3Service).saveJsonString(TEST_GROUP_NAME + TEST_FOLDER_NAME + TEST_SUB_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME, new ObjectMapper().writeValueAsString(new ReportsConfig()));

        service.createFolder(TEST_GROUP_NAME, TEST_FOLDER_NAME + TEST_SUB_FOLDER_NAME);
    }

    @Test
    public void createFolderNotExistTest() throws Exception {
        doNothing().when(s3Service).saveJsonString(TEST_GROUP_NAME + TEST_FOLDER_NAME + TEST_SUB_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME, new ObjectMapper().writeValueAsString(new ReportsConfig()));

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(false);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(true);
        try {
            service.createFolder(TEST_GROUP_NAME, TEST_FOLDER_NAME + TEST_SUB_FOLDER_NAME);
            fail("Expected GroupDoesNotExistException but got no exception.");
        } catch(GroupDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(false);
        try {
            service.createFolder(TEST_GROUP_NAME, TEST_FOLDER_NAME + TEST_SUB_FOLDER_NAME);
            fail("Expected FolderDoesNotExistException but got no exception.");
        } catch(FolderDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected FolderDoesNotExistException but got " + e.getClass().getName());
        }

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(false);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(false);
        try {
            service.createFolder(TEST_GROUP_NAME, TEST_FOLDER_NAME + TEST_SUB_FOLDER_NAME);
            fail("Expected GroupDoesNotExistException but got no exception.");
        } catch(GroupDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }
    }

    @Test
    public void createFolderAlreadyExistsTest() {
        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + TEST_SUB_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(true);

        try {
            service.createFolder(TEST_GROUP_NAME, TEST_FOLDER_NAME + TEST_SUB_FOLDER_NAME);
            fail("Expected FolderAlreadyExistsException but got no exception.");
        } catch(FolderAlreadyExistsException e) {
            // Success
        } catch(Exception e) {
            fail("Expected FolderAlreadyExistsException but got " + e.getClass().getName());
        }
    }

    @Test
    public void deleteFolderBasicTest() {
        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(true);
        doNothing().when(s3Service).deleteFolder(TEST_GROUP_NAME + TEST_FOLDER_NAME);

        service.deleteFolder(TEST_GROUP_NAME, TEST_FOLDER_NAME);
    }

    @Test
    public void deleteFolderNotExistTest() {
        doNothing().when(s3Service).deleteFolder(TEST_GROUP_NAME + TEST_FOLDER_NAME);

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(false);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(true);
        try {
            service.deleteFolder(TEST_GROUP_NAME, TEST_FOLDER_NAME);
            fail("Expected GroupDoesNotExistException but got no exception.");
        } catch(GroupDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(false);
        try {
            service.deleteFolder(TEST_GROUP_NAME, TEST_FOLDER_NAME);
            fail("Expected FolderDoesNotExistException but got no exception.");
        } catch(FolderDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected FolderDoesNotExistException but got " + e.getClass().getName());
        }

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(false);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(false);
        try {
            service.deleteFolder(TEST_GROUP_NAME, TEST_FOLDER_NAME);
            fail("Expected GroupDoesNotExistException but got no exception.");
        } catch(GroupDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }
    }

    @Test
    public void saveReportBasicTest() throws Exception {
        HashMap<String, String> basicDefaults = new HashMap<>();
        basicDefaults.put("test_key", "test_value");
        SavedReportConfiguration basicReport = new SavedReportConfiguration();
        basicReport.setCreatedUser("test_user");
        basicReport.setId("test_report");
        basicReport.setLastModifiedUser("test_user");
        basicReport.setParameterValues(basicDefaults);
        basicReport.setReportName("test_name");
        basicReport.setReportType("test_type");
        SavedReportConfiguration newReport = new SavedReportConfiguration();
        newReport.setCreatedUser("test_user");
        newReport.setId("test_new_report");
        newReport.setLastModifiedUser("test_user");
        newReport.setParameterValues(basicDefaults);
        newReport.setReportName("test_name");
        newReport.setReportType("test_type");
        HashMap<String, SavedReportConfiguration> basicReports = new HashMap<>();
        basicReports.put("test_report", basicReport);
        ReportsConfig basicConfig = new ReportsConfig();
        basicConfig.setParameterDefaults(basicDefaults);
        basicConfig.setSavedReports(basicReports);

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.getFileAsString(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(new ObjectMapper().writeValueAsString(basicConfig));

        service.saveReport(TEST_GROUP_NAME, TEST_FOLDER_NAME, newReport, false);
        newReport.setId("test_report");
        service.saveReport(TEST_GROUP_NAME, TEST_FOLDER_NAME, newReport, true);
    }


    @Test
    public void saveReportNotExistTest() throws Exception {
        HashMap<String, String> basicDefaults = new HashMap<>();
        basicDefaults.put("test_key", "test_value");
        SavedReportConfiguration basicReport = new SavedReportConfiguration();
        basicReport.setCreatedUser("test_user");
        basicReport.setId("test_report");
        basicReport.setLastModifiedUser("test_user");
        basicReport.setParameterValues(basicDefaults);
        basicReport.setReportName("test_name");
        basicReport.setReportType("test_type");
        SavedReportConfiguration newReport = new SavedReportConfiguration();
        newReport.setCreatedUser("test_user");
        newReport.setId("test_new_report");
        newReport.setLastModifiedUser("test_user");
        newReport.setParameterValues(basicDefaults);
        newReport.setReportName("test_name");
        newReport.setReportType("test_type");
        HashMap<String, SavedReportConfiguration> basicReports = new HashMap<>();
        basicReports.put("test_report", basicReport);
        ReportsConfig basicConfig = new ReportsConfig();
        basicConfig.setParameterDefaults(basicDefaults);
        basicConfig.setSavedReports(basicReports);

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(false);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.getFileAsString(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(new ObjectMapper().writeValueAsString(basicConfig));
        try {
            service.saveReport(TEST_GROUP_NAME, TEST_FOLDER_NAME, newReport, false);
            fail("Expected GroupDoesNotExistException but got no exception.");
        } catch(GroupDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }
        
        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(false);
        given(s3Service.getFileAsString(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(new ObjectMapper().writeValueAsString(basicConfig));
        try {
            service.saveReport(TEST_GROUP_NAME, TEST_FOLDER_NAME, newReport, false);
            fail("Expected FolderDoesNotExistException but got no exception.");
        } catch(FolderDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected FolderDoesNotExistException but got " + e.getClass().getName());
        }
        
        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.getFileAsString(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(new ObjectMapper().writeValueAsString(basicConfig));
        service.saveReport(TEST_GROUP_NAME, TEST_FOLDER_NAME, newReport, false);
        try {
            service.saveReport(TEST_GROUP_NAME, TEST_FOLDER_NAME, newReport, true);
            fail("Expected ReportDoesNotExistException but got no exception.");
        } catch(ReportDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected ReportDoesNotExistException but got " + e.getClass().getName());
        }

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(false);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(false);
        given(s3Service.getFileAsString(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(new ObjectMapper().writeValueAsString(basicConfig));
        try {
            service.saveReport(TEST_GROUP_NAME, TEST_FOLDER_NAME, newReport, true);
            fail("Expected GroupDoesNotExistException but got no exception.");
        } catch(GroupDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }
    }

    @Test
    public void saveReportAlreadyExistsTest() throws Exception {
        HashMap<String, String> basicDefaults = new HashMap<>();
        basicDefaults.put("test_key", "test_value");
        SavedReportConfiguration basicReport = new SavedReportConfiguration();
        basicReport.setCreatedUser("test_user");
        basicReport.setId("test_report");
        basicReport.setLastModifiedUser("test_user");
        basicReport.setParameterValues(basicDefaults);
        basicReport.setReportName("test_name");
        basicReport.setReportType("test_type");
        HashMap<String, SavedReportConfiguration> basicReports = new HashMap<>();
        basicReports.put("test_report", basicReport);
        ReportsConfig basicConfig = new ReportsConfig();
        basicConfig.setParameterDefaults(basicDefaults);
        basicConfig.setSavedReports(basicReports);

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.getFileAsString(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(new ObjectMapper().writeValueAsString(basicConfig));
        try {
            service.saveReport(TEST_GROUP_NAME, TEST_FOLDER_NAME, basicReport, false);
            fail("Expected ReportAlreadyExistsException but got no exception.");
        } catch(ReportAlreadyExistsException e) {
            // Success
        } catch(Exception e) {
            fail("Expected ReportAlreadyExistsException but got " + e.getClass().getName());
        }
    }

    @Test
    public void deleteReportBasicTest() throws Exception {
        HashMap<String, String> basicDefaults = new HashMap<>();
        basicDefaults.put("test_key", "test_value");
        SavedReportConfiguration basicReport = new SavedReportConfiguration();
        basicReport.setCreatedUser("test_user");
        basicReport.setId("test_report");
        basicReport.setLastModifiedUser("test_user");
        basicReport.setParameterValues(basicDefaults);
        basicReport.setReportName("test_name");
        basicReport.setReportType("test_type");
        HashMap<String, SavedReportConfiguration> basicReports = new HashMap<>();
        basicReports.put("test_report", basicReport);
        ReportsConfig basicConfig = new ReportsConfig();
        basicConfig.setParameterDefaults(basicDefaults);
        basicConfig.setSavedReports(basicReports);

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.getFileAsString(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(new ObjectMapper().writeValueAsString(basicConfig));
        
        service.deleteReport(TEST_GROUP_NAME, TEST_FOLDER_NAME, "test_report");
    }

    @Test
    public void deleteReportNotExistTest() throws Exception {
        HashMap<String, String> basicDefaults = new HashMap<>();
        basicDefaults.put("test_key", "test_value");
        SavedReportConfiguration basicReport = new SavedReportConfiguration();
        basicReport.setCreatedUser("test_user");
        basicReport.setId("test_report");
        basicReport.setLastModifiedUser("test_user");
        basicReport.setParameterValues(basicDefaults);
        basicReport.setReportName("test_name");
        basicReport.setReportType("test_type");
        HashMap<String, SavedReportConfiguration> basicReports = new HashMap<>();
        basicReports.put("test_report", basicReport);
        ReportsConfig basicConfig = new ReportsConfig();
        basicConfig.setParameterDefaults(basicDefaults);
        basicConfig.setSavedReports(basicReports);

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(false);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.getFileAsString(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(new ObjectMapper().writeValueAsString(basicConfig));
        try {
            service.deleteReport(TEST_GROUP_NAME, TEST_FOLDER_NAME, "test_report");
            fail("Expected GroupDoesNotExistException but got no exception.");
        } catch(GroupDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }
        
        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(false);
        given(s3Service.getFileAsString(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(new ObjectMapper().writeValueAsString(basicConfig));
        try {
            service.deleteReport(TEST_GROUP_NAME, TEST_FOLDER_NAME, "test_report");
            fail("Expected FolderDoesNotExistException but got no exception.");
        } catch(FolderDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected FolderDoesNotExistException but got " + e.getClass().getName());
        }
        
        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(true);
        given(s3Service.getFileAsString(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(new ObjectMapper().writeValueAsString(basicConfig));
        service.deleteReport(TEST_GROUP_NAME, TEST_FOLDER_NAME, "test_report");

        try {
            service.deleteReport(TEST_GROUP_NAME, TEST_FOLDER_NAME, "test_other_report");
            fail("Expected ReportDoesNotExistException but got no exception.");
        } catch(ReportDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected ReportDoesNotExistException but got " + e.getClass().getName());
        }

        given(s3Service.doesFileExist(TEST_GROUP_NAME + ReportConfigsService.GROUP_CONFIG_FILE_NAME)).willReturn(false);
        given(s3Service.doesFileExist(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(false);
        given(s3Service.getFileAsString(TEST_GROUP_NAME + TEST_FOLDER_NAME + ReportConfigsService.REPORT_CONFIG_FILE_NAME)).willReturn(new ObjectMapper().writeValueAsString(basicConfig));
        try {
            service.deleteReport(TEST_GROUP_NAME, TEST_FOLDER_NAME, "test_report");
            fail("Expected GroupDoesNotExistException but got no exception.");
        } catch(GroupDoesNotExistException e) {
            // Success
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }
    }

    private Boolean jsonEqual(Object ob1, Object ob2) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(ob1).equals(mapper.writeValueAsString(ob2));
    }
}