/**
 * 
 */
package com.avnet.emasia.webquote.web.quote.strategy;

import java.util.Arrays;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;

/**
 * @author 042659
 *
 */
public class FilterExcelColumnsWithDelStrategy implements FilterExcelColumnsStrategy {

	Logger LOGGER= Logger.getLogger(FilterExcelColumnsWithDelStrategy.class.getName());
	private static final long serialVersionUID = 2171620685904694691L;

	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.web.quote.strategy.FilterExcelColumnsStrategy#deleteColumn(org.apache.poi.hssf.usermodel.HSSFSheet, int[])
	 */
	@Override
	public void deleteColumns(HSSFSheet sheet, int[] columnNums) {

		LOGGER.info("Call deleteColumn method begin,the columns number is:[" + columnNums.toString() + "]");
		if (columnNums.length == 0) {
			return;
		}

		Arrays.sort(columnNums);


		for (int i = 0; i < columnNums.length; i++) {
			LOGGER.info("Delete columns:[" + columnNums[i] + "] begin");
			deleteColumn(sheet, columnNums[i] - i);
			LOGGER.info("Delete columns:[" + columnNums[i] + "] end");
		}

		LOGGER.info("Call deleteColumn method end");

	}

	/**   
	* @Description: delete columns 
	* @author 042659
	* @param sheet
	* @param i       
	* @return void    
	* @throws  
	*/  
	private void deleteColumn(HSSFSheet sheet, int delColumnNum) {
		
		int maxColumn = 0;
		for (int r = 0; r < sheet.getLastRowNum() + 1; r++) {
			HSSFRow row = sheet.getRow(r);

			// if no row exists here; then nothing to do; next!
			if (row == null)
				continue;

			// if the row doesn't have this many columns then we are good; next!
			int lastColumn = row.getLastCellNum();
			if (lastColumn > maxColumn)
				maxColumn = lastColumn;

			if (lastColumn < delColumnNum)
				continue;

			for (int x = delColumnNum + 1; x < lastColumn + 1; x++) {
				Cell oldCell = row.getCell(x - 1);
				if (oldCell != null)
					row.removeCell(oldCell);

				Cell nextCell = row.getCell(x);
				if (nextCell != null) {
					Cell newCell = row.createCell(x - 1, nextCell.getCellType());
					cloneCell(newCell, nextCell);
				}
			}
		}
		
		
	}

	/**   
	* @Description: copy next to next -1 
	* @author 042659
	* @param newCell
	* @param nextCell       
	* @return void    
	* @throws  
	*/  
	private void cloneCell(Cell newCell, Cell oldCell) {

		// Use old cell style
		newCell.setCellStyle(oldCell.getCellStyle());

		// If there is a cell comment, copy
		if (newCell.getCellComment() != null) {
			newCell.setCellComment(oldCell.getCellComment());
		}

		// If there is a cell hyperlink, copy
		if (oldCell.getHyperlink() != null) {
			newCell.setHyperlink(oldCell.getHyperlink());
		}

		// Set the cell data type
		newCell.setCellType(oldCell.getCellType());

		// Set the cell data value
		switch (oldCell.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			newCell.setCellValue(oldCell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_ERROR:
			newCell.setCellErrorValue(oldCell.getErrorCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA:
			newCell.setCellFormula(oldCell.getCellFormula());
			break;
		case Cell.CELL_TYPE_NUMERIC:
			newCell.setCellValue(oldCell.getNumericCellValue());
			break;
		case Cell.CELL_TYPE_STRING:
			newCell.setCellValue(oldCell.getRichStringCellValue());
			break;
		}
	
	}

}
