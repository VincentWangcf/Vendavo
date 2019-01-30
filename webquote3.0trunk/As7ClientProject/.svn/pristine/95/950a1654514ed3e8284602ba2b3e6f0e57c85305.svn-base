package com.avnet.emasia.webquote.masterData.ejb.client;

import java.util.Properties;

import javax.naming.NamingException;

import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.StatelessEJBLocator;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;

import com.avnet.emasia.webquote.masterData.ejb.job.CustomerLoadingJob;
import com.avnet.emasia.webquote.masterData.ejb.job.DrmsAgpReaLoadingJob;
import com.avnet.emasia.webquote.masterData.ejb.job.DrmsLoadingJob;
import com.avnet.emasia.webquote.masterData.ejb.job.FreeStockLoadingJob;
import com.avnet.emasia.webquote.masterData.ejb.job.MaterialLoadingJob;
import com.avnet.emasia.webquote.masterData.ejb.job.MfrLoadingJob;
import com.avnet.emasia.webquote.masterData.ejb.job.PosLoadingJob;
import com.avnet.emasia.webquote.masterData.ejb.job.ReferenceMarginPatch;
import com.avnet.emasia.webquote.masterData.ejb.job.SalesCostFlagToSapJob;
import com.avnet.emasia.webquote.masterData.ejb.job.SalesCostPricerBalQtyUpdateLoadingJob;
import com.avnet.emasia.webquote.masterData.ejb.job.SalesCostPricerToSapJob;
import com.avnet.emasia.webquote.masterData.ejb.job.SalesOrderLoadingJob;
import com.avnet.emasia.webquote.masterData.ejb.job.SalesOrgLoadingJob;
import com.avnet.emasia.webquote.masterData.ejb.job.SapDpaCustomerLoadingJob;
import com.avnet.emasia.webquote.masterData.ejb.job.SpecialCustomerLoadingJob;
import com.avnet.emasia.webquote.masterData.ejb.remote.CustomerRemote;
import com.avnet.emasia.webquote.masterData.ejb.remote.DrmsAgpReaRemote;
import com.avnet.emasia.webquote.masterData.ejb.remote.DrmsRemote;
import com.avnet.emasia.webquote.masterData.ejb.remote.FreeStockRemote;
import com.avnet.emasia.webquote.masterData.ejb.remote.MaterialRemote;
import com.avnet.emasia.webquote.masterData.ejb.remote.MfrRemote;
import com.avnet.emasia.webquote.masterData.ejb.remote.PosRemote;
import com.avnet.emasia.webquote.masterData.ejb.remote.ReferenceMarginPatchRemote;
import com.avnet.emasia.webquote.masterData.ejb.remote.SalesCostFlagToSapRemote;
import com.avnet.emasia.webquote.masterData.ejb.remote.SalesCostPricerBalQtyUpdateRemote;
import com.avnet.emasia.webquote.masterData.ejb.remote.SalesCostPricerToSapRemote;
import com.avnet.emasia.webquote.masterData.ejb.remote.SalesOrderRemote;
import com.avnet.emasia.webquote.masterData.ejb.remote.SalesOrgRemote;
import com.avnet.emasia.webquote.masterData.ejb.remote.SapDpaCustomerRemote;
import com.avnet.emasia.webquote.masterData.ejb.remote.SpecialCustomerRemote;


public class RemoteEJBClientJp {
	
	public static final String CLIENT_ENDPOINT = "client-endpoint";

	public static final String SSL_ENABLED = "false";
	
	public static final String SASL_POLICY_NOANONYMOUS = "true";
	
	
	//prod
	public static final String REMOTE_HOST = "alero1";
	public static final String REMOTE_PORT = "21182";
	
	//stage
	//public static final String REMOTE_HOST = "logan1";
	//public static final String REMOTE_PORT = "21182";
	
	//test
//	public static final String REMOTE_HOST = "albany1";
//	public static final String REMOTE_PORT = "21182";
	
	//dev
	//public static final String REMOTE_HOST = "Anaheim1";
	//public static final String REMOTE_PORT = "21295";
	
	public static final String USERNAME = "controlm";
	
	public static final String PASSWORD = "controlm123";
	

	public static void main(String[] args) throws Exception {
//		invokeSalesOrgEJB();
//		invokeSalesOrderEJB();
//		invokePosEJB();
//		invokeMfrEJB();
//		invokeMaterialEJB();
		invokeFreeStockEJB();
//		invokeCustomerEJB();
//		invokeDrmsEJB();
//		invokeDrmsAgpReaEJB();
//		invokeReferenceMarginPatchEJB();
//		invokeSpecialCustomerEJB();
	}
	
	private static EJBClientConfiguration getEJBClientConfiguration() {
		Properties prop = new Properties();
		prop.put("endpoint.name", CLIENT_ENDPOINT);
		prop.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", SSL_ENABLED);
		prop.put("remote.connections", "default");
		prop.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", SASL_POLICY_NOANONYMOUS);
		prop.put("remote.connection.default.host", REMOTE_HOST);
		prop.put("remote.connection.default.port", REMOTE_PORT);
		prop.put("remote.connection.default.username", USERNAME);
		prop.put("remote.connection.default.password", PASSWORD);
		return new PropertiesBasedEJBClientConfiguration(prop);
	}

	public static int invokeSalesOrgEJB() throws NamingException {

		System.out.println("Calll invokeSalesOrgEJB start");
		int returnInt = 1; 
		final SalesOrgRemote ejb = lookupRemoteSaleOrgEJB();
		returnInt = ejb.executeTask();
		System.out.println("Calll invokeSalesOrgEJB end");
		return returnInt;
	}

	public static int invokeSalesOrderEJB() throws NamingException {

		System.out.println("Calll invokeSalesOrderEJB start");
		int returnInt = 1; 
		final SalesOrderRemote ejb = lookupRemoteSalesOrderEJB();
		returnInt = ejb.executeTask();
		System.out.println("Calll invokeSalesOrderEJB end");
		return returnInt;
	}
	public static int invokePosEJB() throws NamingException {

		System.out.println("Calll invokePosEJB start");
		int returnInt = 1; 
		final PosRemote ejb = lookupRemotePosEJB();
		returnInt = ejb.executeTask();
		System.out.println("Calll invokePosEJB end");
		return returnInt;
	}
	public static int invokeMfrEJB() throws NamingException {

		System.out.println("Calll invokeMfrEJB start");
		int returnInt = 1; 
		final MfrRemote ejb = lookupRemoteMfrEJB();
		returnInt = ejb.executeTask();
		System.out.println("Calll invokeMfrEJB end");
		return returnInt;
	}
	public static int invokeMaterialEJB() throws NamingException {

		System.out.println("Calll invokeMaterialEJB start");
		int returnInt = 1; 
		final MaterialRemote ejb = lookupRemoteMaterialEJB();
		returnInt = ejb.executeTask();
		System.out.println("Calll invokeMaterialEJB end");
		return returnInt;
	}
	public static int invokeFreeStockEJB() throws NamingException {

		System.out.println("Calll invokeFreeStockEJB start");
		int returnInt = 1; 
		final FreeStockRemote ejb = lookupRemoteFreeStockEJB();
		returnInt = ejb.executeTask();
		System.out.println("Calll invokeFreeStockEJB end");
		return returnInt;
	}
	
	public static int invokeDrmsEJB() throws NamingException {

		System.out.println("Calll invokeDrmsEJB start");
		int returnInt = 1; 
		final DrmsRemote ejb = lookupRemoteDrmsEJB();
		returnInt = ejb.executeTask();
		System.out.println("Calll invokeDrmsEJB end");
		return returnInt;
	}
	
	public static int invokeCustomerEJB() throws NamingException {

		System.out.println("Calll invokeCustomerEJB start");
		int returnInt = 1; 
		final CustomerRemote ejb = lookupRemoteCustomerEJB();
		returnInt = ejb.executeTask();
		System.out.println("Calll invokeCustomerEJB end");
		return returnInt;
	}
	
	public static int invokeDrmsAgpReaEJB() throws NamingException {

		System.out.println("Calll invokeDrmsAgpReaEJB start");
		int returnInt = 1; 
		final DrmsAgpReaRemote ejb = lookupRemoteDrmsApgReaEJB();
		returnInt = ejb.executeTask();
		System.out.println("Calll invokeDrmsAgpReaEJB end");
		return returnInt;
	}
	
	public static int invokeReferenceMarginPatchEJB() throws NamingException {

		System.out.println("Calll invokeReferenceMarginPatchEJB start");
		int returnInt = 1; 
		final ReferenceMarginPatchRemote ejb = lookupRemoteReferenceMarginPatchaEJB();
		returnInt = ejb.executeTask();
		System.out.println("Calll invokeReferenceMarginPatchEJB end");
		return returnInt;
	}
	
	
	public static int invokeSpecialCustomerEJB() throws NamingException {

		System.out.println("Calll invokeSpecialCustomerEJB start");
		int returnInt = 1; 
		final SpecialCustomerRemote ejb = lookupRemoteSpecialCustomerEJB();
		returnInt = ejb.executeTask();
		System.out.println("Calll invokeSpecialCustomerEJB end");
		return returnInt;
	}
	
	
	public static int invokeSapDpaCustomerEJB() throws NamingException {

		System.out.println("Calll invokeSapDpaCustomerEJB start");
		int returnInt = 1; 
		final SapDpaCustomerRemote ejb = lookupRemoteSapDpaCustomerEJB();
		returnInt = ejb.executeTask();
		System.out.println("Calll invokeSapDpaCustomerEJB end");
		return returnInt;
	}
	
	private static SapDpaCustomerRemote lookupRemoteSapDpaCustomerEJB() {
		EJBClientConfiguration ejbClientCfg = getEJBClientConfiguration();
		final ContextSelector<EJBClientContext> ejbClientContextSelector = new ConfigBasedEJBClientContextSelector(ejbClientCfg);
		EJBClientContext.setSelector(ejbClientContextSelector);
		final String appName = "WebQuote_jpEar";
		final String moduleName = "WQ_EJB_MasterData";
		final String distinctName = "";
		final String beanName = SapDpaCustomerLoadingJob.class.getSimpleName();
		final String viewClassName = SapDpaCustomerRemote.class.getName();
		System.out.println("Looking EJB via JNDI ");
		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
		SapDpaCustomerRemote ejbService = (SapDpaCustomerRemote)getEjbService(SapDpaCustomerRemote.class, appName,moduleName, beanName, distinctName);               
		return ejbService;
	}

	public static int invokeSalesCostPricerToSapEJB() throws NamingException {

		System.out.println("Calll invokeSalesCostPricerToSapEJB start");
		int returnInt = 1; 
		final SalesCostPricerToSapRemote ejb = lookupRemoteSalesCostPricerToSapEJB();
		returnInt = ejb.executeTask();
		System.out.println("Calll invokeSalesCostPricerToSapEJB end");
		return returnInt;
	}
	
	private static SalesCostPricerToSapRemote lookupRemoteSalesCostPricerToSapEJB() {
		EJBClientConfiguration ejbClientCfg = getEJBClientConfiguration();
		final ContextSelector<EJBClientContext> ejbClientContextSelector = new ConfigBasedEJBClientContextSelector(ejbClientCfg);
		EJBClientContext.setSelector(ejbClientContextSelector);
		final String appName = "WebQuote_jpEar";
		final String moduleName = "WQ_EJB_MasterData";
		final String distinctName = "";
		final String beanName = SalesCostPricerToSapJob.class.getSimpleName();
		final String viewClassName = SalesCostPricerToSapRemote.class.getName();
		System.out.println("Looking EJB via JNDI ");
		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
		SalesCostPricerToSapRemote ejbService = (SalesCostPricerToSapRemote)getEjbService(SalesCostPricerToSapRemote.class, appName,moduleName, beanName, distinctName);               
		return ejbService;
	}

	public static int invokeSalesCostFlagToSapEJB() throws NamingException {

		System.out.println("Calll invokeSalesCostFlagToSapEJB start");
		int returnInt = 1; 
		final SalesCostFlagToSapRemote ejb = lookupRemoteSalesCostFlagToSapEJB();
		returnInt = ejb.executeTask();
		System.out.println("Calll invokeSalesCostFlagToSapEJB end");
		return returnInt;
	}
	
	private static SalesCostFlagToSapRemote lookupRemoteSalesCostFlagToSapEJB() {
		EJBClientConfiguration ejbClientCfg = getEJBClientConfiguration();
		final ContextSelector<EJBClientContext> ejbClientContextSelector = new ConfigBasedEJBClientContextSelector(ejbClientCfg);
		EJBClientContext.setSelector(ejbClientContextSelector);
		final String appName = "WebQuote_jpEar";
		final String moduleName = "WQ_EJB_MasterData";
		final String distinctName = "";
		final String beanName = SalesCostFlagToSapJob.class.getSimpleName();
		final String viewClassName = SalesCostFlagToSapRemote.class.getName();
		System.out.println("Looking EJB via JNDI ");
		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
		SalesCostFlagToSapRemote ejbService = (SalesCostFlagToSapRemote)getEjbService(SalesCostFlagToSapRemote.class, appName,moduleName, beanName, distinctName);               
		return ejbService;
	}

	public static int invokeSalesCostPricerBalQtyUpdateEJB() throws NamingException {

		System.out.println("Calll invokeSalesCostPricerBalQtyUpdateEJB start");
		int returnInt = 1; 
		final SalesCostPricerBalQtyUpdateRemote ejb = lookupRemoteSalesCostPricerBalQtyUpdateEJB();
		returnInt = ejb.executeTask();
		System.out.println("Calll invokeSalesCostPricerBalQtyUpdateEJB end");
		return returnInt;
	}
	
	
	private static SalesCostPricerBalQtyUpdateRemote lookupRemoteSalesCostPricerBalQtyUpdateEJB() {
		EJBClientConfiguration ejbClientCfg = getEJBClientConfiguration();
		final ContextSelector<EJBClientContext> ejbClientContextSelector = new ConfigBasedEJBClientContextSelector(ejbClientCfg);
		EJBClientContext.setSelector(ejbClientContextSelector);
		final String appName = "WebQuote_jpEar";
		final String moduleName = "WQ_EJB_MasterData";
		final String distinctName = "";
		final String beanName = SalesCostPricerBalQtyUpdateLoadingJob.class.getSimpleName();
		final String viewClassName = SalesCostPricerBalQtyUpdateRemote.class.getName();
		System.out.println("Looking EJB via JNDI ");
		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
		SalesCostPricerBalQtyUpdateRemote ejbService = (SalesCostPricerBalQtyUpdateRemote)getEjbService(SalesCostPricerBalQtyUpdateRemote.class, appName,moduleName, beanName, distinctName);               
		return ejbService;
	}
	
	
	private static SalesOrgRemote lookupRemoteSaleOrgEJB() throws NamingException {
		EJBClientConfiguration ejbClientCfg = getEJBClientConfiguration();
		final ContextSelector<EJBClientContext> ejbClientContextSelector = new ConfigBasedEJBClientContextSelector(ejbClientCfg);
		EJBClientContext.setSelector(ejbClientContextSelector);
		final String appName = "WebQuote_jpEar";
		final String moduleName = "WQ_EJB_MasterData";
		final String distinctName = "";
		final String beanName = SalesOrgLoadingJob.class.getSimpleName();
		final String viewClassName = SalesOrgRemote.class.getName();
		System.out.println("Looking EJB via JNDI ");
		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
		SalesOrgRemote ejbService = (SalesOrgRemote)getEjbService(SalesOrgRemote.class, appName,moduleName, beanName, distinctName);               
		return ejbService;
	}

	private static SalesOrderRemote lookupRemoteSalesOrderEJB() throws NamingException {
		EJBClientConfiguration ejbClientCfg = getEJBClientConfiguration();
		final ContextSelector<EJBClientContext> ejbClientContextSelector = new ConfigBasedEJBClientContextSelector(ejbClientCfg);
		EJBClientContext.setSelector(ejbClientContextSelector);
		final String appName = "WebQuote_jpEar";
		final String moduleName = "WQ_EJB_MasterData";
		final String distinctName = "";
		final String beanName = SalesOrderLoadingJob.class.getSimpleName();
		final String viewClassName = SalesOrderRemote.class.getName();
		System.out.println("Looking EJB via JNDI ");
		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
		SalesOrderRemote ejbService = (SalesOrderRemote)getEjbService(SalesOrderRemote.class, appName,moduleName, beanName, distinctName);               
		return ejbService;
	}
	
	private static PosRemote lookupRemotePosEJB() throws NamingException {
		EJBClientConfiguration ejbClientCfg = getEJBClientConfiguration();
		final ContextSelector<EJBClientContext> ejbClientContextSelector = new ConfigBasedEJBClientContextSelector(ejbClientCfg);
		EJBClientContext.setSelector(ejbClientContextSelector);
		final String appName = "WebQuote_jpEar";
		final String moduleName = "WQ_EJB_MasterData";
		final String distinctName = "";
		final String beanName = PosLoadingJob.class.getSimpleName();
		final String viewClassName = PosRemote.class.getName();
		System.out.println("Looking EJB via JNDI ");
		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
		PosRemote ejbService = (PosRemote)getEjbService(PosRemote.class, appName,moduleName, beanName, distinctName);               
		return ejbService;
	}
	
	private static MfrRemote lookupRemoteMfrEJB() throws NamingException {
		EJBClientConfiguration ejbClientCfg = getEJBClientConfiguration();
		final ContextSelector<EJBClientContext> ejbClientContextSelector = new ConfigBasedEJBClientContextSelector(ejbClientCfg);
		EJBClientContext.setSelector(ejbClientContextSelector);
		final String appName = "WebQuote_jpEar";
		final String moduleName = "WQ_EJB_MasterData";
		final String distinctName = "";
		final String beanName = MfrLoadingJob.class.getSimpleName();
		final String viewClassName = MfrRemote.class.getName();
		System.out.println("Looking EJB via JNDI ");
		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
		MfrRemote ejbService = (MfrRemote)getEjbService(MfrRemote.class, appName,moduleName, beanName, distinctName);               
		return ejbService;
	}
	
	private static MaterialRemote lookupRemoteMaterialEJB() throws NamingException {
		EJBClientConfiguration ejbClientCfg = getEJBClientConfiguration();
		final ContextSelector<EJBClientContext> ejbClientContextSelector = new ConfigBasedEJBClientContextSelector(ejbClientCfg);
		EJBClientContext.setSelector(ejbClientContextSelector);
		final String appName = "WebQuote_jpEar";
		final String moduleName = "WQ_EJB_MasterData";
		final String distinctName = "";
		final String beanName = MaterialLoadingJob.class.getSimpleName();
		final String viewClassName = MaterialRemote.class.getName();
		System.out.println("Looking EJB via JNDI ");
		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
		MaterialRemote ejbService = (MaterialRemote)getEjbService(MaterialRemote.class, appName,moduleName, beanName, distinctName);               
		return ejbService;
	}
	
	
	private static FreeStockRemote lookupRemoteFreeStockEJB() throws NamingException {
		EJBClientConfiguration ejbClientCfg = getEJBClientConfiguration();
		final ContextSelector<EJBClientContext> ejbClientContextSelector = new ConfigBasedEJBClientContextSelector(ejbClientCfg);
		EJBClientContext.setSelector(ejbClientContextSelector); 
		final String appName = "WebQuote_jpEar";
		final String moduleName = "WQ_EJB_MasterData";
		final String distinctName = "";
		final String beanName = FreeStockLoadingJob.class.getSimpleName();
		final String viewClassName = FreeStockRemote.class.getName();
		System.out.println("Looking EJB via JNDI ");
		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
		FreeStockRemote ejbService = (FreeStockRemote)getEjbService(FreeStockRemote.class, appName,moduleName, beanName, distinctName);               
		return ejbService;
	}
	
	
	private static DrmsRemote lookupRemoteDrmsEJB() throws NamingException {
		EJBClientConfiguration ejbClientCfg = getEJBClientConfiguration();
		final ContextSelector<EJBClientContext> ejbClientContextSelector = new ConfigBasedEJBClientContextSelector(ejbClientCfg);
		EJBClientContext.setSelector(ejbClientContextSelector);              
		final String appName = "WebQuote_jpEar";
		final String moduleName = "WQ_EJB_MasterData";
		final String distinctName = "";
		final String beanName = DrmsLoadingJob.class.getSimpleName();
		final String viewClassName = DrmsRemote.class.getName();
		System.out.println("Looking EJB via JNDI ");
		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
		DrmsRemote ejbService = (DrmsRemote)getEjbService(DrmsRemote.class, appName,moduleName, beanName, distinctName);               
		return ejbService;
		

	}

	private static CustomerRemote lookupRemoteCustomerEJB() throws NamingException {
		EJBClientConfiguration ejbClientCfg = getEJBClientConfiguration();
		final ContextSelector<EJBClientContext> ejbClientContextSelector = new ConfigBasedEJBClientContextSelector(ejbClientCfg);
		EJBClientContext.setSelector(ejbClientContextSelector);
		final String appName = "WebQuote_jpEar";
		final String moduleName = "WQ_EJB_MasterData";
		final String distinctName = "";
		final String beanName = CustomerLoadingJob.class.getSimpleName();
		final String viewClassName = CustomerRemote.class.getName();
		System.out.println("Looking EJB via JNDI ");
		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
		CustomerRemote ejbService = (CustomerRemote)getEjbService(CustomerRemote.class, appName,moduleName, beanName, distinctName);               
		return ejbService;
	}
	
	private static DrmsAgpReaRemote lookupRemoteDrmsApgReaEJB() throws NamingException {
		EJBClientConfiguration ejbClientCfg = getEJBClientConfiguration();
		final ContextSelector<EJBClientContext> ejbClientContextSelector = new ConfigBasedEJBClientContextSelector(ejbClientCfg);
		EJBClientContext.setSelector(ejbClientContextSelector);
		final String appName = "WebQuote_jpEar";
		final String moduleName = "WQ_EJB_MasterData";
		final String distinctName = "";
		final String beanName = DrmsAgpReaLoadingJob.class.getSimpleName();
		final String viewClassName = DrmsAgpReaRemote.class.getName();
		System.out.println("Looking EJB via JNDI ");
		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
		DrmsAgpReaRemote ejbService = (DrmsAgpReaRemote)getEjbService(DrmsAgpReaRemote.class, appName,moduleName, beanName, distinctName);               
		return ejbService;
	}
	
	
	private static ReferenceMarginPatchRemote lookupRemoteReferenceMarginPatchaEJB() throws NamingException {
		EJBClientConfiguration ejbClientCfg = getEJBClientConfiguration();
		final ContextSelector<EJBClientContext> ejbClientContextSelector = new ConfigBasedEJBClientContextSelector(ejbClientCfg);
		EJBClientContext.setSelector(ejbClientContextSelector);
		final String appName = "WebQuote_jpEar";
		final String moduleName = "WQ_EJB_MasterData";
		final String distinctName = "";
		final String beanName = ReferenceMarginPatch.class.getSimpleName();
		final String viewClassName = ReferenceMarginPatchRemote.class.getName();
		System.out.println("Looking EJB via JNDI ");
		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
		ReferenceMarginPatchRemote ejbService = (ReferenceMarginPatchRemote)getEjbService(ReferenceMarginPatchRemote.class, appName,moduleName, beanName, distinctName);               
		return ejbService;

	}
	
	
	private static SpecialCustomerRemote lookupRemoteSpecialCustomerEJB() throws NamingException {
		EJBClientConfiguration ejbClientCfg = getEJBClientConfiguration();
		final ContextSelector<EJBClientContext> ejbClientContextSelector = new ConfigBasedEJBClientContextSelector(ejbClientCfg);
		EJBClientContext.setSelector(ejbClientContextSelector);
		final String appName = "WebQuote_jpEar";
		final String moduleName = "WQ_EJB_MasterData";
		final String distinctName = "";
		final String beanName = SpecialCustomerLoadingJob.class.getSimpleName();
		final String viewClassName = SpecialCustomerRemote.class.getName();
		System.out.println("Looking EJB via JNDI ");
		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
		SpecialCustomerRemote ejbService = (SpecialCustomerRemote)getEjbService(SpecialCustomerRemote.class, appName,moduleName, beanName, distinctName);               
		return ejbService;
	}
	
	private static Object getEjbService(Class viewType, String appName, String moduleName, String beanName, String distinctName) {
		StatelessEJBLocator locator = new StatelessEJBLocator(viewType, appName,moduleName, beanName, distinctName);               
		return org.jboss.ejb.client.EJBClient.createProxy(locator);
	}

}
