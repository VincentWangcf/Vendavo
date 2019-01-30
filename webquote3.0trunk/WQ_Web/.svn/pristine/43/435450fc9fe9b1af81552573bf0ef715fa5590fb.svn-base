package com.avnet.emasia.webquote.web.user;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.user.ejb.GroupSB;
import com.avnet.emasia.webquote.user.ejb.ProductGroupSB;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-1-24
 */

@FacesConverter("webquote.user.ProductGroup")
public class ProductGroupConverter implements Converter {
	
	private ProductGroupSB productGroupSB;
	
	List<ProductGroup> productGroups;
	
	private void initialize(){
		
		try {
			Context context = new InitialContext();
			
			productGroupSB = (ProductGroupSB) context.lookup("java:app/WQ_EJB_UserAndAuthor/ProductGroupSB!com.avnet.emasia.webquote.user.ejb.ProductGroupSB");
			
			productGroups = productGroupSB.findAll();
			
		} catch (NamingException ne) {
			throw new WebQuoteRuntimeException(ne.getMessage(), new RuntimeException(ne));
		}
		
	}

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		if(productGroupSB == null){
			initialize();
		}
		
		for(ProductGroup productGroup : productGroups){
			if(productGroup.getId() == Long.parseLong(arg2)){
				return productGroup;
			}
		}
		
		return null;
		
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		
		return arg2.toString();
	}

}
