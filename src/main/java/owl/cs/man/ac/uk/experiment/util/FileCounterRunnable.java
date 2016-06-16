package owl.cs.man.ac.uk.experiment.util;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import owl.cs.man.ac.uk.experiment.file.FileFeatureCounter;


public class FileCounterRunnable extends MyRunnable {

	private int fileCount;
	private List<File> files;
	private Component component;

	public FileCounterRunnable(List<File> files, Component component) {
		super("Line Counter");
		this.files = files;
		this.fileCount = files.size();
	}
	
	@Override
	protected void processRun() {
		int lineCounter = 0;
		int fileCounter = 0;
		int charCounter = 0;
		
		for(File f:files) {
			fileCounter++;
			try {
				lineCounter+=FileFeatureCounter.countLines(f);
				charCounter+=FileFeatureCounter.countUnicodeChars(f);
				setChanged();
				notifyObservers(fileCounter);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		JOptionPane.showMessageDialog(this.component, "Number of lines: "+lineCounter + "\n" + "Number of chars: " + charCounter);
	}
	
	public int getAmountOfTasks() {
		return this.fileCount;
	}
}
