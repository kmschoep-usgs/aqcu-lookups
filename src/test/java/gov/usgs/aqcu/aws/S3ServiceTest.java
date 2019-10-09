package gov.usgs.aqcu.aws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
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
public class S3ServiceTest {	
    @MockBean
    AmazonS3ClientBuilder builder;

    @MockBean
    AmazonS3 client;

    S3Service service;

    @Before
    public void setup() {
        when(builder.build()).thenReturn(client);

        service = new S3Service(builder);
    }

    @Test
    public void saveJsonStringTest() {
        service.saveJsonString("test/test.json", "test");
        verify(client, times(1)).putObject("test-bucket", "test/test.json", "test");
    }

    @Test
    public void deleteFolderTest() {

    }

    @Test
    public void doesFileExistTest() {

    }

    @Test
    public void getFolderSubPathsTest() {

    }

    @Test
    public void getFileAsStringTest() {

    }
}