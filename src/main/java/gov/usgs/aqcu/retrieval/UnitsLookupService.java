package gov.usgs.aqcu.retrieval;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitMetadata;

@Repository
public class UnitsLookupService {
	private static final Logger LOG = LoggerFactory.getLogger(UnitsLookupService.class);	

	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public UnitsLookupService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

	protected UnitListServiceResponse getRawResponse() {
		UnitListServiceRequest request = new UnitListServiceRequest();
		UnitListServiceResponse unitList = aquariusRetrievalService.executePublishApiRequest(request);
		return unitList;
	}

	public List<UnitMetadata> getUnits() {
		return getRawResponse().getUnits();
	}
}
