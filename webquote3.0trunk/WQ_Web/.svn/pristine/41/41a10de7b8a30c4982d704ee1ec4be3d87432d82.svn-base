package com.avnet.emasia.webquote.commodity.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.eclipse.jetty.util.log.Log;

import com.avnet.emasia.webquote.commodity.helper.ProgRfqSubmitHelper;
import com.avnet.emasia.webquote.entity.ProgramPricer;
import com.avnet.emasia.webquote.vo.Oqmsp;

/**
 * @author Tonmy Li Created on 2013-4-10
 */

@ManagedBean
@Stateless
public class OqmspConverterManager {
	private static final Map<String, Converter> coverterMap = new HashMap<String, Converter>();
	static {
		OqmspConverter.PartternEnum[] arrays = OqmspConverter.PartternEnum.values();
		for (OqmspConverter.PartternEnum pttern : arrays) {
			coverterMap.put(pttern.toString(), new OqmspConverter(pttern));
		}
	}

	public Converter getCoverter(String parttern) throws Exception {
		Converter coverter = coverterMap.get(parttern);
		if(coverter==null) {
			throw new Exception("Incorrect parameter for getCoverter. Must in OqmspConverter.PartternEnum.");
		}
		return coverter;
	}
}