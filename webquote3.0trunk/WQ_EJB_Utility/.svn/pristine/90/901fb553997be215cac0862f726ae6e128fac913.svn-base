package com.avnet.emasia.webquote.utilities.util;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.DateConverter;

import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;



public class BeanUtilsExtends extends BeanUtils {
	static final Logger LOG= Logger.getLogger(BeanUtilsExtends.class.getSimpleName());
	private static final BigDecimal BIGDECIMAL_ZERO = new BigDecimal("0");
	static {
		BigDecimalConverter bd = new BigDecimalConverter(BIGDECIMAL_ZERO);
		Converter dc = new DateConverter(null);
		ConvertUtils.register(dc, java.util.Date.class);
//		ConvertUtils.register(new DateConvert(), java.sql.Date.class);
		ConvertUtils.register(bd, BigDecimal.class);
	}

	public static void copyProperties(Object dest, Object orig) {
		try {
			BeanUtils.copyProperties(dest, orig);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			LOG.log(Level.SEVERE, "Error occured while copying properties from origin "+orig.toString()+" to destination "+dest.toString()+", Reason to failed: "+MessageFormatorUtil.getParameterizedStringFromException(ex),ex);
		} 
	}
}
