package com.avnet.emasia.webquote.reports.vo;

import java.io.Serializable;

public class ColumnModelVo implements Serializable{
	 /**
	 * 
	 */
	private static final long serialVersionUID = -8780211974140538927L;
	/**
	 * 
	 */

	private String header;
    private String property;
    
    private String xlsSpecSignal;
    private int xlsSignalPos; //0 is left, 1 is right

    public ColumnModelVo(String header, String property) {
        this.header = header;
        this.property = property;
    }
    
    public ColumnModelVo(String header, String property,String signal,int pos) {
        this.header = header;
        this.property = property;
        this.xlsSignalPos = pos;
        this.xlsSpecSignal = signal;
    }
    
    
    
   
    public String getHeader() {
        return header;
    }

    public String getProperty() {
        return property;
    }


	public String getXlsSpecSignal() {
		return xlsSpecSignal;
	}


	public void setXlsSpecSignal(String xlsSpecSignal) {
		this.xlsSpecSignal = xlsSpecSignal;
	}

	public int getXlsSignalPos() {
		return xlsSignalPos;
	}

	public void setXlsSignalPos(int xlsSignalPos) {
		this.xlsSignalPos = xlsSignalPos;
	}


	
    
    

}
