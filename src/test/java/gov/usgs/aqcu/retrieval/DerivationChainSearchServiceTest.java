package gov.usgs.aqcu.retrieval;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.time.Instant;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesThreshold;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesThresholdPeriod;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ExtendedAttribute;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ProcessorListServiceResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import gov.usgs.aqcu.util.AquariusRetrievalUtils;

@RunWith(SpringRunner.class)
public class DerivationChainSearchServiceTest {	
	@MockBean
	private UpchainProcessorListService upchainProcessorListService;
	@MockBean
	private DownchainProcessorListService downchainProcessorListService;
	@MockBean
	private TimeSeriesDescriptionListService timeSeriesDescriptionListService;
	
	private DerivationChainSearchService service;
	
	private static final TimeSeriesThresholdPeriod p1 = new TimeSeriesThresholdPeriod()
		.setStartTime(Instant.parse("2017-01-01T00:00:00Z"))
		.setEndTime(Instant.parse("2017-02-01T00:00:00Z"))
		.setAppliedTime(Instant.parse("2017-02-01T00:00:00Z"))
		.setComments("comments-1")
		.setReferenceValue(1.0)
		.setSecondaryReferenceValue(2.0)
		.setSuppressData(false);

	private static final TimeSeriesThreshold threshold1 = new TimeSeriesThreshold()
		.setDescription("desc-1")
		.setDisplayColor("color1")
		.setName("name-1")
		.setPeriods(new ArrayList<TimeSeriesThresholdPeriod>(Arrays.asList(p1)));

	public static final TimeSeriesDescription DESC_PRIMARY_PUBLISH = new TimeSeriesDescription()
		.setComment("comment-1")
		.setComputationIdentifier("comp-id-1")
		.setComputationPeriodIdentifier("period-id-1")
		.setDescription("desc-1")
		.setExtendedAttributes(Arrays.asList(new ExtendedAttribute()
			.setName(AquariusRetrievalUtils.getPrimaryFilter().getFilterName())
			.setType("type")
			.setValue(AquariusRetrievalUtils.getPrimaryFilter().getFilterValue())))
		.setIdentifier("id-1")
		.setLabel("label-1")
		.setLastModified(Instant.parse("2017-01-01T00:00:00Z"))
		.setLocationIdentifier("loc-id-1")
		.setParameter("param-1")
		.setPublish(true)
		.setRawStartTime(Instant.parse("2017-01-01T00:00:00Z"))
		.setRawEndTime(Instant.parse("2017-01-02T00:00:00Z"))
		.setSubLocationIdentifier("sub-id-1")
		.setThresholds(Arrays.asList(threshold1))
		.setTimeSeriesType("type-1")
		.setUniqueId("uid-1")
		.setUnit("unit-1")
		.setUtcOffset(0.0)
		.setUtcOffsetIsoDuration(Duration.ofHours(0));
	
	public static final TimeSeriesDescription DESC_PRIMARY = new TimeSeriesDescription()
		.setComment("comment-1")
		.setComputationIdentifier("comp-id-1")
		.setComputationPeriodIdentifier("period-id-1")
		.setDescription("desc-1")
		.setExtendedAttributes(Arrays.asList(new ExtendedAttribute()
			.setName(AquariusRetrievalUtils.getPrimaryFilter().getFilterName())
			.setType("type")
			.setValue(AquariusRetrievalUtils.getPrimaryFilter().getFilterValue())))
		.setIdentifier("id-1")
		.setLabel("label-1")
		.setLastModified(Instant.parse("2017-01-01T00:00:00Z"))
		.setLocationIdentifier("loc-id-1")
		.setParameter("param-1")
		.setPublish(false)
		.setRawStartTime(Instant.parse("2017-01-01T00:00:00Z"))
		.setRawEndTime(Instant.parse("2017-01-02T00:00:00Z"))
		.setSubLocationIdentifier("sub-id-1")
		.setThresholds(Arrays.asList(threshold1))
		.setTimeSeriesType("type-1")
		.setUniqueId("uid-1")
		.setUnit("unit-1")
		.setUtcOffset(0.0)
		.setUtcOffsetIsoDuration(Duration.ofHours(0));
	
	public static final TimeSeriesDescription DESC_PUBLISH = new TimeSeriesDescription()
		.setComment("comment-1")
		.setComputationIdentifier("comp-id-1")
		.setComputationPeriodIdentifier("period-id-1")
		.setDescription("desc-1")
		.setExtendedAttributes(new ArrayList<>())
		.setIdentifier("id-1")
		.setLabel("label-1")
		.setLastModified(Instant.parse("2017-01-01T00:00:00Z"))
		.setLocationIdentifier("loc-id-1")
		.setParameter("param-1")
		.setPublish(true)
		.setRawStartTime(Instant.parse("2017-01-01T00:00:00Z"))
		.setRawEndTime(Instant.parse("2017-01-02T00:00:00Z"))
		.setSubLocationIdentifier("sub-id-1")
		.setThresholds(Arrays.asList(threshold1))
		.setTimeSeriesType("type-1")
		.setUniqueId("uid-1")
		.setUnit("unit-1")
		.setUtcOffset(0.0)
		.setUtcOffsetIsoDuration(Duration.ofHours(0));

	public static final TimeSeriesDescription DESC_NEITHER = new TimeSeriesDescription()
		.setComment("comment-1")
		.setComputationIdentifier("comp-id-1")
		.setComputationPeriodIdentifier("period-id-1")
		.setDescription("desc-1")
		.setExtendedAttributes(new ArrayList<>())
		.setIdentifier("id-1")
		.setLabel("label-1")
		.setLastModified(Instant.parse("2017-01-01T00:00:00Z"))
		.setLocationIdentifier("loc-id-1")
		.setParameter("param-1")
		.setPublish(false)
		.setRawStartTime(Instant.parse("2017-01-01T00:00:00Z"))
		.setRawEndTime(Instant.parse("2017-01-02T00:00:00Z"))
		.setSubLocationIdentifier("sub-id-1")
		.setThresholds(Arrays.asList(threshold1))
		.setTimeSeriesType("type-1")
		.setUniqueId("uid-1")
		.setUnit("unit-1")
		.setUtcOffset(0.0)
		.setUtcOffsetIsoDuration(Duration.ofHours(0));

	public static final TimeSeriesDescription DESC_NO_TIMES = new TimeSeriesDescription()
		.setComment("comment-1")
		.setComputationIdentifier("comp-id-1")
		.setComputationPeriodIdentifier("period-id-1")
		.setDescription("desc-1")
		.setExtendedAttributes(new ArrayList<>())
		.setIdentifier("id-1")
		.setLabel("label-1")
		.setLastModified(Instant.parse("2017-01-01T00:00:00Z"))
		.setLocationIdentifier("loc-id-1")
		.setParameter("param-1")
		.setPublish(false)
		.setSubLocationIdentifier("sub-id-1")
		.setThresholds(Arrays.asList(threshold1))
		.setTimeSeriesType("type-1")
		.setUniqueId("uid-1")
		.setUnit("unit-1")
		.setUtcOffset(0.0)
		.setUtcOffsetIsoDuration(Duration.ofHours(0));

	@Before
	public void setup() {
		service = new DerivationChainSearchService(upchainProcessorListService, downchainProcessorListService, timeSeriesDescriptionListService);
	}

	@Test
	public void timeSeriesMatchesFilterCriteriaNullTest() {
		assertFalse(service.timeSeriesMatchesFilterCriteria(null, null, null, null, null, null, null, null));
	}

	@Test
	public void timeSeriesMatchesFilterCriteriaTrueTest() {
		assertTrue(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, null, null, null, null, null, null, null));
		assertTrue(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, true, true, null, null, null, null, null));
		assertTrue(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, true, true, 
			DESC_PRIMARY_PUBLISH.getParameter(), null, null, null, null));
		assertTrue(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, true, true, 
			DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), null, null, null));
		assertTrue(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, true, true, 
			DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), 
			DESC_PRIMARY_PUBLISH.getRawStartTime(), null));
		assertTrue(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, true, true, 
			DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), 
			null, DESC_PRIMARY_PUBLISH.getRawEndTime()));
		assertTrue(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, null, null, 
			DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), 
			DESC_PRIMARY_PUBLISH.getRawStartTime(), DESC_PRIMARY_PUBLISH.getRawEndTime()));
		assertTrue(service.timeSeriesMatchesFilterCriteria(DESC_NEITHER, null, null, 
			DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), 
			DESC_PRIMARY_PUBLISH.getRawStartTime(), DESC_PRIMARY_PUBLISH.getRawEndTime()));
		assertTrue(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, true, true, 
			DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), 
			DESC_PRIMARY_PUBLISH.getRawStartTime(), DESC_PRIMARY_PUBLISH.getRawEndTime()));
		assertTrue(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY, true, false, 
			DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), 
			DESC_PRIMARY_PUBLISH.getRawStartTime(), DESC_PRIMARY_PUBLISH.getRawEndTime()));
		assertTrue(service.timeSeriesMatchesFilterCriteria(DESC_PUBLISH, false, true, 
			DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), 
			DESC_PRIMARY_PUBLISH.getRawStartTime(), DESC_PRIMARY_PUBLISH.getRawEndTime()));
		assertTrue(service.timeSeriesMatchesFilterCriteria(DESC_NEITHER, false, false, 
			DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), 
			DESC_PRIMARY_PUBLISH.getRawStartTime(), DESC_PRIMARY_PUBLISH.getRawEndTime()));
	}
	
	@Test
	public void timeSeriesMatchesFilterCriteriaFalseTest() {
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, false, null, null, null, null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, null, false, null, null, null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, false, false, null, null, null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, true, false, null, null, null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, false, true, null, null, null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, true, true, "Incorrect", null, null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, null, null, "Incorrect", null, null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, true, true, DESC_PRIMARY_PUBLISH.getParameter(), "Incorrect", null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, null, null, null, "Incorrect", null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, true, true, DESC_PRIMARY_PUBLISH.getParameter(), 
			DESC_PRIMARY_PUBLISH.getComputationIdentifier(), "Incorrect", null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, null, null, null, null, "Incorrect", null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, true, true, DESC_PRIMARY_PUBLISH.getParameter(), 
			DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), Instant.parse("2000-01-01T00:00:00Z"), Instant.parse("2000-01-01T00:00:00Z")));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, null, null, null, null, null, Instant.parse("2000-01-01T00:00:00Z"), Instant.parse("2000-01-01T00:00:00Z")));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY, false, null, null, null, null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY, false, true, null, null, null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PUBLISH, null, false, null, null, null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PUBLISH, true, false, null, null, null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_NEITHER, false, true, null, null, null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_NEITHER, null, true, null, null, null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_NEITHER, true, true, null, null, null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_NEITHER, true, false, null, null, null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_NEITHER, true, null, null, null, null, null, null));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, false, true, 
			DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), 
			DESC_PRIMARY_PUBLISH.getRawStartTime(), DESC_PRIMARY_PUBLISH.getRawEndTime()));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, true, false, 
			DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), 
			DESC_PRIMARY_PUBLISH.getRawStartTime(), DESC_PRIMARY_PUBLISH.getRawEndTime()));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, true, true, 
			"Incorrect", DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), 
			DESC_PRIMARY_PUBLISH.getRawStartTime(), DESC_PRIMARY_PUBLISH.getRawEndTime()));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, true, true, 
			DESC_PRIMARY_PUBLISH.getParameter(), "Incorrect", DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), 
			DESC_PRIMARY_PUBLISH.getRawStartTime(), DESC_PRIMARY_PUBLISH.getRawEndTime()));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, true, true, 
			DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), "Incorrect", 
			DESC_PRIMARY_PUBLISH.getRawStartTime(), DESC_PRIMARY_PUBLISH.getRawEndTime()));
		assertFalse(service.timeSeriesMatchesFilterCriteria(DESC_PRIMARY_PUBLISH, true, true, 
			DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), 
			Instant.parse("2000-01-01T00:00:00Z"), Instant.parse("2000-01-01T00:00:00Z")));
		assertTrue(service.timeSeriesMatchesFilterCriteria(DESC_NO_TIMES, null, null, null, null, null, null, null));
		assertTrue(service.timeSeriesMatchesFilterCriteria(DESC_NO_TIMES, null, null, null, null, null, Instant.parse("2000-01-01T00:00:00Z"), Instant.parse("2000-01-01T00:00:00Z")));
		
	}
	
	@Test
	public void findTimeSeriesNullTest() {
		List<TimeSeriesDescription> result = service.findTimeSeriesInDerivationChain(null, null, null, null, null, null, null, null, null, null);
		assertTrue(result.isEmpty());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void findTimeSeriesUpchainTest() {
		given(upchainProcessorListService.getRawResponse(any(String.class), any(Instant.class), any(Instant.class)))
			.willReturn(new ProcessorListServiceResponse().setProcessors(new ArrayList<>()));
		given(upchainProcessorListService.getInputTimeSeriesUniqueIdList(any(ArrayList.class)))
			.willReturn(new ArrayList<String>(Arrays.asList("any")));
		given(timeSeriesDescriptionListService.getTimeSeriesDescriptionList(any(ArrayList.class)))
			.willReturn(new ArrayList<TimeSeriesDescription>(Arrays.asList(DESC_PRIMARY_PUBLISH, DESC_PRIMARY, DESC_PUBLISH, DESC_NEITHER)));
		Instant testStart = DESC_PRIMARY_PUBLISH.getRawStartTime();
		Instant testEnd = DESC_PRIMARY_PUBLISH.getRawEndTime();
		
		List<TimeSeriesDescription> result = service.findTimeSeriesInDerivationChain(null, "upchain", null, null, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH, DESC_PRIMARY, DESC_PUBLISH, DESC_NEITHER));
		result = service.findTimeSeriesInDerivationChain(null, "upchain", true, true, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH));
		result = service.findTimeSeriesInDerivationChain(null, "upchain", true, null, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH, DESC_PRIMARY));
		result = service.findTimeSeriesInDerivationChain(null, "upchain", null, true, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH, DESC_PUBLISH));
		result = service.findTimeSeriesInDerivationChain(null, "upchain", false, null, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PUBLISH, DESC_NEITHER));
		result = service.findTimeSeriesInDerivationChain(null, "upchain", null, false, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY, DESC_NEITHER));
		result = service.findTimeSeriesInDerivationChain(null, "upchain", false, false, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_NEITHER));
		result = service.findTimeSeriesInDerivationChain(null, "upchain", null, null, DESC_PRIMARY_PUBLISH.getParameter(), null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH, DESC_PRIMARY, DESC_PUBLISH, DESC_NEITHER));
		result = service.findTimeSeriesInDerivationChain(null, "upchain", null, null, "Incorrect", null, null, testStart, testEnd, false);
		assertTrue(result.isEmpty());
		result = service.findTimeSeriesInDerivationChain(null, "upchain", true, true, DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void findTimeSeriesDownchainTest() {
		given(downchainProcessorListService.getRawResponse(any(String.class), any(Instant.class), any(Instant.class)))
			.willReturn(new ProcessorListServiceResponse().setProcessors(new ArrayList<>()));
		given(downchainProcessorListService.getOutputTimeSeriesUniqueIdList(any(ArrayList.class)))
			.willReturn(new ArrayList<String>(Arrays.asList("any")));
		given(timeSeriesDescriptionListService.getTimeSeriesDescriptionList(any(ArrayList.class)))
			.willReturn(new ArrayList<TimeSeriesDescription>(Arrays.asList(DESC_PRIMARY_PUBLISH, DESC_PRIMARY, DESC_PUBLISH, DESC_NEITHER)));
		Instant testStart = DESC_PRIMARY_PUBLISH.getRawStartTime();
		Instant testEnd = DESC_PRIMARY_PUBLISH.getRawEndTime();
		
		List<TimeSeriesDescription> result = service.findTimeSeriesInDerivationChain(null, "downchain", null, null, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH, DESC_PRIMARY, DESC_PUBLISH, DESC_NEITHER));
		result = service.findTimeSeriesInDerivationChain(null, "downchain", true, true, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH));
		result = service.findTimeSeriesInDerivationChain(null, "downchain", true, null, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH, DESC_PRIMARY));
		result = service.findTimeSeriesInDerivationChain(null, "downchain", null, true, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH, DESC_PUBLISH));
		result = service.findTimeSeriesInDerivationChain(null, "downchain", false, null, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PUBLISH, DESC_NEITHER));
		result = service.findTimeSeriesInDerivationChain(null, "downchain", null, false, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY, DESC_NEITHER));
		result = service.findTimeSeriesInDerivationChain(null, "downchain", false, false, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_NEITHER));
		result = service.findTimeSeriesInDerivationChain(null, "downchain", null, null, DESC_PRIMARY_PUBLISH.getParameter(), null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH, DESC_PRIMARY, DESC_PUBLISH, DESC_NEITHER));
		result = service.findTimeSeriesInDerivationChain(null, "downchain", null, null, "Incorrect", null, null, testStart, testEnd, false);
		assertTrue(result.isEmpty());
		result = service.findTimeSeriesInDerivationChain(null, "downchain", true, true, DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void findTimeSeriesUpchainDownchainTest() {
		List<String> upList = Arrays.asList("up");
		List<String> downList = Arrays.asList("down");
		given(upchainProcessorListService.getRawResponse(any(String.class), any(Instant.class), any(Instant.class)))
			.willReturn(new ProcessorListServiceResponse().setProcessors(new ArrayList<>()));
		given(upchainProcessorListService.getInputTimeSeriesUniqueIdList(any(ArrayList.class)))
			.willReturn(new ArrayList<String>(upList));
		given(downchainProcessorListService.getRawResponse(any(String.class), any(Instant.class), any(Instant.class)))
			.willReturn(new ProcessorListServiceResponse().setProcessors(new ArrayList<>()));
		given(downchainProcessorListService.getOutputTimeSeriesUniqueIdList(any(ArrayList.class)))
			.willReturn(new ArrayList<String>(downList));
		given(timeSeriesDescriptionListService.getTimeSeriesDescriptionList(upList))
			.willReturn(new ArrayList<TimeSeriesDescription>(Arrays.asList(DESC_PRIMARY_PUBLISH, DESC_PRIMARY)));
		given(timeSeriesDescriptionListService.getTimeSeriesDescriptionList(downList))
			.willReturn(new ArrayList<TimeSeriesDescription>(Arrays.asList(DESC_PUBLISH, DESC_NEITHER)));
		Instant testStart = DESC_PRIMARY_PUBLISH.getRawStartTime();
		Instant testEnd = DESC_PRIMARY_PUBLISH.getRawEndTime();
		
		List<TimeSeriesDescription> result = service.findTimeSeriesInDerivationChain(null, "upchain", null, null, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH, DESC_PRIMARY));
		result = service.findTimeSeriesInDerivationChain(null, "upchain", true, true, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH));
		result = service.findTimeSeriesInDerivationChain(null, "upchain", true, null, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH, DESC_PRIMARY));
		result = service.findTimeSeriesInDerivationChain(null, "upchain", null, true, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH));
		result = service.findTimeSeriesInDerivationChain(null, "upchain", false, null, null, null, null, testStart, testEnd, false);
		assertTrue(result.isEmpty());
		result = service.findTimeSeriesInDerivationChain(null, "upchain", null, false, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY));
		result = service.findTimeSeriesInDerivationChain(null, "upchain", false, false, null, null, null, testStart, testEnd, false);
		assertTrue(result.isEmpty());
		result = service.findTimeSeriesInDerivationChain(null, "upchain", null, null, DESC_PRIMARY_PUBLISH.getParameter(), null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH, DESC_PRIMARY));
		result = service.findTimeSeriesInDerivationChain(null, "upchain", null, null, "Incorrect", null, null, testStart, testEnd, false);
		assertTrue(result.isEmpty());
		result = service.findTimeSeriesInDerivationChain(null, "upchain", true, true, DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PRIMARY_PUBLISH));
		result = service.findTimeSeriesInDerivationChain(null, "upchain", false, false, DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), testStart, testEnd, false);
		assertTrue(result.isEmpty());
		result = service.findTimeSeriesInDerivationChain(null, "downchain", null, null, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PUBLISH, DESC_NEITHER));
		result = service.findTimeSeriesInDerivationChain(null, "downchain", true, true, null, null, null, testStart, testEnd, false);
		assertTrue(result.isEmpty());
		result = service.findTimeSeriesInDerivationChain(null, "downchain", true, null, null, null, null, testStart, testEnd, false);
		assertTrue(result.isEmpty());
		result = service.findTimeSeriesInDerivationChain(null, "downchain", null, true, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PUBLISH));
		result = service.findTimeSeriesInDerivationChain(null, "downchain", false, null, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PUBLISH, DESC_NEITHER));
		result = service.findTimeSeriesInDerivationChain(null, "downchain", null, false, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_NEITHER));
		result = service.findTimeSeriesInDerivationChain(null, "downchain", false, false, null, null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_NEITHER));
		result = service.findTimeSeriesInDerivationChain(null, "downchain", null, null, DESC_PRIMARY_PUBLISH.getParameter(), null, null, testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_PUBLISH, DESC_NEITHER));
		result = service.findTimeSeriesInDerivationChain(null, "downchain", null, null, "Incorrect", null, null, testStart, testEnd, false);
		assertTrue(result.isEmpty());
		result = service.findTimeSeriesInDerivationChain(null, "downchain", true, true, DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), testStart, testEnd, false);
		assertTrue(result.isEmpty());
		result = service.findTimeSeriesInDerivationChain(null, "downchain", false, false, DESC_PRIMARY_PUBLISH.getParameter(), DESC_PRIMARY_PUBLISH.getComputationIdentifier(), DESC_PRIMARY_PUBLISH.getComputationPeriodIdentifier(), testStart, testEnd, false);
		assertThat(result, containsInAnyOrder(DESC_NEITHER));
	}
}