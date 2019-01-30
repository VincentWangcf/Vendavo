package com.avnet.emasia.webquote.reports.offlinereport;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;

import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.quote.vo.QuoteItemVo;
import com.avnet.emasia.webquote.reports.offlinereport.vo.MyQuoteResultBean;
import com.avnet.emasia.webquote.reports.utility.RPTEJBReportsUtility;




/**
 * @author 906893
 * @createdOn 20130424
 */
public class MyQuoteExcelFileGenerator  {

	private static final Logger LOGGER = Logger.getLogger(MyQuoteExcelFileGenerator.class
			.getName());
    /* (non-Javadoc)
     * @see com.avnetasia.webquote.domain.logic.ExcelFileGenerateFacade#generateFileForSale()
     */
    public HSSFWorkbook generateFileForSale(String bizUnitName, List resultList)throws WebQuoteException{
    	HSSFWorkbook workbook = null;
        List newList = ReportConvertor.convert2ResultBean(resultList);
        newList = ReportConvertor.noFinishQuoteHandling(newList);
        
        workbook = RPTEJBReportsUtility.generateExcelFile(bizUnitName,newList,OfflineReportConstants.SALE_DOWNLOAD_TEMPLATE_HEADER_TITLE,OfflineReportConstants.SALE_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS,
                    "example","Sales Report","sales","myQuoteExcelFileGenerator");
        return workbook ;
    }

    public HSSFWorkbook generateFileForInsideSale(String bizUnitName, List resultList) throws WebQuoteException{
    	HSSFWorkbook workbook = null;
        List newList = ReportConvertor.convert2ResultBean(resultList);
        newList = ReportConvertor.noFinishQuoteHandling(newList);
        workbook = RPTEJBReportsUtility.generateExcelFile(bizUnitName,newList,OfflineReportConstants.SALE_DOWNLOAD_TEMPLATE_HEADER_TITLE,OfflineReportConstants.SALE_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS,
                    "example","Sales Report","sales","myQuoteExcelFileGenerator");
        return workbook ;
    }
    /* (non-Javadoc)
     * @see com.avnetasia.webquote.domain.logic.ExcelFileGenerateFacade#generateFileForCS()
     */
    public HSSFWorkbook generateFileForCS(String bizUnitName, List resultList) throws WebQuoteException {
        HSSFWorkbook workbook = null;
        List newList = ReportConvertor.convert2ResultBean(resultList);
        newList = ReportConvertor.noFinishQuoteHandling(newList);
        workbook = RPTEJBReportsUtility.generateExcelFile(bizUnitName,newList,OfflineReportConstants.CS_DOWNLOAD_TEMPLATE_HEADER_TITLE,OfflineReportConstants.CS_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS,
                    "example","Customer Service Report","cs","myQuoteExcelFileGenerator");
        return workbook ;
    }

    /* (non-Javadoc)
     * @see com.avnetasia.webquote.domain.logic.ExcelFileGenerateFacade#generateFileForQC()
     */
    public HSSFWorkbook generateFileForQC(String bizUnitName, List resultList) throws WebQuoteException{

        HSSFWorkbook workbook = null;
        List newList = ReportConvertor.convert2ResultBean(resultList);
        workbook = RPTEJBReportsUtility.generateExcelFile(bizUnitName,newList,OfflineReportConstants.QC_DOWNLOAD_TEMPLATE_HEADER_TITLE,OfflineReportConstants.QC_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS,
                    "example","Quote Centre Report","qc","myQuoteExcelFileGenerator");

        return workbook ;
    }

    /* (non-Javadoc)
     * @see com.avnetasia.webquote.domain.logic.ExcelFileGenerateFacade#generateFileForMM()
     */
    public HSSFWorkbook generateFileForMM(String bizUnitName, List resultList) throws WebQuoteException {
        HSSFWorkbook workbook = null;
        List newList = ReportConvertor.convert2ResultBean(resultList);
        workbook = RPTEJBReportsUtility.generateExcelFile(bizUnitName,newList,OfflineReportConstants.MM_DOWNLOAD_TEMPLATE_HEADER_TITLE,OfflineReportConstants.MM_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS,
                    "example","Material Management Report","mm","myQuoteExcelFileGenerator");

        return workbook ;
    }

    /* (non-Javadoc)
     * @see com.avnetasia.webquote.domain.logic.ExcelFileGenerateFacade#generateFileForPM()
     */
    public HSSFWorkbook generateFileForPM(String bizUnitNam, List resultList) throws WebQuoteException {
        HSSFWorkbook workbook = null;
        List newList = ReportConvertor.convert2ResultBean(resultList);
        workbook = RPTEJBReportsUtility.generateExcelFile(bizUnitNam,newList,OfflineReportConstants.PM_DOWNLOAD_TEMPLATE_HEADER_TITLE,OfflineReportConstants.PM_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS,
                    "example","Production Management Report","pm","myQuoteExcelFileGenerator");
        return workbook ;
    }
    public HSSFWorkbook generateFileForBMT(String bizUnit,
			List<QuoteItemVo> resultList) throws WebQuoteException {
        HSSFWorkbook workbook = null;
        List newList = ReportConvertor.convert2ResultBean(resultList);
        workbook = RPTEJBReportsUtility.generateExcelFile(bizUnit,newList,OfflineReportConstants.BMT_DOWNLOAD_TEMPLATE_HEADER_TITLE,OfflineReportConstants.BMT_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS,
                    "example","Production Management Report","bmt","myQuoteExcelFileGenerator");
        return workbook ;
    }
}
