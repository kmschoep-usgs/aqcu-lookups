package gov.usgs.aqcu.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="aquarius.reference")
public class AquariusReferenceListConfig {
	private List<String> computations = new ArrayList<>();
	private List<String> periods = new ArrayList<>();

	public List<String> getComputations() {
		return computations;
	}
	public List<String> getPeriods() {
		return periods;
	}
}