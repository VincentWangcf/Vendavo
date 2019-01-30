package com.avnet.emasia.webquote.commodity.vo;

import java.util.List;
import java.util.logging.Logger;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import com.avnet.emasia.webquote.entity.QtyBreakPricer;


public class QtyBreakPricerModel extends ListDataModel<QtyBreakPricer> implements SelectableDataModel<QtyBreakPricer> , java.io.Serializable{  

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger("SpecialPricerModel");
	public QtyBreakPricerModel() {
    }

    public QtyBreakPricerModel(List<QtyBreakPricer> data) {
        super(data);
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public QtyBreakPricer getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data
        List<QtyBreakPricer> pricers = (List<QtyBreakPricer>) getWrappedData();
        
        for(QtyBreakPricer p : pricers) {
            if(String.valueOf(p.getId()).equals(rowKey))
                return p;
        }
        
        return null;
    }

    @Override
    public Object getRowKey(QtyBreakPricer qbpricer) {
        return qbpricer.getId();
    }
}
                    