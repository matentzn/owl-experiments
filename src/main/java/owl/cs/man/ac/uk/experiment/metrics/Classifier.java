package owl.cs.man.ac.uk.experiment.metrics;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Classifier {

	private static OWLOntology ontology;
	private static OWLOntologyManager manager;
	private static final String UCLASSIFY_URL = "http://uclassify.com/browse/";
	private static final String UCLASIIFY_DEFAULT_PATH = "uClassify/";
	private static final String SUFFIX = "/ClassifyText?readkey=7kjIwOoRIQFKTIiEMxhkkh8LZh4&text=";// +

	private static double THRESHOLD_CUTOFF = 0.1;
	private static Set<String> ignore = new HashSet<String>();

	public static String getDefaultServiceURL(String service) {
		return UCLASSIFY_URL + UCLASIIFY_DEFAULT_PATH + service + SUFFIX;
	}

	public static Set<Classification> classifyTopic(OWLOntologyManager man,
			OWLOntology o) {
		return classify(man, o, getDefaultServiceURL("Topics"));
	}

	public static Set<Classification> classifyScienceTopic(
			OWLOntologyManager man, OWLOntology o) {
		return classify(man, o, getDefaultServiceURL("Science%20Topics"));
	}

	public static Set<Classification> classifyComputerTopic(
			OWLOntologyManager man, OWLOntology o) {
		return classify(man, o, getDefaultServiceURL("Computer-Topics"));
	}

	public static Set<Classification> classifyHealthTopic(
			OWLOntologyManager man, OWLOntology o) {
		return classify(man, o, getDefaultServiceURL("Health-Topics"));
	}

	public static Set<Classification> classifyBusinessTopic(
			OWLOntologyManager man, OWLOntology o) {
		return classify(man, o, getDefaultServiceURL("Business-Topics"));
	}

	public static Set<Classification> classifyNewsTopic(OWLOntologyManager man,
			OWLOntology o) {
		return classify(man, o, UCLASSIFY_URL + "mvazquez/News-Classifier"
				+ SUFFIX);
	}

	public static Set<Classification> classifyWebPageContent(
			OWLOntologyManager man, OWLOntology o) {
		return classify(man, o, UCLASSIFY_URL + "cbaproject076/Webpagecontent"
				+ SUFFIX);
	}

	public static Set<Classification> classify(OWLOntologyManager man,
			OWLOntology o, String service) {
		ontology = o;
		manager = man;
		String wordBag = compileWordBag().toString();
		String param = wordBag.isEmpty() ? "" : wordBag.substring(0, wordBag.length() - 1);
		if(param.isEmpty()) {
			return new HashSet<Classification>();
		}
		String url_string = service + param;
		System.out.println(url_string);
		Set<Classification> classification = callWebService(url_string);
		return classification;
	}

	private static Set<Classification> callWebService(String url_string) {
		Set<Classification> classification = new HashSet<Classification>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(new URL(url_string).openStream());
			NodeList nodes = doc.getElementsByTagName("class");

			// iterate the employees
			for (int j = 0; j < nodes.getLength(); j++) {
				Element element = (Element) nodes.item(j);
				Attr className = element.getAttributeNode("className");
				Attr p = element.getAttributeNode("p");
				double coverage = Double.valueOf(p.getTextContent());
				if (coverage > THRESHOLD_CUTOFF) {
					classification.add(new Classification(className
							.getTextContent(), coverage));
				}
			}
			// System.out.println(format(doc));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return classification;
	}

	private static String compileWordBag() {
		StringBuilder builder = new StringBuilder();
		List<String> wordlist = new ArrayList<String>();
		for (OWLAxiom ax : ontology.getAxioms()) {
			for (OWLEntity e : ax.getClassesInSignature()) {
				wordlist.add(getLabel(e));
			}
			for (OWLEntity e : ax.getIndividualsInSignature()) {
				wordlist.add(getLabel(e));
			}
		}

		List<String> sublist = new ArrayList<String>();
		for (String s : wordlist) {
			if (!ignore.contains(s)) {
				sublist.add(s);
			}
		}
		Collections.shuffle(sublist);

		if (sublist.size() > 120) {
			sublist = sublist.subList(0, 120);
		}
		for (String term : sublist) {
			// System.out.println(term);
			builder.append(term + "+");
		}
		return builder.toString();
	}

	private static String getLabel(OWLEntity e) {
		StringBuilder label = new StringBuilder();
		String annotation = getAnnotation(e);
		if (!annotation.isEmpty()) {
			return annotation.replaceAll(" ", "+").replaceAll("[^A-Za-z+]", "");
		}

		String fragment = e.getIRI().getRemainder().or("unkown");
		if (fragment == null) {
			return "";
		}
		if (fragment.equals(fragment.toUpperCase())) {
			return fragment;
		} else {
			for (String s : fragment.split("(?=\\p{Upper})")) {
				if (s.length() > 1) {
					label.append(s + "+");
				}
			}
			if (label.toString().endsWith("+")) {
				return label.toString().substring(0,
						label.toString().length() - 1);
			} else {
				return label.toString();
			}
		}

	}

	private static String getAnnotation(OWLEntity e) {
		OWLDataFactory df = manager.getOWLDataFactory();
		OWLAnnotationProperty label = df
				.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

		for (OWLAnnotation annotation : EntitySearcher.getAnnotations(e, ontology,label)) {
			if (annotation.getValue() instanceof OWLLiteral) {
				OWLLiteral val = (OWLLiteral) annotation.getValue();
				return val.getLiteral();
			}
		}
		return "";
	}

	public static String format(Document doc) throws TransformerException {
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		// initialize StreamResult with File object to save to file
		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);
		String xmlString = result.getWriter().toString();
		return xmlString;
	}

	public static void clearDefaultIgnoreTerms() {
		ignore.clear();
	}

	public static void setDefaultIgnoreTerms(Set<String> ignorenew) {
		ignore.addAll(ignorenew);
	}

	public static String classificationSetToString(Set<Classification> cl) {
		StringBuilder builder = new StringBuilder();
		for(Classification c:cl) {
			builder.append(c.getClassName()+":"+(new BigDecimal(c.getMembership()*100).setScale(2, RoundingMode.HALF_EVEN))+";");
		}
		return builder.toString();
	}
}
