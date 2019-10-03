package gov.usgs.aqcu.retrieval;

import java.util.ArrayList;
import java.util.List;

import gov.usgs.aqcu.model.ReportBasicParameter;
import gov.usgs.aqcu.model.ReportParameterConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ReportParameterConfigLookupServiceTest {

	private ReportParameterConfigLookupService service;

	@Before
	public void setup() {
		service = new ReportParameterConfigLookupService();
	}

	@Test
	public void getGwVRStatParameterConfig() {
		List<ReportBasicParameter> parameters = new ArrayList<>();
		parameters.add(new ReportBasicParameter("locationIdentifier", "Primary Location", "location"));
		ReportParameterConfig gwVRStatReportParamConfig = new ReportParameterConfig();
		gwVRStatReportParamConfig.setReportType("gw-vrstat");
		gwVRStatReportParamConfig.setParameters(parameters);
		
		ReportParameterConfig testGwVRStatReportParamConfig = service.getReportParameterConfig("gw-vrstat");
		assertEquals(testGwVRStatReportParamConfig.getReportType(), gwVRStatReportParamConfig.getReportType());
		assertEquals(testGwVRStatReportParamConfig.getParameters().get(0).getName(), gwVRStatReportParamConfig.getParameters().get(0).getName());
		assertEquals(testGwVRStatReportParamConfig.getParameters().get(0).getDisplay(), gwVRStatReportParamConfig.getParameters().get(0).getDisplay());
		assertEquals(testGwVRStatReportParamConfig.getParameters().get(0).getType(), gwVRStatReportParamConfig.getParameters().get(0).getType());
	}
}
