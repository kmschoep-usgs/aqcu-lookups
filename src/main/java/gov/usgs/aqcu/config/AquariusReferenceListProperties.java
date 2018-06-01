package gov.usgs.aqcu.config;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="aquarius.reference")
public class AquariusReferenceListProperties {
    private String modified;
	private List<String> computations;
	private List<String> periods;

	public Instant getLastModifiedInstant() {
		return ZonedDateTime.parse(modified).toInstant();
    }
    public String getModified() {
        return modified;
    }
	public List<String> getComputations() {
		return computations;
	}
	public List<String> getPeriods() {
		return periods;
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
	public void setComputations(List<String> computations) {
		this.computations = computations;
	}
	public void setPeriods(List<String> periods) {
		this.periods = periods;
	}
}