package gov.usgs.aqcu.retrieval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Repository;

@Repository
public class UnitsLookupServiceTest {
	private static final Logger LOG = LoggerFactory.getLogger(UnitsLookupServiceTest.class);	

	@MockBean
	private AquariusRetrievalService aquariusRetrievalService;
}
