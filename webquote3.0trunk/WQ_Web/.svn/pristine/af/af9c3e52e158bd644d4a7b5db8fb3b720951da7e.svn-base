package com.avnet.emasia.webquote.web.quote.vo;

import java.util.List;
import java.util.logging.Logger;

import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

import com.avnet.emasia.webquote.entity.RestrictedCustomerMapping;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-02-04
 */

public class RestrictedCustomerPopupModel extends ListDataModel<RestrictedCustomerMapping> implements SelectableDataModel<RestrictedCustomerMapping> , java.io.Serializable{  

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger("RestrictedCustomerPopupModel");
	public RestrictedCustomerPopupModel() {
    }

    public RestrictedCustomerPopupModel(List<RestrictedCustomerMapping> data) {
        super(data);
    }
    
    @Override
    public RestrictedCustomerMapping getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data
        List<RestrictedCustomerMapping> restrictedCustomers = (List<RestrictedCustomerMapping>) getWrappedData();
        
        for(RestrictedCustomerMapping pm : restrictedCustomers) {
            if(String.valueOf(pm.getId()).equals(rowKey))
                return pm;
        }
        
        return null;
    }

    @Override
    public Object getRowKey(RestrictedCustomerMapping pm) {
        return pm.getId();
    }

}
                    