package gov.usgs.aqcu.retrieval;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceResponse;

import net.servicestack.client.IReturn;


@RunWith(SpringRunner.class)
public class LocationDataServiceTest {

	@MockBean
    private AquariusRetrievalService aquariusRetrievalService;
    private LocationDataService service;
    private LocationDataServiceResponse loc1 = new LocationDataServiceResponse()
        .setIdentifier("testid1")
        .setLocationName("test1");

	@Before
	public void setup() {
		service = new LocationDataService(aquariusRetrievalService);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void locationDataEmptyTest() {
		given(aquariusRetrievalService.executePublishApiRequest(any(IReturn.class)))
            .willReturn(new LocationDataServiceResponse());
        
		LocationDataServiceResponse result = service.getRawResponse("123");
        assertNull(result.getIdentifier());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void locationDataTest() {
		given(aquariusRetrievalService.executePublishApiRequest(any(IReturn.class)))
        .willReturn(loc1);
		LocationDataServiceResponse result = service.getRawResponse("testid1");
        assertEquals(result.getLocationName(), loc1.getLocationName());
        assertEquals(result.getIdentifier(), loc1.getIdentifier());
	}
}
