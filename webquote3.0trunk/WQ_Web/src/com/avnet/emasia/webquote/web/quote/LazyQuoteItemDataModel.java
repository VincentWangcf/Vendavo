package com.avnet.emasia.webquote.web.quote;

import java.lang.reflect.InvocationTargetException;
/**
 * 
 */
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.avnet.emasia.webquote.entity.QuoteItem;

/**
 * @author 914384
 * 
 */
public class LazyQuoteItemDataModel extends LazyDataModel<QuoteItem>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger
			.getLogger(LazyQuoteItemDataModel.class.getName());
	private List<QuoteItem> dataSource;

	/**
	 * 
	 */
	public LazyQuoteItemDataModel(List<QuoteItem> quoteItems)
	{
		dataSource = quoteItems;
	}

	public void setDataSource(List<QuoteItem> quoteItems)
	{
		dataSource = quoteItems;
	}

	@Override
	public QuoteItem getRowData(String rowKey)
	{
		for (QuoteItem quoteItem : dataSource)
		{
			if (String.valueOf(quoteItem.getId()).equals(rowKey))
			{
				return quoteItem;
			}
		}
		return null;
	}

	@Override
	public Object getRowKey(QuoteItem quoteItem)
	{
		return quoteItem.getId();
	}

	//Returns the method in the class given the field name
	private Method getMethodFrmFieldName(Class clazz, String fieldName)
	{
		String methodName = "" + fieldName.charAt(0);
		methodName = methodName.toUpperCase();
		methodName += fieldName.substring(1);
		methodName = "get" + methodName;
		try
		{
			return clazz.getMethod(methodName);
		}
		catch (NoSuchMethodException|SecurityException e)
		{
			LOG.log(Level.SEVERE, "Exception in getting from method from field name : "+fieldName+" , Exception message : "+e.getMessage(), e);
		}
		return null;
	}
	
	@Override
	public List<QuoteItem> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String,Object> filters)
	{
		LOG.info("$$$ load() called");
		List<QuoteItem> data = new ArrayList<QuoteItem>();

		LOG.info("$$$ Filter size=" + filters.size());
		Object[] filterKeys = filters.keySet().toArray();
		//LOG.info("$$$ Filter keys=" + keys.toString());

		//Handle the filters specified
		if (filterKeys != null && filterKeys.length > 0)
		{
			//LOG.info("$$$ Filter key=" + keys[0].toString() + ", value=" + filters.get(keys[0]));
			for (QuoteItem quoteItem : dataSource)
			{
				boolean match = true;

				for (int i=0; i<filterKeys.length; i++)
				{
					try
					{
						String filterProperty = filterKeys[i].toString();
						String filterValue = (String)filters.get(filterKeys[i]);
						
						Method method = getMethodFrmFieldName(QuoteItem.class, filterProperty);
						if (method != null)
						{
							String fieldValue = method.invoke(quoteItem).toString();
							//LOG.info("$$$ Default value for method, " + methodName + ", is " + fieldValue);
							//if (filterValue == null	|| fieldValue.startsWith(filterValue))
							if (filterValue == null	|| (fieldValue != null && fieldValue.indexOf(filterValue) > -1))
							{
								match = true;
							}
							else
							{
								match = false;
								break;
							}
						}
					}
					catch (Exception e)
					{
						LOG.log(Level.WARNING, "Exception occurred for page size: "+pageSize+"EXception message: "+e.getMessage());
						match = false;
						break;
					}
				}

				if (match)
				{
					data.add(quoteItem);
				}
			}
		}
		else
		{
			data = dataSource;
		}

		// sort
		if (sortField != null)
		{
			Collections.sort(data, new LazySorter(sortField, sortOrder));
		}

		// rowCount
		int dataSize = data.size();
		this.setRowCount(dataSize);

		// paginate
		if (dataSize > pageSize)
		{
			try
			{
				return data.subList(first, first + pageSize);
			}
			catch (IndexOutOfBoundsException e)
			{
				LOG.log(Level.WARNING, "Exception occurred for page size: "+pageSize+"EXception in getting sub list"+e.getMessage());
				return data.subList(first, first + (dataSize % pageSize));
			}
		}
		else
		{
			return data;
		}
	}
}
