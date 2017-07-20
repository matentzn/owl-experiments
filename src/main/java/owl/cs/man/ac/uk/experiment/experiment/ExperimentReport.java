package owl.cs.man.ac.uk.experiment.experiment;

import java.util.HashMap;
import java.util.Map;


public class ExperimentReport  {
	
	Map<String,String> general = new HashMap<>();
	
	String name;
	
	public ExperimentReport(String name) {
		this.name = name;
	}

	public void addGeneralInfoToReport(String attributeName, String attributeValue) {
		general.put(attributeName, attributeValue);
	}
	
	public Map<String, String> getGeneral() {
		return general;
	}

	public String getName() {
		return name;
	}

	
}