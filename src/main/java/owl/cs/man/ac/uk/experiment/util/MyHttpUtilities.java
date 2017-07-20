package owl.cs.man.ac.uk.experiment.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class MyHttpUtilities {

	public static boolean urlAlive(String strUrl) {

		try {
			URL url = new URL(strUrl);
			if (url.getProtocol().equals("http")||url.getProtocol().equals("https")) {
				HttpURLConnection urlConn = (HttpURLConnection) url
						.openConnection();
				urlConn.connect();
				if (HttpURLConnection.HTTP_OK != urlConn.getResponseCode()) {
					return false;
				}
			} else if (url.getProtocol().equals("ftp")) {
				System.out.println("FTP CONNECTION ALIVE NESS NOT YET IMPLEMENTED, OMITTING");
				return true;
			}
		} catch (IOException e) {
			System.err.println("Error creating HTTP connection");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean hasEqualDomain(String url_str1, String url_str2) {
		try {
			URL url1 = new URL(url_str1);
			URL url2 = new URL(url_str2);
			String url1Domain = url1.getAuthority();
			String url2Domain = url2.getAuthority();
			
			//System.out.println("Comparing domain "+url1Domain+" with domain "+url2Domain+"...");
			
			if(url1Domain==null||url2Domain==null) {
				System.out.println(":Not equal domains, at least one is null!");
				return false;
			}
			
			if(url1Domain.equals(url2Domain)) {
				System.out.println(":Equal!");
				return true;
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean isValidURL(String url) {
		try {
			new URL(url);
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static int getHttpStatus(URL url) throws IOException {
		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");	
		connection.connect();
		return connection.getResponseCode();
	}
	
	public static int getHttpStatus(URL url, int timeout) throws IOException {
		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(timeout);		
		connection.connect();
		return connection.getResponseCode();
	} 

	public static String getDomainFromUrl(String urlstring) throws MalformedURLException {
		URL uri = new URL(urlstring);
		return uri.getProtocol()+"://"+uri.getAuthority();
	}

	public static String getWellformedUrl(String urls) throws MalformedURLException {
		URL url = new URL(urls);
		return url.toExternalForm();
	}

	public static Date getURLLastChanged(URL url) throws IOException {
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

	    long date = httpCon.getLastModified();
	    if (date == 0) {
	      System.out.println("No last-modified information, taking today.");
	      return new Date();
	    }
	    else {
	      return new Date(date);
	    }
	}
}
