package gov.usgs.aqcu.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Matchers.any;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

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
import gov.usgs.aqcu.exception.ReportAlreadyExistsException;
import gov.usgs.aqcu.exception.ReportDoesNotExistException;
import gov.usgs.aqcu.model.config.FolderData;
import gov.usgs.aqcu.model.config.persist.GroupConfig;
import gov.usgs.aqcu.model.config.GroupData;
import gov.usgs.aqcu.model.config.persist.SavedReportConfiguration;
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
        try {
            result = controller.getAllGroups();
            fail("Expected AmazonS3Exception but got no exception");
        } catch(AmazonS3Exception e) {
            assertTrue(e.getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected AmazonS3Exception but got " + e.getClass().getName());
        }
    }

    @Test
    public void createGroupTest() throws Exception {
        ResponseEntity<?> result = controller.createGroup("group1");
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(null, result.getBody());

        doThrow(new GroupAlreadyExistsException("group2")).when(service).createGroup("group2");
        try {
            result = controller.createGroup("group2");
            fail("Expected GroupAlreadyExistsException but got no exception");
        } catch(GroupAlreadyExistsException e) {
            assertTrue(e.getMessage().contains("group2"));
        } catch(Exception e) {
            fail("Expected GroupAlreadyExistsException but got " + e.getClass().getName());
        }

        doThrow(new RuntimeException("test_error")).when(service).createGroup("group4");
        try {
            result = controller.createGroup("group4");
            fail("Expected RuntimeException but got no exception");
        } catch(RuntimeException e) {
            assertTrue(e.getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected RuntimeException but got " + e.getClass().getName());
        }
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
        try {
            result = controller.getGroup("group2");
            fail("Expected GroupDoesNotExistException but got no exception");
        } catch(GroupDoesNotExistException e) {
            assertTrue(e.getMessage().contains("group2"));
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }

        given(service.getGroupData("group3")).willThrow(new RuntimeException("test_error"));
        try {
            result = controller.getGroup("group3");
            fail("Expected RuntimeException but got no exception");
        } catch(RuntimeException e) {
            assertTrue(e.getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected RuntimeException but got " + e.getClass().getName());
        }
    }

    @Test
    public void deleteGroupTest() throws Exception {
        ResponseEntity<?> result = controller.deleteGroup("group1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(null, result.getBody());

        doThrow(new GroupDoesNotExistException("group2")).when(service).deleteGroup("group2");
        try {
            result = controller.deleteGroup("group2");
            fail("Expected GroupDoesNotExistException but got no exception");
        } catch(GroupDoesNotExistException e) {
            assertTrue(e.getMessage().contains("group2"));
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }
        
        doThrow(new RuntimeException("test_error")).when(service).deleteGroup("group3");
        try {
            result = controller.deleteGroup("group3");
            fail("Expected GroupDoesNotExistException but got no exception");
        } catch(RuntimeException e) {
            assertTrue(e.getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }
    }

    @Test
    public void createFolderTest() throws Exception {
        ResponseEntity<?> result = controller.createFolder("group1", "folder1");
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(null, result.getBody());

        doThrow(new GroupDoesNotExistException("group2")).when(service).createFolder("group2", "folder1");
        try {
            result = controller.createFolder("group2", "folder1");
            fail("Expected GroupDoesNotExistException but got no exception");
        } catch(GroupDoesNotExistException e) {
            assertTrue(e.getMessage().contains("group2"));
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }

        doThrow(new FolderDoesNotExistException("group1", "folder1")).when(service).createFolder("group1", "folder1/folder2");
        try {
            result = controller.createFolder("group1", "folder1/folder2");
            fail("Expected FolderDoesNotExistException but got no exception");
        } catch(FolderDoesNotExistException e) {
            assertTrue(e.getMessage().contains("group1") && e.getMessage().contains("folder1"));
        } catch(Exception e) {
            fail("Expected FolderDoesNotExistException but got " + e.getClass().getName());
        }

        doThrow(new FolderAlreadyExistsException("group1", "folder1")).when(service).createFolder("group1", "folder1");
        try {
            result = controller.createFolder("group1", "folder1");
            fail("Expected FolderAlreadyExistsException but got no exception");
        } catch(FolderAlreadyExistsException e) {
            assertTrue(e.getMessage().contains("group1") && e.getMessage().contains("folder1"));
        } catch(Exception e) {
            fail("Expected FolderAlreadyExistsException but got " + e.getClass().getName());
        }

        doThrow(new RuntimeException("test_error")).when(service).createFolder("group1", "bad_folder");
        try {
            result = controller.createFolder("group1", "bad_folder");
            fail("Expected RuntimeException but got no exception");
        } catch(RuntimeException e) {
            assertTrue(e.getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected RuntimeException but got " + e.getClass().getName());
        }
    }

    @Test
    public void getFolderTest() throws Exception {
        HashMap<String, String> defaults = new HashMap<>();
        HashMap<String, List<String>> params = new HashMap<>();
        params.put("test_param", Arrays.asList("test_param_value"));
        defaults.put("test_key", "test_value");
        SavedReportConfiguration testReport = new SavedReportConfiguration();
        testReport.setCreatedUser("test_user");
        testReport.setId("test_id");
        testReport.setLastModifiedUser("test_user");
        testReport.setParameterValues(params);
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
        try {
            result = controller.getFolder("group2", "folder1");
            fail("Expected GroupDoesNotExistException but got no exception");
        } catch(GroupDoesNotExistException e) {
            assertTrue(e.getMessage().contains("group2"));
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }

        given(service.getFolderData("group1", "folder2")).willThrow(new FolderDoesNotExistException("group1", "folder2"));
        try {
            result = controller.getFolder("group1", "folder2");
            fail("Expected FolderDoesNotExistException but got no exception");
        } catch(FolderDoesNotExistException e) {
            assertTrue(e.getMessage().contains("group1") && e.getMessage().contains("folder2"));
        } catch(Exception e) {
            fail("Expected FolderDoesNotExistException but got " + e.getClass().getName());
        }

        given(service.getFolderData("group1", "bad_folder")).willThrow(new RuntimeException("test_error"));
        try {
            result = controller.getFolder("group1", "bad_folder");
            fail("Expected RuntimeException but got no exception");
        } catch(RuntimeException e) {
            assertTrue(e.getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected RuntimeException but got " + e.getClass().getName());
        }
    }

    @Test
    public void deleteFolderTest() throws Exception {
        ResponseEntity<?> result = controller.deleteFolder("group1", "folder1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(null, result.getBody());

        doThrow(new GroupDoesNotExistException("group2")).when(service).deleteFolder("group2", "folder1");
        try {
            result = controller.deleteFolder("group2", "folder1");
            fail("Expected GroupDoesNotExistException but got no exception");
        } catch(GroupDoesNotExistException e) {
            assertTrue(e.getMessage().contains("group2"));
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }

        doThrow(new FolderDoesNotExistException("group1", "folder2")).when(service).deleteFolder("group1", "folder2");
        try {
            result = controller.deleteFolder("group1", "folder2");
            fail("Expected FolderDoesNotExistException but got no exception");
        } catch(FolderDoesNotExistException e) {
            assertTrue(e.getMessage().contains("group1") && e.getMessage().contains("folder2"));
        } catch(Exception e) {
            fail("Expected FolderDoesNotExistException but got " + e.getClass().getName());
        }

        doThrow(new RuntimeException("test_error")).when(service).deleteFolder("group1", "bad_folder");
        try {
            result = controller.deleteFolder("group1", "bad_folder");
            fail("Expected RuntimeException but got no exception");
        } catch(RuntimeException e) {
            assertTrue(e.getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected RuntimeException but got " + e.getClass().getName());
        }
    }

    @Test
    public void createReportTest() throws Exception {
        ResponseEntity<?> result = controller.createReport(new SavedReportConfiguration(), "group1", "folder1");
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(null, result.getBody());

        doThrow(new GroupDoesNotExistException("group2")).when(service).saveReport(eq("group2"), eq("folder1"), any(SavedReportConfiguration.class), eq(false));
        try {
            result = controller.createReport(new SavedReportConfiguration(), "group2", "folder1");
            fail("Expected GroupDoesNotExistException but got no exception");
        } catch(GroupDoesNotExistException e) {
            assertTrue(e.getMessage().contains("group2"));
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }

        doThrow(new FolderDoesNotExistException("group1", "folder2")).when(service).saveReport(eq("group1"), eq("folder2"), any(SavedReportConfiguration.class), eq(false));
        try {
            result = controller.createReport(new SavedReportConfiguration(), "group1", "folder2");
            fail("Expected FolderDoesNotExistException but got no exception");
        } catch(FolderDoesNotExistException e) {
            assertTrue(e.getMessage().contains("group1") && e.getMessage().contains("folder2"));
        } catch(Exception e) {
            fail("Expected FolderDoesNotExistException but got " + e.getClass().getName());
        }

        doThrow(new ReportAlreadyExistsException("group1", "folder1", "report1")).when(service).saveReport(eq("group1"), eq("folder1"), any(SavedReportConfiguration.class), eq(false));
        try {
            result = controller.createReport(new SavedReportConfiguration(), "group1", "folder1");
            fail("Expected ReportAlreadyExistsException but got no exception");
        } catch(ReportAlreadyExistsException e) {
            assertTrue(e.getMessage().contains("group1") && e.getMessage().contains("folder1") && e.getMessage().contains("report1"));
        } catch(Exception e) {
            fail("Expected ReportAlreadyExistsException but got " + e.getClass().getName());
        }
        
        doThrow(new RuntimeException("test_error")).when(service).saveReport(eq("group1"), eq("bad_folder"), any(SavedReportConfiguration.class), eq(false));
        try {
            result = controller.createReport(new SavedReportConfiguration(), "group1", "bad_folder");
            fail("Expected RuntimeException but got no exception");
        } catch(RuntimeException e) {
            assertTrue(e.getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected RuntimeException but got " + e.getClass().getName());
        }
    }

    @Test
    public void updateReportTest() throws Exception {
        ResponseEntity<?> result = controller.updateReport(new SavedReportConfiguration(), "group1", "folder1", "report1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(null, result.getBody());

        doThrow(new GroupDoesNotExistException("group2")).when(service).saveReport(eq("group2"), eq("folder1"), any(SavedReportConfiguration.class), eq(true));
        try {
            result = controller.updateReport(new SavedReportConfiguration(), "group2", "folder1", "report1");
            fail("Expected GroupDoesNotExistException but got no exception");
        } catch(GroupDoesNotExistException e) {
            assertTrue(e.getMessage().contains("group2"));
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }

        doThrow(new FolderDoesNotExistException("group1", "folder2")).when(service).saveReport(eq("group1"), eq("folder2"), any(SavedReportConfiguration.class), eq(true));
        try {
            result = controller.updateReport(new SavedReportConfiguration(), "group1", "folder2", "report1");
            fail("Expected FolderDoesNotExistException but got no exception");
        } catch(FolderDoesNotExistException e) {
            assertTrue(e.getMessage().contains("group1") && e.getMessage().contains("folder2"));
        } catch(Exception e) {
            fail("Expected FolderDoesNotExistException but got " + e.getClass().getName());
        }

        doThrow(new ReportAlreadyExistsException("group1", "folder1", "report1")).when(service).saveReport(eq("group1"), eq("folder1"), any(SavedReportConfiguration.class), eq(true));
        try {
            result = controller.updateReport(new SavedReportConfiguration(), "group1", "folder1", "report1");
            fail("Expected ReportAlreadyExistsException but got no exception");
        } catch(ReportAlreadyExistsException e) {
            assertTrue(e.getMessage().contains("group1") && e.getMessage().contains("folder1") && e.getMessage().contains("report1"));
        } catch(Exception e) {
            fail("Expected ReportAlreadyExistsException but got " + e.getClass().getName());
        }
        
        doThrow(new RuntimeException("test_error")).when(service).saveReport(eq("group1"), eq("bad_folder"), any(SavedReportConfiguration.class), eq(true));
        try {
            result = controller.updateReport(new SavedReportConfiguration(), "group1", "bad_folder", "report1");
            fail("Expected RuntimeException but got no exception");
        } catch(RuntimeException e) {
            assertTrue(e.getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected RuntimeException but got " + e.getClass().getName());
        }
    }

    @Test
    public void deleteReportTest() throws Exception {
        ResponseEntity<?> result = controller.deleteReport("group1", "folder1", "report1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(null, result.getBody());

        doThrow(new GroupDoesNotExistException("group2")).when(service).deleteReport("group2", "folder1", "report1");
        try {
            result = controller.deleteReport("group2", "folder1", "report1");
            fail("Expected GroupDoesNotExistException but got no exception");
        } catch(GroupDoesNotExistException e) {
            assertTrue(e.getMessage().contains("group2"));
        } catch(Exception e) {
            fail("Expected GroupDoesNotExistException but got " + e.getClass().getName());
        }

        doThrow(new FolderDoesNotExistException("group1", "folder2")).when(service).deleteReport("group1", "folder2", "report1");
        try {
            result = controller.deleteReport("group1", "folder2", "report1");
            fail("Expected FolderDoesNotExistException but got no exception");
        } catch(FolderDoesNotExistException e) {
            assertTrue(e.getMessage().contains("group1") && e.getMessage().contains("folder2"));
        } catch(Exception e) {
            fail("Expected FolderDoesNotExistException but got " + e.getClass().getName());
        }

        doThrow(new ReportDoesNotExistException("group1", "folder1", "report2")).when(service).deleteReport("group1", "folder1", "report2");
        try {
            result = controller.deleteReport("group1", "folder1", "report2");
            fail("Expected ReportDoesNotExistException but got no exception");
        } catch(ReportDoesNotExistException e) {
            assertTrue(e.getMessage().contains("group1") && e.getMessage().contains("folder1") && e.getMessage().contains("report2"));
        } catch(Exception e) {
            fail("Expected ReportDoesNotExistException but got " + e.getClass().getName());
        }

        doThrow(new RuntimeException("test_error")).when(service).deleteReport("group1", "bad_folder", "report1");
        try {
            result = controller.deleteReport("group1", "bad_folder", "report1");
            fail("Expected RuntimeException but got no exception");
        } catch(RuntimeException e) {
            assertTrue(e.getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected RuntimeException but got " + e.getClass().getName());
        }
    }

    @Test
    public void groupNameRegexTest() {
        Pattern pattern = Pattern.compile(ConfigController.GROUP_NAME_REGEX);

        assertTrue(pattern.matcher("test").matches());
        assertTrue(pattern.matcher("test_test").matches());
        assertTrue(pattern.matcher("test-test").matches());
        assertTrue(pattern.matcher("TEST").matches());
        assertTrue(pattern.matcher("TeSt").matches());
        assertTrue(pattern.matcher("1234").matches());
        assertTrue(pattern.matcher("12_34-12").matches());
        assertTrue(pattern.matcher("  test  ").matches());

        assertFalse(pattern.matcher("test.").matches());
        assertFalse(pattern.matcher("").matches());
        assertFalse(pattern.matcher("/").matches());
        assertFalse(pattern.matcher("//").matches());
        assertFalse(pattern.matcher("test/test").matches());
        assertFalse(pattern.matcher("/test/test").matches());
        assertFalse(pattern.matcher("test/test/").matches());
        assertFalse(pattern.matcher("/test/test/").matches());
        assertFalse(pattern.matcher("test!").matches());
        assertFalse(pattern.matcher("/1234+1234/").matches());
        assertFalse(pattern.matcher("te  st").matches());
        assertFalse(pattern.matcher("/  test  /").matches());
        assertFalse(pattern.matcher("/test").matches());
        assertFalse(pattern.matcher("/test/").matches());
        assertFalse(pattern.matcher("test/").matches());
        assertFalse(pattern.matcher("/1234/").matches());
        assertFalse(pattern.matcher("  /test/  ").matches());
    }

    @Test
    public void folderPathRegexTest() {
        Pattern pattern = Pattern.compile(ConfigController.FOLDER_PATH_REGEX);

        assertTrue(pattern.matcher("test").matches());
        assertTrue(pattern.matcher("/test").matches());
        assertTrue(pattern.matcher("/test/").matches());
        assertTrue(pattern.matcher("test/").matches());
        assertTrue(pattern.matcher("test_test").matches());
        assertTrue(pattern.matcher("test-test").matches());
        assertTrue(pattern.matcher("TEST").matches());
        assertTrue(pattern.matcher("TeSt").matches());
        assertTrue(pattern.matcher("1234").matches());
        assertTrue(pattern.matcher("/1234/").matches());
        assertTrue(pattern.matcher("12_34-12").matches());
        assertTrue(pattern.matcher("  test  ").matches());
        assertTrue(pattern.matcher("  /test/  ").matches());
        assertTrue(pattern.matcher("test/test").matches());
        assertTrue(pattern.matcher("/test/test").matches());
        assertTrue(pattern.matcher("test/test/").matches());
        assertTrue(pattern.matcher("/test/test/").matches());
        assertTrue(pattern.matcher("  /test/test/  ").matches());
        assertTrue(pattern.matcher("  test/test"  ).matches());

        assertFalse(pattern.matcher("test.").matches());
        assertFalse(pattern.matcher("").matches());
        assertFalse(pattern.matcher("/").matches());
        assertFalse(pattern.matcher("//").matches());
        assertFalse(pattern.matcher("test!").matches());
        assertFalse(pattern.matcher("/1234+1234/").matches());
        assertFalse(pattern.matcher("te  st").matches());
        assertFalse(pattern.matcher("/  test  /").matches());
        assertFalse(pattern.matcher("test / test").matches());
        assertFalse(pattern.matcher("/test/test/test 2/").matches());
        assertFalse(pattern.matcher("/  test/test/test  /").matches());
    }
}