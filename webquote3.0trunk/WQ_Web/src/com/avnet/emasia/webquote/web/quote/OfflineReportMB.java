package com.avnet.emasia.webquote.web.quote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import com.avnet.emasia.webquote.entity.OfflineReport;


import com.avnet.emasia.webquote.reports.ejb.OfflineReportSB;


@ManagedBean
@RequestScoped
public class OfflineReportMB implements Serializable {
	

	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger("OfflineReportMB");
	
	@EJB
	transient OfflineReportSB offlineRptSB;
	
	List<OfflineReport> reportBeanList = new ArrayList<OfflineReport>();
	

    public void submitOfflineRequest()
    {
//		int totalRecords = Integer.parseInt(num);


//		reportBean.setServiceBeanMethod(this.serviceBeanMethodQC);
//		reportBean.setSearchBeanClass(searchBean.getClass().getName());
//		
//		reportBean.setCreateDateTime(DateUtils.getDefaultDateTimeStr("yyyy/MM/dd HH:mm:ss"));
//		reportBean.setLastModifiedTime(DateUtils.getDefaultDateTimeStr("yyyy/MM/dd HH:mm:ss"));
//		
//		List<OfflineReport> reportBeanList = new ArrayList<OfflineReport>();
//		
//		OfflineReport  offRpt = new OfflineReport();
//		
//	    int count = totalRecords / pageSize;
//	    for( int i=0;i<=count;i++ )
//	    {
//			
//			searchBean.setStart(String.valueOf(i*pageSize));
//			String beanValue = JSONUtil.getJsonString4JavaPOJO(searchBean);  
//			reportBean.setSearchBeanValue(beanValue);
//			reportBeanList.add(reportBean);
			
//	    }
//	    offlineRptSB.createOfflineReportRequest(reportBeanList);
    }
	
     
}
                    
