package com.avnet.emasia.webquote.commodity.vo;

import java.util.List;
import java.util.logging.Logger;

import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

import com.avnet.emasia.webquote.entity.ProgramPricer;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-02-04
 */

public class ProgramMaterialModel extends ListDataModel<ProgramPricer> implements SelectableDataModel<ProgramPricer> , java.io.Serializable{  

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger("ProgramMaterialModel");
	public ProgramMaterialModel() {
    }

    public ProgramMaterialModel(List<ProgramPricer> data) {
        super(data);
    }
    
    @Override
    public ProgramPricer getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data
        List<ProgramPricer> programMaterials = (List<ProgramPricer>) getWrappedData();
        
        for(ProgramPricer pm : programMaterials) {
            if(String.valueOf(pm.getId()).equals(rowKey))
                return pm;
        }
        
        return null;
    }

    @Override
    public Object getRowKey(ProgramPricer pm) {
        return pm.getId();
    }
}
                    