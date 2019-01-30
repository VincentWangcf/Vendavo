package com.avnet.emasia.webquote.utilites.web.common;

import java.io.Serializable;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;
import com.avnet.emasia.webquote.entity.ReportRecipient;

/**
 * 
 * @author 914975
 * 
 */
public class RecipientMaintenanceModel extends ListDataModel<ReportRecipient> implements
		SelectableDataModel<ReportRecipient>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8666138194071150683L;

	public RecipientMaintenanceModel(List<ReportRecipient> pryLst) {
		super(pryLst);
	}

	@Override
	public ReportRecipient getRowData(String pry) {
		// TODO Auto-generated method stub
		List<ReportRecipient> rpLst = (List<ReportRecipient>) getWrappedData();
		for (ReportRecipient rp : rpLst) {
			String str = String.valueOf(rp.getId());
			if (str.equals(pry)) {
				return rp;
			}
		}
		return null;
	}

	@Override
	public Object getRowKey(ReportRecipient rp) {
		// TODO Auto-generated method stub
		return rp.getId();
	}

}
