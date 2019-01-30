
package com.avnet.emasia.webquote.web.quote.strategy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jboss.logmanager.Level;

import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.web.quote.vo.QuotationEmailVO;

/**
 * @author 042659
 *
 */
public interface SendQuotationEmailStrategy extends Serializable {
	Logger LOGGER= Logger.getLogger(SendQuotationEmailStrategy.class.getName());
	default  Map<String, Object> getLookupSB(){
		LOGGER.info("call method getLookupSB begin...");
		try {
			     Map<String, Object> sBMap =  new HashMap<String,Object>();
				Context context = new InitialContext();		
				UserSB userSB = (UserSB) context.lookup("java:app/WQ_EJB_UserAndAuthor/UserSB!com.avnet.emasia.webquote.user.ejb.UserSB");
				if(userSB !=null){
					sBMap.put("userSB", userSB);
					
				}else{
					LOGGER.info("the userSB from context lookup is null.");
				}
				SysConfigSB sysConfigSB=(SysConfigSB) context.lookup("java:app/WQ_EJB_Utility/SysConfigSB!com.avnet.emasia.webquote.utilities.common.SysConfigSB");
				
				if(sysConfigSB !=null){
					sBMap.put("sysConfigSB", sysConfigSB);
					
				}else{
					LOGGER.info("the sysConfigSB from context lookup is null.");
				}
				MailUtilsSB mailUtilsSB=(MailUtilsSB) context.lookup("java:app/WQ_EJB_Utility/MailUtilsSB!com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB");
				
				if(mailUtilsSB !=null){
					sBMap.put("mailUtilsSB", mailUtilsSB);
					
				}else{
					LOGGER.info("the mailUtilsSB from context lookup is null.");
				}
				SystemCodeMaintenanceSB systemCodeMaintenanceSB=(SystemCodeMaintenanceSB) context.lookup("java:app/WQ_EJB_Quote/SystemCodeMaintenanceSB!com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB");
				if(systemCodeMaintenanceSB !=null){
					sBMap.put("systemCodeMaintenanceSB", systemCodeMaintenanceSB);
					
				}else{
					LOGGER.info("the systemCodeMaintenanceSB from context lookup is null.");
				}
				
				
				CacheUtil cacheUtil=(CacheUtil) context.lookup("java:app/WQ_Web/CacheUtil!com.avnet.emasia.webquote.utilites.web.common.CacheUtil");
				if(cacheUtil !=null){
					sBMap.put("cacheUtil", cacheUtil);
					
				}else{
					LOGGER.info("the systemCodeMaintenanceSB from context lookup is null.");
				}

				EJBCommonSB ejbCommonSB=(EJBCommonSB) context.lookup("java:app/WQ_Web/EJBCommonSB!com.avnet.emasia.webquote.dp.EJBCommonSB");
				if(ejbCommonSB !=null){
					sBMap.put("ejbCommonSB", ejbCommonSB);
					
				}else{
					LOGGER.info("the ejbCommonSB from context lookup is null.");
				}
				
				context = null;
				LOGGER.info("call method getLookupSB end and return the map size is :"+sBMap.size());
				return sBMap;
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, "Exception in getting UserSB object, exception message : "+e.getMessage(), e);
		}
		return null;
	}
	

	/**
	 * @param QuotationEmailVO
	 * @return 
	 */
	public void sendQuotationEmail(QuotationEmailVO vo);
	/**
	 * @param soldToCustomer
	 * @param quote
	 * @param isSubmissionDateFromQuote
	 * @return HSSFWorkbook
	 */
	public HSSFWorkbook getQuoteTemplateBySoldTo(Customer soldToCustomer, Quote quote, boolean isSubmissionDateFromQuote);
	
	public QuotationEmailVO genQuotationEmailVO(Quote quote, User user);
}
