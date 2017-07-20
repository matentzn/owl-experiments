package owl.cs.man.ac.uk.experiment.metrics;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owl.cs.man.ac.uk.experiment.csv.CSVUtilities;

public class ClassifierTester {

	public static void main(String[] args) {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		//List<Map<String,String>> data = new ArrayList<Map<String,String>>();
		File csv = new File("D:\\0testout\\classifier\\records.csv");
		for(File file:(new File("D:\\Data\\bp11-2012\\ontologies")).listFiles()) {
			Map<String,String> cldata = new HashMap<String,String>();
			OWLOntology o;
			try {
				o = manager.loadOntologyFromOntologyDocument(file);
				cldata.put("class_topic", Classifier.classificationSetToString(Classifier.classifyTopic(manager, o)));
				cldata.put("class_topic1", Classifier.classificationSetToString(Classifier.classifyTopic(manager, o)));
				cldata.put("class_topic2", Classifier.classificationSetToString(Classifier.classifyTopic(manager, o)));
				cldata.put("class_health", Classifier.classificationSetToString(Classifier.classifyHealthTopic(manager, o)));
				cldata.put("class_webpagecontent", Classifier.classificationSetToString(Classifier.classifyWebPageContent(manager, o)));
				cldata.put("class_computer", Classifier.classificationSetToString(Classifier.classifyComputerTopic(manager, o)));
				cldata.put("class_business", Classifier.classificationSetToString(Classifier.classifyBusinessTopic(manager, o)));
				cldata.put("class_news", Classifier.classificationSetToString(Classifier.classifyNewsTopic(manager, o)));
				//cldata.putAll(ExperimentUtils.getDefaultExperimentData("TopicClassification", file.getName()));
				CSVUtilities.appendCSVData(csv, cldata);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
