package gov.usgs.aqcu.retrieval;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescriptionListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescriptionListServiceResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class FieldVisitDescriptionListServiceTest {
	@MockBean
	private AquariusRetrievalService aquariusRetrievalService;
	@MockBean
	private LocationDataService locationDataService;
	private FieldVisitDescription desc1 = new FieldVisitDescription()
		.setStartTime(Instant.parse("2017-01-01T00:00:00Z"))
		.setEndTime(Instant.parse("2017-01-02T00:00:00Z"));
	private FieldVisitDescription desc2 = new FieldVisitDescription()
		.setStartTime(Instant.parse("2017-01-02T00:00:00Z"))
		.setEndTime(Instant.parse("2017-01-03T00:00:00Z"));
	private FieldVisitDescription desc3 = new FieldVisitDescription()
		.setStartTime(Instant.parse("2017-01-03T00:00:00Z"))
		.setEndTime(Instant.parse("2017-01-04T00:00:00Z"));

	private FieldVisitDescriptionListService service;

	@Before
	public void setup() {
		service = new FieldVisitDescriptionListService(aquariusRetrievalService, locationDataService);
	}

	@Test
	public void getFieldVisitDatesEmptyTest() {
		given(aquariusRetrievalService.executePublishApiRequest(any(FieldVisitDescriptionListServiceRequest.class)))
			.willReturn(new FieldVisitDescriptionListServiceResponse().setFieldVisitDescriptions(new ArrayList<>()));
		List<String> result = service.getFieldVisitDates(null);
		assertTrue(result.isEmpty());
	}

	@Test
	public void getFieldVisitDatesTest() {
		given(aquariusRetrievalService.executePublishApiRequest(any(FieldVisitDescriptionListServiceRequest.class)))
			.willReturn(new FieldVisitDescriptionListServiceResponse().setFieldVisitDescriptions(new ArrayList<>(Arrays.asList(desc1,desc2,desc3))));
		List<String> result = service.getFieldVisitDates("any");
		assertEquals(result.size(), 3);
		assertThat(result, containsInAnyOrder("2017-01-01","2017-01-02","2017-01-03"));
	}
}
