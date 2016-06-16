package owl.cs.man.ac.uk.experiment.csv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import owl.cs.man.ac.uk.experiment.file.FileUtilities;
import owl.cs.man.ac.uk.experiment.ontology.MetricsLabels;

public class CSVUtilities {

	/**
	 * @param args
	 */

	private static void appendNonExistingColumns(Map<String, String> record,
			List<String> columns) {
		for (String column : record.keySet()) {
			if (column.isEmpty()) {
				continue;
			}
			if (!columns.contains(column)) {
				columns.add(column);
			}
		}
	}

	public static void writeCSVData(File file, Map<String, String> record,
			boolean append) {
		writeCSVData(file, Collections.singleton(record), append);
	}

	public static void writeCSVData(File file,
			Collection<Map<String, String>> records, boolean append) {
		try {
			List<String> columns = new ArrayList<String>();

			filterBadData(records);

			if (append) {
				String csvhead = FileUtilities.getFirstLine(file);

				if (csvhead != null) {
					columns.addAll(Arrays.asList(csvhead.split(",")));
					boolean changed = appendNonExistingColumns(records, columns);
					if (changed) {
						List<Map<String, String>> old = getAllRecords(file);
						writeCSVHead(file, columns);
						appendRecords(file, old, columns);
					}
					appendRecords(file, records, columns);
				} else {
					appendNonExistingColumns(records, columns);
					writeCSVHead(file, columns);
					appendRecords(file, records, columns);
				}
			} else {
				appendNonExistingColumns(records, columns);
				writeCSVHead(file, columns);
				appendRecords(file, records, columns);
			}

			System.out.println("Saved CSV to " + file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void filterBadData(Collection<Map<String, String>> records) {
		for (Map<String, String> rec : records) {
			if (rec.containsKey("")) {
				rec.remove("");
			}
		}
	}

	public static void writeCSVHead(File file, List<String> columns)
			throws IOException {
		FileUtils.writeStringToFile(file, listToCSV(columns) + "\n", false);
	}

	private static void appendRecords(File file,
			Collection<Map<String, String>> records, List<String> columns)
			throws IOException {
		StringBuilder content = new StringBuilder();
		for (Map<String, String> record : records) {
			content.append(createRecord(record, columns));
		}
		FileUtils.writeStringToFile(file, content.toString(), true);
	}

	private static boolean appendNonExistingColumns(
			Collection<Map<String, String>> records, List<String> columns) {
		boolean changed = false;
		int s = columns.size();
		for (Map<String, String> record : records) {
			appendNonExistingColumns(record, columns);
		}
		if (s != columns.size()) {
			changed = true;
		}
		return changed;
	}

	private static String createRecord(Map<String, String> record,
			List<String> columns) {
		StringBuilder content = new StringBuilder();

		for (String column : columns) {
			String value = record.containsKey(column) ? record.get(column) : "";
			value = value == null ? "" : value;
			value = value.equalsIgnoreCase("true") ? "1" : value;
			value = value.equalsIgnoreCase("false") ? "0" : value;
			value = processCellValueForCSV(value);

			content.append(value + ",");

		}
		content.append("\n");
		return content.toString();
	}

	public static String listToCSV(List<?> r) {
		StringBuilder inner = new StringBuilder();
		for (Object v : r) {
			String vs = processCellValueForCSV(v.toString());
			inner.append(vs + ",");
		}
		return inner.toString();
	}

	private static String processCellValueForCSV(String s) {
		String vs = s;
		// If the string contains quotes, escape them. If they contain commas,
		// wrap in quotes.
		if (vs.contains("\"")) {
			vs = vs.replaceAll("\"", "\\\"");
		}
		if (vs.contains(",")) {
			vs = "\"" + vs + "\"";
		}
		return vs;
	}

	public static List<Map<String, String>> getAllRecords(File csv) {
		List<Map<String, String>> records = new ArrayList<Map<String, String>>();
		if (csv.isDirectory()) {
			records.addAll(getAllRecordsFromDir(csv));
		} else if (csv.isFile()) {
			records.addAll(getAllRecordsFromCSV(csv));
		} else {
			System.err.println(csv + " does not exist.");
		}
		return records;
	}

	public static List<Map<String, String>> getAllRecordsFromDir(File csv_dir) {
		List<Map<String, String>> allRecords = new ArrayList<Map<String, String>>();
		for (File csv : csv_dir.listFiles(new CSVFileNameFilter())) {
			if (csv.isDirectory()) {
				continue;
			}
			allRecords.addAll(getAllRecordsFromCSV(csv));
		}
		return allRecords;
	}

	public static void consolidateRecords(List<Map<String, String>> allRecords,
			Set<String> columns, String id) {
		Map<String, Map<String, String>> consolidated = new HashMap<String, Map<String, String>>();

		for (Map<String, String> record : allRecords) {
			String recordid = record.get(id);
			if (recordid == null) {
				System.err.println("Record " + record
						+ " does not contain an id, ignoring!");
				continue;
			} else if (recordid.isEmpty()) {
				System.err.println("Record " + record
						+ " does not contain an id, ignoring!");
				continue;
			}
			if (consolidated.containsKey(recordid)) {
				Map<String, String> consolidatedRecord = consolidated
						.get(recordid);
				for (String key : record.keySet()) {
					boolean keyInRecord = false;
					String value = record.get(key);
					Map<String, String> consolidatedRecordAdd = new HashMap<String, String>();
					for (String keyConsolidated : consolidatedRecord.keySet()) {

						if (key.equals(keyConsolidated)) {

							keyInRecord = true;
							String valueConsolidated = consolidatedRecord
									.get(key);
							if (value.equals(valueConsolidated)) {
								// all good, no need to do anything
							} else {
								if (value.trim().equals("NULL")) {
								} else if (value.trim().isEmpty()) {
								} else if (valueConsolidated.equals("CONFLICT")) {
									// do nothing
								} else if (valueConsolidated.trim().equals(
										"NULL")) {
									consolidatedRecordAdd.put(key, value);
								} else if (valueConsolidated.trim().isEmpty()) {
									consolidatedRecordAdd.put(key, value);
								} else {
									// simply conflicting values.
									consolidatedRecordAdd.put(key, "CONFLICT");
								}
							}
						}

					}
					if (!keyInRecord) {
						consolidatedRecordAdd.put(key, value);
					}

					for (String remkey : consolidatedRecordAdd.keySet()) {
						consolidatedRecord.remove(remkey);
					}
					consolidatedRecord.putAll(consolidatedRecordAdd);
				}
			} else {
				consolidated.put(recordid, record);
			}
		}

		allRecords.clear();
		allRecords.addAll(consolidated.values());
	}

	public static Set<Map<String, String>> filterRecords(Set<String> columns,
			List<Map<String, String>> allRecords) {
		Set<Map<String, String>> filteredRecords = new HashSet<Map<String, String>>();
		for (Map<String, String> v : allRecords) {
			Map<String, String> newRecord = new HashMap<String, String>();
			for (String key : v.keySet()) {
				if (!columns.isEmpty()) {
					if (columns.contains(key)) {
						newRecord.put(key, v.get(key));
					}
				} else {
					newRecord.put(key, v.get(key));
				}
			}
			filteredRecords.add(newRecord);
		}
		return filteredRecords;
	}

	public static Map<String, Map<String, String>> getRecordsIndexedByFilename(
			File csv) {
		return getRecordsIndexedBy(csv, MetricsLabels.FILENAME);
	}

	public static Map<String, Map<String, String>> getRecordsIndexedBy(
			File csv, String index) {
		Map<String, Map<String, String>> allFilesByName = new HashMap<String, Map<String, String>>();
		List<Map<String, String>> records = getAllRecords(csv);
		for (Map<String, String> record : records) {
			String filename = record.get(index);
			if (filename != null) {
				allFilesByName.put(filename, record);
			}
		}
		return allFilesByName;
	}

	public static List<Map<String, String>> getAllRecordsFromCSV(File csv) {
		return getRecords(csv);
	}

	public static void appendCSVData(File csvFile, Map<String, String> data) {
		writeCSVData(csvFile, data, true);
	}

	public static void sortColumnsAlphabetically(File csv) {

		if (!csv.exists()) {
			System.err.println("CSV " + csv + " does not exist, not sorting..");
			return;
		}
		List<Map<String, String>> data = getAllRecords(csv);
		List<String> sorted = new ArrayList<String>();
		if (data.size() > 0) {
			List<String> columns = new ArrayList<String>(data.get(0).keySet());
			columns.remove("");
			Collections.sort(columns);
			sorted.add(listToCSV(columns));
			for (Map<String, String> rec : data) {
				StringBuilder sb = new StringBuilder();
				for (String column : columns) {
					if(rec.containsKey(column)) {
						sb.append(rec.get(column));
					}
					sb.append(",");					
				}
				sorted.add(sb.toString().substring(0,sb.toString().length()-1));
			}
		}
		
		try {
			FileUtils.writeLines(new File(csv.getParentFile(),"sorted_"+csv.getName()), sorted);
			System.out.println(csv + " sorted.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	public static List<String> getColumns(File csv) {
		List<String> allRecords = new ArrayList<String>();
		try {
			UltimateCSVReader csvreader = new UltimateCSVReader(csv);
			allRecords.addAll(csvreader.getColumns());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allRecords;
	}

	public static List<Map<String, String>> getRecords(File csv) {
		List<Map<String, String>> allRecords = new ArrayList<Map<String, String>>();
		try {
			UltimateCSVReader csvreader = new UltimateCSVReader(csv);
			allRecords.addAll(csvreader.getRecords());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allRecords;
	}

}
