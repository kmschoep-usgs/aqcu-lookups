package gov.usgs.aqcu.aws;

import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class S3Service {
	private final String S3_BUCKET;

	private final AmazonS3 s3;

	@Autowired
	public S3Service(
		AmazonS3ClientBuilder amazonS3ClientBuilder, 
		@Value("${s3.bucket}") String s3_bucket
	) {
		this.s3 = amazonS3ClientBuilder.build();
		this.S3_BUCKET = s3_bucket;
	}

	public void saveJsonString(String filePath, String jsonString) {
		s3.putObject(S3_BUCKET, formatS3Path(filePath), jsonString);
	}

	public void deleteFolder(String rootDir) {
		ListObjectsV2Request lookupRequest = new ListObjectsV2Request();
		lookupRequest.setBucketName(S3_BUCKET);
		lookupRequest.setPrefix(formatS3Path(rootDir));

		ListObjectsV2Result lookupResult = s3.listObjectsV2(lookupRequest);

		DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(S3_BUCKET);
		deleteRequest.withKeys(lookupResult.getObjectSummaries().stream().map(s -> new KeyVersion(s.getKey())).collect(Collectors.toList()));

		s3.deleteObjects(deleteRequest);
	}

	public Boolean doesFileExist(String filePath) {
		return s3.doesObjectExist(S3_BUCKET, formatS3Path(filePath));
	}

	public List<String> getSubFolderNames(String root) {
		ListObjectsV2Request request = new ListObjectsV2Request();

		final String rootDir = formatS3Path(root);

		request.setBucketName(S3_BUCKET);
		request.setDelimiter("/");

		if(!rootDir.isEmpty()) {
			request.setPrefix(rootDir);
		}

		ListObjectsV2Result response = s3.listObjectsV2(request);

		return response.getCommonPrefixes().stream()
			.map(r -> r.replaceFirst(rootDir, ""))
			.map(r -> r.endsWith("/") ? r.substring(0, r.length()-1) : r)
			.collect(Collectors.toList());
	}

	public String getFileAsString(String filePath) {
		try {
			return s3.getObjectAsString(S3_BUCKET, formatS3Path(filePath));
		} catch(AmazonS3Exception e) {
			if(e.getStatusCode() != HttpStatus.NOT_FOUND.value()) {
				throw e;
			}
			
			return null;
		}
	}

	private String formatS3Path(String path) {
		// Remove leading slash, add trailing slash (if not file)
		path = path.startsWith("/") ? path.substring(1) : path;
		path = path.endsWith("/") || path.contains(".") || path.isEmpty() ? path : path + "/";

		return path;
	}
}