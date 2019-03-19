package gov.usgs.aqcu.retrieval;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitMetadata;

import gov.usgs.aqcu.util.LogExecutionTime;

@Repository
public class UnitsLookupService {
	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public UnitsLookupService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

        @LogExecutionTime
	protected UnitListServiceResponse getRawResponse() {
		UnitListServiceRequest request = new UnitListServiceRequest();
		UnitListServiceResponse unitList = aquariusRetrievalService.executePublishApiRequest(request);
		return unitList;
	}

        @LogExecutionTime
	public List<UnitMetadata> getUnits() {
		return getRawResponse().getUnits();
	}
}
