package owl.cs.man.ac.uk.experiment.util;

public class TerribleTimeoutThread extends Thread {
	
	final long millis;

	public TerribleTimeoutThread(long millis) {
    	super("TerribleTimeoutThread");
    	this.millis = millis;
    }
    public void run() {
    	try {
			Thread.sleep(millis);
			throw new RuntimeException("TerribleTimeoutThread");
		} catch (InterruptedException e) {
			System.out.println("Sleep interrupted, which means that the process ended by itself.");
		}
    }
}
