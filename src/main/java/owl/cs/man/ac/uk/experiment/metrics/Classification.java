package owl.cs.man.ac.uk.experiment.metrics;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Classification {
	
	private final String className;
	private final double membership;
	
	public Classification(String className, double membership) {
		this.className = className;
		this.membership = membership;
	}
	public String getClassName() {
		return className;
	}
	public double getMembership() {
		return membership;
	}
	@Override
	public String toString() {
		BigDecimal bd = new BigDecimal(membership*100).setScale(2, RoundingMode.HALF_EVEN);
		return className + ": "+ bd.doubleValue()+" %";
	}

}
