package owl.cs.man.ac.uk.experiment.util;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import owl.cs.man.ac.uk.experiment.file.FileUtilities;


public class DefaultConfigFileReader implements ConfigFileReader {

	HashMap<String, String> configuration = new HashMap<String, String>();
	
	public void readConfigurationFile(File file) {
		List<String> configFileContent = FileUtilities.readFileLineByLineIntoList(file);
		extractConfiguration(configFileContent);
	}

	private void extractConfiguration(List<String> configFileContent) {
		for(String line:configFileContent){
			if(line.isEmpty()) {
				//ignore
			} 
			else if(line.startsWith(";")) {
				//ignore, comment
			}
			else if(line.startsWith("#")) {
				//ignore, comment
			}
			else if(line.contains("=")){
				saveConfiguration(line);
			}
			
		}
	}
	
	public void readConfigurationFile(InputStream is) {		
		List<String> configFileContent = FileUtilities.readFileLineByLineIntoList(is);
		extractConfiguration(configFileContent);
	}
	
	private void saveConfiguration(String line) {
		int count = line.replaceAll("[^=]", "").length();
		if(count==1) {
			String configurationKey = line.split("=")[0];
			String value = line.split("=")[1];
			System.out.println("Save key: "+configurationKey+" Value: "+value);
			configuration.put(configurationKey.trim(), value.trim());
		}
		
	}

	public String getConfiguration(String configName) {
		return configuration.get(configName);
	}

}