package com.avnet.emasia.webquote.web.datatable.export;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.UISelectMany;
import javax.faces.component.ValueHolder;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.jboss.logmanager.Level;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.datatable.DataTable;

import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.utilites.web.util.ExcelUtil;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;

public class ExtExcelExporter extends XSSFExcelExporter {
	static final Logger LOGGER=Logger.getLogger(ExtExcelExporter.class.getSimpleName());

//	@Override
	protected String exportValueB(FacesContext context, UIComponent component) {
		if (component instanceof UIPanel) {
			String headerValue = null;
			String header = "";
			for (UIComponent child : component.getChildren()) {
				headerValue = ExcelUtil.exportValue(context, child);
				header = header + headerValue;
			}
			return headerValue;
		} else if (component instanceof HtmlCommandLink) {
			HtmlCommandLink htmlCommandLink = (HtmlCommandLink) component;
			Object value = htmlCommandLink.getValue();

			if (value != null) {
				return String.valueOf(value);
			} else {
				// export first value holder
				for (UIComponent child : htmlCommandLink.getChildren()) {
					if (child instanceof ValueHolder) {
						return ExcelUtil.exportValue(context, child);
					}
				}

				return "";
			}
		} else if (component instanceof ValueHolder) {
			if (component instanceof EditableValueHolder) {
				Object submittedValue = ((EditableValueHolder) component).getSubmittedValue();
				if (submittedValue != null) {
					return submittedValue.toString();
				}
			}

			ValueHolder valueHolder = (ValueHolder) component;
			Object value = valueHolder.getValue();
			if (value == null) {
				return "";
			}

			Converter converter = valueHolder.getConverter();
			if (converter == null) {
				Class valueType = value.getClass();
				converter = context.getApplication().createConverter(valueType);
			}

			if (converter != null) {
				if (component instanceof UISelectMany) {
					StringBuilder builder = new StringBuilder();
					List collection = null;

					if (value instanceof List) {
						collection = (List) value;
					} else if (value.getClass().isArray()) {
						collection = Arrays.asList(value);
					} else {
						throw new FacesException(MessageFormatorUtil.getParameterizedString(CommonConstants.WQ_EJB_MASTER_DATA_VALUE_OF_MUST_BE_A_LIST_OR_AN_ARRAY, component.getClientId(context)));
					}

					int collectionSize = collection.size();
					for (int i = 0; i < collectionSize; i++) {
						Object object = collection.get(i);
						builder.append(converter.getAsString(context, component, object));

						if (i < (collectionSize - 1)) {
							builder.append(",");
						}
					}

					String valuesAsString = builder.toString();
					builder.setLength(0);

					return valuesAsString;
				} else {
					try {
						return converter.getAsString(context, component, value);
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, "Exception message : "+e.getMessage(), e);
						return value.toString();
					}
				}
			} else {
				return value.toString();
			}
		} else if (component instanceof CellEditor) {
			return ExcelUtil.exportValue(context, ((CellEditor) component).getFacet("output"));
		} else if (component instanceof HtmlGraphicImage) {
			return (String) component.getAttributes().get("alt");
		} else {
			// This would get the plain texts on UIInstructions when using
			// Facelets
			String value = component.toString();

			if (value != null) {
				return value.trim();
			} else {
				return "";
			}
		}
	}

	public String getSheetName(FacesContext context, DataTable table) {
//		UIComponent uic = table.findComponent("exportSheetName");
//		if (uic != null) {
//			return ComponentUtils.getValueToRender(context, table.findComponent("exportSheetName"));
//		}
		return "sheet 0";
	}
}
