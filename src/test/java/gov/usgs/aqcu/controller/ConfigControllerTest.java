package gov.usgs.aqcu.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Matchers.any;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.amazonaws.services.s3.model.AmazonS3Exception;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import gov.usgs.aqcu.exception.FolderAlreadyExistsException;
import gov.usgs.aqcu.exception.FolderDoesNotExistException;
import gov.usgs.aqcu.exception.GroupAlreadyExistsException;
import gov.usgs.aqcu.exception.GroupDoesNotExistException;
import gov.usgs.aqcu.exception.InvalidFolderNameException;
import gov.usgs.aqcu.exception.ReportAlreadyExistsException;
import gov.usgs.aqcu.exception.ReportDoesNotExistException;
import gov.usgs.aqcu.model.config.FolderData;
import gov.usgs.aqcu.model.config.GroupConfig;
import gov.usgs.aqcu.model.config.GroupData;
import gov.usgs.aqcu.model.report.SavedReportConfiguration;
import gov.usgs.aqcu.reports.ReportConfigsService;

@RunWith(SpringRunner.class)
@SuppressWarnings("unchecked")
public class ConfigControllerTest {	
    @MockBean
    ReportConfigsService service;

    ConfigController controller;

    @Before
    public void setup() {
        controller = new ConfigController(service);
    }

    @Test
    public void getAllGroupsTest() throws Exception {
        given(service.getAllGroups()).willReturn(Arrays.asList("group1", "group2"));
        ResponseEntity<?> result = controller.getAllGroups();
        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertThat((List<String>)result.getBody(), containsInAnyOrder("group1", "group2"));

        given(service.getAllGroups()).willThrow(new AmazonS3Exception("test_error"));
        result = controller.getAllGroups();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("An error occurred while reading all groups.", result.getBody().toString());
    }

    @Test
    public void createGroupTest() throws Exception {
        ResponseEntity<?> result = controller.createGroup("group1");
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Group created.", result.getBody().toString());

        doThrow(new GroupAlreadyExistsException("group2")).when(service).createGroup("group2");
        result = controller.createGroup("group2");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group2"));

        doThrow(new InvalidFolderNameException("group2")).when(service).createGroup("group3");
        result = controller.createGroup("group3");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group2"));

        doThrow(new RuntimeException("test_error")).when(service).createGroup("group4");
        result = controller.createGroup("group4");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("An error occurred while creating the group.", result.getBody().toString());
    }

    @Test
    public void getGroupTest() throws Exception {
        GroupConfig config = new GroupConfig();
        config.setAuthorizedUsers(Arrays.asList("user1", "user2"));
        GroupData group1Data = new GroupData();
        group1Data.setConfig(config);
        group1Data.setFolders(Arrays.asList("folder1", "folder2"));
        group1Data.setGroupName("group1");

        given(service.getGroupData("group1")).willReturn(group1Data);
        ResponseEntity<?> result = controller.getGroup("group1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertThat(((GroupData)result.getBody()).getConfig().getAuthorizedUsers(), containsInAnyOrder("user1", "user2"));
        assertThat(((GroupData)result.getBody()).getFolders(), containsInAnyOrder("folder1", "folder2"));
        assertEquals(((GroupData)result.getBody()).getGroupName(), "group1");

        given(service.getGroupData("group2")).willThrow(new GroupDoesNotExistException("group2"));
        result = controller.getGroup("group2");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group2"));

        given(service.getGroupData("group3")).willThrow(new RuntimeException("test_error"));
        result = controller.getGroup("group3");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("An error occurred while reading the group.", result.getBody().toString());
    }

    @Test
    public void deleteGroupTest() throws Exception {
        ResponseEntity<?> result = controller.deleteGroup("group1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Group deleted.", result.getBody().toString());

        doThrow(new GroupDoesNotExistException("group2")).when(service).deleteGroup("group2");
        result = controller.deleteGroup("group2");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group2"));

        doThrow(new RuntimeException("test_error")).when(service).deleteGroup("group3");
        result = controller.deleteGroup("group3");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("An error occurred while deleting the group.", result.getBody().toString());
    }

    @Test
    public void createFolderTest() throws Exception {
        ResponseEntity<?> result = controller.createFolder("group1", "folder1");
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Folder created.", result.getBody().toString());

        doThrow(new GroupDoesNotExistException("group2")).when(service).createFolder("group2", "folder1");
        result = controller.createFolder("group2", "folder1");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group2"));

        doThrow(new FolderDoesNotExistException("group2", "folder1")).when(service).createFolder("group2", "folder1/folder2");
        result = controller.createFolder("group2", "folder1/folder2");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group2") && result.getBody().toString().contains("folder1"));

        doThrow(new FolderAlreadyExistsException("group1", "folder1")).when(service).createFolder("group1", "folder1");
        result = controller.createFolder("group1", "folder1");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("folder1"));

        doThrow(new InvalidFolderNameException("bad_folder")).when(service).createFolder("group1", "bad_folder");
        result = controller.createFolder("group1", "bad_folder");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("bad_folder"));

        doThrow(new RuntimeException("test_error")).when(service).createFolder("group1", "error");
        result = controller.createFolder("group1", "error");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("An error occurred while creating the folder.", result.getBody().toString());
    }

    @Test
    public void getFolderTest() throws Exception {
        HashMap<String, String> defaults = new HashMap<>();
        defaults.put("test_key", "test_value");
        SavedReportConfiguration testReport = new SavedReportConfiguration();
        testReport.setCreatedUser("test_user");
        testReport.setId("test_id");
        testReport.setLastModifiedUser("test_user");
        testReport.setParameterValues(defaults);
        testReport.setReportName("test_report");
        testReport.setReportType("test_type");
        FolderData folder1Data = new FolderData();
        folder1Data.setReports(Arrays.asList(testReport));
        folder1Data.setParameterDefaults(defaults);
        folder1Data.setFolders(Arrays.asList("folder1", "folder2"));
        folder1Data.setGroupName("group1");
        folder1Data.setCurrentPath("folder1/");

        given(service.getFolderData("group1", "folder1")).willReturn(folder1Data);
        ResponseEntity<?> result = controller.getFolder("group1", "folder1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertThat(((FolderData)result.getBody()).getReports(), containsInAnyOrder(testReport));
        assertThat(((FolderData)result.getBody()).getFolders(), containsInAnyOrder("folder1", "folder2"));
        assertEquals(((FolderData)result.getBody()).getGroupName(), "group1");
        assertEquals(((FolderData)result.getBody()).getCurrentPath(), "folder1/");
        assertEquals(((FolderData)result.getBody()).getParameterDefaults(), defaults);

        given(service.getFolderData("group2", "folder1")).willThrow(new GroupDoesNotExistException("group2"));
        result = controller.getFolder("group2", "folder1");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group2"));

        given(service.getFolderData("group1", "folder2")).willThrow(new FolderDoesNotExistException("group1", "folder2"));
        result = controller.getFolder("group1", "folder2");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group1") && result.getBody().toString().contains("folder2"));

        given(service.getFolderData("group1", "bad_folder")).willThrow(new RuntimeException("test_error"));
        result = controller.getFolder("group1", "bad_folder");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("An error occurred while reading the folder.", result.getBody().toString());
    }

    @Test
    public void deleteFolderTest() throws Exception {
        ResponseEntity<?> result = controller.deleteFolder("group1", "folder1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Folder deleted.", result.getBody().toString());

        doThrow(new GroupDoesNotExistException("group2")).when(service).deleteFolder("group2", "folder1");
        result = controller.deleteFolder("group2", "folder1");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group2"));

        doThrow(new FolderDoesNotExistException("group1", "folder2")).when(service).deleteFolder("group1", "folder2");
        result = controller.deleteFolder("group1", "folder2");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group1") && result.getBody().toString().contains("folder2"));

        doThrow(new RuntimeException("test_error")).when(service).deleteFolder("group1", "bad_folder");
        result = controller.deleteFolder("group1", "bad_folder");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("An error occurred while deleting the folder.", result.getBody().toString());
    }

    @Test
    public void createReportTest() throws Exception {
        ResponseEntity<?> result = controller.createReport(new SavedReportConfiguration(), "group1", "folder1");
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Report created.", result.getBody().toString());

        doThrow(new GroupDoesNotExistException("group2")).when(service).saveReport(eq("group2"), eq("folder1"), any(SavedReportConfiguration.class), eq(false));
        result = controller.createReport(new SavedReportConfiguration(), "group2", "folder1");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group2"));

        doThrow(new FolderDoesNotExistException("group1", "folder2")).when(service).saveReport(eq("group1"), eq("folder2"), any(SavedReportConfiguration.class), eq(false));
        result = controller.createReport(new SavedReportConfiguration(), "group1", "folder2");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group1") && result.getBody().toString().contains("folder2"));

        doThrow(new ReportAlreadyExistsException("group1", "folder1", "report1")).when(service).saveReport(eq("group1"), eq("folder1"), any(SavedReportConfiguration.class), eq(false));
        result = controller.createReport(new SavedReportConfiguration(), "group1", "folder1");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group1") && result.getBody().toString().contains("folder1") && result.getBody().toString().contains("report1"));

        doThrow(new RuntimeException("test_error")).when(service).saveReport(eq("group1"), eq("bad_folder"), any(SavedReportConfiguration.class), eq(false));
        result = controller.createReport(new SavedReportConfiguration(), "group1", "bad_folder");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("An error occurred while creating the report.", result.getBody().toString());
    }

    @Test
    public void updateReportTest() throws Exception {
        ResponseEntity<?> result = controller.updateReport(new SavedReportConfiguration(), "group1", "folder1", "report1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Report updated.", result.getBody().toString());

        doThrow(new GroupDoesNotExistException("group2")).when(service).saveReport(eq("group2"), eq("folder1"), any(SavedReportConfiguration.class), eq(true));
        result = controller.updateReport(new SavedReportConfiguration(), "group2", "folder1", "report1");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group2"));

        doThrow(new FolderDoesNotExistException("group1", "folder2")).when(service).saveReport(eq("group1"), eq("folder2"), any(SavedReportConfiguration.class), eq(true));
        result = controller.updateReport(new SavedReportConfiguration(), "group1", "folder2", "report1");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group1") && result.getBody().toString().contains("folder2"));

        doThrow(new ReportDoesNotExistException("group1", "folder1", "report2")).when(service).saveReport(eq("group1"), eq("folder1"), any(SavedReportConfiguration.class), eq(true));
        result = controller.updateReport(new SavedReportConfiguration(), "group1", "folder1", "report2");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group1") && result.getBody().toString().contains("folder1") && result.getBody().toString().contains("report2"));

        doThrow(new RuntimeException("test_error")).when(service).saveReport(eq("group1"), eq("bad_folder"), any(SavedReportConfiguration.class), eq(true));
        result = controller.updateReport(new SavedReportConfiguration(), "group1", "bad_folder", "report1");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("An error occurred while updating the report.", result.getBody().toString());
    }

    @Test
    public void deleteReportTest() throws Exception {
        ResponseEntity<?> result = controller.deleteReport("group1", "folder1", "report1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Report deleted.", result.getBody().toString());

        doThrow(new GroupDoesNotExistException("group2")).when(service).deleteReport("group2", "folder1", "report1");
        result = controller.deleteReport("group2", "folder1", "report1");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group2"));

        doThrow(new FolderDoesNotExistException("group1", "folder2")).when(service).deleteReport("group1", "folder2", "report1");
        result = controller.deleteReport("group1", "folder2", "report1");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group1") && result.getBody().toString().contains("folder2"));

        doThrow(new ReportDoesNotExistException("group1", "folder1", "report2")).when(service).deleteReport("group1", "folder1", "report2");
        result = controller.deleteReport("group1", "folder1", "report2");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("group1") && result.getBody().toString().contains("folder1") && result.getBody().toString().contains("report2"));

        doThrow(new RuntimeException("test_error")).when(service).deleteReport("group1", "bad_folder", "report1");
        result = controller.deleteReport("group1", "bad_folder", "report1");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("An error occurred while deleting the report.", result.getBody().toString());
    }
}