package owl.cs.man.ac.uk.experiment.dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class DownloadWebDirectory {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws MalformedURLException, InterruptedException {
		URL url;
	    InputStream is = null;
	    BufferedReader br;
	    String line;
	    String targetDir = "D:\\Data\\biokb";

	    try {
	    	String urls = "http://www.ai.sri.com/~halo/public/exported-kb/owl/";
	        url = new URL(urls);
	        is = url.openStream();  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is));

	        while ((line = br.readLine()) != null) {
	        	if(line.contains("href=\"")&&line.contains(".ofn")) {
	        	String filename = line.substring(line.indexOf("href=\"")+6, line.indexOf(".ofn"));
	        	System.out.println(filename);
	        	File destination = new File(targetDir, filename+".ofn");
	        	if(!destination.exists()) {
	        		URL fileurl = new URL(urls+"/"+filename+".ofn");
	        		FileUtils.copyURLToFile(fileurl, destination, 2000, 5000);
	        		Thread.sleep(1000);
	        	}	        	
	        	}
	        }
	        
	    } catch (MalformedURLException mue) {
	         mue.printStackTrace();
	    } catch (IOException ioe) {
	         ioe.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException ioe) {
	            // nothing to see here
	        }
	    }
	
	}

}
