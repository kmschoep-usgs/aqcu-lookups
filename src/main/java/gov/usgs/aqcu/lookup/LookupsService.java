package gov.usgs.aqcu.lookup;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitMetadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.usgs.aqcu.model.LocationBasicData;
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
import gov.usgs.aqcu.retrieval.TimeSeriesDescriptionListService;
import gov.usgs.aqcu.retrieval.UnitsLookupService;
import gov.usgs.aqcu.retrieval.UpchainRatingModelSearchService;
import gov.usgs.aqcu.util.TimeSeriesUtils;
import gov.usgs.aqcu.util.LogExecutionTime;

@Repository
public class LookupsService {
	private TimeSeriesDescriptionListService timeSeriesDescriptionListService;
	private ProcessorTypesService processorTypesService;
	private LocationSearchService locationSearchService;
	private ComputationReferenceService computationReferenceService;
	private ControlConditionReferenceService controlConditionReferenceService;
	private PeriodReferenceService periodReferenceService;
	private UnitsLookupService unitsLookupService;
	private FieldVisitDescriptionListService fieldVisitDescriptionListService;
	private DerivationChainSearchService derivationChainService;
	private UpchainRatingModelSearchService upchainRatingModelSearchService;

	@Autowired
	public LookupsService(
		TimeSeriesDescriptionListService timeSeriesDescriptionListService,
		ProcessorTypesService processorTypesService,
		LocationSearchService locationSearchService,
		UnitsLookupService unitsLookupService,
		ComputationReferenceService computationReferenceService,
		ControlConditionReferenceService controlConditionReferenceService,
		PeriodReferenceService periodReferenceService,
		FieldVisitDescriptionListService fieldVisitDescriptionListService,
		DerivationChainSearchService derivationChainService,
		UpchainRatingModelSearchService upchainRatingModelSearchService) {
			this.timeSeriesDescriptionListService = timeSeriesDescriptionListService;
			this.processorTypesService = processorTypesService;
			this.locationSearchService = locationSearchService;
			this.unitsLookupService = unitsLookupService;
			this.computationReferenceService = computationReferenceService;
			this.controlConditionReferenceService = controlConditionReferenceService;
			this.periodReferenceService = periodReferenceService;
			this.fieldVisitDescriptionListService = fieldVisitDescriptionListService;
			this.derivationChainService = derivationChainService;
			this.upchainRatingModelSearchService = upchainRatingModelSearchService;
	}

        @LogExecutionTime
	public Map<String,TimeSeriesBasicData> getTimeSeriesDescriptions(TimeSeriesIdentifiersRequestParameters params) {
		List<TimeSeriesDescription> descs = timeSeriesDescriptionListService.getTimeSeriesDescriptionList(params.getComputationIdentifier(), params.getComputationPeriodIdentifier(),
			params.getStationId(), params.getParameter(), params.getPublish(), params.getPrimary());
		return descs.stream().map(d -> new TimeSeriesBasicData(d)).collect(Collectors.toMap(TimeSeriesBasicData::getUniqueId, Function.identity()));
	}

        @LogExecutionTime
	public List<String> searchDerivationChain(FindInDerivationChainRequestParameters params) {
		ZoneOffset primaryZoneOffset = getZoneOffset(params.getTimeSeriesIdentifier());
		List<TimeSeriesDescription> descList = derivationChainService.findTimeSeriesInDerivationChain(params.getTimeSeriesIdentifier(), params.getDirection(), params.getPrimary(), null, params.getParameter(), 
			params.getComputationIdentifier(), params.getComputationPeriodIdentifier(), params.getStartInstant(primaryZoneOffset), params.getEndInstant(primaryZoneOffset), params.getFullChain());
		return descList.stream().map(d -> d.getUniqueId()).collect(Collectors.toList());
	}

        @LogExecutionTime
	public List<String> getRatingModel(GetUpchainRatingModelsRequestParameters params) {
		ZoneOffset primaryZoneOffset = getZoneOffset(params.getTimeSeriesIdentifier());
		return upchainRatingModelSearchService.getRatingModelsUpchain(params.getTimeSeriesIdentifier(), params.getStartInstant(primaryZoneOffset), params.getEndInstant(primaryZoneOffset), params.getFullChain());
	}

        @LogExecutionTime
	public Map<String, List<String>> getProcessorTypes(ProcessorTypesRequestParameters params) {
		ZoneOffset primaryZoneOffset = getZoneOffset(params.getTimeSeriesIdentifier());
		return processorTypesService.getProcessorTypes(params.getTimeSeriesIdentifier(), params.getStartInstant(primaryZoneOffset), params.getEndInstant(primaryZoneOffset));
	}

        @LogExecutionTime
	public List<LocationBasicData> searchSites(SiteSearchRequestParameters params) {
		return locationSearchService.searchSites(params.getSiteNumber(), params.getPageSize());
	}

        @LogExecutionTime
	public List<String> getFieldVisitDates(FieldVisitDatesRequestParameters params) {
		return fieldVisitDescriptionListService.getFieldVisitDates(params.getSiteNumber());
	}

        @LogExecutionTime
	public List<String> getControlConditions() {
		return controlConditionReferenceService.get();
	}

        @LogExecutionTime
	public List<String> getComputations() {
		return computationReferenceService.get();
	}

        @LogExecutionTime
	public List<String> getPeriods() {
		 return periodReferenceService.get();
	}

        @LogExecutionTime
	public List<String> getUnits() {
		List<UnitMetadata> unitMetadataList = unitsLookupService.getUnits();
		return unitMetadataList.stream().map(u -> u.getIdentifier()).collect(Collectors.toList());
	}

        @LogExecutionTime
	protected ZoneOffset getZoneOffset(String timeSeriesIdentifier) {
		TimeSeriesDescription primaryDescription = null;

		if(timeSeriesIdentifier != null) {
			primaryDescription = timeSeriesDescriptionListService.getTimeSeriesDescription(timeSeriesIdentifier);
		}
		
		return TimeSeriesUtils.getZoneOffset(primaryDescription);
	}
}