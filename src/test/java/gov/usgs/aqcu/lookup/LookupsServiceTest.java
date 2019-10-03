package gov.usgs.aqcu.lookup;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesThreshold;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesThresholdPeriod;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitMetadata;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ExtendedAttribute;

import gov.usgs.aqcu.model.LocationBasicData;
import gov.usgs.aqcu.model.ReportBasicParameter;
import gov.usgs.aqcu.model.ReportParameterConfig;
import gov.usgs.aqcu.model.TimeSeriesBasicData;
import gov.usgs.aqcu.parameter.FieldVisitDatesRequestParameters;
import gov.usgs.aqcu.parameter.FindInDerivationChainRequestParameters;
import gov.usgs.aqcu.parameter.GetUpchainRatingModelsRequestParameters;
import gov.usgs.aqcu.parameter.ProcessorTypesRequestParameters;
import gov.usgs.aqcu.parameter.SiteSearchRequestParameters;
import gov.usgs.aqcu.parameter.TimeSeriesIdentifiersRequestParameters;
import gov.usgs.aqcu.reference.ComputationReferenceService;
import gov.usgs.aqcu.reference.ControlConditionReferenceService;
import gov.usgs.aqcu.reference.PeriodReferenceService;
import gov.usgs.aqcu.retrieval.DerivationChainSearchService;
import gov.usgs.aqcu.retrieval.FieldVisitDescriptionListService;
import gov.usgs.aqcu.retrieval.LocationSearchService;
import gov.usgs.aqcu.retrieval.ProcessorTypesService;
import gov.usgs.aqcu.retrieval.ReportParameterConfigLookupService;
import gov.usgs.aqcu.retrieval.TimeSeriesDescriptionListService;
import gov.usgs.aqcu.retrieval.UnitsLookupService;
import gov.usgs.aqcu.retrieval.UpchainRatingModelSearchService;
import gov.usgs.aqcu.util.AquariusRetrievalUtils;

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

public class LookupsServiceTest {
	@MockBean
	private TimeSeriesDescriptionListService timeSeriesDescriptionListService;
	@MockBean
	private ProcessorTypesService processorTypesService;
	@MockBean
	private LocationSearchService locationSearchService;
	@MockBean
	private ComputationReferenceService computationReferenceService;
	@MockBean
	private ControlConditionReferenceService controlConditionReferenceService;
	@MockBean
	private PeriodReferenceService periodReferenceService;
	@MockBean
	private UnitsLookupService unitsLookupService;
	@MockBean
	private FieldVisitDescriptionListService fieldVisitDescriptionListService;
	@MockBean
	private DerivationChainSearchService derivationChainService;
	@MockBean
	private UpchainRatingModelSearchService upchainRatingModelSearchService;
	@MockBean
	private ReportParameterConfigLookupService reportParameterConfigLookupService;
	
	private TimeSeriesThresholdPeriod p1 = new TimeSeriesThresholdPeriod()
		.setStartTime(Instant.parse("2017-01-01T00:00:00Z"))
		.setEndTime(Instant.parse("2017-02-01T00:00:00Z"))
		.setAppliedTime(Instant.parse("2017-02-01T00:00:00Z"))
		.setComments("comments-1")
		.setReferenceValue(1.0)
		.setSecondaryReferenceValue(2.0)
		.setSuppressData(false);
	private TimeSeriesThreshold threshold1 = new TimeSeriesThreshold()
		.setDescription("desc-1")
		.setDisplayColor("color1")
		.setName("name-1")
		.setPeriods(new ArrayList<TimeSeriesThresholdPeriod>(Arrays.asList(p1)));
	private TimeSeriesDescription tsDesc1 = new TimeSeriesDescription()
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
		.setUtcOffset(1.0)
		.setUtcOffsetIsoDuration(Duration.ofHours(0));
	private UnitMetadata unit1 = new UnitMetadata()
		.setDisplayName("name1")
		.setIdentifier("identifier1")
		.setSymbol("symbol1");
	private UnitMetadata unit2 = new UnitMetadata()
		.setDisplayName("name2")
		.setIdentifier("identifier2")
		.setSymbol("symbol2");
	private UnitMetadata unit3 = new UnitMetadata()
		.setDisplayName("name3")
		.setIdentifier("identifier3")
		.setSymbol("symbol3");	
	private LookupsService service;

	@Before
	public void setup() {
		service = new LookupsService(timeSeriesDescriptionListService, processorTypesService, locationSearchService,
			unitsLookupService, computationReferenceService, controlConditionReferenceService, periodReferenceService,
			fieldVisitDescriptionListService, derivationChainService, upchainRatingModelSearchService, reportParameterConfigLookupService);
	}

	@Test
	public void getTimeSeriesDescriptionsEmptyParamsTest() {
		given(timeSeriesDescriptionListService.getTimeSeriesDescriptionList(any(String.class), any(String.class), any(String.class), any(String.class), any(Boolean.class), any(Boolean.class)))
			.willReturn(new ArrayList<>());
		
		Map<String,TimeSeriesBasicData> result = service.getTimeSeriesDescriptions(new TimeSeriesIdentifiersRequestParameters());
		assertTrue(result.isEmpty());
	}
	

	@Test
	public void getTimeSeriesDescriptionsEmptyTest() {
		given(timeSeriesDescriptionListService.getTimeSeriesDescriptionList(any(String.class), any(String.class), any(String.class), any(String.class), any(Boolean.class), any(Boolean.class)))
			.willReturn(new ArrayList<>());
		TimeSeriesIdentifiersRequestParameters params = new TimeSeriesIdentifiersRequestParameters();
		params.setStationId("any");
		params.setParameter("any");
		params.setComputationIdentifier("any");
		params.setComputationPeriodIdentifier("any");
		params.setPrimary(false);
		params.setPublish(false);

		Map<String,TimeSeriesBasicData> result = service.getTimeSeriesDescriptions(params);
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void getTimeSeriesDescriptionsTest() {
		given(timeSeriesDescriptionListService.getTimeSeriesDescriptionList(any(String.class), any(String.class), any(String.class), any(String.class), any(Boolean.class), any(Boolean.class)))
			.willReturn(new ArrayList<>(Arrays.asList(tsDesc1)));
		TimeSeriesIdentifiersRequestParameters params = new TimeSeriesIdentifiersRequestParameters();
		params.setStationId("any");
		params.setParameter("any");
		params.setComputationIdentifier("any");
		params.setComputationPeriodIdentifier("any");
		params.setPrimary(false);
		params.setPublish(false);
	
		Map<String,TimeSeriesBasicData> result = service.getTimeSeriesDescriptions(params);
		assertEquals(result.size(), 1);
		assertTrue(result.containsKey(tsDesc1.getUniqueId()));
		assertTrue(result.get(tsDesc1.getUniqueId()).getParameter().equals(tsDesc1.getParameter()));
		assertTrue(result.get(tsDesc1.getUniqueId()).getUniqueId().equals(tsDesc1.getUniqueId()));
	}

	@Test
	public void searchDerivationChainEmptyTest() {
		given(derivationChainService.findTimeSeriesInDerivationChain(any(String.class), any(String.class), any(Boolean.class), any(Boolean.class), any(String.class),
			any(String.class), any(String.class), any(Instant.class), any(Instant.class), any(Boolean.class)))
		.willReturn(new ArrayList<>());
		FindInDerivationChainRequestParameters params = new FindInDerivationChainRequestParameters();
		params.setComputationIdentifier("any");
		params.setComputationPeriodIdentifier("any");
		params.setDirection("upchain");
		params.setFullChain(false);
		params.setLastMonths(12);
		params.setParameter("any");
		params.setPrimary(false);
		params.setTimeSeriesIdentifier("any");

		List<String> result = service.searchDerivationChain(params);
		assertTrue(result.isEmpty());
	}

	@Test
	public void searchDerivationChainTest() {
		given(derivationChainService.findTimeSeriesInDerivationChain(any(String.class), any(String.class), any(Boolean.class), any(Boolean.class), any(String.class),
			any(String.class), any(String.class), any(Instant.class), any(Instant.class), any(Boolean.class)))
		.willReturn(new ArrayList<>(Arrays.asList(tsDesc1)));
		FindInDerivationChainRequestParameters params = new FindInDerivationChainRequestParameters();
		params.setComputationIdentifier("any");
		params.setComputationPeriodIdentifier("any");
		params.setDirection("upchain");
		params.setFullChain(false);
		params.setLastMonths(12);
		params.setParameter("any");
		params.setPrimary(false);
		params.setTimeSeriesIdentifier("any");

		List<String> result = service.searchDerivationChain(params);
		assertEquals(result.size(), 1);
		assertThat(result, containsInAnyOrder(tsDesc1.getUniqueId()));
	}
	
	@Test
	public void getRatingModelEmptyTest() {
		given(upchainRatingModelSearchService.getRatingModelsUpchain(any(String.class), any(Instant.class), any(Instant.class), any(Boolean.class)))
			.willReturn(new ArrayList<>());
		GetUpchainRatingModelsRequestParameters params = new GetUpchainRatingModelsRequestParameters();
		params.setTimeSeriesIdentifier("any");
		params.setFullChain(false);
		params.setLastMonths(12);

		List<String> result = service.getRatingModel(params);
		assertTrue(result.isEmpty());
	}

	@Test
	public void getRatingModelTest() {
		given(upchainRatingModelSearchService.getRatingModelsUpchain(any(String.class), any(Instant.class), any(Instant.class), any(Boolean.class)))
			.willReturn(new ArrayList<>(Arrays.asList("test1", "test2")));
		GetUpchainRatingModelsRequestParameters params = new GetUpchainRatingModelsRequestParameters();
		params.setTimeSeriesIdentifier("any");
		params.setFullChain(false);
		params.setLastMonths(12);

		List<String> result = service.getRatingModel(params);
		assertEquals(result.size(), 2);
		assertThat(result, containsInAnyOrder("test1", "test2"));
	}

	@Test
	public void getProcessorTypesEmptyTest() {
		given(processorTypesService.getProcessorTypes(any(String.class), any(Instant.class), any(Instant.class)))
			.willReturn(new HashMap<String, List<String>>());
		ProcessorTypesRequestParameters params = new ProcessorTypesRequestParameters();
		params.setLastMonths(12);
		params.setTimeSeriesIdentifier("any");

		Map<String, List<String>> result = service.getProcessorTypes(params);
		assertTrue(result.isEmpty());
	}

	@Test
	public void getProcessorTypesTest() {
		Map<String, List<String>> expected = new HashMap<>();
		List<String> up = new ArrayList<>(Arrays.asList("testup"));
		List<String> down = new ArrayList<>(Arrays.asList("testdown"));
		expected.put("upChain", up);
		expected.put("downChain", down);
		given(processorTypesService.getProcessorTypes(any(String.class), any(Instant.class), any(Instant.class)))
			.willReturn(expected);
		ProcessorTypesRequestParameters params = new ProcessorTypesRequestParameters();
		params.setLastMonths(12);
		params.setTimeSeriesIdentifier("any");

		Map<String, List<String>> result = service.getProcessorTypes(params);
		assertEquals(result, expected);
	}

	@Test
	public void searchSitesEmptyTest() {
		given(locationSearchService.searchSites(any(String.class), any(Integer.class)))
			.willReturn(new ArrayList<>());
		SiteSearchRequestParameters params = new SiteSearchRequestParameters();
		params.setPageSize(1);
		params.setSiteNumber("any");

		List<LocationBasicData> result = service.searchSites(params);
		assertTrue(result.isEmpty());
	}

	@Test
	public void searchSitesTest() {
		LocationBasicData loc1 = new LocationBasicData();
		loc1.setSiteNumber("0001");
		loc1.setSiteName("test1");
		LocationBasicData loc2 = new LocationBasicData();
		loc2.setSiteNumber("0002");
		loc2.setSiteName("test2");
		given(locationSearchService.searchSites(any(String.class), any(Integer.class)))
			.willReturn(new ArrayList<>(Arrays.asList(loc1, loc2)));
		SiteSearchRequestParameters params = new SiteSearchRequestParameters();
		params.setPageSize(1);
		params.setSiteNumber("any");

		List<LocationBasicData> result = service.searchSites(params);
		assertEquals(result.size(), 2);
		assertThat(result, containsInAnyOrder(loc1, loc2));
	}

	@Test
	public void getFieldVisitDatesEmptyTest() {
		given(fieldVisitDescriptionListService.getFieldVisitDates(any(String.class)))
			.willReturn(new ArrayList<>());
		FieldVisitDatesRequestParameters params = new FieldVisitDatesRequestParameters();
		params.setSiteNumber("any");
		
		List<String> result = service.getFieldVisitDates(params);
		assertTrue(result.isEmpty());
	}

	@Test
	public void getFieldVisitDatesTest() {
		given(fieldVisitDescriptionListService.getFieldVisitDates(any(String.class)))
			.willReturn(new ArrayList<>(Arrays.asList("2017-01-01", "2017-02-01")));
		FieldVisitDatesRequestParameters params = new FieldVisitDatesRequestParameters();
		params.setSiteNumber("any");
		
		List<String> result = service.getFieldVisitDates(params);
		assertEquals(result.size(), 2);
		assertThat(result, containsInAnyOrder("2017-01-01", "2017-02-01"));
	}

	@Test
	public void getControlConditionsEmptyTest() {
		given(controlConditionReferenceService.get()).willReturn(new ArrayList<>());
		List<String> result = service.getControlConditions();
		assertTrue(result.isEmpty());
	}

	@Test
	public void getControlConditionsTest() {
		given(controlConditionReferenceService.get()).willReturn(new ArrayList<>(Arrays.asList("test1", "test2")));
		List<String> result = service.getControlConditions();
		assertThat(result, containsInAnyOrder("test1", "test2"));
	}

	@Test
	public void getComputationsEmptyTest() {
		given(computationReferenceService.get()).willReturn(new ArrayList<>());
		List<String> result = service.getComputations();
		assertTrue(result.isEmpty());
	}

	@Test
	public void getComputationsTest() {
		given(computationReferenceService.get()).willReturn(new ArrayList<>(Arrays.asList("test1", "test2")));
		List<String> result = service.getComputations();
		assertThat(result, containsInAnyOrder("test1", "test2"));
	}

	@Test
	public void getPeriodsEmptyTest() {
		given(periodReferenceService.get()).willReturn(new ArrayList<>());
		List<String> result = service.getPeriods();
		assertTrue(result.isEmpty());
	}

	@Test
	public void getPeriodsTest() {
		given(periodReferenceService.get()).willReturn(new ArrayList<>(Arrays.asList("test1", "test2")));
		List<String> result = service.getPeriods();
		assertThat(result, containsInAnyOrder("test1", "test2"));
	}

	@Test
	public void getUnitsEmptyTest() {
		given(unitsLookupService.getUnits()).willReturn(new ArrayList<>());
		List<String> result = service.getUnits();
		assertTrue(result.isEmpty());
	}

	@Test
	public void getUnitsTest() {
		given(unitsLookupService.getUnits()).willReturn(new ArrayList<>(Arrays.asList(unit1, unit2, unit3)));
		List<String> result = service.getUnits();
		assertThat(result, containsInAnyOrder(unit1.getIdentifier(), unit2.getIdentifier(), unit3.getIdentifier()));
	}

	@Test
	public void getZoneOffsetNullTest() {
		assertEquals(service.getZoneOffset(null), ZoneOffset.UTC);
	}
	
	@Test
	public void getZoneOffsetTest() {
		given(timeSeriesDescriptionListService.getTimeSeriesDescription(any(String.class)))
			.willReturn(tsDesc1);
		assertEquals(service.getZoneOffset(tsDesc1.getIdentifier()), ZoneOffset.ofHours(1));
	}
	
	@Test
	public void getGwVRStatParameterConfig() {
		List<ReportBasicParameter> parameters = new ArrayList<>();
		parameters.add(new ReportBasicParameter("locationIdentifier", "Primary Location", "location"));
		ReportParameterConfig gwVRStatReportParamConfig = new ReportParameterConfig();
		gwVRStatReportParamConfig.setReportType("gw-vrstat");
		gwVRStatReportParamConfig.setParameters(parameters);
		
		given(reportParameterConfigLookupService.getReportParameterConfig(any(String.class)))
		.willReturn(gwVRStatReportParamConfig);
		assertEquals("gw-vrstat", gwVRStatReportParamConfig.getReportType());
		assertEquals("locationIdentifier", gwVRStatReportParamConfig.getParameters().get(0).getName());
		assertEquals("Primary Location", gwVRStatReportParamConfig.getParameters().get(0).getDisplay());
		assertEquals("location", gwVRStatReportParamConfig.getParameters().get(0).getType());
	}
}