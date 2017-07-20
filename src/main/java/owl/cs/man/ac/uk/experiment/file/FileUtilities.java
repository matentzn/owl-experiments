package owl.cs.man.ac.uk.experiment.file;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

public class FileUtilities {

	public static File createDir(File parent, String name) {
		File experiment = new File(parent, name);
		boolean dirFlag = false;

		try {
			dirFlag = experiment.mkdir();
		} catch (SecurityException Se) {
			System.out.println("Error while creating directory in Java:" + Se);
		}

		if (dirFlag)
			System.out.println(name + " directory created successfully");
		else
			System.out
					.println(name + " directory was not created successfully");

		return experiment;
	}

	public static boolean deleteDirContents(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirContents(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}


	public static void writeStringToFile(File file, String content) throws IOException {
		writeStringToFile(file, content, false);
	}
	
	public static void writeStringToFile(File file, String content, boolean append) throws IOException {
		FileUtils.writeStringToFile(file, content,StandardCharsets.UTF_8, append);
	}

	public static File addExtension(File file, String extension) {
		if (!file.getName().endsWith("." + extension)) {
			return new File(file.getParentFile(), file.getName() + "."
					+ extension);
		} else {
			return file;
		}
	}

	public static String getExtension(File file) {
		String extension = file.getName().substring(
				file.getName().lastIndexOf("."));
		return extension;
	}

	public static List<String> readFileLineByLineIntoList(File file) {
		List<String> lines = new ArrayList<String>();
		try {
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				lines.add(strLine);
			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return lines;
	}

	public static List<String> readFileLineByLineIntoList(InputStream is) {
		List<String> lines = new ArrayList<String>();
		try {
			DataInputStream in = new DataInputStream(is);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				lines.add(strLine);
			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return lines;
	}

	public static void openExternally(File file) throws IOException {
		Desktop dt = Desktop.getDesktop();
		dt.open(file);
	}

	public static List<String> readFileLineByLineIntoList(File file, int i) {
		List<String> lines = new ArrayList<String>();
		int counter = 0;
		try {
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				if (counter >= i) {
					break;
				}
				lines.add(strLine);
				counter++;
			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return lines;
	}

	public static String createUniqueFilenameFromString(String url,
			int offsetFromEnd) {
		String uniqueid = UUID.randomUUID().toString();
		String filename = url.substring(url.lastIndexOf("/"));
		filename = filename.replaceAll("[^a-zA-Z0-9\\._]", "");

		if (filename.length() > offsetFromEnd) {
			filename = filename.substring(filename.length()
					- (offsetFromEnd - 1), filename.length());
		}

		return uniqueid + "_" + filename;
	}

	public static String getFirstLine(File file) {
		Scanner scan;
		try {
			scan = new Scanner(file);
			while (scan.hasNextLine()) {
				String nl = scan.nextLine(); 
				scan.close();
				return nl;
			}
		} catch (FileNotFoundException e) {
			System.err.println("File " + file + " does not exist.");
		}
		return null;
	}

	public static void appendStringToLineInFileAndWrite(File file,
			String columnames, int linenumber) throws IOException {
		List<String> oldfile = FileUtils.readLines(file,StandardCharsets.UTF_8);
		String firstline = oldfile.get(linenumber);
		firstline += columnames;
		oldfile.set(linenumber, firstline);
		FileUtils.writeLines(file, oldfile);
	}

	public static void zip(List<File> allfiles, File zipFile) {
		
		try {
			// create byte buffer
			byte[] buffer = new byte[1024];

			FileOutputStream fos = new FileOutputStream(zipFile);

			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File f : allfiles) {
				FileInputStream fis = new FileInputStream(f);
				zos.putNextEntry(new ZipEntry(f.getName()));

				int length;

				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}

				zos.closeEntry();

				// close the InputStream
				fis.close();
			}

			// close the ZipOutputStream
			zos.close();
			fos.close();

		} catch (IOException ioe) {
			System.out.println("Error creating zip file" + ioe);
		}

	}
	
	public static void unzip(File zipFilePath, File destDir) throws IOException {
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDir.getAbsolutePath() + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
	
	/**
     * @param zipFile the file to unzip
	 * @param tmpDir 
     * @return the directory of the file that has been unzipped
     */
    public static File unzipOwlFile(File zipFile, String filename, File tmpDir) {
    	System.out.println("PROCESSING: "+filename);
        String out = tmpDir.getAbsolutePath() + "/"+filename+"_unzipped/";
        File outfile = zipFile;
        final int BUFFER = 2048;
        try {
            BufferedOutputStream dest = null;
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            boolean created = false;
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
            	if(!created) {
            		 new File(out).mkdir();
            		 created = true;
            		 outfile = new File(out);
            	}
                int count;
                byte data[] = new byte[BUFFER];
                // write the files to the disk
                String entryName = entry.getName();
                /*if (entryName.equals(zipFile.getName())) {
                    System.out.println("same name");
                    entryName = entryName + ".unzipped";
                }*/
                
                FileOutputStream fos = new FileOutputStream(out + entryName);
                dest = new BufferedOutputStream(fos, BUFFER);
                while ((count = zis.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
            }
            zis.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } catch (Error e) {
            System.err.println(e.getMessage());
        }        
        return outfile;
    }
	
	private static void extractFile(ZipInputStream zipIn, String filePath)
			throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}
	
	private static final int BUFFER_SIZE = 4096;

	/*
	 * unzip method from
	 * http://www.codejava.net/java-se/file-io/programmatically
	 * -extract-a-zip-file-using-java
	 */

	/**
	 * Extracts a zip entry (file entry)
	 * 
	 * @param zipIn
	 * @param filePath
	 * @throws IOException
	 */
	

	/*
	 * isZipFile method from http://www.java2s.com/Code/Java/File-Input-Output/
	 * DeterminewhetherafileisaZIPFile.htm
	 */

	public static boolean isZipFile(File file) {
		if (file.isDirectory()) {
			return false;
		}
		if (!file.canRead()) {
			return false;
		}
		if (file.length() < 4) {
			return false;
		}
		DataInputStream in;
		try {
			in = new DataInputStream(new BufferedInputStream(
					new FileInputStream(file)));
			int test = in.readInt();
			in.close();
			return test == 0x504b0304;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
