package gov.usgs.aqcu.aws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.internal.AmazonS3ExceptionBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

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

        service = new S3Service(builder, "test-bucket");
    }

    @Test
    public void saveJsonStringTest() {
        service.saveJsonString("test/test.json", "test");

        verify(client, times(1)).putObject("test-bucket", "test/test.json", "test");
    }

    @Test
    public void deleteFolderTest() {
        S3ObjectSummary summary1 = new S3ObjectSummary();
        summary1.setKey("/test_dir/object1.json");
        S3ObjectSummary summary2 = new S3ObjectSummary();
        summary2.setKey("/test_dir/object2.json");
        S3ObjectSummary summary3 = new S3ObjectSummary();
        summary3.setKey("/test_dir/test_sub_dir/object3.json");

        ListObjectsV2Result listObjectResult = Mockito.mock(ListObjectsV2Result.class);
        given(listObjectResult.getObjectSummaries()).willReturn(Arrays.asList(summary1, summary2, summary3));
        given(client.listObjectsV2(any(ListObjectsV2Request.class))).willReturn(listObjectResult);

        service.deleteFolder("test_dir/");

        ArgumentCaptor<ListObjectsV2Request> listCaptor = ArgumentCaptor.forClass(ListObjectsV2Request.class);
        verify(client, times(1)).listObjectsV2(listCaptor.capture());
        assertEquals("test-bucket", listCaptor.getValue().getBucketName());
        assertEquals("test_dir/", listCaptor.getValue().getPrefix());

        ArgumentCaptor<DeleteObjectsRequest> deleteCaptor = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
        verify(client, times(1)).deleteObjects(deleteCaptor.capture());
        assertEquals("test-bucket", deleteCaptor.getValue().getBucketName());
        assertThat(
            deleteCaptor.getValue().getKeys().stream().map(k -> k.getKey()).collect(Collectors.toList()), 
            containsInAnyOrder(summary1.getKey(), summary2.getKey(), summary3.getKey())
        );
    }

    @Test
    public void doesFileExistTest() {
        service.doesFileExist("test_dir/test.json");

        verify(client, times(1)).doesObjectExist("test-bucket", "test_dir/test.json");
    }

    @Test
    public void getSubFolderNamesTest() {
        ListObjectsV2Result listObjectResult = Mockito.mock(ListObjectsV2Result.class);
        given(listObjectResult.getCommonPrefixes()).willReturn(Arrays.asList("test_dir/test1/", "test_dir/test2"));
        given(client.listObjectsV2(any(ListObjectsV2Request.class))).willReturn(listObjectResult);

        List<String> subPaths = service.getSubFolderNames("test_dir/");
        
        assertThat(subPaths, containsInAnyOrder("test1", "test2"));

        ArgumentCaptor<ListObjectsV2Request> listCaptor = ArgumentCaptor.forClass(ListObjectsV2Request.class);
        verify(client, times(1)).listObjectsV2(listCaptor.capture());
        assertEquals("test-bucket", listCaptor.getValue().getBucketName());
        assertEquals("test_dir/", listCaptor.getValue().getPrefix());
        assertEquals("/", listCaptor.getValue().getDelimiter());
    }

    @Test
    public void getFileAsStringBasicTest() {
        given(client.getObjectAsString("test-bucket", "test_dir/test.json")).willReturn("test_file_contents");

        String result = service.getFileAsString("test_dir/test.json");
        assertEquals("test_file_contents", result);
    }

    @Test
    public void getFileAsStringErrorTest() {
        AmazonS3ExceptionBuilder exceptionBuilder = new AmazonS3ExceptionBuilder();

        exceptionBuilder.setStatusCode(404);
        given(client.getObjectAsString("test-bucket", "test_dir/test1.json")).willThrow(exceptionBuilder.build());
        String result = service.getFileAsString("test_dir/test1.json");
        assertNull(result);

        exceptionBuilder.setStatusCode(400);
        given(client.getObjectAsString("test-bucket", "test_dir/test2.json")).willThrow(exceptionBuilder.build());
        try {
            service.getFileAsString("test_dir/test2.json");
            fail("Expected exception of type AmazonS3Exception but got no exception.");
        }
        catch(AmazonS3Exception e) {
            // Success
        } catch(Exception e) {
            fail("Expected exception of type AmazonS3Exception but got exception of type " + e.getClass().getName());
        }
    }
}