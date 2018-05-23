package gov.usgs.aqcu.retrieval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;

public class FieldVisitDescriptionListServiceTest {
	private static final Logger LOG = LoggerFactory.getLogger(FieldVisitDescriptionListServiceTest.class);	

	@MockBean
	private AquariusRetrievalService aquariusRetrievalService;
	@MockBean
	private LocationDataService locationDataService;
}
