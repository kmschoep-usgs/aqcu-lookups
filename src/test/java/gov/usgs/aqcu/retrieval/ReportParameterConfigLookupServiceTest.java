package gov.usgs.aqcu.retrieval;

import java.util.LinkedHashMap;
import java.util.Map;

import gov.usgs.aqcu.model.ReportParameterConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ReportParameterConfigLookupServiceTest {

	private ReportParameterConfigLookupService service;
	private Map<String, String> reportParamConfigs = new LinkedHashMap<>();
	
	@Before
	public void setup() {
		service = new ReportParameterConfigLookupService();
		reportParamConfigs.put("gwvrstatreport", "GW_VRSTAT");
	}

	@Test
	public void getByReportTypeTest() {
		for (Map.Entry<String, String> entry : reportParamConfigs.entrySet()) {
			String value = entry.getKey();
			ReportParameterConfig config = ReportParameterConfig.getByReportType(value);
			assertEquals(config.getReportType(), value);
		}
	}
	
	@Test
	public void getByReportTypeServiceTest() {
		for (Map.Entry<String, String> entry : reportParamConfigs.entrySet()) {
			String value = entry.getKey();
			ReportParameterConfig config = service.getByReportType(value);
			assertEquals(config.getReportType(), value);
		}
	}
}
