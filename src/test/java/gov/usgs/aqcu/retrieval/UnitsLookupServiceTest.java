package gov.usgs.aqcu.retrieval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitMetadata;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitListServiceResponse;

import net.servicestack.client.IReturn;


@RunWith(SpringRunner.class)
public class UnitsLookupServiceTest {

	@MockBean
    private AquariusRetrievalService aquariusRetrievalService;
    private UnitsLookupService service;
    private UnitMetadata unit1 = new UnitMetadata()
        .setIdentifier("unitId1")
        .setDisplayName("unit1");
    private UnitMetadata unit2 = new UnitMetadata()
            .setIdentifier("unitId2")
            .setDisplayName("unit2");
    private UnitListServiceResponse unitList = new UnitListServiceResponse()
    		.setUnits(new ArrayList<>(Arrays.asList(unit1,unit2)));
    		

	@Before
	public void setup() {
		service = new UnitsLookupService(aquariusRetrievalService);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void locationDataEmptyTest() {
		given(aquariusRetrievalService.executePublishApiRequest(any(IReturn.class)))
            .willReturn(new UnitListServiceResponse());
        
		UnitListServiceResponse result = service.getRawResponse();
        assertNull(result.getUnits());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void locationDataTest() {
		given(aquariusRetrievalService.executePublishApiRequest(any(IReturn.class)))
        .willReturn(unitList);
		UnitListServiceResponse results = service.getRawResponse();
		assertEquals(results, unitList);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getTimeSeriesDescriptionListTest() {
		given(aquariusRetrievalService.executePublishApiRequest(any(IReturn.class)))
        .willReturn(unitList);
		List<UnitMetadata> results = service.getUnits();
		assertEquals(results.size(), 2);
		assertThat(results, containsInAnyOrder(unit2, unit1));
	}
}
