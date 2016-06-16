package owl.cs.man.ac.uk.experiment.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtilities {
	
	public static final String REGEX_BEGIN_OR_WHITESPACE_OR_UNDERSCORE = "(?<![^\\s_])";
	public static final String REGEX_END_OR_NONALPHANUMERIC = "(?![^\\s\\W_])";
	public static final String REGEX_NUMERIC = "[-+]?[0-9]*[\\.,]?[0-9]+([eE][-+]?[0-9]+)?";
	
	public static int countSubstring(String s, String sub) {
		Pattern p = Pattern.compile(sub);
		Matcher m = p.matcher(s);
		int count = 0;
		while (m.find()) {
			count += 1;
		}
		return count;
	}
	
	public static String lrs(String s) {

		// / FROM: http://introcs.cs.princeton.edu/java/42sort/LRS.java.html

		// form the N suffixes
		int N = s.length();
		String[] suffixes = new String[N];
		for (int i = 0; i < N; i++) {
			suffixes[i] = s.substring(i, N);
		}

		// sort them
		Arrays.sort(suffixes);

		// find longest repeated substring by comparing adjacent sorted suffixes
		String lrs = "";
		for (int i = 0; i < N - 1; i++) {
			String x = lcp(suffixes[i], suffixes[i + 1]);
			if (x.length() > lrs.length())
				lrs = x;
		}
		return lrs;
	}

	public static String lcp(String s, String t) {

		// / FROML http://introcs.cs.princeton.edu/java/42sort/LRS.java.html
		int n = Math.min(s.length(), t.length());
		for (int i = 0; i < n; i++) {
			if (s.charAt(i) != t.charAt(i))
				return s.substring(0, i);
		}
		return s.substring(0, n);
	}
	
	public static boolean regexMatchesString(String regex, String string, boolean ignoreCase) {
		Pattern p = ignoreCase ? Pattern.compile(regex , Pattern.CASE_INSENSITIVE) : Pattern.compile(regex);
	    Matcher m = p.matcher(string);
	    if(m.find()) {
	    	return true;
	    } 
	    else {
	    	return false;
	    }
	}
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}

}
