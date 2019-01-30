package com.avnet.emasia.webquote.commodity.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.avnet.emasia.webquote.reports.vo.ReportGroupVo;
import com.avnet.emasia.webquote.web.quote.QuoteHitRateReportMB;


@FacesConverter("webquote.commodity.PickListConverter")
public class PickListConverter implements Converter {

        private static Map<Integer,ReportGroupVo> groupByDB;
        
        static {
                groupByDB = new HashMap<Integer,ReportGroupVo>();
                List<ReportGroupVo> grpLst = QuoteHitRateReportMB.getGroupsObj();
                int i=0;
                for(ReportGroupVo vo : grpLst) {
                	i++;
                	groupByDB.put(i,vo);
                }
                
               /* groupByDB.put(1,new QHRReportGroupVo(QuoteHitRateReportMB.HEADER_QUOTE_PERIOD, 1, "qi.LAST_QC_UPDATED_ON"));
                groupByDB.put(2,new QHRReportGroupVo(QHRReportGroupVo.HEADER_SALES_MGR, 2, "sales_mgr.name"));
                groupByDB.put(3,new QHRReportGroupVo(QHRReportGroupVo.header, 3, "sales_team.name"));
                groupByDB.put(4,new QHRReportGroupVo("Sales", 4, "sales.name"));
                groupByDB.put(5,new QHRReportGroupVo("Customer", 5, "cs.CUSTOMER_NUMBER"));
                groupByDB.put(6,new QHRReportGroupVo("MFR", 6, "mfr.NAME"));  
                groupByDB.put(7,new QHRReportGroupVo("Product Group 1", 7, "pg1.name"));  
                groupByDB.put(8,new QHRReportGroupVo("Product Group 2", 8, "pg2.name"));  
                groupByDB.put(9,new QHRReportGroupVo("Product Group 3", 9, "qi.product_group3"));  
                groupByDB.put(10,new QHRReportGroupVo("Product Group 4", 10, "qi.product_group4"));  
                groupByDB.put(11,new QHRReportGroupVo("Quote Handler", 11, "qi.LAST_UPDATED_QC"));  
                groupByDB.put(12,new QHRReportGroupVo("BU (Region)", 12, "q.BIZ_UNIT"));  
                groupByDB.put(13,new QHRReportGroupVo("PN ", 13, "qi.QUOTED_PART_NUMBER"));  */
        }
        
        public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
                if(submittedValue.equals(""))
                        return null;
                else {
                        return groupByDB.get(Integer.valueOf(submittedValue));
                }
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
                if(value == null)
                        return null;
                else
                        return String.valueOf(((ReportGroupVo) value).getId());
        }

}
