package com.avnet.emasia.webquote.quote.ejb;

import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.NormalPricer;
import com.avnet.emasia.webquote.entity.RestrictedCustomerMapping;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.CatalogSearchItemBean;
import com.avnet.emasia.webquote.quote.vo.CatalogSearchResult;
import com.avnet.emasia.webquote.quote.vo.RestrictedCustomerMappingParameter;
import com.avnet.emasia.webquote.quote.vo.RestrictedCustomerMappingSearchCriteria;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilities.util.MaterialHelper;
import com.avnet.emasia.webquote.vo.PricerInfo;


/**
 * Session Bean implementation class QuoteSB
 */
@Stateless
@LocalBean
public class CatalogSearchSB {
	
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	@EJB
	UserSB userSB;
	@EJB
	RestrictedCustomerMappingSB restrictedCustomerSB;
	
	@EJB
	PricerTypeMappingSB pricerTypeMappingSB;
	
	@EJB
	MaterialSB materialSB;
	
	@EJB
	QuoteSB quoteSB;
	
	@EJB
	private CustomerSB customerSB;
	
	private static final Logger LOG =Logger.getLogger(CatalogSearchSB.class.getName());
    
	public CatalogSearchSB(){
		
	}
	

	

public List<PricerInfo> checkPrice(List<CatalogSearchItemBean> items, 
		User user, String selectedBizUnit, int maxResults ,String rfqCurr){
		
		//List<CatalogSearchResult> results = new ArrayList<CatalogSearchResult>();
		user = em.find(User.class, user.getId());
		List<PricerInfo> pricerInfos = new ArrayList<>();

		for (CatalogSearchItemBean item : items) {

			if(isEmpty(item.getFullMfrPart())){
				continue;
			}
			// 
			List<Material> materials = materialSB.materialFindForCataLogSearch(item.getMfr().toUpperCase().trim(), 
					item.getFullMfrPart().toUpperCase().trim(), selectedBizUnit, maxResults);
			//material.
			for (Material material : materials) {
				//TODO
				PricerInfo pricerInfo = new PricerInfo();
				pricerInfo.setRfqCurr(rfqCurr);
				//material.fillDefaultCostIndicatorInPricerInfo(pricerInfo);
				pricerInfo.setBizUnit(selectedBizUnit);
				pricerInfo.setQuotedMfr(material.getManufacturer());
				pricerInfo.setQuotedMaterial(material);
				//pricerInfo.setBizUnit(selectedBizUnit);
				pricerInfo.setRequestedQty(item.getRequestQty());
				pricerInfo.setMfr(material.getManufacturer().getName());
				pricerInfo.setFullMfrPartNumber(material.getFullMfrPartNumber());
				pricerInfo.setSoldToCustomer(customerSB.findByPK(item.getSoldToCode()));
				if(pricerInfo.getSoldToCustomer() != null) {
					pricerInfo.setSoldToCustomerNumber(item.getSoldToCode());
					pricerInfo.setSoldToCustomerName(pricerInfo.getSoldToCustomer().getName());
				}
				pricerInfo.setEndCustomer(customerSB.findByPK(item.getEndCustomerCode()));
				if(pricerInfo.getEndCustomer() != null) {
					pricerInfo.setEndToCustomerNumber(item.getEndCustomerCode());
					pricerInfo.setEndToCustomerName(pricerInfo.getEndCustomer().getName());
				}
				pricerInfo.setAllowedCustomers(user.getCustomers());
				pricerInfo.setExRateDate(new Date());
				//material.applyDefaultCostIndicatorLogic(pricerInfo, true);
				//use sb
				quoteSB.applyDefaultCostIndicatorLogic(pricerInfo, true);
				pricerInfo.setBizUnit(selectedBizUnit);
				if (pricerInfo.getCostIndicator() != null) {
					pricerInfos.add(pricerInfo);
				}
			}
			
		}
	  
		
		int i = 1;
		for(PricerInfo result : pricerInfos){
			result.setSeq(i++);
		}
		
		return pricerInfos;
	}
	
	
	private boolean isEmpty(String source){
		if(source == null){
			return true;
		}
		if(source.equals("")){
			return true;
		}
		return false;
	}
	
	private Date convertPriceValidityToQuoteExpiryDate(String priceValidity){
		if(priceValidity == null){
			return null;
		}
		
		Date validity = null;

		if (priceValidity != null) {
			if (priceValidity.matches("^(0|[1-9][0-9]*)$")) {
				int shift = Integer.parseInt(priceValidity);
				validity = shiftDate(new Date(), shift);
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				try {
					validity = sdf.parse(priceValidity);
				} catch (ParseException e) {
					throw new WebQuoteRuntimeException(e.getMessage(),new RuntimeException(e));
				}
			}
		}
		validity = shiftDate(validity, QuoteSBConstant.QUOTE_EXPIRY_DATE_SHIFT_NORMAL);
		return validity;
	}
	
	private static Date shiftDate(Date date, int shift){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, shift);
		return cal.getTime();
	}
	
	
	public List<RestrictedCustomerMapping> getRestrictedCustomerList(List<CatalogSearchItemBean> items, String selectedBizUnit )
	{
		List<RestrictedCustomerMappingSearchCriteria> restrictedCustomerCriteriaList = new ArrayList<RestrictedCustomerMappingSearchCriteria>();
		List<RestrictedCustomerMapping>  returnRestrictedCustomerList = new ArrayList<RestrictedCustomerMapping>();
		if(items!=null && items.size()>0)
		{
			for(CatalogSearchItemBean ipci: items)
			{
				if(ipci.getSoldToCode()!=null && !ipci.getSoldToCode().isEmpty())
				{
					RestrictedCustomerMappingSearchCriteria rcc = new RestrictedCustomerMappingSearchCriteria();
					rcc.setBizUnit(selectedBizUnit);
					rcc.setMfrKeyword(ipci.getMfr());
					rcc.setSoldToCodeKeyword(ipci.getSoldToCode());
					restrictedCustomerCriteriaList.add(rcc);
				}
			}
			returnRestrictedCustomerList = restrictedCustomerSB.findRestrictedCust(restrictedCustomerCriteriaList);				
		}
		else
		{
			returnRestrictedCustomerList = new ArrayList<RestrictedCustomerMapping>();
		}
		
		return returnRestrictedCustomerList;
	}
	
/*	private CatalogSearchResult convertToCheckResult(PricerInfo pricerInfo){
		LOG.info("== CatalogSearchSB convertToCheckResult ");
		CatalogSearchResult result = new CatalogSearchResult();
		result.setMfr(pricerInfo.getMfr());
		result.setFullMfrPart(pricerInfo.getFullMfrPartNumber());
		result.setMpq(pricerInfo.getMpq());
		result.setMoq(pricerInfo.getMoq());
		result.setTargetResale(pricerInfo.getBottomPrice());
		if(pricerInfo.getCostIndicator() != null){
			result.setCostIndicator(pricerInfo.getCostIndicator());
		}
		
		//result.setQuoteExpiryDate(null);
		result.setPriceValidity(pricerInfo.getPriceValidity());
		result.setQuoteExpiryDate(convertPriceValidityToQuoteExpiryDate(pricerInfo.getPriceValidity()));
		
		
		result.setLeadTime(pricerInfo.getLeadTime());
		result.setBizUnit(pricerInfo.getBizUnit());
		result.setDescription(pricerInfo.getDescription());
		
		//WebQuote 2.2 enhancement: instance pricing checking
		if(pricerInfo.isContractPricer())
		{
			result.setPriceType(QuoteSBConstant.PRICE_TYPE_CONTRACT);
			result.setQuotationEffectiveDate(null);


		}
		else
		{
			if(pricerInfo.getMaterialTypeId()==null || "".equalsIgnoreCase(pricerInfo.getMaterialTypeId()))
			{
				result.setPriceType("");
			}
			else if(QuoteSBConstant.PRICE_TYPE_NORMAL.equalsIgnoreCase(pricerInfo.getMaterialTypeId()))
			{
				result.setPriceType(QuoteSBConstant.PRICE_TYPE_NORMAL);
				result.setQuotationEffectiveDate(pricerInfo.getDisplayQuoteEffDate());
				result.setSoldToCustomerCode("");
				result.setSoldToCustomerName("");
			}
			else if(QuoteSBConstant.PRICE_TYPE_PROGRAM.equalsIgnoreCase(pricerInfo.getMaterialTypeId()))
			{
				result.setPriceType(QuoteSBConstant.PRICE_TYPE_PROGRAM);
				result.setQuotationEffectiveDate(pricerInfo.getDisplayQuoteEffDate());
				result.setSoldToCustomerCode("");
				result.setSoldToCustomerName("");
			}
		}
		
		return result;
	}
	
*/
}
