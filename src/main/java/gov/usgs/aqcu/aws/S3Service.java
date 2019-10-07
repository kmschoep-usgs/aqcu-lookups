package gov.usgs.aqcu.aws;

import java.util.stream.Collectors;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gov.usgs.aqcu.model.S3DirectoryData;

@Service
public class S3Service {
    @Value("${s3.bucket}")
    private String S3_BUCKET;

    private AmazonS3ClientBuilder amazonS3ClientBuilder;

    @Autowired
    public S3Service(AmazonS3ClientBuilder amazonS3ClientBuilder) {
        this.amazonS3ClientBuilder = amazonS3ClientBuilder;
    }

    public String getFileData(String path) {
        final AmazonS3 s3 = amazonS3ClientBuilder.build();
        path = formatFilePath(path);

        return s3.getObjectAsString(S3_BUCKET, path);
    }

    public S3DirectoryData getPathData(String rawRoot) {
        final AmazonS3 s3 = amazonS3ClientBuilder.build();
        final String root = formatDirectoryPath(rawRoot);
        
        ListObjectsV2Request request = new ListObjectsV2Request();
        request.setBucketName(S3_BUCKET);
        request.setDelimiter("/");

        if(!root.isEmpty()) {
            request.setPrefix(root);
        }

        ListObjectsV2Result response = s3.listObjectsV2(request);

        S3DirectoryData result = new S3DirectoryData();
        result.setSubPaths(response.getCommonPrefixes().stream().map(r -> r.replace(root, "")).collect(Collectors.toList()));
        result.setRootPath(root);

        return result;
    }

    private String formatDirectoryPath(String path) {
        // 1. Remove leading slash, handle null
        path = path != null ? 
            (path.trim().startsWith("/") ? 
                path.trim().substring(1) 
                : path.trim())
            : "";
        
        // 2. Add trailing slash if not empty
        path = path.isEmpty() || path.endsWith("/") ?
            path
            : path + "/";
        return path;
    }

    private String formatFilePath(String path) {
        // 1. Remove leading slash, handle null
        path = path != null ? 
            (path.trim().startsWith("/") ? 
                path.trim().substring(1) 
                : path.trim())
            : "";
        
        return path;
    }
}