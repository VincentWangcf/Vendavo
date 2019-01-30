package com.avnet.emasia.webquote.web.datatable;

import java.util.HashMap;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;

/**
 * Extending PF data table to allow for binding of the filter map.
 */
@FacesComponent("com.avnet.emasia.FilterEnhancedDataTable")
public class FilterEnhancedDataTable extends DataTable {

   /**
    * Locates the filterMap attribute on the datatable.
    * <p>
    * @return the ValueExpression provided in the filterMap attribute if
    *         specified or null if not
    */
   protected ValueExpression getFilterFacetValueExpression() {
      ValueExpression ve = getValueExpression("filterMap");
      return ve;
   }

   /*
    * (non-Javadoc)
    * @see org.primefaces.component.datatable.DataTable#getFilters()
    */
   @Override
   public java.util.Map<String,Object> getFilters() {
      ValueExpression ve = getFilterFacetValueExpression();
      if (ve == null)
         return super.getFilters();

      Map<String, Object> map = (Map<String, Object>) ve.getValue(FacesContext.getCurrentInstance().getELContext());
      if (map == null)
         return new HashMap<String, Object>();
      else
         return map;
   };

   /*
    * (non-Javadoc)
    * @see
    * org.primefaces.component.datatable.DataTable#setFilters(java.util.Map)
    */
   @Override
   public void setFilters(java.util.Map<String, Object> filters) {
      ValueExpression ve = getFilterFacetValueExpression();
      if (ve == null) {
         super.setFilters(filters);
         return;
      }

      ve.setValue(FacesContext.getCurrentInstance().getELContext(), filters);
   };
}