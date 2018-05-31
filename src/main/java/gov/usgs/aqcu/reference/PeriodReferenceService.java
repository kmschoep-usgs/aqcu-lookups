package gov.usgs.aqcu.reference;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.usgs.aqcu.config.AquariusReferenceListProperties;

@Repository
public class PeriodReferenceService {
	private static final Logger LOG = LoggerFactory.getLogger(PeriodReferenceService.class);

	private AquariusReferenceListProperties aquariusReferenceListProperties;

	@Autowired
	public PeriodReferenceService(AquariusReferenceListProperties aquariusReferenceListProperties) {
		this.aquariusReferenceListProperties = aquariusReferenceListProperties;
	}

	public List<String> get() {
		//TODO: Cache this list somewhere else
		return aquariusReferenceListProperties.getPeriods();
	}
}