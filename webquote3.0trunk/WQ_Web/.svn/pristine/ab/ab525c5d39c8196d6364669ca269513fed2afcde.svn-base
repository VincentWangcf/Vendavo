package com.avnet.emasia.webquote.web.quote;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.logging.Logger;

import org.primefaces.model.SortOrder;

import com.avnet.emasia.webquote.entity.QuoteItem;

/**
 * @author 914384
 * 
 */
public class LazySorter implements Comparator<QuoteItem>
{
	private static final Logger LOG = Logger.getLogger(LazySorter.class.getName());
	private String methodName;
	private SortOrder sortOrder;

	public LazySorter(String sortField, SortOrder sortOrder)
	{
		//this.sortField = sortField;
		methodName = "" + sortField.charAt(0);
		methodName = methodName.toUpperCase();
		methodName += sortField.substring(1);
		methodName = "get" + methodName;
		this.sortOrder = sortOrder;
		LOG.info("### Sort field = " + sortField + ", Sort Order = " + sortOrder);
	}

	public int compare(QuoteItem quoteItem1, QuoteItem quoteItem2)
	{
		try
		{
			//Object value1 = QuoteItem.class.getField(this.sortField).get(quoteItem1);
			//Object value2 = QuoteItem.class.getField(this.sortField).get(quoteItem2);
			Object value1 = QuoteItem.class.getMethod(methodName).invoke(quoteItem1);
			Object value2 = QuoteItem.class.getMethod(methodName).invoke(quoteItem2);
			int value = ((Comparable)value1).compareTo(value2);

			return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
		}
		catch (Exception e)
		{
			throw new RuntimeException();
		}
	}
}

