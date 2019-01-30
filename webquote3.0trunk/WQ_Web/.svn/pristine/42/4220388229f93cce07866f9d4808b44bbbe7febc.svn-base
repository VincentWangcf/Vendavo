package com.avnet.emasia.webquote.web.datatable.export;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.ValueHolder;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.primefaces.component.datatable.DataTable;

public abstract class ExtExporter {

	protected enum ColumnType {
		HEADER("header"), FOOTER("footer");

		private final String facet;

		ColumnType(String facet) {
			this.facet = facet;
		}

		public String facet() {
			return facet;
		}

		@Override
		public String toString() {
			return facet;
		}
	};

	public abstract void export(FacesContext facesContext, DataTable table,
			String outputFileName, boolean pageOnly, boolean selectionOnly,
			String encodingType, MethodExpression exportOrder,
			MethodExpression preProcessor, MethodExpression postProcessor)
			throws IOException;

	protected List<UIColumn> getColumnsToExport(UIData table) {
		List<UIColumn> columns = new ArrayList<UIColumn>();
		int columnIndex = -1;

		for (UIComponent child : table.getChildren()) {
			if (child instanceof UIColumn) {
				UIColumn column = (UIColumn) child;
				columnIndex++;

				columns.add(column);
			}
		}

		return columns;
	}

	protected boolean hasColumnFooter(List<UIColumn> columns) {
		for (UIColumn column : columns) {
			if (column.getFooter() != null)
				return true;
		}

		return false;
	}


	
	protected String exportHeaderValue(UIComponent component) {
		String value = null;
		if (component instanceof HtmlOutputText){
			value = ((HtmlOutputText) component).getValue().toString();
		}else{
			value = component.toString();
		}
		if (value != null){
			value = value.trim().replaceAll("- ", "").replace(" -", "").replaceAll("<br/>-", "").replaceAll("<br>", " ").replaceAll("-<br */>", "").replaceAll("<br */>", " ");
		}else{
			value =  "";
		}
		return value;
		}
}
