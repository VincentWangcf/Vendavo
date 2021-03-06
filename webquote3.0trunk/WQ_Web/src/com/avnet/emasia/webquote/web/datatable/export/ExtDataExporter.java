package com.avnet.emasia.webquote.web.datatable.export;

import java.io.IOException;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.primefaces.component.datatable.DataTable;

import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;

public class ExtDataExporter implements ActionListener, StateHolder {

	private ValueExpression target;
	
	private ValueExpression type;
	
	private ValueExpression fileName;
	
	private ValueExpression encoding;
	
	private ValueExpression pageOnly;
    
	private ValueExpression selectionOnly;
	
	private MethodExpression exportOrder;
	
	private MethodExpression preProcessor;
	
	private MethodExpression postProcessor;
	
	public ExtDataExporter() {}

	public ExtDataExporter(MethodExpression exportOrder, ValueExpression target, ValueExpression type, ValueExpression fileName, ValueExpression pageOnly, ValueExpression selectionOnly, ValueExpression encoding, MethodExpression preProcessor, MethodExpression postProcessor) {
		this.target = target;
		this.type = type;
		this.fileName = fileName;
		this.pageOnly = pageOnly;
		this.selectionOnly = selectionOnly;
		this.preProcessor = preProcessor;
		this.postProcessor = postProcessor;
		this.encoding = encoding;
		this.exportOrder = exportOrder;
	}

	public void processAction(ActionEvent event){

		FacesContext context = FacesContext.getCurrentInstance();
		ELContext elContext = context.getELContext();
		
		String tableId = (String) target.getValue(elContext);
		String exportAs = (String) type.getValue(elContext);
		String outputFileName = (String) fileName.getValue(elContext);
	
		String encodingType = "UTF-8";
		if(encoding != null) {
			encodingType = (String) encoding.getValue(elContext);
		}

		boolean isPageOnly = false;
		if(pageOnly != null) {
			isPageOnly = pageOnly.isLiteralText() ? Boolean.valueOf(pageOnly.getValue(context.getELContext()).toString()) : (Boolean) pageOnly.getValue(context.getELContext());
		}
		
        boolean isSelectionOnly = false;
		if(selectionOnly != null) {
			isSelectionOnly = selectionOnly.isLiteralText() ? Boolean.valueOf(selectionOnly.getValue(context.getELContext()).toString()) : (Boolean) selectionOnly.getValue(context.getELContext());
		}
		
		try {
			ExtExporter exporter = ExtExporterFactory.getExporterForType(exportAs);
            
			UIComponent component = event.getComponent().findComponent(tableId);
			if(component == null) {
				throw new FacesException(MessageFormatorUtil.getParameterizedString(ResourceMB.getText(CommonConstants.WQ_EJB_MASTER_DATA_CANNOT_FIND_COMPONENT_IN_VIEW), tableId));
            }
            
			if(!(component instanceof DataTable)) {
				throw new FacesException(ResourceMB.getParameterizedString("wq.message.unsupportedException",component.getClass().getName())+".");
            }
            
			DataTable table = (DataTable) component;
			exporter.export(context, table, outputFileName, isPageOnly, isSelectionOnly, encodingType,exportOrder, preProcessor, postProcessor);
			
			context.responseComplete();
		} 
        catch (IOException e) {
			throw new FacesException(e);
		}
	}

	private int[] resolveExcludedColumnIndexes(Object columnsToExclude) {
        if(columnsToExclude == null || columnsToExclude.equals("")) {
            return null;
        }
        else {
            String[] columnIndexesAsString = ((String) columnsToExclude).split(",");
            int[] indexes = new int[columnIndexesAsString.length];

            for(int i=0; i < indexes.length; i++) {
                indexes[i] = Integer.parseInt(columnIndexesAsString[i].trim());
            }

            return indexes;
        }
	}

	public boolean isTransient() {
		return false;
	}

	public void setTransient(boolean value) {
		//NoOp
	}
	
	 public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;

		target = (ValueExpression) values[0];
		type = (ValueExpression) values[1];
		fileName = (ValueExpression) values[2];
		pageOnly = (ValueExpression) values[3];
		selectionOnly = (ValueExpression) values[4];
		preProcessor = (MethodExpression) values[5];
		postProcessor = (MethodExpression) values[6];
		encoding = (ValueExpression) values[7];
	}

	public Object saveState(FacesContext context) {
		Object values[] = new Object[8];

		values[0] = target;
		values[1] = type;
		values[2] = fileName;
		values[3] = pageOnly;
		values[4] = selectionOnly;
		values[5] = preProcessor;
		values[6] = postProcessor;
		values[7] = encoding;
		
		return ((Object[]) values);
	}
}
