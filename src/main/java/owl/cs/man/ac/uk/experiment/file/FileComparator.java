package owl.cs.man.ac.uk.experiment.file;

import java.io.File;

public abstract class FileComparator {

	public abstract void process(File first, File second);

	public void compareAll(File first, File second) {
		if (first.isDirectory()) {
			String[] childrenFirst = first.list();
			for (int i = 0; i < childrenFirst.length; i++) {
				if (second.isDirectory()) {
					String[] childrenSecond = second.list();
					for (int j = 0; j < childrenSecond.length; j++) {
						compareAll(new File(first, childrenFirst[i]), new File(
								second, childrenSecond[j]));
					}

				} else {
					compareAll(new File(first, childrenFirst[i]),second);
				}
			}
		} else {
			if (second.isDirectory()) {
				String[] childrenSecond = second.list();
				for (int j = 0; j < childrenSecond.length; j++) {
					compareAll(first, new File(
							second, childrenSecond[j]));
				}
			} else {
				process(first,second);
			}
		}
	}

	
}
