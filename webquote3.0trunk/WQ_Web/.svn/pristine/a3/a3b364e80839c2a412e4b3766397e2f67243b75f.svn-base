package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.avnet.emasia.webquote.cache.DesignLocationCacheManager;
import com.avnet.emasia.webquote.cache.MfrDetailCacheManager;
import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.City;
import com.avnet.emasia.webquote.entity.CostIndicator;
import com.avnet.emasia.webquote.entity.Country;
import com.avnet.emasia.webquote.entity.DesignLocation;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.ManufacturerDetail;
import com.avnet.emasia.webquote.entity.Plant;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.SalesOrg;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.entity.ValidationRule;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.quote.ejb.CitySB;
import com.avnet.emasia.webquote.quote.ejb.CostIndicatorSB;
import com.avnet.emasia.webquote.quote.ejb.CountrySB;
import com.avnet.emasia.webquote.quote.ejb.CustomerSB;
import com.avnet.emasia.webquote.quote.ejb.DesignLocationSB;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.ValidationRuleSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.user.ejb.ActiveSessionSB;
import com.avnet.emasia.webquote.user.ejb.BizUnitSB;
import com.avnet.emasia.webquote.user.ejb.PlantSB;
import com.avnet.emasia.webquote.user.ejb.RoleSB;
import com.avnet.emasia.webquote.user.ejb.SalesOrgSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.web.quote.vo.AvnetQuoteCentreCommentVO;
import com.avnet.emasia.webquote.web.quote.vo.MfrVO;
import com.avnet.emasia.webquote.web.quote.vo.TermsAndConditionsVO;
import com.avnet.emasia.webquote.web.quote.vo.ValidationRuleVO;
@Deprecated
public class CacheManager implements Serializable {

	private transient SystemCodeMaintenanceSB systemCodeMaintenanceSB;
	private transient ManufacturerSB manufacturerSB;
	private transient BizUnitSB bizUnitSB;
	private transient SalesOrgSB salesOrgSB;
	private transient PlantSB plantSB;
	private transient ValidationRuleSB validationRuleSB;
	private transient CustomerSB customerSB;
	private transient CountrySB countrySB;
	private transient CitySB citySB;
	private transient QuoteSB quoteSB;
	private transient UserSB userSB;
	private transient CostIndicatorSB costIndicatorSB;
	private transient RoleSB roleSB;
	private transient ActiveSessionSB activeSessionSB;
	private transient DesignLocationSB designLocationSB;
	
	@EJB
	private EJBCommonSB ejbCommonSB;
/*	
	private boolean enableBizUnit = true;
	private boolean enableRole = true;
	private boolean enableSalesOrg = false;
	private boolean enableCountry = false;
	private boolean enableCity = false;
	private boolean enableCostIndicator = false;
	private boolean enableApplication = false;
	private boolean enableEnquirySegment = true;
	private boolean enableDesignLocation = true;
	private boolean enableMfr = true;
	private boolean enableMfrDetail = false;
	private boolean enableProjectName = false;
	private boolean enableValidationRule = false;
	private boolean enableUser = true;
*/	

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
	
	public CacheManager() {
		
		try {
			Context context = new InitialContext();
			systemCodeMaintenanceSB = (SystemCodeMaintenanceSB) context.lookup("java:app/WQ_EJB_Quote/SystemCodeMaintenanceSB!com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB");			
			
			if(enableBizUnit) bizUnitSB = (BizUnitSB) context.lookup("java:app/WQ_EJB_UserAndAuthor/BizUnitSB!com.avnet.emasia.webquote.user.ejb.BizUnitSB");
			if(enableRole) roleSB = (RoleSB) context.lookup("java:app/WQ_EJB_UserAndAuthor/RoleSB!com.avnet.emasia.webquote.user.ejb.RoleSB");
			if(enableSalesOrg) salesOrgSB = (SalesOrgSB) context.lookup("java:app/WQ_EJB_UserAndAuthor/SalesOrgSB!com.avnet.emasia.webquote.user.ejb.SalesOrgSB");
			if(enablePlant) plantSB = (PlantSB) context.lookup("java:app/WQ_EJB_UserAndAuthor/PlantSB!com.avnet.emasia.webquote.user.ejb.PlantSB");
			if(enableCostIndicator) costIndicatorSB = (CostIndicatorSB) context.lookup("java:app/WQ_EJB_Quote/CostIndicatorSB!com.avnet.emasia.webquote.quote.ejb.CostIndicatorSB");			
			if(enableCountry) countrySB = (CountrySB) context.lookup("java:app/WQ_EJB_Quote/CountrySB!com.avnet.emasia.webquote.quote.ejb.CountrySB");
			if(enableCity) citySB = (CitySB) context.lookup("java:app/WQ_EJB_Quote/CitySB!com.avnet.emasia.webquote.quote.ejb.CitySB");
			if(enableMfr) manufacturerSB = (ManufacturerSB) context.lookup("java:app/WQ_EJB_Quote/ManufacturerSB!com.avnet.emasia.webquote.quote.ejb.ManufacturerSB");
			if(enableUser) userSB = (UserSB) context.lookup("java:app/WQ_EJB_UserAndAuthor/UserSB!com.avnet.emasia.webquote.user.ejb.UserSB");			
			if(enableProjectName) quoteSB = (QuoteSB) context.lookup("java:app/WQ_EJB_Quote/QuoteSB!com.avnet.emasia.webquote.quote.ejb.QuoteSB");			
			if(enableValidationRule) validationRuleSB = (ValidationRuleSB) context.lookup("java:app/WQ_EJB_Quote/ValidationRuleSB!com.avnet.emasia.webquote.quote.ejb.ValidationRuleSB");			
		
			activeSessionSB = (ActiveSessionSB) context.lookup("java:app/WQ_EJB_UserAndAuthor/ActiveSessionSB!com.avnet.emasia.webquote.user.ejb.ActiveSessionSB");	
		} catch (NamingException ne) {
			throw new WebQuoteRuntimeException(ne.getMessage(), new RuntimeException(ne));
		}    	
    	

    	System.out.println("initiate the AccountGroup Cache");
    	AccountGroupCacheManager.putAccountGroup("0001", "Sold-to party");
    	AccountGroupCacheManager.putAccountGroup("0002", "Ship-to party");
    	AccountGroupCacheManager.putAccountGroup("0005", "Prospective Customer");
    	AccountGroupCacheManager.putAccountGroup("Z004", "End Customer");
    	System.out.println("END OF the AccountGroup Cache, total (accountGroupCache) = " + AccountGroupCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	if(enableCountry){
	    	System.out.println("initiate the Country Cache");
	    	
	    	List<Country> countries = countrySB.findAll();
	    	CountryCacheManager.setCountries(countries);
	    	   	
	    	System.out.println("END OF initiate the Country Cache, total (countryCache) = " + CountryCacheManager.getCountries().size());
    	}
    	
    	/*
     	 * Clear Session on server restart ---------start added by June 20140807
     	 * =================================================================================================================================
     	 */
    	/*System.out.println("Start to clear Session ");
    	activeSessionSB.clearSession();
    	System.out.println("End to clear Session successfully");*/
    	
    	/*
     	 * Clear Session on server restart ---------End added by June 20140807
     	 */
    	
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	if(enableCity){
	    	System.out.println("initiate the City Cache");
	    	
	    	List<City> cities = citySB.findAll();
	    	for(City city : cities)
	    		CityCacheManager.putCity(city);
	    	   	
	    	System.out.println("END OF initiate the City Cache, total (countryCache) = N/A");
    	}
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	System.out.println("initiate the BizUnit Cache");
    	if(enableBizUnit){  
	    	List<BizUnit> bizUnits = bizUnitSB.findAll();
	    	for(BizUnit bizUnit : bizUnits){
	    		BizUnitCacheManager.putBizUnit(bizUnit);    		
	    	}
    	}	    	
    	System.out.println("END OF the BizUnit Cache, total (bizUnitCacheByRegion) = " + BizUnitCacheManager.getSize());		
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	System.out.println("initiate the Role Cache");
    	if(enableRole){  
	    	List<Role> roles = roleSB.findAll();
	    	for(Role role : roles){
	    		RoleCacheManager.putRole(role);    		
	    	}
    	}	    	
    	System.out.println("END OF the Role Cache, total (roleCacheByRegion) = " + RoleCacheManager.getSize());		
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */    	
    	if(enableSalesOrg){ 
    		System.out.println("initiate the SalesOrg Cache");
	    	List<SalesOrg> salesOrgs = salesOrgSB.findAll();
	    	for(SalesOrg salesOrg : salesOrgs){
	    		SalesOrgCacheManager.putSalesOrg(salesOrg);    		
	    	}
    	}
    	System.out.println("END OF the SalesOrg Cache, total (salesOrgCacheByRegion) = " + SalesOrgCacheManager.getSize());		
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */    	
    	if(enablePlant){ 
    		System.out.println("initiate the Plant Cache");
	    	List<Plant> plants = plantSB.findAll();
	    	for(Plant plant : plants){
	    		PlantCacheManager.putPlant(plant);    		
	    	}
    	}
    	System.out.println("END OF the Plant Cache, total (plantCacheByRegion) = " + PlantCacheManager.getSize());	
    	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	System.out.println("initiate the CostIndicator Cache");
    	if(enableCostIndicator){
	    	List<CostIndicator> costIndicators = costIndicatorSB.findAllActive();
	    	for(CostIndicator costIndicator : costIndicators){
	    		CostIndicatorCacheManager.putCostIndicator(costIndicator);
	    	}
    	}
    	System.out.println("END OF initiate the CostIndicator Cache, total (costIndicatorCache) = " + CostIndicatorCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	System.out.println("initiate the QtyIndicator Cache");
    	if(enableQtyIndicator){
    		List<String> qtyIndicators = systemCodeMaintenanceSB.findQuantityIndicator();  	
	    	for(String qtyIndicator : qtyIndicators){
	    		QtyIndicatorCacheManager.putQtyIndicator(qtyIndicator);
	    	}
    	}
    	System.out.println("END OF initiate the QtyIndicator Cache, total (qtyIndicatorCache) = " + QtyIndicatorCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	System.out.println("initiate the Avnet Quote Centre Comment Cache"); 	
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
    	System.out.println("END OF initiate the Avnet Quote Centre Comment Cache, total (avnetQuoteCentreCommentCache) = " + AvnetQuoteCentreCommentCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	System.out.println("initiate the Terms And Conditions Cache"); 	
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
    	System.out.println("END OF initiate the Terms And Conditions Cache, total (termsAndConditionsCache) = " + TermsAndConditionsCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */    	
    	System.out.println("initiate the Application Cache"); 	
    	if(enableApplication){     	
    		List<String> applications = systemCodeMaintenanceSB.findApplication();  	
	    	for(String application : applications){
	    		ApplicationCacheManager.putApplication(application);
	    	}
    	}
    	System.out.println("END OF initiate the Application Cache, total (applicationCache) = " + ApplicationCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	System.out.println("initiate the EnquirySegment Cache");
    	if(enableEnquirySegment){      	
    		List<String> enquirySegments = systemCodeMaintenanceSB.findEnquirySegment();  	
	    	for(String enquirySegment : enquirySegments){
	    		EnquirySegmentCacheManager.putEnquirySegment(enquirySegment);
	    	}
    	}
    	System.out.println("END OF initiate the EnquirySegment Cache, total (enquirySegmentCache) = " + EnquirySegmentCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	System.out.println("initiate the DesignLocation Cache");
    	if(enableDesignLocation){     	
    		List<DesignLocation> designLocations = designLocationSB.findAllDesignLocation();    	
	    	for(DesignLocation designLocation : designLocations){
	    		DesignLocationCacheManager.putDesignLocation(designLocation);
	    	}
    	}
    	System.out.println("END OF initiate the DesignLocation Cache, total (designLocationCache) = " + DesignLocationCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	System.out.println("initiate the DesignRegion Cache");
    	if(enableDesignLocation){     	
    		List<String> designRegions = designLocationSB.findAllRegion();    	
	    	for(String designRegion : designRegions){
	    		DesignLocationCacheManager.putDesignRegion(designRegion);
	    	}
    	}
    	System.out.println("END OF initiate the DesignRegion Cache, total (DesignRegion) = " + DesignLocationCacheManager.getDesignRegionSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	System.out.println("initiate the Mfr Cache");	
    	if(enableMfr){
	    	List<BizUnit> bizUnits = bizUnitSB.findAll();
	    	for(BizUnit bizUnit : bizUnits){    		    		
	    		List<Manufacturer> manufacturers = manufacturerSB.findManufacturerByBizUnit(bizUnit);
   				MfrCacheManager.putMfrListByBizUnit(manufacturers, bizUnit.getName());
	    	}
    	}
    	System.out.println("END OF the Mfr Cache, total (mfrCacheByRegion) = " + MfrCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	System.out.println("initiate the MfrDetail Cache");
    	if(enableMfrDetail){    	
	    	List<ManufacturerDetail> mfrDetails = manufacturerSB.findAllManufacturerDetail();
	    	for(ManufacturerDetail mfrDetail : mfrDetails){
	    		MfrDetailCacheManager.putMfrDetail(mfrDetail);
	    	}
    	}
    	System.out.println("END OF the MfrDetail Cache, total (mfrDetailCacheByRegion) = " + MfrDetailCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	System.out.println("initiate the Project Name Cache");
    	if(enableProjectName){ 
    		
    	}
    	System.out.println("END OF the Project Name Cache, total (mfrCacheByRegion) = " + MfrCacheManager.getSize());  
    	
    	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */    	
    	System.out.println("initiate the Design in cat Cache"); 	
    	if(enableDesignInCat){     	
    		List<String> designInCats = systemCodeMaintenanceSB.findDesignInCat(); 	
	    	for(String designInCat : designInCats){
	    		DesignInCatCacheManager.putDesignInCat(designInCat);
	    	}
    	}
    	System.out.println("END OF initiate the DesignInCat Cache, total (DesignInCatCache) = " + DesignInCatCacheManager.getSize());
    	
    	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */    	
    	System.out.println("initiate the Quote Type Cache"); 	
    	if(enableApplication){     	
    		List<String> quoteTypes = systemCodeMaintenanceSB.findQuoteType();	
	    	for(String quoteType : quoteTypes){
	    		QuoteTypeCacheManager.putQuoteType(quoteType);
	    	}
    	}
    	System.out.println("END OF initiate the Quote Type Cache, total (quoteTypeCache) = " + QuoteTypeCacheManager.getSize());
    	
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	System.out.println("initiate the ValidationRule Cache");
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
     	System.out.println("END OF the ValidationRule Cache, total (validationRuleCache) = " + ValidationRuleCacheManager.getSize());
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
     	/*
    	System.out.println("initiate the User Cache");  
    	if(enableUser){     	
	    	List<User> users = userSB.findAll();
	    	for(User user : users){
	    		UserCacheManager.putUser(user);    		
	    	}
    	}
    	System.out.println("END OF the User Cache, total (userCache) = " + UserCacheManager.getSize());
    	*/	
     	/*
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 * =================================================================================================================================
     	 */
    	System.out.println("initiate the EmailAddress Cache");  
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
    	System.out.println("END OF the EmailAddress Cache, total (emailAddressCache) = " + UserCacheManager.getSize());	
	}

	

}
