/*package com.avnet.emasia.webquote.web.quote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.Pricer;
import com.avnet.emasia.webquote.quote.ejb.MaterialSB;
import com.avnet.emasia.webquote.quote.ejb.SapMaterialSB;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.vo.PricerInfo;

@ManagedBean
@SessionScoped
public class PartSearchLogicMB implements Serializable{

	private static final long serialVersionUID = -8715036043331749776L;

	@EJB
	SysConfigSB sysConfigSB;
	
	@EJB
	MaterialSB materialSB;
	
	@EJB
	SapMaterialSB sapMaterialSB;
	
	private static final String SEPERATOR = "|";




	
	public List<PricerInfo> newSearchParts(String mfr, String fullPartNumber, String bizUnitName, boolean excatQuery, List<Customer> allowCustomers,
			int itemNumber, int first, int limit) {
		List<Material> materials = null;
		List<PricerInfo> alternateParts = null;
        materials =materialSB.getMaterialSortList(mfr, fullPartNumber, bizUnitName, excatQuery, allowCustomers,first,limit);
		if (materials != null) {
			alternateParts = new ArrayList<PricerInfo>();
			int index = 0;
			for (Material material : materials) {
				  PricerInfo alternatePart = new PricerInfo();
						alternatePart.setFullMfrPartNumber(material
								.getFullMfrPartNumber());
						alternatePart.setMfr(material.getManufacturer()
								.getName());
						alternatePart.setSapPartNumber(material
								.getSapPartNumber());
						if (material != null) {
							List<Pricer> pricers = material.getPricers();
									
							if (pricers != null) {
								for (Pricer pricer : pricers) {
									pricer.fillupPricerInfo(alternatePart);
								}
							}
						}

						alternatePart.setItemNumber(itemNumber);
						alternatePart.setPricerId(++index);
						alternateParts.add(alternatePart);
					}

		}
		return alternateParts;
	}
}

*/