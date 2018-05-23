package gov.usgs.aqcu.retrieval;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeRange;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Processor;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ProcessorListServiceResponse;

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
public class UpchainRatingModelSearchServiceTest {	

	@MockBean
	private UpchainProcessorListService upchainProcessorListService;
	private Processor proc1 = new Processor()
		.setDescription("test1")
		.setInputRatingModelIdentifier("id1")
		.setInputTimeSeriesUniqueIds(new ArrayList<>(Arrays.asList("id1", "id2")))
		.setOutputTimeSeriesUniqueId("out1")
		.setProcessorPeriod(new TimeRange().setStartTime(Instant.parse("2017-01-01T00:00:00Z")).setEndTime(Instant.parse("2017-01-01T00:00:00Z")))
		.setProcessorType("test-type-1");
	private Processor proc2 = new Processor()
		.setDescription("test2")
		.setInputRatingModelIdentifier("id2")
		.setInputTimeSeriesUniqueIds(new ArrayList<>(Arrays.asList("id1", "id2")))
		.setOutputTimeSeriesUniqueId("out1")
		.setProcessorPeriod(new TimeRange().setStartTime(Instant.parse("2017-01-01T00:00:00Z")).setEndTime(Instant.parse("2017-01-01T00:00:00Z")))
		.setProcessorType("test-type-2");
	private Processor proc3 = new Processor()
		.setDescription("test2")
		.setInputRatingModelIdentifier("id3")
		.setInputTimeSeriesUniqueIds(new ArrayList<>(Arrays.asList("id1", "id2")))
		.setOutputTimeSeriesUniqueId("out1")
		.setProcessorPeriod(new TimeRange().setStartTime(Instant.parse("2017-01-01T00:00:00Z")).setEndTime(Instant.parse("2017-01-01T00:00:00Z")))
		.setProcessorType("test-type-3");

	private UpchainRatingModelSearchService service;

	@Before
	public void setup() {
		service = new UpchainRatingModelSearchService(upchainProcessorListService);
	}

	@Test
	public void getRatingModelsUpchainNullTest() {
		given(upchainProcessorListService.getRawResponse(any(String.class), any(Instant.class), any(Instant.class)))
			.willReturn(new ProcessorListServiceResponse().setProcessors(new ArrayList<>()));

		List<String> results = service.getRatingModelsUpchain("any", Instant.parse("2017-01-01T00:00:00Z"), Instant.parse("2017-01-01T00:00:00Z"), false);
		assertTrue(results.isEmpty());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getRatingModelsUpchainTest() {
		given(upchainProcessorListService.getRawResponse(any(String.class), any(Instant.class), any(Instant.class)))
			.willReturn(new ProcessorListServiceResponse().setProcessors(new ArrayList<>(Arrays.asList(proc1, proc2, proc3))));
		given(upchainProcessorListService.getRatingModelUniqueIdList(any(ArrayList.class)))
			.willReturn(Arrays.asList(proc1.getInputRatingModelIdentifier(), proc2.getInputRatingModelIdentifier(), proc3.getInputRatingModelIdentifier()));

		List<String> results = service.getRatingModelsUpchain("any", Instant.parse("2017-01-01T00:00:00Z"), Instant.parse("2017-01-01T00:00:00Z"), false);
		assertThat(results, containsInAnyOrder(proc1.getInputRatingModelIdentifier(), proc2.getInputRatingModelIdentifier(), proc3.getInputRatingModelIdentifier()));
	}
}
