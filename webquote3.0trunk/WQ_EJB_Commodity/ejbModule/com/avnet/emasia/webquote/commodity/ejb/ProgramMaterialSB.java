package com.avnet.emasia.webquote.commodity.ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.jboss.ejb3.annotation.TransactionTimeout;

import com.avnet.emasia.webquote.commodity.dto.CheckBoxBean;
import com.avnet.emasia.webquote.commodity.dto.PISearchBean;
import com.avnet.emasia.webquote.commodity.util.CommodityUtil;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.MaterialRegional;
import com.avnet.emasia.webquote.entity.MaterialType;
import com.avnet.emasia.webquote.entity.Pricer;
import com.avnet.emasia.webquote.entity.ProgramPricer;
import com.avnet.emasia.webquote.entity.ProgramType;
import com.avnet.emasia.webquote.entity.QtyBreakPricer;
import com.avnet.emasia.webquote.entity.QuantityBreakPrice;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.quote.ejb.MaterialSB;
import com.avnet.emasia.webquote.utilities.DateUtils;
/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-11
 * 
 */
@Stateless
public class ProgramMaterialSB {

	@PersistenceContext(unitName = "Server_Source")
	public EntityManager em;
	private static Logger logger = Logger.getLogger(ProgramMaterialSB.class.getName());
	private final static String LIKE_SIGN = "%";
	private final static String TYPE_MFR = "MFR";
	private final static String TYPE_PRODUCT_GROUP = "PRODUCT_GROUP";
	private final static String TYPE_PROGRAM_TYPE = "PROGRAM_TYPE";

	private final static String FIELD_NAME_PROGRAM_TYPE = "programType";
	private final static String FIELD_NAME_MANUFACTURER = "manufacturer";
	private final static String FIELD_NAME_MATERIALREGIONALS = "materialRegionals";
	private final static String FIELD_NAME_FULL_MFR_PART_NUMBER = "fullMfrPartNumber";
	private final static String FIELD_NAME_MATERIAL = "material";
	private final static String FIELD_NAME_BIZ_UNIT_BEAN = "bizUnitBean";
	private final static String FIELD_NAME_NAME = "name";
	private final static String FIELD_NAME_PRODUCT_GROUP_2 = "productGroup2";

	// private final static String FIELD_NAME_MATERIAL_CATEGORY =
	// "materialCategory";
	private final static String FIELD_NAME_PROGRAM_EFFECTIVE_DATE = "programEffectiveDate";
	private final static String FIELD_NAME_PROGRAM_CLOSING_DATE = "programClosingDate";
	private final static String FIELD_NAME_PROGRAM_SPECIAL_ITEM_FLAG = "specialItemFlag";
	private final static String FIELD_NAME_MATERIAL_TYPE = "materialType";
	private final static String FIELD_NAME_LAST_UPDATE_ON = "lastUpdatedOn";
	private final static String FIELD_NAME_DISPLAY_ON_TOP = "displayOnTop";
	private final static String FIELD_NAME_ID = "id";

	  
	@EJB
	private MaterialSB materialSB;
	   
	    
	   public List<CheckBoxBean> getMfrList(PISearchBean pisb)
	   {
		   logger.fine("call getMfrList : PISearchBean : "+ pisb.toString());
		   List<CheckBoxBean> mfrlist = new ArrayList<CheckBoxBean>();
		   List<String> programMaterialList = searchByType(pisb,TYPE_MFR);
		   if(programMaterialList !=null)
			   for(String str : programMaterialList)
			   {
				   CheckBoxBean cbb = new CheckBoxBean();
				   cbb.setName(str);
				   cbb.setEnable(true);
				   cbb.setChecked(true);
				   mfrlist.add(cbb);
			   }
		   return mfrlist;
	   }
	   
	   public List<CheckBoxBean> getProductGroupList(PISearchBean pisb)
	   {
		   logger.fine("call getProductGroupList : PISearchBean : "+ pisb.toString());
		   List<CheckBoxBean> productGroupList = new ArrayList<CheckBoxBean>();
		   List<String> programMaterialList = searchByType(pisb,TYPE_PRODUCT_GROUP);
		   if(programMaterialList !=null)
			   for(String str : programMaterialList)
			   {
				   CheckBoxBean cbb = new CheckBoxBean();
				   cbb.setName(str);
				   cbb.setEnable(true);
				   cbb.setChecked(true);
				   productGroupList.add(cbb);
			   }
		   return productGroupList;
	   }
	   
	   public List<CheckBoxBean> getProgramTypeList(PISearchBean pisb)
	   {
		   logger.fine("call getProgramTypeList : PISearchBean : "+ pisb.toString());
		   List<CheckBoxBean> programTypeList = new ArrayList<CheckBoxBean>();
		   List<String> programMaterialList = searchByType(pisb,TYPE_PROGRAM_TYPE);
		   if(programMaterialList !=null)
			   for(String str : programMaterialList)
			   {
				   CheckBoxBean cbb = new CheckBoxBean();
				   cbb.setName(str);
				   cbb.setEnable(true);
				   cbb.setChecked(true);
				   programTypeList.add(cbb);
			   }
		   return programTypeList;
	   }
	 /*  @TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS) 
	   public List<ProgramPricer> searchProgramMaterial(PISearchBean pisb)
	   {
		    logger.fine("call searchProgramMaterial : PISearchBean : "+ pisb.toString());
		    CriteriaBuilder cb = em.getCriteriaBuilder();
		    CriteriaQuery<ProgramPricer> cq = cb.createQuery(ProgramPricer.class);
		   	cq = CommodityUtil.programMaterialCriteriaQuery(pisb, cb, cq);
		   	TypedQuery<ProgramPricer> q = em.createQuery(cq);
		   	
		   	return  q.getResultList();
		   
	   }*/
	   
	   @TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS) 
	   public List<QtyBreakPricer> searchSpecialPricer(PISearchBean pisb)
	   {
		    logger.fine("call searchSpecialPricer : PISearchBean : "+ pisb.toString());
		    CriteriaBuilder cb = em.getCriteriaBuilder();
		    CriteriaQuery<QtyBreakPricer> cq = cb.createQuery(QtyBreakPricer.class);
		   	cq = CommodityUtil.qtyBreakPricerCriteriaQuery(pisb, cb, cq);
		   	TypedQuery<QtyBreakPricer> q = em.createQuery(cq);
		   	
		   	return  q.getResultList();
		   
	   }
	   
	   
	   /*@TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS) 
	   public List<ProgramPricer> searchProgramMaterialByPage(PISearchBean pisb)
	   {
		    logger.fine("call searchProgramMaterial : PISearchBean : "+ pisb.toString());
		    CriteriaBuilder cb = em.getCriteriaBuilder();
		    CriteriaQuery<ProgramPricer> cq = cb.createQuery(ProgramPricer.class);
		    cq = CommodityUtil.programMaterialCriteriaQuery(pisb, cb, cq);

		   	TypedQuery<ProgramPricer> q = em.createQuery(cq);
		    
		   	if (pisb.getPageSize() >= 0)
		   	{
	            q.setMaxResults(pisb.getPageSize());
	        }
	        
		   	if (pisb.getFirstPage() >= 0)
	        {
	            q.setFirstResult(pisb.getFirstPage());
	        }
		   	List<ProgramPricer> list= q.getResultList();
		   	//Add to solve Send Off Line Report java.lang.OutOfMemoryError: Java heap space
		   	em.clear();
		   	return  list;
		   
	   }*/
	   
	@TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS)
	public List<QtyBreakPricer> searchQtyBreakPricerByPage(PISearchBean pisb) {
		logger.fine("call searchQtyBreakPricerByPage : PISearchBean : " + pisb.toString());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QtyBreakPricer> cq = cb.createQuery(QtyBreakPricer.class);
		cq = CommodityUtil.qtyBreakPricerCriteriaQuery(pisb, cb, cq);

		TypedQuery<QtyBreakPricer> q = em.createQuery(cq);

		if (pisb.getPageSize() >= 0) {
			q.setMaxResults(pisb.getPageSize());
		}

		if (pisb.getFirstPage() >= 0) {
			q.setFirstResult(pisb.getFirstPage());
		}
		List<QtyBreakPricer> list = q.getResultList();
		// Add to solve Send Off Line Report java.lang.OutOfMemoryError: Java
		// heap space
		em.clear();
		return list;

	}
	   
	   @TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS) 
	public int searchQtyBreakPricerCount(PISearchBean pisb) {
		logger.fine("call searchQtyBreakPricerCount : PISearchBean : " + pisb.toString());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);

		Root<QtyBreakPricer> pricer = cq.from(QtyBreakPricer.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		Expression<String> fullMfrPartNumber = pricer.<Material>get(FIELD_NAME_MATERIAL)
				.get(FIELD_NAME_FULL_MFR_PART_NUMBER);
		Expression<String> mfrName = pricer.<Material>get(FIELD_NAME_MATERIAL)
				.<Manufacturer>get(FIELD_NAME_MANUFACTURER).get(FIELD_NAME_NAME);
		
		Expression<String> programTypeName = pricer.<ProgramType>get(FIELD_NAME_PROGRAM_TYPE).get(FIELD_NAME_NAME);
		
		Expression<String> bizUnit = pricer.<BizUnit>get(FIELD_NAME_BIZ_UNIT_BEAN).get(FIELD_NAME_NAME);

		Expression<Manufacturer> mfr = pricer.<Material>get(FIELD_NAME_MATERIAL)
				.<Manufacturer>get(FIELD_NAME_MANUFACTURER);
		//Expression<ProductGroup> productGroup = pricer.<Material>get(FIELD_NAME_MATERIAL).<ProductGroup>get(FIELD_NAME_PRODUCT_GROUP_2);
		Expression<ProgramType> programType = pricer.<ProgramType>get(FIELD_NAME_PROGRAM_TYPE);
		

		if (pisb.getBizUnit() != null) {
			predicates.add(cb.equal(bizUnit, pisb.getBizUnit()));
		}

		if (pisb.getMfrPart() != null && !pisb.getMfrPart().isEmpty())
			if (pisb.getExact() == null) {
				predicates.add(
						cb.like(cb.upper(fullMfrPartNumber), LIKE_SIGN + pisb.getMfrPart().toUpperCase() + LIKE_SIGN));
			} else {
				predicates.add(cb.equal(fullMfrPartNumber, pisb.getMfrPart()));
			}

		if (pisb.getMfr() != null) {
			predicates.add(cb.like(cb.upper(mfrName), LIKE_SIGN + pisb.getMfr().toUpperCase() + LIKE_SIGN));
		}

		if (pisb.getMfrList() != null) {
			if (pisb.getMfrList().isEmpty()) {
				predicates.add(cb.isNull(mfr));
			} else {
				predicates.add(mfrName.in(pisb.getMfrList()));
			}
		}

		Predicate allPredicate = CommodityUtil.getAllPredicate(pisb, cb, pricer, predicates, mfrName,programTypeName, mfr, programType,"search");

		cq.select(cb.countDistinct(pricer));
		cq.where(allPredicate);

		int count = em.createQuery(cq).getSingleResult().intValue();

		return count;

	}
	   
	   
	   /*@TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS) 
	   public int searchProgramMaterialCount(PISearchBean pisb)
	   {
		    logger.fine("call searchProgramMaterial : PISearchBean : "+ pisb.toString());
		    CriteriaBuilder cb = em.getCriteriaBuilder();
		   	CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		
		   	Root<ProgramPricer> programMaterial = cq.from(ProgramPricer.class);
		   	List<Predicate> predicates = new ArrayList<Predicate>();
		   
		   	Expression<String>  fullMfrPartNumber = programMaterial.<Material>get(FIELD_NAME_MATERIAL).get(FIELD_NAME_FULL_MFR_PART_NUMBER);
		   	Expression<String>  mfrName = programMaterial.<Material>get(FIELD_NAME_MATERIAL).<Manufacturer>get(FIELD_NAME_MANUFACTURER).get(FIELD_NAME_NAME);
		   	Expression<String>  productGroupName = programMaterial.<Material>get(FIELD_NAME_MATERIAL).<ProductGroup>get(FIELD_NAME_PRODUCT_GROUP_2).get(FIELD_NAME_NAME);
		   	Expression<String>  programTypeName = programMaterial.<ProgramType>get(FIELD_NAME_PROGRAM_TYPE).get(FIELD_NAME_NAME);
		   	Expression<String>  bizUnit = programMaterial.<BizUnit>get(FIELD_NAME_BIZ_UNIT_BEAN).get(FIELD_NAME_NAME);
			
		   	Expression<Manufacturer>  mfr = programMaterial.<Material>get(FIELD_NAME_MATERIAL).<Manufacturer>get(FIELD_NAME_MANUFACTURER);
		   	Expression<ProductGroup>  productGroup = programMaterial.<Material>get(FIELD_NAME_MATERIAL).<ProductGroup>get(FIELD_NAME_PRODUCT_GROUP_2);
		   	Expression<ProgramType>  programType = programMaterial.<ProgramType>get(FIELD_NAME_PROGRAM_TYPE);
		   	//Expression<MaterialType> materialType = programMaterial.<Material>get(FIELD_NAME_MATERIAL).<MaterialType>get(FIELD_NAME_MATERIAL_TYPE);
		 //  Expression<String> materialType = programMaterial.<String>get(FIELD_NAME_MATERIAL_CATEGORY);  by DamonChen
		   	
		   	if(pisb.getBizUnit()!=null)
			predicates.add(cb.equal(bizUnit, pisb.getBizUnit()));
		   	
			if(pisb.getMfrPart()!=null && !pisb.getMfrPart().isEmpty())
		   	if(pisb.getExact()==null)
		   	{
		   		predicates.add(cb.like(cb.upper(fullMfrPartNumber), LIKE_SIGN+pisb.getMfrPart().toUpperCase()+LIKE_SIGN));
		   	}
		   	else
		   	{
		   		predicates.add(cb.equal(fullMfrPartNumber, pisb.getMfrPart()));	
		   	}
			
			if(pisb.getMfr()!=null)
		   	{
				predicates.add(cb.like(cb.upper(mfrName), LIKE_SIGN+pisb.getMfr().toUpperCase()+LIKE_SIGN));
		   	}
			
			if(pisb.getSalesCostFlag() != null) {
				javax.persistence.criteria.Join<ProgramPricer, Material> materail = programMaterial.join(FIELD_NAME_MATERIAL, JoinType.INNER);
				javax.persistence.criteria.Join<Material, MaterialRegional> materialRegional = materail.join(FIELD_MATERIAL_REGIONALS, JoinType.INNER);
				predicates.add(cb.equal(materialRegional.<Boolean>get(FIELD_SALES_COST_FLAG), pisb.getSalesCostFlag()));
			}
			
			// Added by Punit for CPD Exercise
			predicates = CommodityUtil.preparePISearchBean(pisb, predicates, mfr, cb, programType, programTypeName, productGroupName,
				productGroup, mfrName);
			
			if(pisb.getActive()!=null && !pisb.getActive().isEmpty())
		   	{
		   		predicates.add(cb.lessThanOrEqualTo(programMaterial.<Date>get(FIELD_NAME_PROGRAM_EFFECTIVE_DATE), DateUtils.getCurrentAsiaDateObj()));
		   		predicates.add(cb.greaterThanOrEqualTo(programMaterial.<Date>get(FIELD_NAME_PROGRAM_CLOSING_DATE), DateUtils.getCurrentAsiaDateObj()));
		   	}
			
			// Added by Punit for CPD Exercise
			predicates = CommodityUtil.securityCheckPISearchBean(pisb, cb, programMaterial, predicates);
			
			cq.select(cb.countDistinct(programMaterial));
		   	cq.where(predicates.toArray(new Predicate[]{}));
		   
		   	int count = em.createQuery(cq).getSingleResult().intValue();
		    
		   	return  count;
		   
	   }*/
	   
		public List<String> searchByType(PISearchBean pisb, String type)
		{
			logger.fine("call searchByType : type : "+ type);
			logger.fine("call searchByType : PISearchBean : "+ pisb.toString());
			CriteriaBuilder cb = em.getCriteriaBuilder();
		   	CriteriaQuery<String> cq = cb.createQuery(String.class);
		
		   	Root<QtyBreakPricer> programMaterial = cq.from(QtyBreakPricer.class);
		   	List<Predicate> predicates = new ArrayList<Predicate>();

		   	//predicates.add(cb.equal(programMaterial.get("region"), pisb.getRegion()));
		   	
		 	Expression<String>  fullMfrPartNumber = programMaterial.<Material>get(FIELD_NAME_MATERIAL).get(FIELD_NAME_MANUFACTURER).get(FIELD_NAME_NAME);
		   	Expression<String>  mfrName = programMaterial.<Material>get(FIELD_NAME_MATERIAL).<Manufacturer>get(FIELD_NAME_MANUFACTURER).get(FIELD_NAME_NAME);
		   	//Expression<String>  productGroupName = programMaterial.<Material>get(FIELD_NAME_MATERIAL).<ProductGroup>get(FIELD_NAME_PRODUCT_GROUP_2).get(FIELD_NAME_NAME);
		   	Expression<String>  programTypeName = programMaterial.<ProgramType>get(FIELD_NAME_PROGRAM_TYPE).get(FIELD_NAME_NAME);
			Expression<String>  bizUnit = programMaterial.<BizUnit>get(FIELD_NAME_BIZ_UNIT_BEAN).get(FIELD_NAME_NAME);
			
		   	Expression<Manufacturer>  mfr = programMaterial.<Material>get(FIELD_NAME_MATERIAL).<Manufacturer>get(FIELD_NAME_MANUFACTURER);
		   //Expression<ProductGroup>  productGroup = programMaterial.<Material>get(FIELD_NAME_MATERIAL).<ProductGroup>get(FIELD_NAME_PRODUCT_GROUP_2);
		   	Expression<ProgramType>  programType = programMaterial.<ProgramType>get(FIELD_NAME_PROGRAM_TYPE);
		   //	Expression<String> materialType = programMaterial.<String>get(FIELD_NAME_MATERIAL_CATEGORY); DamonChen
		   	
		   	 	
		   	
		   	if(pisb.getBizUnit()!=null) {
			   	predicates.add(cb.equal(bizUnit, pisb.getBizUnit()));

		   	}
		   	
		   	if(pisb.getMfrPart()!=null && !pisb.getMfrPart().isEmpty())
			if (pisb.getExact() == null) {
				predicates.add(
						cb.like(cb.upper(fullMfrPartNumber), LIKE_SIGN + pisb.getMfrPart().toUpperCase() + LIKE_SIGN));
			} else {
				predicates.add(cb.equal(fullMfrPartNumber, pisb.getMfrPart()));

			}
		   	
		   	Predicate allPredicate = CommodityUtil.getAllPredicate(pisb, cb, programMaterial, predicates, mfrName, programTypeName, mfr, programType,type);
			
			if(TYPE_PROGRAM_TYPE.equals(type))
			{
				Selection<String> prograTypeSelection = programMaterial.get(FIELD_NAME_PROGRAM_TYPE).get(FIELD_NAME_NAME);
				cq.select(prograTypeSelection).distinct(true);
			}
			else if(TYPE_MFR.equals(type))
			{
				Selection<String>  manufacturerSelection = programMaterial.<Material>get(FIELD_NAME_MATERIAL).get(FIELD_NAME_MANUFACTURER).get(FIELD_NAME_NAME);
				cq.select(manufacturerSelection).distinct(true);
			}
			else if(TYPE_PRODUCT_GROUP.equals(type))
			{
				//Selection<String>  productGroupSelection = programMaterial.<Material>get(FIELD_NAME_MATERIAL).get(FIELD_NAME_PRODUCT_GROUP_2).get(FIELD_NAME_NAME);
				Selection<String>  productGroupSelection = programMaterial.<Material>get(FIELD_NAME_MATERIAL).<MaterialRegional>get(FIELD_NAME_MATERIALREGIONALS).get(FIELD_NAME_PRODUCT_GROUP_2).get(FIELD_NAME_NAME);
				cq.select(productGroupSelection).distinct(true);
			}
		   	cq.where(allPredicate);
		   	TypedQuery<String> q = em.createQuery(cq);
		   	List<String> results = q.getResultList();
		   	return results;
		   	
	 }
		
 
	  
	  
	  /*
	   * Created by Tonmy on 8 Aug 2013 . 
	   * @Parameter: PISearchBean.
	   * @Return : List<ProgramMaterial>
	   * Retrieve the update program item for sending out the notification email.    
	   */
	  @TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS) 
	   public List<ProgramPricer> getUpdateProgramMaterialForEmail(String programTypeStr, BizUnit bizUnit)
	   {
		    logger.fine("call getUpdateProgramMaterialForEmail : materialType : "+ programTypeStr +" bizUnit: "+ bizUnit.getName() );
		    List<ProgramPricer> results = new ArrayList<>();
		    CriteriaBuilder cb = em.getCriteriaBuilder();
		   	CriteriaQuery<ProgramPricer> cq = cb.createQuery(ProgramPricer.class);
		
		   	Root<ProgramPricer> programMaterial = cq.from(ProgramPricer.class);
		   	Join<ProgramPricer, Material> material = programMaterial.join("material");
		   	Join<Material, MaterialRegional> materialRegional = material.join("materialRegionals");
		   	List<Predicate> predicates = new ArrayList<Predicate>();
		   	
		   	Expression<String>  programTypeName = programMaterial.<ProgramType>get(FIELD_NAME_PROGRAM_TYPE).get(FIELD_NAME_NAME);
//			Expression<MaterialType> materialType = programMaterial.<Material>get(FIELD_NAME_MATERIAL).<MaterialType>get(FIELD_NAME_MATERIAL_TYPE);
		   	Expression<String>  fullMfrPartNumber = programMaterial.<Material>get(FIELD_NAME_MATERIAL).get(FIELD_NAME_FULL_MFR_PART_NUMBER);
		   	Expression<String>  mfrName = programMaterial.<Material>get(FIELD_NAME_MATERIAL).<Manufacturer>get(FIELD_NAME_MANUFACTURER).get(FIELD_NAME_NAME);
		   //	Expression<String> materialTypeName = programMaterial.<Material>get(FIELD_NAME_MATERIAL).<MaterialType>get(FIELD_NAME_MATERIAL_TYPE);

		   	predicates.add(cb.equal(materialRegional.<BizUnit>get("bizUnit").<String>get("name"), bizUnit.getName()));
//		   	predicates.add(cb.equal(materialType,"PROGRAM"));
	   		if(bizUnit!=null && bizUnit.getName()!=null && !"".equalsIgnoreCase(bizUnit.getName()))
	   		{
	   			predicates.add(cb.equal(programTypeName, programTypeStr));	
	   		}

			predicates.add(cb.lessThanOrEqualTo(programMaterial.<Date>get(FIELD_NAME_PROGRAM_EFFECTIVE_DATE), DateUtils.getCurrentAsiaDateObj()));
		   	predicates.add(cb.greaterThanOrEqualTo(programMaterial.<Date>get(FIELD_NAME_PROGRAM_CLOSING_DATE), DateUtils.getCurrentAsiaDateObj()));

		   	predicates.add(cb.lessThanOrEqualTo(programMaterial.<Date>get(FIELD_NAME_LAST_UPDATE_ON), DateUtils.getTheDayBeforeEnd()));
		   	predicates.add(cb.greaterThanOrEqualTo(programMaterial.<Date>get(FIELD_NAME_LAST_UPDATE_ON), DateUtils.getTheDayBeforeBegin()));

		   	
			cq.select(programMaterial);
		   	cq.where(predicates.toArray(new Predicate[]{}));
		    cq.orderBy(
		    		cb.asc(cb.selectCase().when(cb.isNull(programMaterial.get(FIELD_NAME_DISPLAY_ON_TOP)),999999).otherwise(programMaterial.get(FIELD_NAME_DISPLAY_ON_TOP))),
		    		cb.asc(cb.selectCase().when(cb.isNull(programMaterial.get(FIELD_NAME_PROGRAM_SPECIAL_ITEM_FLAG)),999999).otherwise(programMaterial.get(FIELD_NAME_PROGRAM_SPECIAL_ITEM_FLAG))),
		    		cb.desc(programMaterial.get(FIELD_NAME_LAST_UPDATE_ON)),
		    		cb.asc(mfrName),
		    		cb.asc(fullMfrPartNumber)
		    		);
		   	TypedQuery<ProgramPricer> q = em.createQuery(cq);
		   	
		   	List<ProgramPricer> resultList = q.getResultList();
		   	for(ProgramPricer pm : resultList)
		   	{
//		   		if(bizUnit.getName().equalsIgnoreCase(pm.getBizUnitBean().getName()) && "PROGRAM".equalsIgnoreCase(pm.getMaterial().getMaterialType()))
		   		if(bizUnit.getName().equalsIgnoreCase(pm.getBizUnitBean().getName()) )
		   		{
		   			results.add(pm);
		   		}
		   	}
		   	return  results;
		   
	   }
	  
	  
	    public Material findExactMaterialByQuoteItem(QuoteItem item, BizUnit bizUnit){
 
	    	if(item!=null && item.getRequestedMfr()!=null && item.getRequestedPartNumber()!=null && bizUnit!=null)
	    	{
	    		Material tempM = (Material)materialSB.findExactMaterialByMfrPartNumber(item.getRequestedMfr().getName(), item.getRequestedPartNumber(), bizUnit);
	    		if(tempM!=null)
	    			return tempM;
	    	}
	    	return null;

	    }

		/**
		* @Description: TODO
		* @author 042659
		* @param pricerId      
		* @return QtyBreakPricer    
		* @throws  
		*/  
		public List<QtyBreakPricer> findQBreakPriceByPricerId(long pricerId) {
			// TODO Auto-generated method stub
		    CriteriaBuilder cb = em.getCriteriaBuilder();
		    CriteriaQuery<QtyBreakPricer> cq = cb.createQuery(QtyBreakPricer.class);
		    
			Root<QtyBreakPricer> pricer = cq.from(QtyBreakPricer.class);
			List<Predicate> predicates = new ArrayList<Predicate>();
			Expression<Long>  priceIdExp = pricer.<Long>get("id");
			predicates.add(cb.equal(priceIdExp, pricerId));
			Predicate p = cb.and(predicates.toArray(new Predicate[] {}));
			cq.where(p);
		   	TypedQuery<QtyBreakPricer> q = em.createQuery(cq);
		   	
		   	return  q.getResultList();
			
		}
		
		public List<QuantityBreakPrice> getOrderQties(QuoteItem quoteItem) {
			// TODO Auto-generated method stub
			QtyBreakPricer QtyBreakPricer = getQBreakPriceByPricerId(quoteItem.getPricerId());
			if (QtyBreakPricer != null) {
				return QtyBreakPricer.calQtyBreakRange();
			}else {
				return new ArrayList<QuantityBreakPrice>();
			}
		}
		/**
		* @Description: TODO
		* @author 042659
		* @param pricerId      
		* @return void    
		* @throws  
		*/  
		public QtyBreakPricer getQBreakPriceByPricerId(long pricerId) {
			if (pricerId == 0L) {
				return null;
			}
			List<QtyBreakPricer> QtyBreakPricerLst = findQBreakPriceByPricerId(pricerId);

			if (QtyBreakPricerLst == null || QtyBreakPricerLst.size() == 0) {
				return null;
			}

			return QtyBreakPricerLst.get(0);

		}	
	  
}