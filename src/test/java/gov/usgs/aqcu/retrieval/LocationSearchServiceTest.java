package gov.usgs.aqcu.retrieval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import gov.usgs.aqcu.aquarius.Apps.LocationSearchResult;
import gov.usgs.aqcu.aquarius.Apps.SearchLocationsServiceResponse;
import gov.usgs.aqcu.model.LocationBasicData;
import net.servicestack.client.IReturn;


@RunWith(SpringRunner.class)
public class LocationSearchServiceTest {

	@MockBean
    private AquariusAppsRetrievalService aquariusAppsRetrievalService;
    private LocationSearchService service;
    private LocationSearchResult loc1 = new LocationSearchResult()
        .setId(1)
        .setName("test1")
        .setIdentifier("testid1");
    private LocationSearchResult loc2 = new LocationSearchResult()
        .setId(2)
        .setName("test2")
        .setIdentifier("testid2");

	@Before
	public void setup() {
		service = new LocationSearchService(aquariusAppsRetrievalService);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void searchSitesEmptyTest() {
		given(aquariusAppsRetrievalService.executeAppsApiRequest(any(IReturn.class)))
            .willReturn(new SearchLocationsServiceResponse().setResults(new ArrayList<>()));
        
        List<LocationBasicData> result = service.searchSites("", 1);
        assertTrue(result.isEmpty());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void searchSitesTest() {
		given(aquariusAppsRetrievalService.executeAppsApiRequest(any(IReturn.class)))
			.willReturn(new SearchLocationsServiceResponse().setResults(new ArrayList<>(Arrays.asList(loc1,loc2))));
        List<LocationBasicData> result = service.searchSites("", 2);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getSiteName(), loc1.getName());
        assertEquals(result.get(0).getSiteNumber(), loc1.getIdentifier());
        assertEquals(result.get(1).getSiteName(), loc2.getName());
        assertEquals(result.get(1).getSiteNumber(), loc2.getIdentifier());
	}
}
