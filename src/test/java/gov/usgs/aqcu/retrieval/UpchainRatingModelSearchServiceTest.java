package gov.usgs.aqcu.retrieval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;

public class UpchainRatingModelSearchServiceTest {
	private static final Logger LOG = LoggerFactory.getLogger(UpchainRatingModelSearchServiceTest.class);	

	@MockBean
	private UpchainProcessorListService upchainProcessorListService;
	@MockBean
	private TimeSeriesDescriptionListService timeSeriesDescriptionListService;
}
