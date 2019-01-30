package com.avnet.emasia.webquote.commodity.util;

import java.util.Comparator;

import com.avnet.emasia.webquote.commodity.bean.QedCheckBean;

public class QedCheckComparator implements Comparator<QedCheckBean> {

	public int compare(QedCheckBean f1, QedCheckBean f2) {

		if (f1.getQuotationEffectiveDate().getTime() > f2.getQuotationEffectiveDate().getTime()) {
			return 1;
		} else if (f1.getQuotationEffectiveDate() == f2.getQuotationEffectiveDate()) {
			return 0;
		} else {
			return -1;
		}
	}
}	