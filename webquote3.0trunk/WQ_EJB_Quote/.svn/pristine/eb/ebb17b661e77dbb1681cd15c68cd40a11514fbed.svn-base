/**
 * 
 */
package com.avnet.emasia.webquote.quote.ejb;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.cxf.common.util.StringUtils;

import com.avnet.emasia.webquote.cache.DesignLocationCacheManager;
import com.avnet.emasia.webquote.cache.ICacheUtil;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.DesignLocation;
import com.avnet.emasia.webquote.entity.DrNedaItem;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.RestrictedCustomerMapping;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.RestrictedCustomerMappingParameter;
import com.avnet.emasia.webquote.quote.vo.RestrictedCustomerMappingSearchCriteria;
import com.avnet.emasia.webquote.quote.vo.WorkingPlatformItemVO;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.DBResourceBundle;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.vo.RfqHeaderVO;
import com.avnet.emasia.webquote.vo.RfqItemVO;

/**
 * @author 042659
 *
 */
@Stateless
@LocalBean
public class RfqItemValidatorSB {

	@PersistenceContext(unitName = "Server_Source")
	EntityManager em;

	@EJB
	transient SysConfigSB sysConfigSB;

	@EJB
	CustomerSB customerSB;

	@EJB
	UserSB userSB;

	@EJB
	ManufacturerSB manufacturerSB;
	
	@EJB
	DrmsSB drmsSB; 
	
	@EJB
	private QuoteSB quoteSB;
	
	@EJB
	private RestrictedCustomerMappingSB restrictedCustomerSB;
	
	@EJB
	private ICacheUtil cacheUtil;
	

	private static final Logger LOG = Logger.getLogger(RfqItemValidatorSB.class.getName());

	public static final String DEFAULT_SELECT = "-select-";

	private static final int ZERO = 0;

	private final static int FOURTY = 40;

	/**
	 * Default constructor.
	 */
	public RfqItemValidatorSB() {

	}

	/**
	 * @param key
	 * @return
	 */
	public static String getText(final String key, final Locale locale) {
		// final Locale locale =
		// FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(DBResourceBundle.class.getName(), locale);
		return bundle.getString(key);
	}
	
	
	
	/**
	 * @param key
	 * @param parameters
	 * @return
	 */
	public static String getParameterizedText(final String key, final String parameters,final Locale locale) {
		//final Locale locale = getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(DBResourceBundle.class.getName(),locale);
		return MessageFormat.format(bundle.getString(key), parameters);
	}

	/**
	 * @Description: TODO
	 * @author 042659
	 * @param rfqHeader
	 * @return void
	 * @throws WebQuoteException,including
	 *             alert message header and detail message
	 */
	public void validateQuoteHeader(RfqHeaderVO rfqHeader, final Locale locale) throws WebQuoteException {
		StringBuffer message = new StringBuffer();
		String resourceMessage = null;
		if (QuoteUtil.isEmpty(rfqHeader.getSalesEmployeeNumber())) {
			resourceMessage = getText("wq.label.salesmanEmpCode", locale);
			message.append(resourceMessage + ", ");
		}
		if (QuoteUtil.isEmpty(rfqHeader.getSalesEmployeeName())) {
			resourceMessage = getText("wq.label.empName", locale);
			message.append(resourceMessage + ", ");
		}

		if (QuoteUtil.isEmpty(rfqHeader.getSoldToCustomerNumber())) {
			resourceMessage = getText("wq.label.SoldToCode", locale);
			message.append(resourceMessage + ", ");
		}

		if (QuoteUtil.isEmpty(rfqHeader.getCustomerType())) {
			resourceMessage = getText("wq.label.custmrType", locale);
			message.append(resourceMessage + ", ");
		}
		if (QuoteUtil.isEmpty(rfqHeader.getEnquirySegment())) {
			resourceMessage = getText("wq.label.enqrySegment", locale);
			message.append(resourceMessage + ", ");
		}
		if (QuoteUtil.isEmpty(rfqHeader.getQuoteType())) {
			resourceMessage = getText("wq.label.QuoteType", locale);
			message.append(resourceMessage + ", ");
		}
		if (QuoteUtil.isEmpty(rfqHeader.getDesignInCat())) {
			resourceMessage = getText("wq.error.designInCat", locale);
			message.append(resourceMessage + ", ");
		}

		if (message.length() != 0) {
			throw generateWQException(getText("wq.message.mandFieldError", locale) + " : ",
					message.append(getText("wq.message.atRFQHeader", locale) + ".").toString());
		} else {
			// check sold to code,end customer,ship to code match with sales man
			// default bizunit
			List<String> soldToAccountGroup = new ArrayList<String>();
			soldToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
			soldToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
			boolean soldToChecking = customerSB.verifyCustomerNumberWithSalesDefaultBizUnit(rfqHeader.getSoldToCustomerNumber(), soldToAccountGroup,
					rfqHeader.getSalesBizUnit());
			if (!soldToChecking) {
				message.append(getText("wq.label.SoldToCode", locale) + ", ");
			}

			if (!QuoteUtil.isEmpty(rfqHeader.getShipToCustomerNumber())) {
				List<String> shipToAccountGroup = new ArrayList<String>();
				shipToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
				shipToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SHIPTO);
				shipToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
				boolean shipToChecking = customerSB.verifyCustomerNumberWithSalesDefaultBizUnit(rfqHeader.getShipToCustomerNumber(),
						shipToAccountGroup, rfqHeader.getSalesBizUnit());
				if (!shipToChecking) {
					message.append(getText("wq.label.ShipToCode", locale) + ", ");
				}
			}

			if (!QuoteUtil.isEmpty(rfqHeader.getEndCustomerNumber())) {
				Customer endChecking = findEndCustomerBySoldToAndEnd(rfqHeader.getEndCustomerNumber(), rfqHeader.getSoldToCustomerNumber(),
						rfqHeader);
				if (endChecking == null) {
					message.append(getText("wq.label.endCust", locale) + ", ");
				}
			}

			if (message.length() != 0) {
				throw generateWQException(getText("wq.message.custValidatnFails", locale) + ":",
						message.append(getText("wq.message.atRFQHeader", locale) + ".").toString());
			}
		}

		validateCustomers(rfqHeader, locale);

		// validate design region and location for BMT require added
		// by Damon@20151229

		if (!QuoteUtil.isEmpty(rfqHeader.getDesignLocation())) {

			//Bryan Start
//			DesignLocation dLocation = com.avnet.emasia.webquote.quote.cache.DesignLocationCacheManager
//					.getDesignLocation(rfqHeader.getDesignLocation());
			DesignLocation dLocation = cacheUtil
					.getDesignLocation(rfqHeader.getDesignLocation());
			//Bryan End
			if (dLocation != null) {

				if (dLocation.getDesignRegion() != null && (!dLocation.getDesignRegion().equals(rfqHeader.getDesignRegion()))) {
					// errorFound = true;
					throw this.generateWQException(getText("wq.message.invalidDesgnLoc", locale) + " :", getText("wq.label.designLoc", locale) + " "
							+ rfqHeader.getDesignLocation() + getText("wq.label.notMatchDesignRegion", locale));
				}

			}

		}

		if (!QuoteUtil.isEmpty(rfqHeader.getDesignRegion()) && (!rfqHeader.getDesignRegion().equals(DEFAULT_SELECT))) {

			//Bryan Start
//			List<String> regionList = DesignLocationCacheManager.getDesignRegionList();
			List<String> regionList = cacheUtil.getDesignRegionList();
			//Bryan End
			if (regionList != null) {
				if (!regionList.contains(rfqHeader.getDesignRegion())) {
					throw this.generateWQException(getText("wq.message.invalidDesgnRegion", locale) + " :", getText("wq.label.designRegion", locale)
							+ " " + rfqHeader.getDesignLocation() + getText("wq.message.doesNotExist", locale));
				}

			}
			// validate DesignRgion is not null,but the DesignLation
			// is null.Can't pass
			if (QuoteUtil.isEmpty(rfqHeader.getDesignLocation())) {
				throw this.generateWQException("", getText("wq.message.designLocDesignRegionError", locale) + ".");
			}

		}

		if (!QuoteUtil.isEmpty(rfqHeader.getSalesEmployeeNumber())) {
			User user = userSB.findByEmployeeIdLazily(rfqHeader.getSalesEmployeeNumber());
			if (user == null) {
				generateWQException(getText("wq.message.invalidSalesmanError", locale) + " :",
						getText("wq.message.invalidSalesNumError", locale) + " " + rfqHeader.getSalesEmployeeNumber());
			} else if (!QuoteUtil.isEmpty(rfqHeader.getSalesEmployeeName()) && !rfqHeader.getSalesEmployeeName().equals(user.getName())) {
				generateWQException(getText("wq.message.invalidSalesmanError", locale) + " :", getText("wq.message.salesmanNumbrError", locale) + " "
						+ rfqHeader.getSalesEmployeeNumber() + " " + getText("wq.message.salesmanNotMatchError", locale));
			}
		}

	}

	/**
	 * @throws WebQuoteException
	 * 			@Description: TODO @author 042659 @param rfqHeader @return
	 *             void @throws
	 */
	private void validateCustomers(RfqHeaderVO rfqHeader, Locale locale) throws WebQuoteException {

		if (!QuoteUtil.isEmpty(rfqHeader.getSoldToCustomerNumber())) {
			Customer customer = customerSB.findByPK(rfqHeader.getSoldToCustomerNumber());
			if (customer == null) {
				throw generateWQException(getText("wq.label.invalidCust", locale) + " :",
						getText("wq.message.invalidSold", locale) + rfqHeader.getSoldToCustomerNumber());
			} else if (!QuoteUtil.isEmpty(rfqHeader.getSoldToCustomerName())
					&& !rfqHeader.getSoldToCustomerName().equals(customer.getCustomerFullName())) {
				throw generateWQException(getText("wq.label.invalidCust", locale) + " :", getText("wq.label.soldCustName", locale)
						+ rfqHeader.getSoldToCustomerNumber() + " " + getText("wq.label.notMatchCustName", locale));
			}

			boolean isInvalidCustomer = customerSB.isInvalidCustomer(rfqHeader.getSoldToCustomerNumber());
			if (isInvalidCustomer) {
				LOG.info("Invalid Special Customer : " + rfqHeader.getSoldToCustomerNumber());
				throw this.generateWQException(getText("wq.label.invalidCust", locale) + " :",
						getText("wq.label.soldToCodeCustomer", locale) + " : " + rfqHeader.getSoldToCustomerNumber());
			}
		}

		if (!QuoteUtil.isEmpty(rfqHeader.getEndCustomerNumber())) {
			Customer headerEndCutomer = findEndCustomerBySoldToAndEnd(rfqHeader.getEndCustomerNumber(), rfqHeader.getSoldToCustomerNumber(),
					rfqHeader);
			if (headerEndCutomer == null) {
				throw this.generateWQException(getText("wq.label.invalidCust", locale) + " :",
						getText("wq.message.invalidCust", locale) + " " + rfqHeader.getEndCustomerNumber());
			} else if (!QuoteUtil.isEmpty(rfqHeader.getEndCustomerName())
					&& !rfqHeader.getEndCustomerName().equals(headerEndCutomer.getCustomerFullName())) {
				throw this.generateWQException(getText("wq.label.invalidCust", locale) + " :", getText("wq.label.endCustNum", locale) + " "
						+ rfqHeader.getEndCustomerNumber() + " " + getText("wq.label.notMatchCustName", locale));
			}

			// add validation ,blocked when it is special customer
			// by damon@20161107
			boolean isInvalidCustomer = customerSB.isInvalidCustomer(rfqHeader.getEndCustomerNumber());
			if (isInvalidCustomer) {
				LOG.info("Invalid Special Customer : " + rfqHeader.getEndCustomerNumber());
				throw this.generateWQException(getText("wq.label.invalidCust", locale) + " :",
						getText("wq.label.endCust", locale) + " : " + rfqHeader.getEndCustomerNumber());
			}
		}

		if (!QuoteUtil.isEmpty(rfqHeader.getShipToCustomerNumber())) {
			if (!QuoteUtil.isEmpty(rfqHeader.getShipToCustomerNumber())) {
				Customer headerdCutomer = customerSB.findByPK(rfqHeader.getShipToCustomerNumber());
				if (headerdCutomer == null) {
					throw this.generateWQException(getText("wq.label.invalidCust", locale) + " :",
							getText("wq.message.invalidShip", locale) + " " + rfqHeader.getShipToCustomerNumber());
				} else if (!QuoteUtil.isEmpty(rfqHeader.getShipToCustomerName())
						&& !rfqHeader.getShipToCustomerName().equals(headerdCutomer.getCustomerFullName())) {
					throw this.generateWQException(getText("wq.label.invalidCust", locale) + " :", getText("wq.label.shipCustNum", locale) + " "
							+ rfqHeader.getShipToCustomerNumber() + " " + getText("wq.label.notMatchCustName", locale));
				}
			}
		}

		// Prevent user to enter the End Customer freely by
		// damon@20161108
		if (!QuoteUtil.isEmpty(rfqHeader.getEndCustomerName()) && QuoteUtil.isEmpty(rfqHeader.getEndCustomerNumber())) {

			throw this.generateWQException(getText("wq.label.invalidCust", locale) + " :",
					getText("wq.message.endCustCode", locale) + ": " + rfqHeader.getEndCustomerName());

		}

	}

	/**
	 * @Description: WebQuoteException,including alert message header and detail
	 *               message @author 042659 @param messageCode @param
	 *               detailMessage @return @return Exception @throws
	 */
	private WebQuoteException generateWQException(String mainMessage, String detailMessage) {
		//return new WebQuoteException(messageCode, new Exception(detailMessage));
		//WebQuoteException wqe=new WebQuoteException("",new Object[]{messageCode,detailMessage});
		//wqe.setMessage(detailMessage);
		return new WebQuoteException("",mainMessage,detailMessage);
	}

	private Customer findEndCustomerBySoldToAndEnd(String endNumber, String soldToNumber, RfqHeaderVO rfqHeader) {
		if (QuoteUtil.isEmpty(endNumber) || QuoteUtil.isEmpty(soldToNumber)) {
			return null;
		}

		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_ENDCUSTOMER);
		// Allow to input account group as 0001 (sold to party) and 0005
		// (prospective customer),added by Damon for BMT requests
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
		Customer customer = customerSB.findEndCustomerForRFQ(endNumber, soldToNumber, accountGroup, rfqHeader.getSalesBizUnit());
		return customer;
	}

	/**
	 * @throws WebQuoteException
	 * @Description: throw WequoteException if rfqItem size is 0
	 * @author 042659
	 * @param rfqItems
	 * @param locale
	 * @return void
	 */
	public void validateRfqItemSize(List<RfqItemVO> rfqItems, final Locale locale) throws WebQuoteException {

		String rfqMandatoryMessage = "";
		if (rfqItems.size() == 0) {
			rfqMandatoryMessage += getText("wq.message.noRfqFound", locale);
		}
		if (!QuoteUtil.isEmpty(rfqMandatoryMessage)) {
			throw generateWQException(getText("wq.message.mandFieldError", locale),
					rfqMandatoryMessage.substring(0, rfqMandatoryMessage.length() - 2));
		}

	}

	/**
	 * 
	 * @Description: TODO
	 * @author 042659
	 * @param rfqItems
	 * @param locale
	 * @return void
	 * @throws WebQuoteException
	 */
	public void validateRfqItem(List<RfqItemVO> rfqItems, Locale locale, BizUnit userDefualtbizUnit,boolean isValidateDrms) throws WebQuoteException {
		for (RfqItemVO rfqItem : rfqItems) {

			LOG.info("FirstPage-CheckPoint one : " + rfqItem.getMfr() + "/" + rfqItem.getRequiredPartNumber() + " - "
					+ rfqItem.getSoldToCustomerNumber());
			//if (rfqItem.getQuoteStage() == null) {
				String rfqMandatoryMessage = " ";
				if (QuoteUtil.isEmpty(rfqItem.getMfr()))
					rfqMandatoryMessage += "MFR, ";
				if (QuoteUtil.isEmpty(rfqItem.getRequiredPartNumber()))
					rfqMandatoryMessage += getText("wq.label.requested", locale) + " P/N, ";
				if (QuoteUtil.isEmpty(rfqItem.getRequiredQty()))
					rfqMandatoryMessage += getText("wq.label.reqQty", locale) + ", ";
				if (QuoteUtil.isEmpty(rfqItem.getSoldToCustomerName()))
					rfqMandatoryMessage += getText("wq.label.SoldToParty", locale) + ", ";
				if (QuoteUtil.isEmpty(rfqItem.getSoldToCustomerNumber()))
					rfqMandatoryMessage += getText("wq.label.SoldToCode", locale) + ", ";
				if (QuoteUtil.isEmpty(rfqItem.getCustomerType()))
					rfqMandatoryMessage += getText("wq.label.custmrType", locale) + ", ";
				if (QuoteUtil.isEmpty(rfqItem.getEnquirySegment()))
					rfqMandatoryMessage += getText("wq.label.segment", locale) + ", ";

				if (QuoteUtil.isEmpty(rfqItem.getDesignInCat()))
					rfqMandatoryMessage += getText("wq.label.DesignInCat", locale) + ", ";

				if (!QuoteUtil.isEmpty(rfqMandatoryMessage)) {
					throw generateWQException(getText("wq.message.mandFieldError", locale),
							rfqMandatoryMessage.substring(0, rfqMandatoryMessage.length() - 2));
				}
				if (!QuoteUtil.isEmpty(rfqItem.getTargetResale())) {
					Double targetRes = Double.parseDouble(rfqItem.getTargetResale());
					if (targetRes == 0d) {
						rfqItem.setTargetResale(null);
					} else {
						if (targetRes < 0d || targetRes > 999999999.99999d) {
						throw 	generateWQException(getText("wq.message.priceVal", locale), " at row " + rfqItem.getItemNumber());
						}
					}

				}
				if (isValidateDrms) {
					vadidateDrmsFields(rfqItem, locale);
				}
				
				
				
				validateCustomerBySalesMan(rfqItem, locale);
				

			//}

			//

			// validate the RequiredQty
			if (rfqItem.getRequiredQty() != null && !"".equals(rfqItem.getRequiredQty().trim()) && Integer.valueOf(rfqItem.getRequiredQty()) == ZERO) {

				throw generateWQException(getText("wq.message.reqQty", locale),
						" " + getText("wq.message.atRow", locale) + " :" + rfqItem.getItemNumber());
			}

			// validate the EAU
			if (rfqItem.getRequiredQty() != null && rfqItem.getEau() != null) {
				int requiredQty = Integer.valueOf(rfqItem.getRequiredQty());
				int eau = Integer.valueOf(rfqItem.getEau());
				if (requiredQty != ZERO && eau != ZERO && requiredQty > eau) {
					String rfqEauMessage = "";
					rfqEauMessage = " " + getText("wq.message.atRow", locale) + " :" + rfqItem.getItemNumber();
					throw this.generateWQException(getText("wq.message.eauSmallerReqQty", locale) + ".", rfqEauMessage);
				}
			}

			// validate the length of RequiredPartNumber
			if (rfqItem.getRequiredPartNumber() != null && rfqItem.getRequiredPartNumber().length() > FOURTY) {
				String rfqPartNumberLengthMessage = "";
				rfqPartNumberLengthMessage = " " + getText("wq.message.atRow", locale) + rfqItem.getItemNumber();
				throw this.generateWQException(getText("wq.message.pnLen", locale), rfqPartNumberLengthMessage);
			}

			// validate SoldToCustomerNumber
			if (!QuoteUtil.isEmpty(rfqItem.getSoldToCustomerNumber())) {
				Customer customer = customerSB.findByPK(rfqItem.getSoldToCustomerNumber());
				if (customer == null) {
					throw this.generateWQException(getText("wq.label.invalidCust", locale) + " :", getText("wq.message.invalidSold", locale) + " "
							+ rfqItem.getSoldToCustomerNumber() + " at row " + rfqItem.getItemNumber());
				}

				// Fixed INC0201753 2015/08/11 041863 start
				boolean isInvalidCustomer = customerSB.isInvalidCustomer(rfqItem.getSoldToCustomerNumber());
				if (isInvalidCustomer) {
					LOG.info("Invalid Special Customer : " + rfqItem.getSoldToCustomerNumber());

					throw this.generateWQException(getText("wq.label.invalidCust", locale) + " :", getText("wq.label.soldToCodeCustomer", locale)
							+ " : " + rfqItem.getSoldToCustomerNumber() + " " + getText("wq.message.atRowt", locale) + " " + rfqItem.getItemNumber());
				}
				// Fixed INC0201753 2015/08/11 041863 end
			}

			if (!QuoteUtil.isEmpty(rfqItem.getShipToCustomerNumber())) {
				Customer customer = customerSB.findByPK(rfqItem.getShipToCustomerNumber());
				if (customer == null) {
					LOG.info("Invalid Customer : " + rfqItem.getShipToCustomerNumber());
					throw this.generateWQException(getText("wq.label.invalidCust", locale) + " :", getText("wq.message.shipToCust", locale) + " : "
							+ rfqItem.getShipToCustomerNumber() + " " + getText("wq.message.atRow", locale) + " " + rfqItem.getItemNumber());
				}

			}

			// Prevent user to enter the End Customer freely by
			// damon@20161108
			if (!QuoteUtil.isEmpty(rfqItem.getEndCustomerName()) && QuoteUtil.isEmpty(rfqItem.getEndCustomerNumber())) {

				LOG.info("Please fill in a valid End Customer Code or remove the End Customer name : " + rfqItem.getEndCustomerName());
				throw this.generateWQException(getText("wq.label.invalidCust", locale) + " :",
						getText("wq.message.endCustCode", locale) + " " + getText("wq.message.atRow", locale) + " " + rfqItem.getItemNumber());
			}

			if (!QuoteUtil.isEmpty(rfqItem.getEndCustomerNumber())) {
				Customer customer = findEndCustomerBySoldToAndEnd(rfqItem.getEndCustomerNumber(), rfqItem.getSoldToCustomerNumber(),
						rfqItem.getRfqHeaderVO());
				if (customer == null) {
					LOG.info("Invalid Customer : " + rfqItem.getEndCustomerNumber());
					throw this.generateWQException(getText("wq.label.invalidCust", locale) + " :", getText("wq.label.endCust", locale) + " : "
							+ rfqItem.getEndCustomerNumber() + " " + getText("wq.message.atRow", locale) + " " + rfqItem.getItemNumber());
				} else if (!QuoteUtil.isEmpty(rfqItem.getEndCustomerName()) && !rfqItem.getEndCustomerName().equals(customer.getCustomerFullName())) {
					throw this.generateWQException(getText("wq.label.invalidCust", locale) + " :",
							getText("wq.label.endCustNum", locale) + " " + rfqItem.getEndCustomerNumber() + " "
									+ getText("wq.label.notMatchCustName", locale) + " " + getText("wq.message.atRow", locale) + " "
									+ rfqItem.getItemNumber());
				}

				// add validation ,blocked when it is special
				// customer by damon@20161107
				boolean isInvalidCustomer = customerSB.isInvalidCustomer(rfqItem.getEndCustomerNumber());
				if (isInvalidCustomer) {
					LOG.info("Invalid Special Customer : " + rfqItem.getEndCustomerNumber());
					throw this.generateWQException(getText("wq.label.invalidCust", locale) + " :", getText("wq.label.endCust", locale) + " : "
							+ rfqItem.getEndCustomerNumber() + " " + getText("wq.message.atRow", locale) + " " + rfqItem.getItemNumber());
				}
			}

			// validate DesignLocation for BMT project
			if (!QuoteUtil.isEmpty(rfqItem.getDesignLocation())) {

				//Bryan Start
//				DesignLocation dLocation = DesignLocationCacheManager.getDesignLocation(rfqItem.getDesignLocation());
				DesignLocation dLocation = cacheUtil.getDesignLocation(rfqItem.getDesignLocation());
				//Bryan End
				if (dLocation != null) {

					if (dLocation.getDesignRegion() != null && (!dLocation.getDesignRegion().equals(rfqItem.getDesignRegion()))) {
						throw this.generateWQException(getText("wq.message.invalidDesgnLoc", locale) + " :",
								getText("wq.label.designLoc", locale) + " " + rfqItem.getDesignLocation() + " "
										+ getText("wq.label.notMatchDesignRegion", locale) + " " + getText("wq.message.atRow", locale) + " "
										+ rfqItem.getItemNumber());
					}

				}

			}
			// validate the DesignRegion.null allowed,but if it's
			// not null,need to match the value of exists data in
			// table
			if (!QuoteUtil.isEmpty(rfqItem.getDesignRegion()) && (!rfqItem.getDesignRegion().equals(DEFAULT_SELECT))) {

				//Bryan Start
//				List<String> regionList = DesignLocationCacheManager.getDesignRegionList();
				List<String> regionList = cacheUtil.getDesignRegionList();
				//Bryan End
				if (regionList != null) {
					if (!regionList.contains(rfqItem.getDesignRegion())) {
						throw this.generateWQException("", " " + getText("wq.message.atRow", locale) + " #<" + rfqItem.getItemNumber() + ">:"
								+ getText("wq.message.ntExitDesignRegion", locale) + ".");
					}

				}
				// validate DesignRgion is not null,but the
				// DesignLation is null.Can't pass
				if (QuoteUtil.isEmpty(rfqItem.getDesignLocation())) {
					throw this.generateWQException("", " " + getText("wq.message.atRow", locale) + " #<" + rfqItem.getItemNumber() + ">:"
							+ getText("wq.message.designLocDesignRegionError", locale) + ".");
				}

			}

			// The End Customer is mandatory field if Design Region
			// is non-ASIA.
			if (!QuoteUtil.isEmpty(rfqItem.getDesignRegion()) && (!rfqItem.getDesignRegion().equals(DEFAULT_SELECT))
					&& (!rfqItem.getDesignRegion().equals("ASIA"))) {

				if (QuoteUtil.isEmpty(rfqItem.getEndCustomerNumber())) {
					throw this.generateWQException("", " " + getText("wq.message.atRow", locale) + " #<" + rfqItem.getItemNumber() + ">:"
							+ getText("wq.message.mandateEndCust", locale) + ".");

				}

			}

			// BMT AQ Flag cannot be checked if Design Region is
			// ASIA/Blank
			if (rfqItem.isBmtCheckedFlag()) {

				if (QuoteUtil.isEmpty(rfqItem.getDesignRegion()) || (rfqItem.getDesignRegion().equals("ASIA"))
						|| (rfqItem.getDesignRegion().equals(DEFAULT_SELECT))) {

					throw this.generateWQException("", " " + getText("wq.message.atRow", locale) + " #<" + rfqItem.getItemNumber() + ">:"
							+ getText("wq.message.designRegionCheckError", locale) + ".");
				}

			}

			//
			if (!QuoteUtil.isEmpty(rfqItem.getMfr())) {
				Manufacturer mfr = manufacturerSB.findManufacturerByName(rfqItem.getMfr(), userDefualtbizUnit);
				if (mfr == null) {
					throw this.generateWQException("", " " + getText("wq.message.atRow", locale) + "#<" + rfqItem.getItemNumber() + ">:"
							+ getText("wq.message.invalidMFRDesignRegion", locale));
				}
			}

		}

	}

	/**
	 * @throws WebQuoteException    
	* @Description:check sold to code,end customer,ship to code match with sales man default bizunit
	* @author 042659
	* @param rfqItem
	* @param locale      
	* @return void    
	* @throws  
	*/  
	private void validateCustomerBySalesMan(RfqItemVO rfqItem, Locale locale) throws WebQuoteException {
		// TODO Auto-generated method stub
		StringBuffer message = new StringBuffer();
		List<String> soldToAccountGroup = new ArrayList<String>();
		soldToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
		soldToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
		boolean soldToChecking = customerSB.verifyCustomerNumberWithSalesDefaultBizUnit(
				rfqItem.getSoldToCustomerNumber(), soldToAccountGroup, rfqItem.getRfqHeaderVO().getSalesBizUnit());
		if (!soldToChecking) {
			message.append(getText("wq.label.SoldToCode",locale) + ", ");
		}

		if (!QuoteUtil.isEmpty(rfqItem.getShipToCustomerNumber())) {
			List<String> shipToAccountGroup = new ArrayList<String>();
			shipToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
			shipToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SHIPTO);
			shipToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
			boolean shipToChecking = customerSB.verifyCustomerNumberWithSalesDefaultBizUnit(
					rfqItem.getShipToCustomerNumber(), shipToAccountGroup, rfqItem.getRfqHeaderVO().getSalesBizUnit());
			if (!shipToChecking) {
				message.append(getText("wq.label.ShipToCode",locale) + ", ");
			}
		}

		if (!QuoteUtil.isEmpty(rfqItem.getEndCustomerNumber())) {
			Customer endChecking = findEndCustomerBySoldToAndEnd(rfqItem.getEndCustomerNumber(),
					rfqItem.getSoldToCustomerNumber(),rfqItem.getRfqHeaderVO());
			if (endChecking == null) {

				message.append(getText("wq.label.endCust",locale) + ", ");
			}
		}
		if (message.length() != 0 && !QuoteUtil.isEmpty(message.toString())) {
			throw this.generateWQException(getText("wq.message.custValidatnFails",locale) + ":", message.append(getText("wq.message.atRow",locale) + ": ").append(rfqItem.getItemNumber())
					.toString());
		}
		
	}

	/**
	 * @throws WebQuoteException    
	* @Description: TODO
	* @author 042659
	* @param rfqItem      
	* @return void    
	* @throws  
	*/  
	private void vadidateDrmsFields(RfqItemVO rfqItem2, Locale locale) throws WebQuoteException {
		// TODO Auto-generated method stub
		String rfqMatchDrmsMessage = "";
		if (rfqItem2.getStage() == null) {
			if (rfqItem2.getDrmsNumber() != null && rfqItem2.getDrmsNumber() != 0l) {
				List<DrNedaItem> drNedaItems = drmsSB.findMatchedDrProject(rfqItem2.getSoldToCustomerNumber(), rfqItem2.getEndCustomerNumber(),
						rfqItem2.getMfr(), rfqItem2.getRequiredPartNumber());
				if (!QuoteUtil.isEmptyNedaItemList(drNedaItems)) {
					boolean findIssueDrms = true;
					for (DrNedaItem drNedaItem : drNedaItems) {
						if (drNedaItem.getDrNedaHeader().getDrProjectHeader().getProjectId() == rfqItem2.getDrmsNumber().longValue()) {
							// the change for DRMS margin
							// retention project
							if (drNedaItem.getId() != null) {
								rfqItem2.setDrNedaId(Long.valueOf(drNedaItem.getId().getNedaId()).intValue());
								rfqItem2.setDrNedaLineNumber(Long.valueOf(drNedaItem.getId().getNedaLineNumber()).intValue());
							}

							findIssueDrms = false;
							break;
						}
					}
					if (findIssueDrms) {
						rfqMatchDrmsMessage = " " + getText("wq.message.atRow", locale) + "  :" + rfqItem2.getItemNumber();
					}
				} else {
					rfqMatchDrmsMessage = " " + getText("wq.message.atRow", locale) + "  :" + rfqItem2.getItemNumber();
				}
			}
			if (!QuoteUtil.isEmpty(rfqMatchDrmsMessage)) {
				throw this.generateWQException(getText("wq.message.invalidTheDRMSId", locale) + ":", rfqMatchDrmsMessage);
			}
		}
	}

	/**
	 * @throws WebQuoteException    
	* @Description: TODO
	* @author 042659
	* @param rfqHeader
	* @param newProspectiveCustomerName1
	* @param newProspectiveCustomerCountry
	* @param newProspectiveCustomerCity
	* @param newProspectiveCustomerSalesOrg
	* @param newProspectiveCustomerAddress3      
	* @return void    
	* @throws  
	*/  
	public void validateNewCustomerCreation(RfqHeaderVO rfqHeader, String newProspectiveCustomerName1, String newProspectiveCustomerCountry,
			String newProspectiveCustomerCity, String newProspectiveCustomerSalesOrg, String newProspectiveCustomerAddress3,Locale locale) throws WebQuoteException {
		if (rfqHeader.isNewCustomer()) {
			String newCustomerMandatoryMessage = "";
			if (QuoteUtil.isEmpty(newProspectiveCustomerName1)) {
				newCustomerMandatoryMessage += getText("wq.label.custmrName",locale) + ", ";
			}
			if (QuoteUtil.isEmpty(newProspectiveCustomerCountry)) {
				newCustomerMandatoryMessage += getText("wq.label.country",locale) + ", ";
			}
			if (QuoteUtil.isEmpty(newProspectiveCustomerCity)) {
				newCustomerMandatoryMessage += getText("wq.label.city",locale) + ", ";
			}
			if (QuoteUtil.isEmpty(newProspectiveCustomerSalesOrg)) {
				newCustomerMandatoryMessage += getText("wq.label.salsOrg",locale) + ", ";
			}
			if (QuoteUtil.isEmpty(newProspectiveCustomerAddress3)) {
				newCustomerMandatoryMessage += getText("wq.label.address",locale) + ", ";
			}
			if (!QuoteUtil.isEmpty(newCustomerMandatoryMessage)) {
				throw this.generateWQException(getText("wq.message.mandFieldError",locale) + " :",newCustomerMandatoryMessage.substring(0, newCustomerMandatoryMessage.length() - 2));
			}
		}
		
	}

	/**   
	* @Description: TODO
	* @author 042659
	* @param duplicatedrfqItems
	* @param tmpMap      
	* @return void    
	* @throws  
	*/  
	public void extractDuplicatedRfqItems(List<RfqItemVO> duplicatedrfqItems, HashMap<String, RfqItemVO> tmpMap,List<RfqItemVO> rfqItems) {
		// TODO Auto-generated method stub
		String rfqItemUk="";
	
		for (RfqItemVO rfqItem : rfqItems) {
			rfqItemUk = generateRfqItemUk(rfqItem);
			if (tmpMap.containsKey(rfqItemUk)) {
				if (tmpMap.get(rfqItemUk) != null) {
					if (!duplicatedrfqItems.contains(tmpMap.get(rfqItemUk))) {
						RfqItemVO vo = tmpMap.get(rfqItemUk);
						vo.setMessages("Duplicated items within Form");
						duplicatedrfqItems.add(vo);
					}
				}
				rfqItem.setMessages("Duplicated items within Form");
				duplicatedrfqItems.add(rfqItem);
			} else {
				
				if(quoteSB.isDuplicatedFromDB(rfqItem)){
					rfqItem.setMessages("Duplicated quotes");
					duplicatedrfqItems.add(rfqItem);
				}else{
					tmpMap.put(rfqItemUk, rfqItem);
				}
			}
		}
		
	}
	
	
	/**   
	* @Description: TODO
	* @author 042659
	* @param rfqItem
	* @return      
	* @return Object    
	* @throws  
	*/  
	private String generateRfqItemUk(RfqItemVO rfqItem) {
		// TODO Auto-generated method stub
		StringBuilder sb=new StringBuilder();
		sb.append(rfqItem.getRfqHeaderVO() ==null?"":rfqItem.getRfqHeaderVO().getSalesBizUnit().getName().trim()).append("=").
		 append(rfqItem.getMfr().trim()).append("=")
		 .append(rfqItem.getRequiredPartNumber()).append("=")
		.append(rfqItem.getSoldToCustomerNumber().trim()).append("=")
		.append(rfqItem.getEndCustomerNumber() ==null?"":rfqItem.getEndCustomerNumber().trim())
		.append(rfqItem.getRequiredQty());
		//LOG.log(Level.INFO, "UK:"+sb.toString());
		return  sb.toString();
	}

	/**
	 * @throws WebQuoteException    
	* @Description: valify the customer based on restricted_customer_mapping table
	* @author 042659
	* @param rfqItems
	* @param locale
	* @param bizUnit       
	* @return void    
	* @throws  
	*/  
	public void validateRestrictedCustomer(List<RfqItemVO> rfqItems, Locale locale, BizUnit bizUnit) throws WebQuoteException {
		// TODO Auto-generated method stub
		List<RestrictedCustomerMappingSearchCriteria> restrictedCustomerCriteriaList = new ArrayList<RestrictedCustomerMappingSearchCriteria>();
		List<RestrictedCustomerMappingParameter> restrictedCustomerMappingParamList = new ArrayList<RestrictedCustomerMappingParameter>();
		for (RfqItemVO rfqItemVo : rfqItems) {
			RestrictedCustomerMappingSearchCriteria rcc = new RestrictedCustomerMappingSearchCriteria();
			rcc.setBizUnit(bizUnit.getName());
			rcc.setMfrKeyword(rfqItemVo.getMfr());
			//rcc.setSoldToCodeKeyword(rfqItemVo.getSoldToCustomerNumber());
			restrictedCustomerCriteriaList.add(rcc);
		}

		for (RfqItemVO rfqItem : rfqItems) {

			RestrictedCustomerMappingParameter rcp = new RestrictedCustomerMappingParameter();
			rcp.setMfr(rfqItem.getMfr());
			rcp.setSoldToCustomerNumber(rfqItem.getSoldToCustomerNumber());
			rcp.setBizUnit(rfqItem.getRfqHeaderVO().getBizUnit());
			rcp.setEndCustomerCode(rfqItem.getEndCustomerNumber());
			rcp.setRequiredPartNumber(rfqItem.getRequiredPartNumber());
			rcp.setProductGroup1Name(rfqItem.getProductGroup1() == null ? null : rfqItem.getProductGroup1().getName());
			rcp.setProductGroup2Name(rfqItem.getProductGroup2() == null ? null : rfqItem.getProductGroup2().getName());
			rcp.setProductGroup3Name(rfqItem.getProductGroup3());
			rcp.setProductGroup4Name(rfqItem.getProductGroup4());
			restrictedCustomerMappingParamList.add(rcp);
		}

		restrictedCustomerSB.validateRestrictedCustList(restrictedCustomerMappingParamList, locale, restrictedCustomerCriteriaList);
	}


}
