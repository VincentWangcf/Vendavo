package com.avnet.emasia.webquote.web.quote.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

@FacesValidator("datatableIntegerValidator")
public class DataTableIntegerValidator implements javax.faces.validator.Validator {


	public DataTableIntegerValidator() {
	}

	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		if(value != null){
			if(!String.valueOf(value).matches("[0-9]{1,}")){
	            FacesMessage msg = new FacesMessage("The input must be integer format.");
	            throw new ValidatorException(msg);

			}
		}
		
	}

}
