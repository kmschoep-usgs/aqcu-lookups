package gov.usgs.aqcu.retrieval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;

public class LocationDataServiceTest {
	private static final Logger LOG = LoggerFactory.getLogger(LocationDataServiceTest.class);	
	
	@MockBean
	private AquariusRetrievalService aquariusRetrievalService;
}
