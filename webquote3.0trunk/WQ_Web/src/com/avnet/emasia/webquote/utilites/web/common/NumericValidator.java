package com.avnet.emasia.webquote.utilites.web.common;

import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.avnet.emasia.webquote.utilites.resources.ResourceMB;

/**
 * @author 914384
 * 
 * JSF Validator to validate if the input value is a number (including decimal)
 *
 */
public class NumericValidator implements Validator
{
	private static final Logger LOGGER = Logger.getLogger(NumericValidator.class.getName());

	public NumericValidator()
	{
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see javax.faces.validator.Validator#validate(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
	 */
	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object value)
			throws ValidatorException
	{
		// TODO Auto-generated method stub
		LOGGER.info("### Validating number - " + value);
		try
		{
			BigDecimal bd = new BigDecimal(value.toString());
		}
		catch (NumberFormatException nfe)
		{
			FacesMessage msg = 
					new FacesMessage(ResourceMB.getText("wq.message.numValidFailed"+"."), 
							nfe.toString());
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
	}

}
