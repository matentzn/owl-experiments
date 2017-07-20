package owl.cs.man.ac.uk.experiment.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzipper {
	/*
	 * Adapted from:
	 * http://www.mkyong.com/java/how-to-decompress-files-from-a-zip-file/
	 */

	/**
	 * Unzip it
	 * 
	 * @param zipFile
	 *            input zip file
	 * @param output
	 *            zip file output folder
	 * @throws IOException
	 */

	public static boolean isZippedFile(File file) throws IOException {
		boolean zipped = false;
		ZipInputStream zis = null;
		try {

			zis = new ZipInputStream(new FileInputStream(file));

			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {
				zipped = true;
				break;
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {

			if (zis != null) {
				zis.closeEntry();
				zis.close();
			}
		}
		return zipped;

	}

	public static File unGzip(File zippedFile, File target) throws IOException {
		
		int sChunk = 8192;
		FileInputStream in = null;
		GZIPInputStream zipin = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(zippedFile);
			zipin = new GZIPInputStream(in);
			byte[] buffer = new byte[sChunk];
			out = new FileOutputStream(target);
			int length;
			while ((length = zipin.read(buffer, 0, sChunk)) != -1)
				out.write(buffer, 0, length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			if (zipin != null) {
				zipin.close();
			}
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}

		}
		return target;
	}

	public static File unZip(File zipFile, File outputFolder)
			throws IOException {

		byte[] buffer = new byte[2048];
		ZipInputStream zis = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		try {
			zis = new ZipInputStream(new FileInputStream(zipFile));

			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null) {
				System.out.println("Unzipping: " + entry.getName());

				int size;

				File destination = new File(outputFolder,entry.getName());
				
				if(!destination.getName().startsWith(".")) {
					fos = new FileOutputStream(destination);
					bos = new BufferedOutputStream(fos,
						buffer.length);

					while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
						bos.write(buffer, 0, size);
					}	
				}
			}

			System.out.println("Done");

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if(bos != null) {
				bos.flush();
				bos.close();
			}
			if (zis != null) {
				zis.closeEntry();
				zis.close();
			}
			if (fos != null) {
				fos.close();
			}
			
		}
		return outputFolder;
	}
}