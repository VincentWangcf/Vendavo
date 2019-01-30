package com.avnet.emasia.webquote.utilites.web.common;

import java.io.Serializable;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;
import com.avnet.emasia.webquote.entity.AppProperty;

/**
 * add edit capability of datatable of property.
 * 
 * @author 914975
 * 
 */
public class PropertyDataModel extends ListDataModel<AppProperty> implements
		SelectableDataModel<AppProperty>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5568812865286760200L;

	public PropertyDataModel(List<AppProperty> pryLst) {
		super(pryLst);
	}

	@Override
	public AppProperty getRowData(String pry) {
		// TODO Auto-generated method stub
		List<AppProperty> pryLst = (List<AppProperty>) getWrappedData();
		for (AppProperty prsy : pryLst) {
			if (prsy.getPropKey().equals(pry)) {
				return prsy;
			}
		}
		return null;
	}

	@Override
	public Object getRowKey(AppProperty pry) {
		// TODO Auto-generated method stub
		return pry.getPropKey();
	}

}
