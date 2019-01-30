package com.avnet.emasia.webquote.commodity.converter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.eclipse.jetty.util.log.Log;
import com.avnet.emasia.webquote.entity.QuantityBreakPrice;
import com.avnet.emasia.webquote.helper.TransferHelper;

/**
 * @author Tonmy Li Created on 2013-4-10
 */

@FacesConverter("webquote.commodity.Oqmsp")
public class OqmspConverter implements Converter {

	private static Logger logger = Logger.getLogger(OqmspConverter.class.getName());
	private final String BLANK = "";

	private PartternEnum parttern;

	public OqmspConverter() {
	}

	public OqmspConverter(PartternEnum parttern) {
		// Log.info(parttern.toString());
		this.parttern = parttern;
	}

	public enum PartternEnum {
		CVT_BREAKINT, // order qty rows in single colum
		CVT_NORMSELL, // normal sell rows in single colum
		CVT_SALESCOST, // Sales cost rows in single colum
		CVT_SUGGESTSALE // Suggests Sale rows in single colum
	}

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

		return null;

	}

	// &lt;br /&gt;
	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		//Log.info(this.hashCode() + parttern.toString());
		List<QuantityBreakPrice> qList = (List<QuantityBreakPrice>) arg2;
		if (qList == null || qList.isEmpty()) {
			return BLANK;
		}
		if (parttern == null) {
			Log.info("parttern should not be blank");
			return BLANK;
		}
		switch (parttern) {
		case CVT_BREAKINT:
			return TransferHelper.convertQrderQty(qList);
		case CVT_NORMSELL:
			return TransferHelper.convertNormalSellPrice(qList);
		case CVT_SALESCOST:
			return TransferHelper.convertSalesCost(qList);
		case CVT_SUGGESTSALE:	
			return TransferHelper.convertSuggestedResale(qList);
		default :
			return "";

		}
	}

	

}
