package gov.usgs.aqcu.model.config.persist;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class FolderConfigTest {	
    
    @Test
    public void setPropertiesTest() {
        FolderConfig testConfig = new FolderConfig();
        SavedReportConfiguration testReport = new SavedReportConfiguration();
        testReport.setId("123");
        testConfig.saveReport(testReport);

        assertTrue(testConfig.getProperties().getCanStoreReports());
        assertFalse(testConfig.getSavedReports().isEmpty());

        FolderProperties newProps = new FolderProperties();
        newProps.setCanStoreReports(false);
        testConfig.setProperties(newProps);

        assertFalse(testConfig.getProperties().getCanStoreReports());
        assertTrue(testConfig.getSavedReports().isEmpty());
    }

    @Test
    public void WhenReportMapHasValues_getSavedReportByTypeReturnsMapWithKeyReportType(){
        FolderConfig testConfig = new FolderConfig();
        SavedReportConfiguration testReport = new SavedReportConfiguration();
        testReport.setId("123");
        testReport.setReportType("report_type");
        testConfig.saveReport(testReport);
        assertTrue(testConfig.getSavedReportByType().containsKey("report_type"));
    }
    
}
