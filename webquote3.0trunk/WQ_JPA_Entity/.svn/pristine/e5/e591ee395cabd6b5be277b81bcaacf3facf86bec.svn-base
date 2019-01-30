package com.avnet.emasia.webquote.entity.util;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.avnet.emasia.webquote.entity.SalesOrder;

public class SalesOrderListener {

	/**
	 * create or replace TRIGGER "WEBQUOTE"."UPDATE_SALES_ORDER_PRICE" 
		BEFORE INSERT OR UPDATE ON SALES_ORDER 
		FOR EACH ROW 
		BEGIN
		    :new.SALES_ORDER_RESALE := null;
		    IF :new.PRICE_UNIT is not null AND :new.PRICE_UNIT > 0 AND :new.USD_PRICE is not null THEN 
		      :new.SALES_ORDER_RESALE := :new.USD_PRICE/:new.PRICE_UNIT;
		    END IF;
		END;
	 * @param order
	 */
	@PrePersist
	@PreUpdate
	public void prePersist(SalesOrder order) {
		if (order.getPriceUnit() != null && order.getPriceUnit() > 0 && order.getUsdPrice() != null) {
			order.setSalesOrderResale(order.getUsdPrice() / order.getPriceUnit());
		} else {
			order.setSalesOrderResale(null);
		}
	}
}
