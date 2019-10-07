package gov.usgs.aqcu.config;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.util.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
    @Value("${s3.region}") 
    String region;

    @Value("${s3.endpoint:}")
    String endpoint;

    @Bean
    public AmazonS3ClientBuilder amazonS3ClientBuilder() {
        AmazonS3ClientBuilder s3ClientBuilder = AmazonS3ClientBuilder.standard();

        if(!StringUtils.isNullOrEmpty(endpoint)) {
            s3ClientBuilder.enablePathStyleAccess();
            s3ClientBuilder.setEndpointConfiguration(
                new EndpointConfiguration(endpoint, region)
            );
        } else {
            s3ClientBuilder.setRegion(region);
        }

        return s3ClientBuilder;
    }
}