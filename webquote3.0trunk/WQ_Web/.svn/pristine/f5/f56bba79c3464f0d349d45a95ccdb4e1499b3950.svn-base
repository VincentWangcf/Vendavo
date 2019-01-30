package com.avnet.emasia.webquote.commodity.helper;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuotationTemplateLoader {
	private static final Logger LOGGER = Logger.getLogger(QuotationTemplateLoader.class.getName());
	private static ResourceLoaderHelper loader = ResourceLoaderHelper.getInstance();
    private static String quotationTemplateFileName ="QuotationTemplate.xls";
	public static HSSFWorkbook getQuotationTemplate() {
		HSSFWorkbook xlsFile = null;
		try {
			xlsFile = loader.getHSSFWorkbook(quotationTemplateFileName);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Exception for file"+quotationTemplateFileName+" , Exception Message"+e.getMessage(), e);
             //throw new ConfigLoadingException(e);
		}
		return xlsFile;
	}
}