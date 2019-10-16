package gov.usgs.aqcu.retrieval;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import gov.usgs.aqcu.model.report.ReportParameterConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(SpringRunner.class)
public class ReportParameterConfigLookupServiceTest {

	private ReportParameterConfigLookupService service;
	private Map<String, String> reportParamConfigs = new LinkedHashMap<>();
	private Map<String, String> reportType;
	
	@Before
	public void setup() {
		service = new ReportParameterConfigLookupService();
		reportParamConfigs.put("gwvisitreviewstatus", "GW_VRSTAT");
		reportType = new HashMap<>();
		reportType.put("reportType", "gwvisitreviewstatus");
		reportType.put("displayName", "GW Visit Review Status");
		reportType.put("serviceType", "LAMBDA");
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
	
	@Test
	public void getReportType2Test() throws JsonProcessingException {
		String result = service.getReportTypes();
		assertEquals("[{\"reportType\":\"gwvisitreviewstatus\",\"reportTypeDisplayName\":\"GW Visit Review Status\",\"serviceType\":\"LAMBDA\",\"parameters\":[{\"name\":\"locationIdentifier\",\"display\":\"Primary Location\",\"type\":\"location\"}]}]", result);
	}
}
