package com.avnet.emasia.webquote.reports.ejb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.avnet.emasia.webquote.entity.ExcelReport;

@Stateless
@LocalBean
public class ExcelReportSB {
	
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	private static Map<String,ExcelReport> reportTempalte = new HashMap<String,ExcelReport>();
	
	private ExcelReport queryExcelReport(String reportName) {
		ExcelReport excelReport = null ;
		TypedQuery<ExcelReport> query = em.createQuery("select e from ExcelReport e where e.reportName = :reportName", ExcelReport.class);
		query.setParameter("reportName", reportName);
		List<ExcelReport> excelReports = query.getResultList();
		if(excelReports != null && excelReports.size() > 0) {
			excelReport = excelReports.get(0);
		}
		return excelReport;
	}
	
	public ExcelReport getExcelReportByReportName(String reportName) {
		ExcelReport excelReport = reportTempalte.get(reportName);
		if(excelReport == null) {
			excelReport = queryExcelReport(reportName);
			reportTempalte.put(reportName, excelReport);
		}
		return excelReport;
	}
	
	
}
