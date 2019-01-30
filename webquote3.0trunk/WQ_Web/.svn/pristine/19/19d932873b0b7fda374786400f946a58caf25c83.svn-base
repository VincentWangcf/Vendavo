package com.avnet.emasia.webquote.web.quote.job;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jboss.logmanager.Level;

import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.exception.WebQuoteException;

public class FileUtil {
	static final Logger LOG = Logger.getLogger(FileUtil.class.getSimpleName());

	public long getFileSize(String fileName) {
		File file = new File(fileName);

		if (!file.exists() || !file.isFile()) {
			return -1;
		}

		return file.length();

	}

	public void doZipFile(String zipFileName, List zippedFileList) throws IOException {

		byte[] buffer = new byte[1024];
		// Create input and output streams
		ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(zipFileName));

		for (Iterator it = zippedFileList.iterator(); it.hasNext();) {
			String zippedFileName = (String) it.next();
			File file = new File(zippedFileName);
			FileInputStream in = new FileInputStream(file);
			outStream.putNextEntry(new ZipEntry(file.getName()));
			int bytesRead;

			// Each chunk of data read from the input stream
			// is written to the output stream
			while ((bytesRead = in.read(buffer)) > 0) {
				outStream.write(buffer, 0, bytesRead);
			}

			// Close zip entry and file streams
			outStream.closeEntry();
			in.close();
		}
		outStream.close();
	}

	public void doZipFile(String zipFileName, String zippedFileName) throws IOException {

		byte[] buffer = new byte[1024];
		// Create input and output streams
		ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(zipFileName));

		File file = new File(zippedFileName);
		FileInputStream in = new FileInputStream(file);
		outStream.putNextEntry(new ZipEntry(file.getName()));
		int bytesRead;

		// Each chunk of data read from the input stream
		// is written to the output stream
		while ((bytesRead = in.read(buffer)) > 0) {
			outStream.write(buffer, 0, bytesRead);
		}

		// Close zip entry and file streams
		outStream.closeEntry();
		in.close();

		outStream.close();
	}

	public void deleteFilesInDir(String directory) {
		File dir = new File(directory);
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
	}

	public void writeToDisc(HSSFWorkbook workbook, String fileName) {
		File file = new File(fileName);
		try {
			FileOutputStream stream = new FileOutputStream(file);
			workbook.write(stream);
		} catch (FileNotFoundException e) {
			LOG.log(Level.SEVERE, "Exception in writting file to disc, file : "+fileName+" , Exception message : "+e.getMessage(), e);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Exception in writting file to disc, file : "+fileName+" , Exception message : "+e.getMessage(), e);
		}
	}

	public static byte[] file2Byte(String filePath) throws WebQuoteException {
		File file = new File(filePath);
		InputStream fis = null;
		try {

			if (file.exists()) {
				fis = new FileInputStream(file);
				return IOUtils.toByteArray(fis);
			}
		} catch (IOException e) {
			new WebQuoteException(e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					LOG.log(Level.SEVERE, "Exception in closing stream for file path: "+filePath+", exception message : "+e.getMessage(), e);
				}
			}
		}
		return new byte[] {};
	}

	public static void byte2File(byte[] buf, String filePath, String fileName) throws IOException  {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		File dir = new File(filePath);
		if (!dir.exists() && dir.isDirectory()) {
			dir.mkdirs();
		}
		file = new File(filePath + File.separator + fileName);
		fos = new FileOutputStream(file);
		bos = new BufferedOutputStream(fos);
		bos.write(buf);
		if (bos != null) {
			bos.close();
		}
		if (fos != null) {
			fos.close();
		}

	}

}
