package com.avnet.emasia.webquote.web.datatable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.FilterEvent;

import com.avnet.emasia.webquote.web.quote.CommonBean;

/**
 * 
 * @author 041863
 */
@SuppressWarnings("unchecked")
public abstract class BaseLazyDataMB<T> extends CommonBean{

	protected static final Logger LOGGER = Logger.getLogger(BaseLazyDataMB.class.getName());
	protected List<T> selectionDatas;

	public void onToggleSelect(ToggleSelectEvent event) {
		if (event.isSelected()) {
			this.getCacheSelectionDatas().addAll(selectionDatas);
		} else {
			this.getCacheSelectionDatas().removeAll(getCurrentPageDatas());
		}
		LOGGER.log(Level.SEVERE, "call onToggleSelect " + selectionDatas.size() + " cache " + this.getCacheSelectionDatas().size());
	}

	public void onRowSelectCheckbox(SelectEvent event) {
		T t = (T) event.getObject();
		this.getCacheSelectionDatas().add(t);
//		selectionDatas.add(t);
		LOGGER.log(Level.SEVERE, "call onRowSelectCheckbox " + selectionDatas.size() + " cache " + this.getCacheSelectionDatas().size());
	}

	public void onRowUnselectCheckbox(UnselectEvent event) {
		T t = (T) event.getObject();
		this.getCacheSelectionDatas().remove(t);
//		selectionDatas.remove(t);
		LOGGER.log(Level.SEVERE, "call onRowUnselectCheckbox " + selectionDatas.size() + " cache " + this.getCacheSelectionDatas().size());
	}

	public void onRowSelect(SelectEvent event) {
		// T t = (T) event.getObject();
		this.getCacheSelectionDatas().removeAll(getCurrentPageDatas());
		this.getCacheSelectionDatas().addAll(selectionDatas);
		LOGGER.log(Level.SEVERE, "call onRowSelect " + selectionDatas.size() + " cache " + this.getCacheSelectionDatas().size());
	}

	public void onRowUnselect(UnselectEvent event) {
		T t = (T) event.getObject();
		this.getCacheSelectionDatas().remove(t);
		LOGGER.log(Level.SEVERE, "call onRowUnselect " + selectionDatas.size() + " cache " + this.getCacheSelectionDatas().size());
	}

	public List<T> getSelectionDatas() {
		selectionDatas = new ArrayList<>(getCacheSelectionDatas());
		return selectionDatas;
	}

	public void setSelectionDatas(List<T> selectionDatas) {
		this.selectionDatas = selectionDatas;
	}

	public void clearCacheSelectionDatas() {
		this.getCacheSelectionDatas().clear();
		this.selectionDatas.clear();
	}

	/**
	 * do cache row data when cell changed
	 * 
	 * @param id
	 *            row key
	 */
	abstract public void cellChangeListener(String id);

	public Set<T> getCacheSelectionDatas() {
		return new HashSet<>();
	}

	protected List<T> getCurrentPageDatas() {
		return new ArrayList<>();
	}

	abstract public void onFilter(FilterEvent event);

	protected abstract LazyEntityDataModel<T> getLazyData();
	
}
