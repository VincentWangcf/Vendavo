package com.avnet.emasia.webquote.web.pricerupload;

import static java.util.stream.Collectors.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timer;

import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logmanager.Level;
import org.jboss.msc.service.ServiceController;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadParametersBean;
import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;
import com.avnet.emasia.webquote.commodity.bean.ProgramItemUploadCounterBean;
import com.avnet.emasia.webquote.commodity.constant.PRICER_TYPE;
import com.avnet.emasia.webquote.commodity.constant.PricerConstant;
import com.avnet.emasia.webquote.commodity.constant.UPLOAD_ACTION;
import com.avnet.emasia.webquote.commodity.ejb.ContractPriceUploadSB;
import com.avnet.emasia.webquote.commodity.ejb.PricerOfflineSB;
import com.avnet.emasia.webquote.commodity.util.PricerTypeAndUkComparator;
import com.avnet.emasia.webquote.commodity.util.PricerUtils;
import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.PricerOffline;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.user.ejb.BizUnitSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.AllPricerProcessSheet;
import com.avnet.emasia.webquote.utilites.web.util.Excel20007Reader;
import com.avnet.emasia.webquote.utilites.web.util.PricerUploadHelper;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.schedule.IScheduleTask;
import com.avnet.emasia.webquote.web.quote.job.EnvironmentService;

@Stateless
public class OfflinePriceUploadTask extends PricerUploadCommonMB implements IScheduleTask {
	private static final long serialVersionUID = -8325169248543071679L;

	private static final Logger LOG = Logger.getLogger(OfflinePriceUploadTask.class.getName());

	@EJB
	SystemCodeMaintenanceSB sysCodeMaintSB;

	@EJB
	ContractPriceUploadSB contractPriceUploadSB;
	@EJB
	PricerOfflineSB pricerOfflineSB;
	@EJB
	UserSB userSB;
	@EJB
	ManufacturerSB manufacturerSB;

	@EJB
	protected EJBCommonSB ejbCommonSB;
	@EJB
	BizUnitSB bizUnitSB;
	
	@Override
	@Asynchronous
	@TransactionTimeout(value=10, unit=TimeUnit.HOURS)
	public void executeTask(Timer timer) {
		LOG.info("Offline Price Upload job beginning...");
		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();
		String pricerUploadOfflinePath = sysCodeMaintSB.getPricerUploadOfflinePath();
		//need delete
		//
		try { 
           String address = InetAddress.getLocalHost().getHostName().toString();
           if("cis2115vmts".equalsIgnoreCase(address)) { 
        	   pricerUploadOfflinePath = "C:\\david\\sharefolder\\tempd"+File.separator;
           }
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
		LOG.info("The Path is[" + pricerUploadOfflinePath + "]");
		try {
			List<PricerOffline> pricerOfflines = pricerOfflineSB.findPricerOffline();
					//pricerOfflineSB.test_findPricerOfflineById(10289301061L);
			// added log by damon
			if (pricerOfflines == null) {
				LOG.info("pricerOfflines is null,get no result from table PRICER_OFFLINE.");
			} else {
				LOG.info("The length of PricerOfflines is " + pricerOfflines.size());
			}

			for (PricerOffline bean : pricerOfflines) {
				LOG.info("Action: " + bean.getAction() + "========" + "FileName: " + bean.getFileName());
				String bizUnitName = bean.getBizUnit();
				String fromEmailAddr = sysCodeMaintSB.getBuzinessProperty("OFFLINE_REPORT_FROM", bizUnitName);
				String fileName = bean.getFileName();
				String action = bean.getAction();
				if (QuoteUtil.isEmpty(action))
					continue;
				User user = userSB.findByEmployeeIdLazily(bean.getEmployeeId());
				//reset locale for keep the same.
				user.setUserLocale(new ResourceMB().getDefaultLocaleAsString());
				List<PricerUploadTemplateBean> beans = this.getUploadOfflineBeans(pricerUploadOfflinePath, fileName);
				//for attention
				pricerOfflineSB.setSendFlag(bean, true);
				//bean.setSendFlag(true);
				try {
					PRICER_TYPE[] pricerTypes = PRICER_TYPE.values();
					String errMsg = "";
					HashMap<String, List<PricerUploadTemplateBean>> hashMapList = new HashMap<String, List<PricerUploadTemplateBean>>();
					HashMap<String, PricerUploadParametersBean> hashMapPubean = new HashMap<String, PricerUploadParametersBean>();
					HashMap<String, List<Manufacturer>> hashMapManufacturerLst = new HashMap<String, List<Manufacturer>>();

					/**************/
					for (int i = 0; i < pricerTypes.length; i++) {
						PRICER_TYPE pricertType = pricerTypes[i];
						String pricerType = pricertType.getName(); // fix

						List<PricerUploadTemplateBean> currentBeans = PricerUploadHelper
								.getUploadBeansByPricerType(beans, pricerType);

						if (currentBeans == null || currentBeans.size() == 0) {// Without

							continue;
						}
						List<Manufacturer> manufacturers = manufacturerSB
								.findManufacturerByBizUnit(user.getDefaultBizUnit());
						hashMapList.put(pricerType, currentBeans);
						List<Manufacturer> manufacturerLst = PricerUtils.getMfrInUploadFile(currentBeans,
								manufacturers);
						hashMapManufacturerLst.put(pricerType, manufacturerLst);
						PricerUploadParametersBean puBean = new PricerUploadParametersBean();
						if (UPLOAD_ACTION.ADD_UPDATE.getName().equals(action)) {
							errMsg = errMsg + this.verifyInDBForAddUpdate(pricerType, currentBeans, user, puBean,
									manufacturerLst);
						} else if (UPLOAD_ACTION.REMOVE_PRICER_ONLY.getName().equals(action)) {
							errMsg = errMsg + this.verifyInDBForRemove(pricerType, currentBeans, user, manufacturerLst);
						}
						hashMapPubean.put(pricerType, puBean);
					}
					end = System.currentTimeMillis();
					LOG.info("verifyInDBForAddUpdate end,takes " + (end - start) + " ms");
					if (PricerUploadHelper.isHaveErrorMsg(errMsg)) {
						sendEmail(bean.getCreatedOn(), fileName, user, errMsg, null, fromEmailAddr);
						continue;
					}
					PriceUploadStrategy uploadStrategy = new PriceUploadStrategy();
					UPLOAD_ACTION actionEnum = UPLOAD_ACTION.getActionByName(action);
					
					
					Map<String, Set<String>> currencyMapRegion = bizUnitSB.findAll().stream().
							collect(toMap(BizUnit::getName, BizUnit::getAllowCurrencies)); ;
					/**** valid the ACCESS TO DATA ****/
					for (String pricerType : hashMapList.keySet()) {
						LOG.info(pricerType + " valid the ACCESS TO DATA begin.");
						List<PricerUploadTemplateBean> currentBeans = hashMapList.get(pricerType);
						if (currentBeans == null || currentBeans.size() == 0)
							continue;
						
						errMsg = errMsg + uploadStrategy.verifyDataAccess(pricerType, user, currentBeans, actionEnum, currencyMapRegion);
						end = System.currentTimeMillis();
						LOG.info(pricerType + " verifyDataAccess end,takes " + (end - start) + " ms");
					}
					
					if (PricerUploadHelper.isHaveErrorMsg(errMsg)) {
						sendEmail(bean.getCreatedOn(), fileName, user, errMsg, null, fromEmailAddr);
					} else {
						ProgramItemUploadCounterBean countBean = new ProgramItemUploadCounterBean();
						for (int k = 0; k < pricerTypes.length; k++) {
							PRICER_TYPE pricertType = pricerTypes[k];
							String pricerType = pricertType.getName();
							List<PricerUploadTemplateBean> currentBeans = hashMapList.get(pricerType);

							if (currentBeans == null || currentBeans.size() == 0)
								continue;

							PricerUploadParametersBean puBean = hashMapPubean.get(pricerType);
							List<Manufacturer> manufacturerLst = hashMapManufacturerLst.get(pricerType);
							this.beansToDataBase(countBean, action, pricerType, currentBeans, user, puBean,
									manufacturerLst);
							end = System.currentTimeMillis();
							LOG.info("upload " + pricerType + " end,takes " + (end - start) + " ms proceed record size "
									+ currentBeans.size());
						}
						this.savePricerUploadSummary2Db(countBean, fileName, user);
						LOG.info("upload " + fileName + " end,takes " + (end - start) + " ms proceed record size "
								+ beans.size());
						sendEmail(bean.getCreatedOn(), fileName, user, "", countBean, fromEmailAddr);
					}
					pricerOfflineSB.markProcessedFile(pricerUploadOfflinePath, fileName);
					
				} catch (Exception e) {
					sendSystemEmail(bean.getCreatedOn(), fileName, user, e.toString(), fromEmailAddr);
					Object[] object = new Object[] { fileName, fromEmailAddr, user.getEmployeeId(), user.getTeam(),
							bean.getCreatedOn(), start, e.getMessage() };
					LOG.log(Level.SEVERE,
							MessageFormatorUtil.getParameterizedString(
									"failed for file {0} from {1} by user {2} , team {3} on date {4} started at {5}, message {6}",
									object),e);
				} 
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed at start uploading at time : " + start + ", message" + e.getMessage(), e);

		}
		try {
			pricerOfflineSB.deleteFile(pricerUploadOfflinePath);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed at start uploading at time : " + start + ", message" + e.getMessage(), e);
		}
		end = System.currentTimeMillis();
		LOG.info("Offline pricer upload end,takes " + (end - start) + " ms");
	}

	public List<PricerUploadTemplateBean> getUploadOfflineBeans(String pricerUploadOfflinePath, String fileName) {

		File f = new File(pricerUploadOfflinePath + fileName);
		FileInputStream ois;
		List<PricerUploadTemplateBean> beans = null;
		try {
			ois = new FileInputStream(f);
			Excel20007Reader reader = new Excel20007Reader(ois, 0, new AllPricerProcessSheet());
			beans = reader.excel2Beans(new PricerTypeAndUkComparator());
		} catch (FileNotFoundException e) {
			LOG.log(Level.SEVERE,
					"failed as file name " + fileName + "or path " + pricerUploadOfflinePath + "is not coorect", e);
		}
		return beans;
	}

}
