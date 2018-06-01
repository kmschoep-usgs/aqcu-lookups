package gov.usgs.aqcu.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AquariusReferenceListProperties.class)
public class AquariusReferenceListConfig {
	
}