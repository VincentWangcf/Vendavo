package com.avnet.emasia.webquote.web.pricerupload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logmanager.Level;
import org.primefaces.model.UploadedFile;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadParametersBean;
import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;
import com.avnet.emasia.webquote.commodity.bean.ProgramItemUploadCounterBean;
import com.avnet.emasia.webquote.commodity.constant.PRICER_TYPE;
import com.avnet.emasia.webquote.commodity.constant.PricerConstant;
import com.avnet.emasia.webquote.commodity.constant.UPLOAD_ACTION;
import com.avnet.emasia.webquote.commodity.ejb.ContractPriceUploadSB;
import com.avnet.emasia.webquote.commodity.ejb.NormalProgramPartMasterUploadSB;
import com.avnet.emasia.webquote.commodity.ejb.PricerEnquirySB;
import com.avnet.emasia.webquote.commodity.ejb.PricerUploadVerifySB;
import com.avnet.emasia.webquote.commodity.util.PricerTypeAndUkComparator;
import com.avnet.emasia.webquote.commodity.util.PricerUtils;
import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.AllPricerProcessSheet;
import com.avnet.emasia.webquote.utilites.web.util.Excel20007Reader;
import com.avnet.emasia.webquote.utilites.web.util.PricerUploadHelper;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;

@Stateless
@LocalBean
public class PricerUploadOffline {

	private static final Logger LOG = Logger.getLogger(PricerUploadOffline.class.getName());

	@EJB
	PricerEnquirySB pricerEnquirySB;
	@EJB
	protected MailUtilsSB mailUtilsSB;

	@EJB
	private SystemCodeMaintenanceSB sysCodeMaintSB;

	@EJB
	private PricerUploadVerifySB pricerUploadVerifySB;

	@EJB
	NormalProgramPartMasterUploadSB normalProgramPartMasterUploadSB;
	

	@EJB
	ContractPriceUploadSB contractPriceUploadSB;
	@Asynchronous
	@TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS)
	public void uploadOffline(UploadedFile uploadFile, User user, PriceUploadStrategy uploadStrategy,
			PricerUploadParametersBean puBean) {
		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();

		try {
			try {

				Thread.sleep(5000);
				System.out.println("trying to make this thread asleep.");
			} catch (InterruptedException e) {
				LOG.log(Level.SEVERE, "Error occured while trying to make this thread asleep, for uploaded file: "+uploadFile.getFileName()+", Exception Message: " + e.getMessage(), e);
			}
			puBean = new PricerUploadParametersBean();
			String fromEmailAddr = sysCodeMaintSB.getBuzinessProperty("OFFLINE_REPORT_FROM",
					user.getDefaultBizUnit().getName());
			Excel20007Reader reader = new Excel20007Reader(uploadFile, 0, new AllPricerProcessSheet());
			List<PricerUploadTemplateBean> beans = reader.excel2Beans(new PricerTypeAndUkComparator());

			/** only hava one action and one pricertype */
			if (beans == null)
				return;
			PricerUploadTemplateBean bean = beans.get(0);
			String action = bean.getAction();
			String pricerType = bean.getPricerType();
			String errMsg = "";
			Boolean isUinque = uploadStrategy.validateUiqueActionOrPricerType(action, pricerType, beans);
			if (!isUinque) {
				errMsg = errMsg + PricerConstant.ACTION_VARIFY_MSG;
			}
			boolean isRemoveForPartMaster = uploadStrategy.verifyRemoveForPartMaster(action, pricerType);
			if (!isRemoveForPartMaster) {
				errMsg = errMsg + "Remove (Pricer Only) cannot be applied to Part Master type file";
				return;
			}
			List<Manufacturer> manufacturerLst = null;
			if (UPLOAD_ACTION.ADD_UPDATE.getName().equals(action)) {
				errMsg = errMsg + uploadStrategy.verifyFields(beans);
			} else if (UPLOAD_ACTION.REMOVE_PRICER_ONLY.getName().equals(action)) {
				errMsg = errMsg + uploadStrategy.verifyFieldsForRemove(beans);
			}
			end = System.currentTimeMillis();

			LOG.info("verifyFields end,takes " + (end - start) + " ms");
			if (errMsg.length() >= PricerConstant.STRING_MAX_LENGTH) {// Validation
																		// error
																		// messages
																		// exceeds
																		// limit,
																		// don't
																		// continue
																		// to
																		// verify
				sendEmail(uploadFile.getFileName(), user, errMsg, null, fromEmailAddr);
				return;
			}

			if (UPLOAD_ACTION.ADD_UPDATE.getName().equals(action)) {
				HashMap<?, ?> hashMapCache = PricerUploadHelper.getPricerCache(user);
				List<Manufacturer> mfrListCache = (List<Manufacturer>) hashMapCache.get("mfr");
				manufacturerLst = PricerUtils.getMfrInUploadFile(beans, mfrListCache);
				errMsg = errMsg + verifyInDBForAddUpdate(pricerType, beans, user, manufacturerLst, puBean);
			} else if (UPLOAD_ACTION.REMOVE_PRICER_ONLY.getName().equals(action)) {
				errMsg = errMsg + verifyInDBForRemove(pricerType, beans, user, manufacturerLst);
			}
			end = System.currentTimeMillis();
			LOG.info("verifyInDBForAddUpdate end,takes " + (end - start) + " ms");
			if (!errMsg.equals("") && !errMsg.equals("[]")) {
				sendEmail(uploadFile.getFileName(), user, errMsg, null, fromEmailAddr);
				return;
			} else {
				ProgramItemUploadCounterBean countBean = beansToDataBase(uploadFile, action, pricerType, beans, user,
						manufacturerLst, puBean);
				sendEmail(uploadFile.getFileName(), user, "", countBean, fromEmailAddr);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "exception in uploading file offline for user : "+user.getName()+" , Exception message"+MessageFormatorUtil.getParameterizedStringFromException(e), e);
		}

		end = System.currentTimeMillis();
		LOG.info("upload end,takes " + (end - start) + " ms");
	}

	public String verifyInDBForAddUpdate(String pricerType, List<PricerUploadTemplateBean> beans, User user,
			List<Manufacturer> manufacturerLst, PricerUploadParametersBean puBean) {
		StringBuffer sb = new StringBuffer("");
		String language = new ResourceMB().getDefaultLocaleAsString();
		if (PRICER_TYPE.NORMAL.getName().equals(pricerType)) {
			if (sb.length() < PricerConstant.STRING_MAX_LENGTH) {
				sb.append(pricerUploadVerifySB.verifyMFRInDB(beans, new Locale(language)));
			}
			if (sb.length() < PricerConstant.STRING_MAX_LENGTH) {
				sb.append(pricerUploadVerifySB.verifyCstIdctInDB(beans, user, puBean));
			}
			if (sb.length() < PricerConstant.STRING_MAX_LENGTH) {
				sb.append(pricerUploadVerifySB.verifyABookCostIndicator(beans, user, manufacturerLst, puBean));
			}
			if (sb.length() < PricerConstant.STRING_MAX_LENGTH) {
				sb.append(pricerUploadVerifySB.batchVerifyEffectiveDateInDB(pricerType, beans, user, manufacturerLst));
			}
		} else if (PRICER_TYPE.PROGRAM.getName().equals(pricerType)) {
			if (sb.length() < PricerConstant.STRING_MAX_LENGTH) {
				sb.append(pricerUploadVerifySB.verifyMFRInDB(beans, new Locale(language)));
			}
			if (sb.length() < PricerConstant.STRING_MAX_LENGTH) {
				sb.append(pricerUploadVerifySB.verifyCstIdctInDB(beans, user, puBean));
			}
			/*
			 * if(sb.length()<PricerConstant.STRING_MAX_LENGTH){
			 * sb.append(pricerUploadVerifySB.verifyProgramInDB(beans)); }
			 */
			if (sb.length() < PricerConstant.STRING_MAX_LENGTH) {
				sb.append(pricerUploadVerifySB.verifyComponedKeyInMtlDtl(beans, user));
			}
			if (sb.length() < PricerConstant.STRING_MAX_LENGTH) {
				sb.append(pricerUploadVerifySB.batchVerifyEffectiveDateInDB(pricerType, beans, user, manufacturerLst));
			}
		} else if (PRICER_TYPE.CONTRACT.getName().equals(pricerType)) {
			if (sb.length() < PricerConstant.STRING_MAX_LENGTH) {
				sb.append(pricerUploadVerifySB.verifyMFRInDB(beans, new Locale(language)));
			}
			if (sb.length() < PricerConstant.STRING_MAX_LENGTH) {
				sb.append(pricerUploadVerifySB.verifyComponedKeyInMtlDtl(beans, user));
			}
			if (sb.length() < PricerConstant.STRING_MAX_LENGTH) {
				sb.append(pricerUploadVerifySB.verifyCstIdctInDB(beans, user, puBean));
			}
			if (sb.length() < PricerConstant.STRING_MAX_LENGTH) {
				sb.append(pricerUploadVerifySB.checkCustomerNumber(beans, new Locale(language)));
			}
			if (sb.length() < PricerConstant.STRING_MAX_LENGTH) {
				sb.append(pricerUploadVerifySB.verifyEffectiveDateForContract(beans, user, manufacturerLst));
			}
		} else if (PRICER_TYPE.PARTMASTER.getName().equals(pricerType)) {
			sb.append(pricerUploadVerifySB.verifyMFRInDB(beans, new Locale(language)));
		}
		return sb.toString();
	}

	public ProgramItemUploadCounterBean beansToDataBase(UploadedFile uploadFile, String action,
			String pricerType, List<PricerUploadTemplateBean> beans, User user, List<Manufacturer> manufacturerLst,
			PricerUploadParametersBean puBean) {
		ProgramItemUploadCounterBean countBean = null;
		if (UPLOAD_ACTION.ADD_UPDATE.getName().equals(action)) {
			if (PRICER_TYPE.NORMAL.getName().equals(pricerType)) {
				countBean = normalProgramPartMasterUploadSB.insertUploadPricer(pricerType, beans, user, manufacturerLst,
						puBean);
			} else if (PRICER_TYPE.PROGRAM.getName().equals(pricerType)) {
				countBean = normalProgramPartMasterUploadSB.insertUploadPricer(pricerType, beans, user, manufacturerLst,
						puBean);
			} else if (PRICER_TYPE.CONTRACT.getName().equals(pricerType)) {
				countBean = contractPriceUploadSB.insertUploadPricer(pricerType, beans, user, manufacturerLst, puBean);
			} else if (PRICER_TYPE.PARTMASTER.getName().equals(pricerType)) {
				countBean = normalProgramPartMasterUploadSB.insertUploadPricer(pricerType, beans, user, manufacturerLst,
						puBean);
			}
		} else if (UPLOAD_ACTION.REMOVE_PRICER_ONLY.getName().equals(action)) {
			if (PRICER_TYPE.NORMAL.getName().equals(pricerType)) {
				countBean = normalProgramPartMasterUploadSB.removeOnlyPricer(pricerType, beans, user);
			} else if (PRICER_TYPE.PROGRAM.getName().equals(pricerType)) {
				countBean = normalProgramPartMasterUploadSB.removeOnlyPricer(pricerType, beans, user);
			} else if (PRICER_TYPE.CONTRACT.getName().equals(pricerType)) {
				countBean = contractPriceUploadSB.removeOnlyPricer(pricerType, beans, user);
			} else if (PRICER_TYPE.PARTMASTER.getName().equals(pricerType)) {
				// Remove (Pricer Only) cannot be applied to Part Master type
				// file
			}
		} else if (UPLOAD_ACTION.REMOVE_PART_AND_ITS_PRICERS.getName().equals(action)) {
			// not implemented
		}
		return countBean;
	}

	private String verifyInDBForRemove(String pricerType, List<PricerUploadTemplateBean> beans, User user,
			List<Manufacturer> manufacturerLst) {
		StringBuffer sb = new StringBuffer("");
		sb.append(pricerUploadVerifySB.pricerMatchingInDB(pricerType, beans, user, manufacturerLst));
		return sb.toString();
	}

	private void sendEmail(String fileName, User user, String errMsg, ProgramItemUploadCounterBean countBean,
			String fromEmailAddr) {

		LOG.info("call e Report sendEmail process");

		MailInfoBean mib = new MailInfoBean();
		List<String> toList = new ArrayList<String>();

		// toList.add("Andy.Hu@AVNET.COM"); // ;user.getEmailAddress()
		toList.add(user.getEmailAddress());

		if (toList.size() > 0) {

			String subject = "price upload offline";
			;
			mib.setMailSubject(subject);
			mib.setMailTo(toList);
			String content = "";
			if (!errMsg.equals("")) {
				content = "Upload file didn't pass the verification, please modify the upload files according to follow information,"
						+ " authentication information is as follows:<br/>";
				content = content + errMsg + "<br/>";
				content += "Best Regards," + "<br/>";
				content += fromEmailAddr + "<br/>";
			}
			mib = EJBCommonSB.sendUploadPricerEmail(null, fileName, user, countBean, fromEmailAddr, mib, content, "pricerUploadOffline");
			try {
				mailUtilsSB.sendAttachedMail(mib);
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Send email failed! For file:"+fileName+", error message: "+errMsg+", exception message : "+e.getMessage(), e);
			}

			LOG.info("call sendEmail end");
		} else {
			LOG.info("call sendEmail end, because receiptor is empty!");
		}
	}
}
