package owl.cs.man.ac.uk.experiment.ontology;

import java.util.Observable;

import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

/**
 * @author Nico Matentzoglu
 */

public class MyProgressMonitor extends Observable implements
		ReasonerProgressMonitor {

	/**
	 * The ReasonerProgressMonitor Interface is provided by the OWL API and
	 * implemented here. It is the way to determine what current state the
	 * reasoner is in: whether is is classiying or realizing, or what the
	 * progress is. Apart from this implementation, MyProgressMonitor is
	 * implemented to be observerable (See Observer Pattern).
	 */
	
	public void reasonerTaskBusy() {
		System.out.println("Reasoner ist busy");

	}

	public void reasonerTaskProgressChanged(int value, int max) {
		System.out.println(value + " von " + max);
		currentValue = value % 100;
		currentMax = max;
		setChanged();
		notifyObservers();
	}

	public void reasonerTaskStarted(String taskName) {
		System.out.println("Current Task: " + taskName);
		currentOperation = taskName;
		setChanged();
		notifyObservers();
	}

	public void reasonerTaskStopped() {
		System.out.println("Reasoner end of Task" + currentOperation);
		currentOperation = "ended";
		setChanged();
		notifyObservers();

	}

	public int getCurrentValue() {
		return currentValue;
	}

	public int getCurrentMax() {
		return currentMax;
	}

	public String getCurrentOperation() {
		return currentOperation;
	}

	private int currentValue = 0;
	private int currentMax = 0;
	private String currentOperation = "start";

}
