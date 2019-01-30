package com.avnet.emasia.webquote.utilites.web.schedule;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.avnet.emasia.webquote.cache.DesignLocationCacheManager;
import com.avnet.emasia.webquote.cache.MfrDetailCacheManager;
import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.AppProperty;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.City;
import com.avnet.emasia.webquote.entity.CostIndicator;
import com.avnet.emasia.webquote.entity.Country;
import com.avnet.emasia.webquote.entity.DesignLocation;
import com.avnet.emasia.webquote.entity.LocaleMaster;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.ManufacturerDetail;
import com.avnet.emasia.webquote.entity.Plant;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.SalesOrg;
import com.avnet.emasia.webquote.entity.SystemCodeMaintenance;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.entity.ValidationRule;
import com.avnet.emasia.webquote.quote.ejb.CitySB;
import com.avnet.emasia.webquote.quote.ejb.CostIndicatorSB;
import com.avnet.emasia.webquote.quote.ejb.CountrySB;
import com.avnet.emasia.webquote.quote.ejb.CustomerSB;
import com.avnet.emasia.webquote.quote.ejb.DesignLocationSB;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.ValidationRuleSB;
import com.avnet.emasia.webquote.quote.strategy.factory.DrmsValidationUpdateStrategyFactory;
import com.avnet.emasia.webquote.user.ejb.ActiveSessionSB;
import com.avnet.emasia.webquote.user.ejb.ApplicationSB;
import com.avnet.emasia.webquote.user.ejb.BizUnitSB;
import com.avnet.emasia.webquote.user.ejb.LocaleMessagesSB;
import com.avnet.emasia.webquote.user.ejb.PlantSB;
import com.avnet.emasia.webquote.user.ejb.RoleSB;
import com.avnet.emasia.webquote.user.ejb.SalesOrgSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilities.DBControl;
import com.avnet.emasia.webquote.utilities.DBResourceBundleReloadEvent;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.schedule.IScheduleTask;
import com.avnet.emasia.webquote.web.quote.cache.AccountGroupCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.ApplicationCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.AvnetQuoteCentreCommentCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.BizUnitCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.CityCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.CostIndicatorCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.CountryCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.DesignInCatCacheManager;
//import com.avnet.emasia.webquote.web.quote.cache.DesignLocationCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.EnquirySegmentCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.MfrCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.PlantCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.QtyIndicatorCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.QuoteTypeCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.RoleCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.SalesOrgCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.TermsAndConditionsCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.UserCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.ValidationRuleCacheManager;
import com.avnet.emasia.webquote.web.quote.vo.AvnetQuoteCentreCommentVO;
import com.avnet.emasia.webquote.web.quote.vo.TermsAndConditionsVO;
import com.avnet.emasia.webquote.web.quote.vo.ValidationRuleVO;
import com.avnet.emasia.webquote.web.security.WebSealUsernamePasswordAuthenticationFilter;

@Startup
@Singleton
@DependsOn({"ApplicationSB"})
@LocalBean
//@Stateless
@Deprecated
public class RefreshCacheTask implements IScheduleTask {
	
	private static final Logger LOGGER = Logger.getLogger(RefreshCacheTask.class.getName());

	@EJB
	SystemCodeMaintenanceSB systemCodeMaintenanceSB;
	@EJB
	ManufacturerSB manufacturerSB;
	@EJB
	BizUnitSB bizUnitSB;
	@EJB
	SalesOrgSB salesOrgSB;
	@EJB
	PlantSB plantSB;
	@EJB
	ValidationRuleSB validationRuleSB;
	@EJB
	CustomerSB customerSB;
	@EJB
	CountrySB countrySB;
	@EJB
	CitySB citySB;
	@EJB
	QuoteSB quoteSB;
	@EJB
	UserSB userSB;
	@EJB
	CostIndicatorSB costIndicatorSB;
	@EJB
	RoleSB roleSB;
	@EJB
	ActiveSessionSB activeSessionSB;
	@EJB
	ApplicationSB applicationSB;

	@EJB
	DesignLocationSB designLocationSB;
	
	@EJB
	LocaleMessagesSB localeMessagesSB;
	
	@EJB
	private EJBCommonSB ejbCommonSB;
	
	@EJB
	private SysConfigSB sysConfigSB;
	
	@Inject
	private Event<DBResourceBundleReloadEvent> event;

	private boolean enableBizUnit = true;
	private boolean enableRole = true;
	private boolean enableSalesOrg = true;
	private boolean enablePlant = true;
	private boolean enableCountry = true;
	private boolean enableCity = false;
	private boolean enableCostIndicator = true;
	private boolean enableQtyIndicator = true;
	private boolean enableAvnetQuoteCentreComment = true;
	private boolean enableApplication = true;
	private boolean enableEnquirySegment = true;
	private boolean enableDesignLocation = true;
	private boolean enableMfr = true;
	private boolean enableMfrDetail = true;
	private boolean enableProjectName = true;
	private boolean enableValidationRule = true;
	private boolean enableUser = true;
	private boolean enableDesignInCat = true;
	private boolean enableQuoteType = true;
	private boolean enableLocaleMessageType = true;
	
	
	@PostConstruct
	public void refresh() {
		AccountGroupCacheManager.clear();
		CountryCacheManager.clear();
		CityCacheManager.clear();
		BizUnitCacheManager.clear();
		RoleCacheManager.clear();
		SalesOrgCacheManager.clear();
		PlantCacheManager.clear();
		CostIndicatorCacheManager.clear();
		QtyIndicatorCacheManager.clear();
		AvnetQuoteCentreCommentCacheManager.clear();
		TermsAndConditionsCacheManager.clear();
		ApplicationCacheManager.clear();
		EnquirySegmentCacheManager.clear();
		DesignLocationCacheManager.clear();
		MfrCacheManager.clear();
		MfrDetailCacheManager.clear();
		DesignInCatCacheManager.clear();
		QuoteTypeCacheManager.clear();
		ValidationRuleCacheManager.clear();
		UserCacheManager.clear();
		DrmsValidationUpdateStrategyFactory.getInstance().clearImplMap();
		AppProperty appProperty = new AppProperty();
		try{
			appProperty.setPropKey(DBControl.LOCALE_RELOAD);
			appProperty.setPropValue("true");
			sysConfigSB.updateProperty(appProperty);
			appProperty.setPropKey(DBControl.LOCALE_RELOAD+"_"+Locale.ENGLISH.getLanguage());
			appProperty.setPropValue("true");
			sysConfigSB.updateProperty(appProperty);
			appProperty.setPropKey(DBControl.LOCALE_RELOAD+"_"+Locale.JAPANESE.getLanguage());
			appProperty.setPropValue("true");
			sysConfigSB.updateProperty(appProperty);
			//Fire event to clear the resource bundle cache  
			event.fire(new DBResourceBundleReloadEvent(WebSealUsernamePasswordAuthenticationFilter.getApplicationAssociate()));
		}catch (Exception e) {
			LOGGER.warning("Failed to clear cache due to :"+e);
		}

    	LOGGER.info("initiate the AccountGroup Cache");
    	AccountGroupCacheManager.putAccountGroup("0001", "Sold-to party");
    	AccountGroupCacheManager.putAccountGroup("0002", "Ship-to party");
    	AccountGroupCacheManager.putAccountGroup("0005", "Prospective Customer");
    	AccountGroupCacheManager.putAccountGroup("Z004", "End Customer");
    	LOGGER.info("END OF the AccountGroup Cache, total (accountGroupCache) = " + AccountGroupCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	if(enableCountry){
	    	LOGGER.info("initiate the Country Cache");
	    	
	    	List<Country> countries = countrySB.findAll();
	    	CountryCacheManager.setCountries(countries);
	    	   	
	    	LOGGER.info("END OF initiate the Country Cache, total (countryCache) = " + CountryCacheManager.getCountries().size());
    	}
    	
    	
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	if(enableCity){
	    	LOGGER.info("initiate the City Cache");
	    	
	    	List<City> cities = citySB.findAll();
	    	for(City city : cities)
	    		CityCacheManager.putCity(city);
	    	   	
	    	LOGGER.info("END OF initiate the City Cache, total (countryCache) = N/A");
    	}
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	LOGGER.info("initiate the BizUnit Cache");
    	if(enableBizUnit){  
	    	List<BizUnit> bizUnits = bizUnitSB.findAll();
	    	for(BizUnit bizUnit : bizUnits){
	    		BizUnitCacheManager.putBizUnit(bizUnit);    		
	    	}
    	}	    	
    	LOGGER.info("END OF the BizUnit Cache, total (bizUnitCacheByRegion) = " + BizUnitCacheManager.getSize());		
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	LOGGER.info("initiate the Role Cache");
    	if(enableRole){  
	    	List<Role> roles = roleSB.findAll();
	    	for(Role role : roles){
	    		RoleCacheManager.putRole(role);    		
	    	}
    	}	    	
    	LOGGER.info("END OF the Role Cache, total (roleCacheByRegion) = " + RoleCacheManager.getSize());		
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */    	
    	if(enableSalesOrg){ 
    		LOGGER.info("initiate the SalesOrg Cache");
	    	List<SalesOrg> salesOrgs = salesOrgSB.findAll();
	    	for(SalesOrg salesOrg : salesOrgs){
	    		SalesOrgCacheManager.putSalesOrg(salesOrg);    		
	    	}
    	}
    	LOGGER.info("END OF the SalesOrg Cache, total (salesOrgCacheByRegion) = " + SalesOrgCacheManager.getSize());		
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */    	
    	if(enablePlant){ 
    		LOGGER.info("initiate the Plant Cache");
	    	List<Plant> plants = plantSB.findAll();
	    	for(Plant plant : plants){
	    		PlantCacheManager.putPlant(plant);    		
	    	}
    	}
    	LOGGER.info("END OF the Plant Cache, total (plantCacheByRegion) = " + PlantCacheManager.getSize());	
    	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	LOGGER.info("initiate the CostIndicator Cache");
    	if(enableCostIndicator){
	    	List<CostIndicator> costIndicators = costIndicatorSB.findAllActive();
	    	for(CostIndicator costIndicator : costIndicators){
	    		CostIndicatorCacheManager.putCostIndicator(costIndicator);
	    	}
    	}
    	LOGGER.info("END OF initiate the CostIndicator Cache, total (costIndicatorCache) = " + CostIndicatorCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	LOGGER.info("initiate the QtyIndicator Cache");
    	if(enableQtyIndicator){
    		List<String> qtyIndicators = systemCodeMaintenanceSB.findQuantityIndicator();  	
	    	for(String qtyIndicator : qtyIndicators){
	    		QtyIndicatorCacheManager.putQtyIndicator(qtyIndicator);
	    	}
    	}
    	LOGGER.info("END OF initiate the QtyIndicator Cache, total (qtyIndicatorCache) = " + QtyIndicatorCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	LOGGER.info("initiate the Avnet Quote Centre Comment Cache"); 	
    	if(enableAvnetQuoteCentreComment){     	
    		List<String> avnetQuoteCentreComments = systemCodeMaintenanceSB.findAvnetQuoteCentreComment();  	
	    	for(int i=0; i<avnetQuoteCentreComments.size(); i++){
	    		String avnetQuoteCentreComment = avnetQuoteCentreComments.get(i);
	    		AvnetQuoteCentreCommentVO avnetQuoteCentreCommentVO = new AvnetQuoteCentreCommentVO();
	    		avnetQuoteCentreCommentVO.setName(avnetQuoteCentreComment);
	    		avnetQuoteCentreCommentVO.setId(i);
	    		AvnetQuoteCentreCommentCacheManager.putAvnetQuoteCentreComment(avnetQuoteCentreCommentVO);
	    	}
    	}
    	LOGGER.info("END OF initiate the Avnet Quote Centre Comment Cache, total (avnetQuoteCentreCommentCache) = " + AvnetQuoteCentreCommentCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	LOGGER.info("initiate the Terms And Conditions Cache"); 	
    	if(enableAvnetQuoteCentreComment){     	
    		List<String> termsAndConditionss = systemCodeMaintenanceSB.findTermsAndConditions();  	
	    	for(int i=0; i<termsAndConditionss.size(); i++){
	    		String termsAndConditions = termsAndConditionss.get(i);
	    		TermsAndConditionsVO termsAndConditionsVO = new TermsAndConditionsVO();
	    		termsAndConditionsVO.setId(i);
	    		termsAndConditionsVO.setName(termsAndConditions);
	    		TermsAndConditionsCacheManager.putTermsAndConditions(termsAndConditionsVO);
	    	}
    	}
    	LOGGER.info("END OF initiate the Terms And Conditions Cache, total (termsAndConditionsCache) = " + TermsAndConditionsCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */    	
    	LOGGER.info("initiate the Application Cache"); 	
    	if(enableApplication){     	
    		List<String> applications = systemCodeMaintenanceSB.findApplication();  	
	    	for(String application : applications){
	    		ApplicationCacheManager.putApplication(application);
	    	}
    	}
    	LOGGER.info("END OF initiate the Application Cache, total (applicationCache) = " + ApplicationCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	LOGGER.info("initiate the EnquirySegment Cache");
    	if(enableEnquirySegment){      	
    		List<String> enquirySegments = systemCodeMaintenanceSB.findEnquirySegment();  	
	    	for(String enquirySegment : enquirySegments){
	    		EnquirySegmentCacheManager.putEnquirySegment(enquirySegment);
	    	}
    	}
    	LOGGER.info("END OF initiate the EnquirySegment Cache, total (enquirySegmentCache) = " + EnquirySegmentCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	LOGGER.info("initiate the DesignLocation Cache");
    	if(enableDesignLocation){     	
	    	List<DesignLocation> designLocations = designLocationSB.findAllDesignLocation();    	
	    	for(DesignLocation designLocation : designLocations){
	    		DesignLocationCacheManager.putDesignLocation(designLocation);
	    	}
    	}
    	LOGGER.info("END OF initiate the DesignLocation Cache, total (designLocationCache) = " + DesignLocationCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	LOGGER.info("initiate the DesignRegion Cache");
    	if(enableDesignLocation){     	
    		List<String> designRegions = designLocationSB.findAllRegion();    	
	    	for(String designRegion : designRegions){
	    		DesignLocationCacheManager.putDesignRegion(designRegion);
	    	}
    	}
    	LOGGER.info("END OF initiate the DesignRegion Cache, total (DesignRegion) = " + DesignLocationCacheManager.getDesignRegionSize());
    	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	LOGGER.info("initiate the Mfr Cache");	
    	if(enableMfr){
	    	List<BizUnit> bizUnits = bizUnitSB.findAll();
	    	for(BizUnit bizUnit : bizUnits){    		    		
	    		List<Manufacturer> manufacturers = manufacturerSB.findManufacturerByBizUnit(bizUnit);
   				MfrCacheManager.putMfrListByBizUnit(manufacturers, bizUnit.getName());
	    	}
    	}
    	LOGGER.info("END OF the Mfr Cache, total (mfrCacheByRegion) = " + MfrCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	LOGGER.info("initiate the MfrDetail Cache");
    	if(enableMfrDetail){    	
	    	List<ManufacturerDetail> mfrDetails = manufacturerSB.findAllManufacturerDetail();
	    	for(ManufacturerDetail mfrDetail : mfrDetails){
	    		MfrDetailCacheManager.putMfrDetail(mfrDetail);
	    	}
    	}
    	LOGGER.info("END OF the MfrDetail Cache, total (mfrDetailCacheByRegion) = " + MfrDetailCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	LOGGER.info("initiate the Project Name Cache");
    	if(enableProjectName){ 
    		
    	}
    	LOGGER.info("END OF the Project Name Cache, total (mfrCacheByRegion) = " + MfrCacheManager.getSize());  
    	
    	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */    	
    	LOGGER.info("initiate the Design in cat Cache"); 	
    	if(enableDesignInCat){     	
    		List<String> designInCats = systemCodeMaintenanceSB.findDesignInCat(); 	
	    	for(String designInCat : designInCats){
	    		DesignInCatCacheManager.putDesignInCat(designInCat);
	    	}
    	}
    	LOGGER.info("END OF initiate the DesignInCat Cache, total (DesignInCatCache) = " + DesignInCatCacheManager.getSize());
    	
    	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */    	
    	LOGGER.info("initiate the Quote Type Cache"); 	
    	if(enableApplication){     	
    		List<String> quoteTypes = systemCodeMaintenanceSB.findQuoteType();	
	    	for(String quoteType : quoteTypes){
	    		QuoteTypeCacheManager.putQuoteType(quoteType);
	    	}
    	}
    	LOGGER.info("END OF initiate the Quote Type Cache, total (quoteTypeCache) = " + QuoteTypeCacheManager.getSize());
    	
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	LOGGER.info("initiate the ValidationRule Cache");
    	if(enableValidationRule){ 
    		List<ValidationRule> validationRules = validationRuleSB.findAll();
	    	for(ValidationRule validationRule : validationRules){
	    		ValidationRuleVO validationRuleVO = new ValidationRuleVO();
	    		validationRuleVO.setMfr(validationRule.getManufacturer().getName());
	    		validationRuleVO.setRegion(validationRule.getBizUnit().getName());
	    		validationRuleVO.setApplication(validationRule.getApplication());
	    		validationRuleVO.setCompetitorInformation(validationRule.getCompetitorInformation());
	    		validationRuleVO.setDesignLocation(validationRule.getDesignLocation());
	    		validationRuleVO.setEau(validationRule.getEau());
	    		validationRuleVO.setEndCustomerCode(validationRule.getEndCustomer());
	    		validationRuleVO.setMpSchedule(validationRule.getMpSchedule());
	    		validationRuleVO.setPpSchedule(validationRule.getPpSchedule());
	    		validationRuleVO.setShipToCode(validationRule.getShipToCode());
	    		validationRuleVO.setSupplierDrNumber(validationRule.getSupplierDrNo());
	    		validationRuleVO.setTargetResale(validationRule.getTargetResale());
	    		validationRuleVO.setProjectName(validationRule.getProject());
	    		ValidationRuleCacheManager.putValidationRule(validationRuleVO);	    		
	    	}
    	}
     	LOGGER.info("END OF the ValidationRule Cache, total (validationRuleCache) = " + ValidationRuleCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
     	/*
    	logger.info("initiate the User Cache");  
    	if(enableUser){     	
	    	List<User> users = userSB.findAll();
	    	for(User user : users){
	    		UserCacheManager.putUser(user);    		
	    	}
    	}
    	logger.info("END OF the User Cache, total (userCache) = " + UserCacheManager.getSize());
    	*/	
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	LOGGER.info("initiate the EmailAddress Cache");  
    	if(enableUser){
    		List<String> roleNames = ejbCommonSB.populateRoleNameList();




    		
	    	List<BizUnit> bizUnits = bizUnitSB.findAll();
	    	for(BizUnit bizUnit : bizUnits){
	    		List<User> users = userSB.findWorkingPlatformEmailListByBizUnit(roleNames, bizUnit);
	    		for(User user : users){
	    			UserCacheManager.putEmailAddress(user);
	    		}
	    	}
    	}
    	LOGGER.info("END OF the EmailAddress Cache, total (emailAddressCache) = " + UserCacheManager.getSize());	
    	
    	
    	//init DrmsValidationUpdateStrategy Impl Class
    	LOGGER.info("initiate the DrmsValidationUpdateStrategy Impl Class Cache"); 
    	try {
    		List<SystemCodeMaintenance> configList = systemCodeMaintenanceSB.getAllCofigByCategory("DRMS_VALIDATION_IMPL");
    		if(configList != null && configList.size() > 0) {
    			Map<String,String> map = new HashMap<String,String>();
    			for(SystemCodeMaintenance config : configList) {
    				String region = config.getRegion().trim();
    				String value = config.getValue().trim();
    				map.put(region, value);
    			}
    			DrmsValidationUpdateStrategyFactory.getInstance().initImplMap(map);
    		}
    		LOGGER.info("END OF the DrmsValidationUpdateStrategy Impl Class Cache, total (DrmsValidationUpdateStrategy) = " + configList.size());
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			LOGGER.info("initiate the DrmsValidationUpdateStrategy has Error"); 
		}
    	
    	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	LOGGER.info("initiate the Local Message Cache");   	
    	
    	if (enableLocaleMessageType) {
			try{
			List<LocaleMaster> localeMasterList = localeMessagesSB.findAll();
			ResourceMB.LOCALE_MASTER.clear();
			for (LocaleMaster localeMaster : localeMasterList) {
				/***** Locale Master filled to Resource MB ***/
				ResourceMB.putLocaleMaster(localeMaster);
			}
			}catch(Exception exception){
				LOGGER.log(Level.SEVERE, "Exception caught on Local Message cache load", exception);		
			}
		}
    	
	}
	
	
	@Override
	@Asynchronous
	public void executeTask(Timer timer) {
		LOGGER.info("RefreshCacheTask job beginning...");

		try {
			refresh();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,"RefreshCacheTask error:" + e.getMessage(),e);
		}

		LOGGER.info("RefreshCacheTask job ended");
	}
	
	
}
