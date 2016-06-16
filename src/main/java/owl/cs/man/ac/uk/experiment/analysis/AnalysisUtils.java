package owl.cs.man.ac.uk.experiment.analysis;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import owl.cs.man.ac.uk.experiment.util.ExperimentUtilities;
import owl.cs.man.ac.uk.experiment.metrics.reasoner.ReasonerMetrics.ReasonerMetaData;

public class AnalysisUtils {

	public static long getMin(List<Long> list) {
		return Collections.min(list);
	}
	
	public static boolean approximatelyEqual(long d1, long d2, double alloweddifference) {
		
		if ((d1 + d2) > 0) {
			double differencechange = (double)(d1 - d2) / (double)((double)(d1 + d2) / 2);
			//AnalysisUtils.p(d1+" "+d2+" "+differencechange);
			if (Math.abs(differencechange) < alloweddifference) {
				return true;
			}
		}
		return false;
	}

	public static long getMax(List<Long> list) {
		return Collections.max(list);
	}
	
	public static long getSum(List<Long> list) {
		Long sum= 0l; 
	     for (Long i:list) {
	         sum = sum + i;
	     }
	     return sum;
	}
	
	public static boolean isInsideRange(long teststart, long testend,
			long rangestart, long rangeend) {
		if (teststart > testend) {
			throw new IllegalArgumentException(
					"Test end smaller than test start!");
		}
		if (teststart < rangestart) {
			p("Starts earlier!");
			return false;
		}
		if (testend > rangeend) {
			p("Ends later!");
			return false;
		}
		return true;
	}

	public static void p(Object string) {
		System.out.println(string);
	}
	public static void pp(Object string) {
		p(string);
		pause();
		pause();
	}
	
	public static double getSeconds(long l) {
		return ExperimentUtilities.getSeconds(l);
	}

	public static long getLong(Map<String, String> rec, ReasonerMetaData rmd) {
		return getLong(rec, rmd.getName());
	}

	public static long getLong(Map<String, String> rec, String name) {
		return ExperimentUtilities.getLong(rec, name);
	}

	public static void e(Object string) {
		System.err.println("Warning: "+string);
	}
	
	public static void pause() {
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean validNumericEntry(Map<String, String> rec, String key) {
		if (rec.containsKey(key)) {
			String value = rec.get(key);
			if (!value.isEmpty()) {
				if (ExperimentUtilities.isNaturalNumber(value)) {
					return true;
				}
			}
		}
		return false;
	}

	public static double percentageDifference(double p1,
			double p2) {
		return Math.abs((p1-p2)/((p1+p2)/2))*100;
	}

	public static double percentageChange(double newV,
			double oldV) {
		return ((newV-oldV)/(Math.abs(oldV)))*100;
	}
	
	public static double foldChange(double p1,
			double p2) {
		double res = p1/p2;
		return ((res < 1) && (res >-1)) ? (1/res)*(-1) : res;
	}
}
