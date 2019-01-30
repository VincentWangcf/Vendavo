package com.avnet.emasia.webquote.web.quote.vo;

import java.util.List;
import java.util.logging.Logger;

import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

import com.avnet.emasia.webquote.vo.PricerInfo;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-02-04
 */

public class PartModel extends ListDataModel<PricerInfo> implements SelectableDataModel<PricerInfo> , java.io.Serializable{  

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger("PartModel");
	public PartModel() {
    }

    public PartModel(List<PricerInfo> data) {
        super(data);
    }
    
    @Override
    public PricerInfo getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data
        List<PricerInfo> PricerInfos = (List<PricerInfo>) getWrappedData();
        
        for(PricerInfo pm : PricerInfos) {
            if(String.valueOf(pm.getItemNumber()).equals(rowKey))
                return pm;
        }
        
        return null;
    }

    @Override
    public Object getRowKey(PricerInfo pm) {
        return pm.getItemNumber();
    }
}
                    