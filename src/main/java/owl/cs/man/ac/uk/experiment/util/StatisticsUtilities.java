package owl.cs.man.ac.uk.experiment.util;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class StatisticsUtilities {
	Double[] data;
	double size;

	public StatisticsUtilities(Double[] data) {
		this.data = data;
		size = data.length;
	}

	public double getMean() {
		double sum = 0.0;
		for (Double a : data)
			sum += a;
		return sum / size;
	}

	public double getVariance() {
		double mean = getMean();
		double temp = 0;
		for (Double a : data)
			temp += (mean - a) * (mean - a);
		return temp / size;
	}

	public double getStdDev() {
		return Math.sqrt(getVariance());
	}

	public double getMedian() {
		Double[] b = new Double[data.length];
		System.arraycopy(data, 0, b, 0, b.length);
		Arrays.sort(b);

		if (data.length % 2 == 0) {
			return (b[(b.length / 2) - 1] + b[b.length / 2]) / 2.0;
		} else {
			return b[b.length / 2];
		}
	}
	
	public static double getPearsonCorrelation(Map<Double,Double> data) {
		PearsonsCorrelation pc = new PearsonsCorrelation();
		double[] x = new double[data.size()];
		double[] y = new double[data.size()];
		int i = 0;
		for(Double xd:data.keySet()) {
			x[i] = xd;
			y[i] = data.get(xd);
			i++;
		}
		return pc.correlation(x, y);
	}
}
