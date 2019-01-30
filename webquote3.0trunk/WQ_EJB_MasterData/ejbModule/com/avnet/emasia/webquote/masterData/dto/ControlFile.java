package com.avnet.emasia.webquote.masterData.dto;


import java.io.*;


import com.avnet.emasia.webquote.masterData.util.FileUtils;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-25
 * 
 */
public class ControlFile {
	
	private File file ;

	public ControlFile(File file){
		this.file = file;
	}
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	public boolean isFile(){
		return file.isFile();
	}

	public int readRecordCount() throws IOException {
		String path = file.getPath();
		String dir = path.substring(0,path.lastIndexOf(File.separator)+1);
		String name = path.substring(path.lastIndexOf(File.separator)+1); 
		BufferedReader reader = FileUtils.readFile(dir,name, "utf-8");
		String line;
		line = reader.readLine();
		reader.close();
		String count = "0";
		if (line != null)
		{
			count = line.substring(line.indexOf(":") + 1);
		}
		
		return Integer.parseInt(count);
	}
}
