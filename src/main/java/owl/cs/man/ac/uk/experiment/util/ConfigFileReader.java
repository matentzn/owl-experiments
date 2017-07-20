package owl.cs.man.ac.uk.experiment.util;

import java.io.File;
import java.io.InputStream;

public interface ConfigFileReader {

	public void readConfigurationFile(File file);
	public String getConfiguration(String configName);
	public void readConfigurationFile(InputStream file);
}
