package gov.usgs.aqcu.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Matchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import gov.usgs.aqcu.exception.FolderAlreadyExistsException;
import gov.usgs.aqcu.exception.FolderDoesNotExistException;
import gov.usgs.aqcu.exception.GroupAlreadyExistsException;
import gov.usgs.aqcu.exception.GroupDoesNotExistException;
import gov.usgs.aqcu.exception.ReportAlreadyExistsException;
import gov.usgs.aqcu.model.config.FolderData;
import gov.usgs.aqcu.model.config.GroupConfig;
import gov.usgs.aqcu.model.config.GroupData;
import gov.usgs.aqcu.model.config.ReportsConfig;
import gov.usgs.aqcu.model.config.SavedReportConfiguration;
import gov.usgs.aqcu.reports.ReportConfigsService;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(secure = false)
@SpringBootTest
@ActiveProfiles("test")
public class ConfigControllerMVCTest {
    @MockBean
    ReportConfigsService reportConfigsService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getAllGroupsTest() throws Exception {
        given(reportConfigsService.getAllGroups()).willReturn(Arrays.asList("group1", "group2"));
        mockMvc.perform(get("/config/groups/")).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$", hasItems("group1", "group2")));
    }

    @Test
    public void getAllGroupsErrorTest() throws Exception {
        given(reportConfigsService.getAllGroups()).willThrow(new AmazonS3Exception("test_error"));
        try {
            mockMvc.perform(get("/config/groups/")).andReturn();
            fail("Expected AmazonS3Exception (unhandled by controller) but got no exception.");
        } catch(NestedServletException e) {
            assertTrue(e.getCause() instanceof AmazonS3Exception);
            assertTrue(e.getCause().getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected NestedServletException but got " + e.getClass().getName());
        }
    }

    @Test
    public void createGroupTest() throws Exception {
        mockMvc.perform(post("/config/groups/")
            .param("groupName", "test")
        ).andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    public void createGroupValidationTest() throws Exception {
        // Success
        mockMvc.perform(post("/config/groups/")
            .param("groupName", "    test    ") 
        ).andDo(print())
            .andExpect(status().isCreated());

        // Failure
        mockMvc.perform(post("/config/groups/")).andDo(print())
            .andExpect(status().isBadRequest());
        
        mockMvc.perform(post("/config/groups/")
            .param("groupName", "") 
        ).andDo(print())
            .andExpect(status().isBadRequest());

        mockMvc.perform(post("/config/groups/")
            .param("groupName", "  ") 
        ).andDo(print())
            .andExpect(status().isBadRequest());

        mockMvc.perform(post("/config/groups/")
            .param("groupName", "t e s t") 
        ).andDo(print())
            .andExpect(status().isBadRequest());

        mockMvc.perform(post("/config/groups/")
            .param("groupName", "/test") 
        ).andDo(print())
            .andExpect(status().isBadRequest());

        mockMvc.perform(post("/config/groups/")
            .param("groupName", "test!") 
        ).andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createGroupErrorTest() throws Exception {
        doThrow(new GroupAlreadyExistsException("test")).when(reportConfigsService).createGroup("test");
        mockMvc.perform(post("/config/groups/")
            .param("groupName", "test") 
        ).andDo(print())
            .andExpect(status().isBadRequest());
        
        doThrow(new AmazonS3Exception("test_error")).when(reportConfigsService).createGroup("test2");
        try {
            mockMvc.perform(post("/config/groups/").param("groupName", "test2")).andReturn();
            fail("Expected AmazonS3Exception (unhandled by controller) but got no exception.");
        } catch(NestedServletException e) {
            assertTrue(e.getCause() instanceof AmazonS3Exception);
            assertTrue(e.getCause().getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected NestedServletException but got " + e.getClass().getName());
        }
    }

    @Test
    public void getGroupTest() throws Exception {
        GroupConfig testConfig = new GroupConfig();
        testConfig.setAuthorizedUsers(Arrays.asList("user1", "user2"));
        GroupData testData = new GroupData();
        testData.setConfig(testConfig);
        testData.setGroupName("test");
        testData.setFolders(Arrays.asList("folder1", "folder2"));

        given(reportConfigsService.getGroupData("test")).willReturn(testData);
        mockMvc.perform(get("/config/groups/test")).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.config.authorizedUsers", hasItems("user1", "user2")))
            .andExpect(jsonPath("$.groupName", is("test")))
            .andExpect(jsonPath("$.folders", hasItems("folder1", "folder2")));
    }

    @Test
    public void getGroupValidationTest() throws Exception {
        // Success
        mockMvc.perform(get("/config/groups/    test    ")).andDo(print())
            .andExpect(status().isOk());

        // Failure
        mockMvc.perform(get("/config/groups/   ")).andDo(print())
            .andExpect(status().isBadRequest());
        
        mockMvc.perform(get("/config/groups/t e s t")).andDo(print())
            .andExpect(status().isBadRequest());

        mockMvc.perform(get("/config/groups/test!")).andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void getGroupErrorTest() throws Exception {
        doThrow(new GroupDoesNotExistException("test")).when(reportConfigsService).getGroupData("test");
        mockMvc.perform(get("/config/groups/test")).andDo(print())
            .andExpect(status().isNotFound());
        
        doThrow(new AmazonS3Exception("test_error")).when(reportConfigsService).getGroupData("test2");
        try {
            mockMvc.perform(get("/config/groups/test2")).andReturn();
            fail("Expected AmazonS3Exception (unhandled by controller) but got no exception.");
        } catch(NestedServletException e) {
            assertTrue(e.getCause() instanceof AmazonS3Exception);
            assertTrue(e.getCause().getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected NestedServletException but got " + e.getClass().getName());
        }
    }

    @Test
    public void deleteGroupTest() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/config/groups/test")
        ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void deleteGroupValidationTest() throws Exception {
        // Success
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/config/groups/    test    ")
        ).andDo(print())
            .andExpect(status().isOk());

        // Failure
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/config/groups/   ")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/config/groups/t e s t")
        ).andDo(print())
            .andExpect(status().isBadRequest());

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/config/groups/test!")
        ).andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteGroupErrorTest() throws Exception {
        doThrow(new GroupDoesNotExistException("test")).when(reportConfigsService).deleteGroup("test");
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/config/groups/test")
        ).andDo(print())
            .andExpect(status().isNotFound());
        
        doThrow(new AmazonS3Exception("test_error")).when(reportConfigsService).deleteGroup("test2");
        try {
            mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test2")).andReturn();
            fail("Expected AmazonS3Exception (unhandled by controller) but got no exception.");
        } catch(NestedServletException e) {
            assertTrue(e.getCause() instanceof AmazonS3Exception);
            assertTrue(e.getCause().getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected NestedServletException but got " + e.getClass().getName());
        }
    }

    @Test
    public void createFolderTest() throws Exception {
        mockMvc.perform(post("/config/groups/test/folders")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    public void createFolderValidationTest() throws Exception {
        // Success
        mockMvc.perform(post("/config/groups/    test    /folders")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isCreated());
        mockMvc.perform(post("/config/groups/test/folders")
            .param("folderPath", "test/test")
        ).andDo(print())
            .andExpect(status().isCreated());
        mockMvc.perform(post("/config/groups/test/folders")
            .param("folderPath", "test/test")
        ).andDo(print())
            .andExpect(status().isCreated());
        mockMvc.perform(post("/config/groups/test/folders")
            .param("folderPath", "/test/test/")
        ).andDo(print())
            .andExpect(status().isCreated());
        mockMvc.perform(post("/config/groups/test/folders")
            .param("folderPath", "/test/test")
        ).andDo(print())
            .andExpect(status().isCreated());
        mockMvc.perform(post("/config/groups/test/folders")
            .param("folderPath", "test/test/")
        ).andDo(print())
            .andExpect(status().isCreated());
        mockMvc.perform(post("/config/groups/test/folders")
            .param("folderPath", "    test/test/    ")
        ).andDo(print())
            .andExpect(status().isCreated());

        // Failure
        mockMvc.perform(post("/config/groups/   /folders")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/config/groups/t e s t/folders")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/config/groups/test!/folders")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/config/groups/test/folders")).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/config/groups/test/folders")
            .param("folderPath", "  ")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/config/groups/test/folders")
            .param("folderPath", "!test/test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/config/groups/test/folders")
            .param("folderPath", "/test/test!/")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/config/groups/test/folders")
            .param("folderPath", "/test//test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/config/groups/test/folders")
            .param("folderPath", "//test/test/")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/config/groups/test/folders")
            .param("folderPath", "/    test/test/    ")
        ).andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createFolderErrorTest() throws Exception {
        doThrow(new GroupDoesNotExistException("test")).when(reportConfigsService).createFolder("test", "test");
        mockMvc.perform(post("/config/groups/test/folders")
            .param("folderPath", "test") 
        ).andDo(print())
            .andExpect(status().isNotFound());
        
        doThrow(new FolderDoesNotExistException("test", "test")).when(reportConfigsService).createFolder("test", "test/test");
        mockMvc.perform(post("/config/groups/test/folders")
            .param("folderPath", "test/test") 
        ).andDo(print())
            .andExpect(status().isNotFound());

        doThrow(new FolderAlreadyExistsException("test", "test1")).when(reportConfigsService).createFolder("test", "test1");
        mockMvc.perform(post("/config/groups/test/folders")
            .param("folderPath", "test1") 
        ).andDo(print())
            .andExpect(status().isBadRequest());
        
        doThrow(new AmazonS3Exception("test_error")).when(reportConfigsService).createFolder("test", "test2");
        try {
            mockMvc.perform(post("/config/groups/test/folders").param("folderPath", "test2")).andReturn();
            fail("Expected AmazonS3Exception (unhandled by controller) but got no exception.");
        } catch(NestedServletException e) {
            assertTrue(e.getCause() instanceof AmazonS3Exception);
            assertTrue(e.getCause().getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected NestedServletException but got " + e.getClass().getName());
        }
    }

    @Test
    public void getFolderTest() throws Exception {
        HashMap<String, String> testDefaults = new HashMap<>();
        HashMap<String, List<String>> testParams = new HashMap<>();
        testParams.put("test_param", Arrays.asList("test_param_value"));
        testDefaults.put("param1", "value1");
        SavedReportConfiguration testReport = new SavedReportConfiguration();
        testReport.setCreatedUser("user1");
        testReport.setId("report1");
        testReport.setReportName("name1");
        testReport.setLastModifiedUser("user1");
        testReport.setReportType("type1");
        testReport.setParameterValues(testParams);
        ReportsConfig testConfig = new ReportsConfig();
        testConfig.setParameterDefaults(testDefaults);
        testConfig.saveReport(testReport);
        FolderData testData = new FolderData();
        testData.setReports(new ArrayList<>(testConfig.getSavedReports().values()));
        testData.setFolders(Arrays.asList("folder1", "folder2"));
        testData.setGroupName("test");
        testData.setParameterDefaults(testDefaults);
        testData.setCurrentPath("test");

        given(reportConfigsService.getFolderData("test", "test")).willReturn(testData);
        mockMvc.perform(get("/config/groups/test/folders")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currentPath", is("test")))
            .andExpect(jsonPath("$.groupName", is("test")))
            .andExpect(jsonPath("$.reports[0].createdUser", is("user1")))
            .andExpect(jsonPath("$.reports[0].id", is("report1")))
            .andExpect(jsonPath("$.reports[0].reportName", is("name1")))
            .andExpect(jsonPath("$.reports[0].lastModifiedUser", is("user1")))
            .andExpect(jsonPath("$.reports[0].reportType", is("type1")))
            .andExpect(jsonPath("$.reports[0].parameterValues.test_param", is(Arrays.asList("test_param_value"))))
            .andExpect(jsonPath("$.folders", hasItems("folder1", "folder2")))
            .andExpect(jsonPath("$.parameterDefaults.param1", is("value1")));
    }

    @Test
    public void getFolderValidationTest() throws Exception {
        // Success
        given(reportConfigsService.getFolderData(anyString(), anyString())).willReturn(new FolderData());
        mockMvc.perform(get("/config/groups/    test    /folders")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(get("/config/groups/test/folders")
            .param("folderPath", "test/test")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(get("/config/groups/test/folders")
            .param("folderPath", "test/test")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(get("/config/groups/test/folders")
            .param("folderPath", "/test/test/")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(get("/config/groups/test/folders")
            .param("folderPath", "/test/test")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(get("/config/groups/test/folders")
            .param("folderPath", "test/test/")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(get("/config/groups/test/folders")
            .param("folderPath", "    test/test/    ")
        ).andDo(print())
            .andExpect(status().isOk());

        // Failure
        mockMvc.perform(get("/config/groups/   /folders")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(get("/config/groups/t e s t/folders")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(get("/config/groups/test!/folders")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(get("/config/groups/test/folders")).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(get("/config/groups/test/folders")
            .param("folderPath", "  ")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(get("/config/groups/test/folders")
            .param("folderPath", "!test/test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(get("/config/groups/test/folders")
            .param("folderPath", "/test/test!/")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(get("/config/groups/test/folders")
            .param("folderPath", "/test//test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(get("/config/groups/test/folders")
            .param("folderPath", "//test/test/")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(get("/config/groups/test/folders")
            .param("folderPath", "/    test/test/    ")
        ).andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void getFolderErrorTest() throws Exception {
        doThrow(new GroupDoesNotExistException("test")).when(reportConfigsService).getFolderData("test", "test");
        mockMvc.perform(get("/config/groups/test/folders")
            .param("folderPath", "test") 
        ).andDo(print())
            .andExpect(status().isNotFound());
        
        doThrow(new FolderDoesNotExistException("test", "test/test")).when(reportConfigsService).getFolderData("test", "test/test");
        mockMvc.perform(get("/config/groups/test/folders")
            .param("folderPath", "test/test") 
        ).andDo(print())
            .andExpect(status().isNotFound());
        
        doThrow(new AmazonS3Exception("test_error")).when(reportConfigsService).getFolderData("test", "test2");
        try {
            mockMvc.perform(get("/config/groups/test/folders").param("folderPath", "test2")).andReturn();
            fail("Expected AmazonS3Exception (unhandled by controller) but got no exception.");
        } catch(NestedServletException e) {
            assertTrue(e.getCause() instanceof AmazonS3Exception);
            assertTrue(e.getCause().getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected NestedServletException but got " + e.getClass().getName());
        }
    }

    @Test
    public void deleteFolderTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void deleteFolderValidationTest() throws Exception {
        // Success
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/    test    /folders")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders")
            .param("folderPath", "test/test")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders")
            .param("folderPath", "test/test")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders")
            .param("folderPath", "/test/test/")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders")
            .param("folderPath", "/test/test")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders")
            .param("folderPath", "test/test/")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders")
            .param("folderPath", "    test/test/    ")
        ).andDo(print())
            .andExpect(status().isOk());

        // Failure
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/   /folders")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/t e s t/folders")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test!/folders")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders")).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders")
            .param("folderPath", "  ")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders")
            .param("folderPath", "!test/test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders")
            .param("folderPath", "/test/test!/")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders")
            .param("folderPath", "/test//test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders")
            .param("folderPath", "//test/test/")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders")
            .param("folderPath", "/    test/test/    ")
        ).andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteFolderErrorTest() throws Exception {
        doThrow(new GroupDoesNotExistException("test")).when(reportConfigsService).deleteFolder("test", "test");
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders")
            .param("folderPath", "test") 
        ).andDo(print())
            .andExpect(status().isNotFound());
        
        doThrow(new FolderDoesNotExistException("test", "test/test")).when(reportConfigsService).deleteFolder("test", "test/test");
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders")
            .param("folderPath", "test/test") 
        ).andDo(print())
            .andExpect(status().isNotFound());
        
        doThrow(new AmazonS3Exception("test_error")).when(reportConfigsService).deleteFolder("test", "test2");
        try {
            mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/folders").param("folderPath", "test2")).andReturn();
            fail("Expected AmazonS3Exception (unhandled by controller) but got no exception.");
        } catch(NestedServletException e) {
            assertTrue(e.getCause() instanceof AmazonS3Exception);
            assertTrue(e.getCause().getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected NestedServletException but got " + e.getClass().getName());
        }
    }

    @Test
    public void createReportTest() throws Exception {
        HashMap<String, List<String>> goodParams = new HashMap<>();
        goodParams.put("locationIdentifier", Arrays.asList("test"));
        SavedReportConfiguration goodConfig = new SavedReportConfiguration();
        goodConfig.setParameterValues(goodParams);
        goodConfig.setReportType("type1");
        goodConfig.setReportName("name1");
        String goodConfigJson = new ObjectMapper().writeValueAsString(goodConfig);

        mockMvc.perform(post("/config/groups/test/reports")
            .param("folderPath", "test")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    public void createReportValidationTest() throws Exception {
        HashMap<String, List<String>> testParams = new HashMap<>();
        testParams.put("locationIdentifier", Arrays.asList("test"));
        SavedReportConfiguration testConfig = new SavedReportConfiguration();
        testConfig.setParameterValues(testParams);
        testConfig.setReportType("type1");
        testConfig.setReportName("name1");
        String goodConfigJson = new ObjectMapper().writeValueAsString(testConfig);

        // Success
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/    test    /reports")
            .param("folderPath", "test")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isCreated());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "test/test")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isCreated());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "test/test")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isCreated());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "/test/test/")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isCreated());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "/test/test")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isCreated());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "test/test/")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isCreated());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "    test/test/    ")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isCreated());

        // Failure
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/   /reports")
            .param("folderPath", "test")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/t e s t/reports")
            .param("folderPath", "test")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test!/reports")
            .param("folderPath", "test")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "  ")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "!test/test")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "/test/test!/")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "/test//test")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "//test/test/")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "/    test/test/    ")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());

        testConfig.setReportName(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "test")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(new ObjectMapper().writeValueAsString(testConfig))
        ).andDo(print())
            .andExpect(status().isBadRequest());
        
        testConfig.setReportName("name1");
        testConfig.setReportType(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "test")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(new ObjectMapper().writeValueAsString(testConfig))
        ).andDo(print())
            .andExpect(status().isBadRequest());
        
        testConfig.setReportType("type1");
        testConfig.setParameterValues(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "test")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(new ObjectMapper().writeValueAsString(testConfig))
        ).andDo(print())
            .andExpect(status().isBadRequest());
        
        testConfig.setParameterValues(new HashMap<>());
        mockMvc.perform(MockMvcRequestBuilders.post("/config/groups/test/reports")
            .param("folderPath", "test")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(new ObjectMapper().writeValueAsString(testConfig))
        ).andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createReportErrorTest() throws Exception {
        HashMap<String, List<String>> testParams = new HashMap<>();
        testParams.put("locationIdentifier", Arrays.asList("test"));
        SavedReportConfiguration testConfig = new SavedReportConfiguration();
        testConfig.setParameterValues(testParams);
        testConfig.setReportType("type1");
        testConfig.setReportName("name1");
        String goodConfigJson = new ObjectMapper().writeValueAsString(testConfig);

        doThrow(new GroupDoesNotExistException("test")).when(reportConfigsService).saveReport(eq("test"), eq("test"), any(SavedReportConfiguration.class), eq(false));
        mockMvc.perform(post("/config/groups/test/reports")
            .param("folderPath", "test") 
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isNotFound());
        
        doThrow(new FolderDoesNotExistException("test", "test/test")).when(reportConfigsService).saveReport(eq("test"), eq("test/test"), any(SavedReportConfiguration.class), eq(false));
        mockMvc.perform(post("/config/groups/test/reports")
            .param("folderPath", "test/test") 
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isNotFound());

        doThrow(new ReportAlreadyExistsException("test", "test2", "test")).when(reportConfigsService).saveReport(eq("test"), eq("test2"), any(SavedReportConfiguration.class), eq(false));
        mockMvc.perform(post("/config/groups/test/reports")
            .param("folderPath", "test2") 
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        
        doThrow(new AmazonS3Exception("test_error")).when(reportConfigsService).saveReport(eq("test"), eq("test3"), any(SavedReportConfiguration.class), eq(false));
        try {
            mockMvc.perform(post("/config/groups/test/reports")
                .param("folderPath", "test3") 
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(goodConfigJson)
            ).andReturn();
            fail("Expected AmazonS3Exception (unhandled by controller) but got no exception.");
        } catch(NestedServletException e) {
            assertTrue(e.getCause() instanceof AmazonS3Exception);
            assertTrue(e.getCause().getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected NestedServletException but got " + e.getClass().getName());
        }
    }

    @Test
    public void updateReportTest() throws Exception {
        HashMap<String, List<String>> goodParams = new HashMap<>();
        goodParams.put("locationIdentifier", Arrays.asList("test"));
        SavedReportConfiguration goodConfig = new SavedReportConfiguration();
        goodConfig.setParameterValues(goodParams);
        goodConfig.setReportType("type1");
        goodConfig.setReportName("name1");
        String goodConfigJson = new ObjectMapper().writeValueAsString(goodConfig);

        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "test")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void updateReportValidationTest() throws Exception {
        HashMap<String, List<String>> testParams = new HashMap<>();
        testParams.put("locationIdentifier", Arrays.asList("test"));
        SavedReportConfiguration testConfig = new SavedReportConfiguration();
        testConfig.setParameterValues(testParams);
        testConfig.setReportType("type1");
        testConfig.setReportName("name1");
        String goodConfigJson = new ObjectMapper().writeValueAsString(testConfig);

        // Success
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/    test    /reports")
            .param("folderPath", "test")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "test/test")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "test/test")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "/test/test/")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "/test/test")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "test/test/")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "    test/test/    ")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isOk());

        // Failure
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
            mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
                .param("folderPath", "test")
                .param("reportId", "report1")
            ).andDo(print())
                .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/   /reports")
            .param("folderPath", "test")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/t e s t/reports")
            .param("folderPath", "test")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test!/reports")
            .param("folderPath", "test")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "  ")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "!test/test")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "/test/test!/")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "/test//test")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "//test/test/")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "/    test/test/    ")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        
        testConfig.setReportName(null);
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "test")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(new ObjectMapper().writeValueAsString(testConfig))
        ).andDo(print())
            .andExpect(status().isBadRequest());
        
        testConfig.setReportName("name1");
        testConfig.setReportType(null);
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "test")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(new ObjectMapper().writeValueAsString(testConfig))
        ).andDo(print())
            .andExpect(status().isBadRequest());
        
        testConfig.setReportType("type1");
        testConfig.setParameterValues(null);
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "test")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(new ObjectMapper().writeValueAsString(testConfig))
        ).andDo(print())
            .andExpect(status().isBadRequest());
        
        testConfig.setParameterValues(new HashMap<>());
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "test")
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(new ObjectMapper().writeValueAsString(testConfig))
        ).andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void updateReportErrorTest() throws Exception {
        HashMap<String, List<String>> testParams = new HashMap<>();
        testParams.put("locationIdentifier", Arrays.asList("test"));
        SavedReportConfiguration testConfig = new SavedReportConfiguration();
        testConfig.setParameterValues(testParams);
        testConfig.setReportType("type1");
        testConfig.setReportName("name1");
        String goodConfigJson = new ObjectMapper().writeValueAsString(testConfig);

        doThrow(new GroupDoesNotExistException("test")).when(reportConfigsService).saveReport(eq("test"), eq("test"), any(SavedReportConfiguration.class), eq(true));
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "test") 
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isNotFound());
        
        doThrow(new FolderDoesNotExistException("test", "test/test")).when(reportConfigsService).saveReport(eq("test"), eq("test/test"), any(SavedReportConfiguration.class), eq(true));
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "test/test") 
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isNotFound());

        doThrow(new ReportAlreadyExistsException("test", "test2", "test")).when(reportConfigsService).saveReport(eq("test"), eq("test2"), any(SavedReportConfiguration.class), eq(true));
        mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
            .param("folderPath", "test2") 
            .param("reportId", "report1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(goodConfigJson)
        ).andDo(print())
            .andExpect(status().isBadRequest());
        
        doThrow(new AmazonS3Exception("test_error")).when(reportConfigsService).saveReport(eq("test"), eq("test3"), any(SavedReportConfiguration.class), eq(true));
        try {
            mockMvc.perform(MockMvcRequestBuilders.put("/config/groups/test/reports")
                .param("folderPath", "test3") 
                .param("reportId", "report1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(goodConfigJson)
            ).andReturn();
            fail("Expected AmazonS3Exception (unhandled by controller) but got no exception.");
        } catch(NestedServletException e) {
            assertTrue(e.getCause() instanceof AmazonS3Exception);
            assertTrue(e.getCause().getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected NestedServletException but got " + e.getClass().getName());
        }
    }

    @Test
    public void deleteReportTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
            .param("folderPath", "test")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void deleteReportValidationTest() throws Exception {
        // Success
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/    test    /reports")
            .param("folderPath", "test")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
            .param("folderPath", "test/test")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
            .param("folderPath", "test/test")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
            .param("folderPath", "/test/test/")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
            .param("folderPath", "/test/test")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
            .param("folderPath", "test/test/")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
            .param("folderPath", "    test/test/    ")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isOk());

        // Failure
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
            .param("folderPath", "test")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/   /reports")
            .param("folderPath", "test")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/t e s t/reports")
            .param("folderPath", "test")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test!/reports")
            .param("folderPath", "test")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
            .param("folderPath", "  ")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
            .param("folderPath", "!test/test")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
            .param("folderPath", "/test/test!/")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
            .param("folderPath", "/test//test")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
            .param("folderPath", "//test/test/")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
            .param("folderPath", "/    test/test/    ")
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteReportErrorTest() throws Exception {
        doThrow(new GroupDoesNotExistException("test")).when(reportConfigsService).deleteReport("test2", "test", "report1");
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test2/reports")
            .param("folderPath", "test") 
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isNotFound());
        
        doThrow(new FolderDoesNotExistException("test", "test/test")).when(reportConfigsService).deleteReport("test", "test/test", "report1");
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
            .param("folderPath", "test/test") 
            .param("reportId", "report1")
        ).andDo(print())
            .andExpect(status().isNotFound());

        doThrow(new ReportAlreadyExistsException("test", "test", "report2")).when(reportConfigsService).deleteReport("test", "test", "report2");
        mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
            .param("folderPath", "test") 
            .param("reportId", "report2")
        ).andDo(print())
            .andExpect(status().isBadRequest());
        
        doThrow(new AmazonS3Exception("test_error")).when(reportConfigsService).deleteReport("test", "test3", "report1");
        try {
            mockMvc.perform(MockMvcRequestBuilders.delete("/config/groups/test/reports")
                .param("folderPath", "test3") 
                .param("reportId", "report1")
            ).andReturn();
            fail("Expected AmazonS3Exception (unhandled by controller) but got no exception.");
        } catch(NestedServletException e) {
            assertTrue(e.getCause() instanceof AmazonS3Exception);
            assertTrue(e.getCause().getMessage().contains("test_error"));
        } catch(Exception e) {
            fail("Expected NestedServletException but got " + e.getClass().getName());
        }
    }
}