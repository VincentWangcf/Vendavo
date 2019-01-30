package com.avnet.emasia.webquote.web.pricerupload;

import java.io.Serializable;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;
import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;

public class PricerUploadDataModel extends
		ListDataModel<PricerUploadTemplateBean> implements
		SelectableDataModel<PricerUploadTemplateBean>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3738354823552388309L;

	public PricerUploadDataModel() {

	}

	public PricerUploadDataModel(List<PricerUploadTemplateBean> pricrLst) {
		super(pricrLst);
	}

	@Override
	public PricerUploadTemplateBean getRowData(String rowKey) {
		// TODO Auto-generated method stub
		List<PricerUploadTemplateBean> pLst = (List<PricerUploadTemplateBean>) getWrappedData();

		for (PricerUploadTemplateBean pricr : pLst) {
			if (pricr.getFullMfrPart().equals(rowKey)) {
				return pricr;
			}
		}
		return null;
	}

	@Override
	public Object getRowKey(PricerUploadTemplateBean pItem) {
		// TODO Auto-generated method stub
		return pItem.getFullMfrPart();
	}
}