package gov.usgs.aqcu.reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ControlConditionType;

@Repository
public class ControlConditionReferenceService {
	private static final Logger LOG = LoggerFactory.getLogger(ControlConditionReferenceService.class);

	@Autowired
	public ControlConditionReferenceService() {}

	public List<Map<String,String>> get() {
        //TODO: Cache this list somewhere else
		List<ControlConditionType> controlConditionTypes = new ArrayList<>(Arrays.asList(ControlConditionType.values()));
		List<Map<String,String>> controlConditionList = new ArrayList<>();
		for(ControlConditionType c : controlConditionTypes) {
			Map<String,String> conditionMap = new HashMap<>();
			conditionMap.put("name: ", c.name());
			conditionMap.put("value: ", c.toString());
			controlConditionList.add(conditionMap);
		}

		return controlConditionList;
	}
}