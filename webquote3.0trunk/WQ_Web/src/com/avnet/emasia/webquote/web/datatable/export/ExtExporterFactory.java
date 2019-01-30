package com.avnet.emasia.webquote.web.datatable.export;

import javax.faces.FacesException;

import org.primefaces.component.export.ExporterType;

public class ExtExporterFactory {

	public static ExtExporter getExporterForType(String type) {
		ExtExporter exporter = null;

		try {
			ExporterType exporterType = ExporterType.valueOf(type.toUpperCase());

			switch (exporterType) {
			case XLS:
				exporter = new ExtExcelExporter();
				break;
			default:
				exporter = new ExtExcelExporter();
				break;
			}
		} catch (IllegalArgumentException e) {
			throw new FacesException(e);
		}

		return exporter;
	}
}
