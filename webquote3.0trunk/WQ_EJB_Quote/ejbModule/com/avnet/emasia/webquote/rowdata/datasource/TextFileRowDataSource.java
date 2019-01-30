package com.avnet.emasia.webquote.rowdata.datasource;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.cxf.common.util.StringUtils;

import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.rowdata.RowDataSource;


/**
 * @author 046755
 *
 */
public class TextFileRowDataSource implements RowDataSource {
	private static Logger logger = Logger.getLogger(TextFileRowDataSource.class.getName());
	private Map<String, Integer> map = new LinkedHashMap<>();
	// private File file;
	private List<String[]> holdingList = new ArrayList<String[]>();
	private Iterator<String[]> iterator = null;
	private String[] data = null;
	private int seq = 0;
	/**
	 * @param is
	 * @param info
	 */
	public TextFileRowDataSource(InputStream is, ResolveInfo info) {
		this.seq = info.getDataContentStartRowIndex();
		try {
			
			this.convertTxt2Java(is,info);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		this.iterator=holdingList.iterator();
	}

	
	
		private static String[] splitForSpecial(String source, String splitor) {
				 return  splitForSpecial(source, 0, splitor);
			} 
		
		/**
		 * when startIndex=0 and splitor="|"
		 * "||" -> {,,};
		 * "||sd" -> {,,"sd"};
		 * "qew||sd" -> {"qew",,"sd"};
		 * 
		 * */
		private static String[] splitForSpecial(String source, int startIndex, String splitor) {
			if (StringUtils.isEmpty(source)||StringUtils.isEmpty(splitor)) {
				return new String[]{source};
			}
			ArrayList<String> list =new ArrayList<String>();
			int cIndex = startIndex;
			int last = startIndex;
			if(source.indexOf(splitor, startIndex)<0) {
				return new String[]{source};
			}
			while((cIndex=source.indexOf(splitor, cIndex))>-1) {
				list.add(source.substring(last, cIndex));
				//skip the splitor;
				cIndex  = cIndex + splitor.length();
				last = cIndex;
			}
			list.add(source.substring(last));
			return list.toArray(new String[]{});
		} 

	public void convertTxt2Java(InputStream is, ResolveInfo info) throws Exception, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

		// nputStreamReader isr = FilesUtils.readFile(fullPath,
		// "UTF-8");
		// BufferedReader br = new BufferedReader(reader);
		String dataLine = null;
		int i = 0;
		// List<String> holdingList = new ArrayList<String>();
		try {
			while ((dataLine = br.readLine()) != null) {

				if (i == info.getHeadRowIndex()) {
					String[] split = splitForSpecial(dataLine,"|");
//					String[] split = dataLine.split("\\|");

					for (int j = 0; j < split.length; j++) {

						map.put(split[j], j);
					}

				}
				else if(i>=info.getDataContentStartRowIndex()){
//					
					holdingList.add(splitForSpecial(dataLine,"|"));
//					String[] split = dataLine.split("\\|");
//					if(dataLine.endsWith("|")){
//						dataLine=dataLine+" ";
//					}
//					holdingList.add(dataLine.split("\\|"));
				}
				i++;
			}
		} catch (IOException e) {
			throw new Exception(CommonConstants.WQ_EJB_MASTER_DATA_TXT_FILE_READ_ERROR + " on file path: ", e);
		} finally {
			try {
				if (null != br)
					br.close();
				if (null != is)
					is.close();
			} catch (IOException e) {
				/* logger.log(Level.SEVERE, e.getMessage(), e); */
				throw e;
			}
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.avnet.emasia.webquote.columndata.ColumnDataSource#getSeq()
	 */
	@Override
	public int getSeq() {
		// TODO Auto-generated method stub
		return seq;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.avnet.emasia.webquote.columndata.ColumnDataSource#hasNext()
	 */
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return iterator.hasNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.avnet.emasia.webquote.columndata.ColumnDataSource#next()
	 */
	@Override
	public void next() {
		// TODO Auto-generated method stub
		this.data=iterator.next();
		seq++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.avnet.emasia.webquote.columndata.ColumnDataSource#getColumnValue(java
	 * .lang.String)
	 */
	@Override
	public String getColumnValue(String columnName) {
		if(map.get(columnName)==null){
			logger.info("curreydata columnName"+columnName+",columnName. ");
		}
		logger.info("this.data " +Arrays.asList(this.data));
		// TODO Auto-generated method stub
		return this.data[map.get(columnName)];
	}
	
	@Override
	public List<String> getHeadNames() {
		return map.keySet().stream().collect(Collectors.toList());
	}
}