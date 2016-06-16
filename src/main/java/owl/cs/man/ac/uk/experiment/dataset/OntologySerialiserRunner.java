package owl.cs.man.ac.uk.experiment.dataset;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class OntologySerialiserRunner {

	/**
	 * @param args
	 * @throws TimeoutException 
	 */
	public static void main(String[] args) {
		if (args.length != 6) {
			throw new IllegalArgumentException(
					"You need exactly four parameters (ontologyfile, target directory, targetformat (owlxml,rdfxml,functional csvlist),merged (mn,my) and the name of the OntologySerialiser,timeoout.");
		}

		String sourcedirpath = args[0];
		String targetdirpath = args[1];
		String targetformat = args[2];
		String [] targetformats = targetformat.split(",");
		String merged = args[3];
		String serialiser = args[4];
		long timeout = Long.valueOf(args[5]);
		
		
		File sourcedir = new File(sourcedirpath);
		File targetdir = new File(targetdirpath);
		
		if(!sourcedir.exists()) {
			throw new IllegalArgumentException(
					sourcedir+" does not exist");
		}
		
		if(!targetdir.exists()) {
			throw new IllegalArgumentException(
					targetdir+" does not exist");
		}
		
		RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = RuntimemxBean.getInputArguments();
		
		for(File file:sourcedir.listFiles()) {
			for(int i = 0;i<targetformats.length;i++) {
				if((new File(targetdir,file.getName())).exists()) {
					System.out.println(file+" exists in source, omitting..");
					continue;
				}
					
				//String command = "java -Xms2G -Xmx4G -jar D:\\ontologyserialiser-0.0.3.jar "+file+" "+targetformats[i]+" "+merged;
				List<String> command = new ArrayList<String>();
				command.add("java");
				command.add(getArgument(arguments,"-Xms\\d*G"));
				command.add(getArgument(arguments,"-Xmx\\d*G"));
				command.add("-jar");
				command.add(serialiser);
				command.add(file.toString());
				command.add(targetdirpath);
				command.add(targetformats[i]);
				command.add(merged);
				
				System.out.println(command);
				try {
					ProcessBuilder pb = new ProcessBuilder(command);
					pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
					pb.redirectError(ProcessBuilder.Redirect.INHERIT);
					Process p = pb.start();
					
					Worker worker = new Worker(p);
					  worker.start();
					  try {
					    worker.join(timeout);
					    if (worker.exit == null)
					      throw new TimeoutException();
					  } catch(InterruptedException ex) {
					    worker.interrupt();
					    Thread.currentThread().interrupt();
					    throw ex;
					  } finally {
					    p.destroy();
					  }
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}

	}
	
	private static String getArgument(List<String> arguments, String regex) {
		for(String s:arguments) {
			if(s.matches(regex)) {
				return s;
			}
		}
		return null;
	}

	private static class Worker extends Thread {
		  private final Process process;
		  public Integer exit;
		  private Worker(Process process) {
		    this.process = process;
		  }
		  public void run() {
		    try { 
		      exit = process.waitFor();
		    } catch (InterruptedException ignore) {
		      return;
		    }
		  }  
		}

}
