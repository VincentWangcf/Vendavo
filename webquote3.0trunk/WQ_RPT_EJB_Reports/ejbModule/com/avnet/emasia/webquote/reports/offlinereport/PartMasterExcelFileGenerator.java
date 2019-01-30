package com.avnet.emasia.webquote.reports.offlinereport;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;

import com.avnet.emasia.webquote.reports.offlinereport.vo.PartMasterResultBean;
import com.avnet.emasia.webquote.reports.utility.RPTEJBReportsUtility;


/**
 * @author 906893
 * @createdOn 20130424
 */
public class PartMasterExcelFileGenerator  {

	private static final Logger LOGGER = Logger.getLogger(PartMasterExcelFileGenerator.class
			.getName());
    /* (non-Javadoc)
     * @see com.avnetasia.webquote.domain.logic.ExcelFileGenerateFacade#generateFileForQC()
     */
    public HSSFWorkbook generateFileForQC(String bizUnitName, List resultList){

        HSSFWorkbook workbook = null;
        List newList = ReportConvertor.convertPartMasterForQC(resultList);
        workbook = RPTEJBReportsUtility.generateExcelFile(bizUnitName,newList,OfflineReportConstants.QC_PART_MASTER_DOWNLOAD_TEMPLATE_HEADER_TITLE,OfflineReportConstants.QC_PART_MASTER_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS,
                    "example","Part Master Report","qc","partMasterExcelFileGenerator");

        return workbook ;
    }
}
