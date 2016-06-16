package owl.cs.man.ac.uk.experiment.util;

import java.util.Observable;

public abstract class MyRunnable extends Observable implements Runnable {
	
	private String runnableName;
	
	public MyRunnable() {
		setRunnableName("Unkown Process");
	}
	
	public MyRunnable(String name) {
		setRunnableName(name);
	}

	public void run() {
		try {
			processRun();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected abstract void processRun();

	public String getRunnableName() {
		return runnableName;
	}

	private void setRunnableName(String runnableName) {
		this.runnableName = runnableName;
	}
	
	public abstract int getAmountOfTasks();

}
