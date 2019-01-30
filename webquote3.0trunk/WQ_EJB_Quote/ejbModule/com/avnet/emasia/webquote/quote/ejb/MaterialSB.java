package com.avnet.emasia.webquote.quote.ejb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.ejb.interceptor.EntityManagerInjector;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.MaterialRegional;
import com.avnet.emasia.webquote.entity.NormalPricer;
import com.avnet.emasia.webquote.entity.Pricer;
import com.avnet.emasia.webquote.entity.ProductCategory;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.ProgramPricer;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.RFQSubmissionTypeEnum;
import com.avnet.emasia.webquote.entity.SapInterfaceBatches;
import com.avnet.emasia.webquote.entity.SapMaterial;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.PartMasterCriteria;
import com.avnet.emasia.webquote.user.ejb.BizUnitSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;
import com.avnet.emasia.webquote.vo.PricerInfo;
import com.avnet.emasia.webquote.vo.RfqItemVO;

@Stateless
@LocalBean
@Interceptors(EntityManagerInjector.class)
public class MaterialSB {
	
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	@EJB
	SysConfigSB sysConfigSB;
	
	@EJB
	ManufacturerSB manufacturerSB;
	
	@EJB
	UserSB userSB;
	
	@EJB
	PricerTypeMappingSB  pricerTypeMappingSB;
	
	//@EJB
   // RestrictedCustomerMappingSB restrictedCustomerSB;
	
	@EJB
	SystemCodeMaintenanceSB systemCodeMaintenanceSB;
	
	@EJB
	BizUnitSB bizUnitSB;	
	private static final Logger LOG =Logger.getLogger(MaterialSB.class.getName());
	private static final int MAX_RESULT_IN_PART_MASTER_DOWNLOAD = 501;


    /**
     * Default constructor. 
     */
    public MaterialSB() {
    }
    
 // Delinkage change on Oct 29 , 2013 by Tonmy
    public Material findMaterialByPK(long id){
    	return em.find(Material.class, id);
    }
    
    public Material createMaterial(Material material){
    	Material m = em.merge(material);
    	em.flush();
    	return m;
    }
    
    public List<Material> wFindMaterialByMfrPartNumberWithPage(String fullMfrPart, List<BizUnit> bizUnits, int firstResult, int maxResults){

		List<String> sBizUnits = new ArrayList<String>();
		for(BizUnit bizUnit : bizUnits){
			sBizUnits.add(bizUnit.getName());
		}
		LOG.info("MaterailSB.wFindMaterialByMfrPartNumberWithPage: fullMfrPart='" + fullMfrPart + "'; bizUnit='" + sBizUnits.toString() + "'; firstResult='" + firstResult + "'; maxResults='" + maxResults + "'");
		
    	TypedQuery<Material> query = em.createQuery("select m from Material m join m.manufacturer f join f.bizUnits b where UPPER(m.fullMfrPartNumber) like :fullMfrPart and b.name in :bizUnits and (m.valid is null or m.valid = 1) ORDER BY UPPER(m.fullMfrPartNumber), m.manufacturer", Material.class);
    	LOG.info("MaterailSB.wFindMaterialByMfrPartNumberWithPage: query='" + query.toString() + "'");
    	
    	query.setFirstResult(firstResult)
    	.setMaxResults(maxResults)
    	.setParameter("fullMfrPart", "%" + fullMfrPart.toUpperCase() + "%")
    	.setParameter("bizUnits", sBizUnits);
    	
    	//LOG.info("MaterailSB.wFindMaterialByMfrPartNumberWithPage: query2='" + query.toString() + "'");
    	return query.getResultList();
    }

    //commented by Damonchen@20180726
/*    public int getStatusColor(String mfr, Material material,BizUnit bizUnit, boolean isPricer)
    {
    	int returnInt = 0 ; 
    	if(material != null)
    	{
    		// If it is null . will be considered as true . updated by Tonmy on 9 Sep 2013.
    		if(material.getValid()==null || material.getValid())
    		{
    			//get material details method change.
    			//List<MaterialDetail> materialDetails = material.getMaterialDetails();
    			//List<MaterialDetail> materialDetails = findMaterialsDetailByMaterialId(material.getId());
    			if(material.isHasPricers())
    			{
    				returnInt = 1 ;
    			}
    			else
    			{
    				if(material.getManufacturer()!=null && isPricer)
    	    		{
    					//SAP
    					if(material.getSapPartNumber()==null)
    					{
    						returnInt = 3 ;
    					}
    					else
    					{
    						returnInt = 2 ;
    					}
    	    		}
    	    		else
    	    		{
    	    			returnInt = 2 ;
    	    		}
    			}
    		}
    		else
    		{
    			LOG.info(" material is invalid");
    			if(material.getManufacturer()!=null && isPricer)
	    		{
    				returnInt = 3 ;
	    		}
    			else
    			{
    				returnInt = 2 ;
    			}
    		}
    		
    	}
    	else
    	{
    		Manufacturer manufacturer = manufacturerSB.findManufacturerByName(mfr, bizUnit);
    		if(manufacturer!=null && isPricer)
    		{
    			returnInt = 3;
    		}
    		else
    		{
    			returnInt = 2;
    		}
    	}
    	return returnInt;
    }*/

    public List<ProgramPricer> findProgramMaterialbyMfrAndPartNumber(List<String[]> mfrAndPartNumbers ){
    	
    	int i = 1;
    	int j = 0;
    	
    	StringBuffer sb = new StringBuffer("");
    	sb.append("select p from ProgramMaterial p join p.material m where ");
    	sb.append("(");
    	for(String[] programMaterial : mfrAndPartNumbers){
    		
    		sb.append("(m.manufacturer.name = ?" + i++);
    		sb.append(" and m.fullMfrPartNumber = ?" + i++ );
    		sb.append(" and p.bizUnitBean.name=?" +i++ + ") ");
    		
    		if(j < (mfrAndPartNumbers.size() -1)){
    			sb.append( " or ");
    		}
    		j++;
    	}
    	sb.append(")");
    	
    	sb.append(" and p.programEffectiveDate <= ?" + i++);
    	
    	sb.append(" and p.programClosingDate >= ?" + i++);
    	
    	TypedQuery<ProgramPricer> query = em.createQuery(sb.toString(),ProgramPricer.class);
    	
    	i = 1;
    	for(String[] mfrAndPartNumber : mfrAndPartNumbers){
    		query.setParameter(i++, mfrAndPartNumber[0]);
    		query.setParameter(i++, mfrAndPartNumber[1]);
    		query.setParameter(i++, mfrAndPartNumber[2]);
    	}
    	query.setParameter(i++, new Date());
    	query.setParameter(i++, new Date());
    	query.setFirstResult(0);
    	query.setMaxResults(Integer.parseInt(sysConfigSB.getProperyValue("MAX_SEARCH_RESULT")));
    	
    	return query.getResultList();
    }
    
    public List<Material> batchFindMaterialbyMfrAndPartNumber(List<String[]> mfrAndPartNumbers){
    	
    	int i = 1;
    	int j = 0;
    	
    	StringBuffer sb = new StringBuffer();
    	sb.append("select m from Material m where ");
    	sb.append("(");
    	for(String[] mfrAndPartNumber : mfrAndPartNumbers){
    		
    		sb.append("(m.manufacturer.name = ?" + i++);
    		sb.append(" and UPPER(m.fullMfrPartNumber) = ?" + i++ + ") " );
    		
    		if(j < (mfrAndPartNumbers.size() -1)){
    			sb.append( " or ");
    		}
    		j++;
    	}
    	sb.append(")");
    	
    	
    	TypedQuery<Material> query = em.createQuery(sb.toString(),Material.class);
    	
    	i = 1;
    	for(String[] mfrAndPartNumber : mfrAndPartNumbers){
    		query.setParameter(i++, mfrAndPartNumber[0]);
    		query.setParameter(i++, mfrAndPartNumber[1]);
    	}
    	
    	
    	return query.getResultList();
    }    
    
	public ProductCategory findProductCategoryByName(String name){
		
		TypedQuery<ProductCategory> query = em.createQuery("select p from ProductCategory p where p.categoryCode = :name", ProductCategory.class);
		query.setParameter("name", name);
		List<ProductCategory> rltLst = query.getResultList();
		if (rltLst != null && rltLst.size() > 0)
		{
			return rltLst.get(0);
		}
		else
		{
			return null;
		}		
	}
	
	public List<ProductCategory> findAllProductCategory(){

		Query query = em.createQuery("select p from ProductCategory p order by upper(p.categoryCode)" );
		List<ProductCategory> productCategorys = (List<ProductCategory>)query.getResultList();
		
		return productCategorys;
	}
	
	public List<ProductCategory> mFindProductCategoryByName(String code){

		TypedQuery<ProductCategory> query = em.createQuery("select DISTINCT p from ProductCategory p where UPPER(p.categoryCode) like :code", ProductCategory.class);
		query.setParameter("code", "%" + code.toUpperCase() + "%");
		return query.getResultList();
	}
	
	public List<NormalPricer> findLowestCostMaterials(String mfr, String partNumber, BizUnit bizUnit){
		
		String sql = "select md from MaterialDetail md join md.material m join m.manufacturer mfr " +
				"where UPPER(mfr.name) like :mfr and UPPER(m.fullMfrPartNumber) like :partNumber " + 
				"and md.bizUnitBean.name like :bizUnit and (m.valid is null or m.valid = true)";
		
		TypedQuery<NormalPricer> query = em.createQuery(sql, NormalPricer.class);
		
		query.setParameter("mfr", "%" + mfr.toUpperCase() + "%").
		setParameter("partNumber", "%" + partNumber.toUpperCase() + "%").
		setParameter("bizUnit", bizUnit.getName()).
		setFirstResult(0).
		setMaxResults(Integer.parseInt(sysConfigSB.getProperyValue("MAX_SEARCH_RESULT")) * 5 );
				
		List<NormalPricer> materialDetailsTemp = query.getResultList();
		List<NormalPricer> materialDetails = new ArrayList<NormalPricer>();
		List<NormalPricer> results = new ArrayList<NormalPricer>();
		
		Set<String> partNumbers = new HashSet<String>();
		
		for(NormalPricer materialDetail :  materialDetailsTemp)
		{
			//roll back the fix 1601 , based on Timothy 's request
			//Removed the validity checked by Tough on May 21 , 2014
//			if(com.avnet.emasia.webquote.utilities.util.QuoteUtil.isValidMaterialDetail(materialDetail.getPriceValidity(),materialDetail.getShipmentValidity(), new Date()))
//			{
				materialDetails.add(materialDetail);
//			}
		}
		for(NormalPricer materialDetail :  materialDetails)
		{
				partNumbers.add(materialDetail.getMaterial().getFullMfrPartNumber());
		}

		for(String s : partNumbers)
		{

			NormalPricer lowMaterial = null; 
			
			for(NormalPricer materialDetail :  materialDetails){
				
				if(s.equals(materialDetail.getMaterial().getFullMfrPartNumber())){
					
					if(lowMaterial == null){
						
						lowMaterial = materialDetail;
						
					}else{
						
						if(lowMaterial.getCost() == null && materialDetail.getCost() != null){
							lowMaterial = materialDetail;
							
						}else if(lowMaterial.getCost() != null && materialDetail.getCost() == null){
							continue;
						
						}else{
							
							if(lowMaterial.getCost() > materialDetail.getCost()){
								lowMaterial = materialDetail;
							}
						}
					}
				}
			}
			results.add(lowMaterial);
			
		}
		
		return results;
	}
	
    public Material findExactMaterialByMfrPartNumber(String mfr, String fullMfrPart, BizUnit bizUnit){

		List<String> sBizUnits = new ArrayList<String>();
		sBizUnits.add(bizUnit.getName());
    	
    	TypedQuery<Material> query = em.createQuery("select m from Material m join m.manufacturer f join f.bizUnits b "
    			+ "where f.name = :mfr and m.fullMfrPartNumber = :fullMfrPart and b.name in :bizUnits and m.valid != 0", Material.class);
    	query.setParameter("fullMfrPart", fullMfrPart.toUpperCase())
    	.setParameter("bizUnits", sBizUnits)
    	.setParameter("mfr", mfr.toUpperCase());
    	
    	List<Material> materials =  query.getResultList();
    	if(!materials.isEmpty() ) {
    		return materials.get(0);
    	}
    	return null;
    }
    

	
	  
	  
	  
	  
	  public NormalPricer findMaterialDetail(String mfr, String partNumber, String costIndicator, String bizUnit){
		  
		  TypedQuery<NormalPricer> query = em.createQuery("select d from MaterialDetail d join d.material m join m.manufacturer f join d.bizUnitBean b join d.costIndicator c " +
		  		"where UPPER(f.name) like :mfr and UPPER(m.fullMfrPartNumber) like :partNumber and b.name = :bizUnit and (m.valid is null or m.valid = 1) " +
		  		"and UPPER(c.name) like :costIndicator " + 
		  		"ORDER BY UPPER(m.fullMfrPartNumber), m.manufacturer", NormalPricer.class);
		 
		  query.setParameter("mfr", mfr.toUpperCase())
		  .setParameter("partNumber", partNumber.toUpperCase())
		  .setParameter("costIndicator", costIndicator.toUpperCase())
		  .setParameter("bizUnit", bizUnit);
		  
		  List<NormalPricer> materialDetails = query.getResultList();
		  if(materialDetails!=null)
		  {
			  materialDetails = filterOutFuturePriceAndExpiredPrice(materialDetails);
		  }
		  
		  if (materialDetails.size() == 0){
			return null;
		  }else{
			return materialDetails.get(0); 
		  }
		  
	  }

	
	private List<NormalPricer> filterOutFuturePriceAndExpiredPrice(List<NormalPricer> materialDetails)
	{
		List<NormalPricer>  results = new ArrayList<NormalPricer>();
		Date tempDate = QuoteUtil.getCurrentDateZeroHour();
		if(materialDetails!=null && materialDetails.size()>0)
		{
			for(NormalPricer md : materialDetails)
			{
				if((md.getQuotationEffectiveDate()!=null && !md.getQuotationEffectiveDate().after(tempDate))
						&& (md.getQuotationEffectiveTo()!=null && !md.getQuotationEffectiveTo().before(tempDate)))
				{
					results.add(md);
				}
			}
			return results;
		}
		else
			return materialDetails;
	}
	

	public List<Pricer> findMaterialsDetailByMaterialId(long materialId){
    	TypedQuery<Pricer> query = em.createQuery("select d from Pricer d where d.material.id = :materialId", Pricer.class);
    	query.setParameter("materialId", materialId);
    	return query.getResultList();
	}
	
	public Long findCountMaterialBymfrAndPN(Manufacturer manufacturer,String fullMfrPartNumber) {
		//SELECT COUNT(*) INTO v_count FROM material WHERE manufacturer_id = v_quoted_mfr AND full_mfr_part_number = v_quoted_pn;        
		TypedQuery<Long> query = em.createQuery("select count(s) from Material s where s.manufacturer = :manufacturer and s.fullMfrPartNumber=:fullMfrPartNumber",Long.class);
		query.setParameter("manufacturer", manufacturer);
		query.setParameter("fullMfrPartNumber", fullMfrPartNumber);

		return query.getSingleResult();
	}
	
	public Material findMinMaterialBymfrAndPN(Manufacturer manufacturer,String fullMfrPartNumber) {
		//SELECT MIN(id) INTO v_seq FROM material WHERE manufacturer_id = v_quoted_mfr AND full_mfr_part_number = v_quoted_pn;        
		try{
			TypedQuery<Material> query = em.createQuery("select s from Material s where s.manufacturer = :manufacturer and s.fullMfrPartNumber=:fullMfrPartNumber order by s.id asc",Material.class);
			query.setParameter("manufacturer", manufacturer);
			query.setParameter("fullMfrPartNumber", fullMfrPartNumber);
			query.setMaxResults(1);
			return query.getSingleResult();
		}catch(NoResultException e){
			LOG.log(Level.SEVERE, "cannot find Material {0}, {1}", new Object[]{manufacturer.getName(), fullMfrPartNumber});
			
			throw e;
		}
	}
	
	public List<Tuple> findSapPartNumbersByPNandMfrName(String fullMfrPart, String mfrName, BizUnit bizUnitPar, boolean useLike){
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();    
		Root<Material> r = cq.from(Material.class);
		Join<Material,Manufacturer> mfr = r.join("manufacturer");
		Join<Manufacturer, BizUnit> bizUnit = mfr.join("bizUnits");
		Join<Material, SapMaterial> sapMaterial = r.join("sapMaterials");
		Predicate predicateBizUnit = cb.equal(bizUnit.<String>get("name"), bizUnitPar.getName());

		Predicate predicateMfrPn = cb.equal(r.<String>get("fullMfrPartNumber"), fullMfrPart);
		
		List<Predicate> predicateSpecialMaterial = new ArrayList<Predicate>();
		//valid selected
		Predicate predicateValid = cb.notEqual(r.<String>get("specialMaterial"), "0001");
		//valid null
		Predicate predicateValidNull =cb.isNull(r.<String>get("specialMaterial"));
		predicateSpecialMaterial.add(predicateValid);
		predicateSpecialMaterial.add(predicateValidNull);

		Predicate predicateMfr = cb.equal(mfr.<String>get("name"), mfrName);
		Predicate predicateNotDeleted = cb.notEqual(sapMaterial.<Boolean>get("deletionFlag"), true);
		String mfrAndPn =mfrName + fullMfrPart;
		if(useLike){
			cq.multiselect(sapMaterial.get("sapPartNumber"))
			    .where(cb.and(
			    		cb.like(sapMaterial.<String>get("sapPartNumber"), mfrName.toUpperCase() + "%"),
			    		cb.or(predicateSpecialMaterial.toArray(new Predicate[0])),
			    		predicateMfrPn,			    		
			    		predicateBizUnit,
			    		predicateMfr,
			    		predicateNotDeleted
			    		))
			    .orderBy(cb.asc(sapMaterial.get("sapPartNumber")));
		} else {
			cq.multiselect(sapMaterial.get("sapPartNumber"))
		    .where(cb.and(
		    		cb.like(sapMaterial.<String>get("sapPartNumber"), mfrAndPn.toUpperCase()),
		    		cb.or(predicateSpecialMaterial.toArray(new Predicate[0])),
		    		predicateMfrPn,		    		
		    		predicateBizUnit,
		    		predicateMfr,
		    		predicateNotDeleted
		    		))
		    .orderBy(cb.asc(sapMaterial.get("sapPartNumber")));			
		}

		Query query = (Query) em.createQuery(cq);
    	
		List<Tuple> tuples = query.getResultList();

		return tuples;
    }

	public List<Material> findMaterialBy(String[] mFRs,String partNumber, String[] productGroup2s,String[] productCategorys,boolean isOnline,int startRow,int range) {
		
		/*CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Material> cq = cb.createQuery(Material.class);
		
		Root<Material> material = cq.from(Material.class);
		Join<Material, Manufacturer> manufacturer = material.join("manufacturer", JoinType.LEFT);
		Join<Material, ProductGroup> productGroup = material.join("productGroup2", JoinType.LEFT);
		Join<Material, ProductCategory> productCategory = material.join("productCategory", JoinType.LEFT);
		
		cq.orderBy(cb.asc(material.get("manufacturer").get("name")));
		
		//construct where 
		List<Predicate> predicates=populatePredicates(cb,
				material,
				manufacturer,
				productGroup,
				productCategory,
				mFRs,
				partNumber,
				productGroup2s,
				productCategorys);
		
		//set where condition
		if (null!=predicates) {
			cq.where(predicates.toArray(new Predicate[]{}));
		} 
		
		TypedQuery<Material> q = em.createQuery(cq);
		if(isOnline) {
			LOG.info("startRow==>>>>>>"+startRow);
			LOG.info("endRow====>>>>"+range);
			q.setFirstResult(startRow);
			q.setMaxResults(range);
			
		}
		
		List<Material> results = q.getResultList();
		return results;*/
		return null;
		
		
		
		
		
		
	}

	private List<Predicate> populatePredicates(CriteriaBuilder cb,
			Root<Material> material,
			Join<Material, Manufacturer> manufacturer,
			Join<Material, ProductGroup> productGroup,
			Join<Material, ProductCategory> productCategory, String[] mFRs,
			String partNumber, String[] productGroup2s,
			String[] productCategorys) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		
		List<Predicate> valids = new ArrayList<Predicate>();
		//valid selected
		Predicate predicateValid = cb.equal(material.<Boolean>get("valid"), true);
		//valid null
		Predicate predicateValidNull = cb.or(cb.isNull(material.<Boolean>get("valid")));
		valids.add(predicateValid);
		valids.add(predicateValidNull);
		
		predicates.add(cb.or(valids.toArray(new Predicate[0])));


		
		//MFR 
		if(null!=mFRs && mFRs.length>0) {
			CriteriaBuilder.In<String> in = cb.in(manufacturer.<String>get("name"));
			for(String name : mFRs){
				in.value(name);
			}
			predicates.add(in);			
		}
		
		//MFR Full Part Name
		if(null!=partNumber && !QuoteUtil.isEmpty(partNumber)) {
			Predicate predicate = cb.like(cb.upper(material.<String>get("fullMfrPartNumber")), "%" + partNumber.toUpperCase() + "%" );
			predicates.add(predicate);
		}
		
		//product group  
		if(null!=productGroup2s && productGroup2s.length>0) {
			CriteriaBuilder.In<String> in = cb.in(productGroup.<String>get("name"));
			for(String name : productGroup2s){
				in.value(name);
			}
			predicates.add(in);			
		}
		
		//product category  
		if(null!=productCategorys && productCategorys.length>0) {
			CriteriaBuilder.In<String> in = cb.in(productCategory.<String>get("categoryCode"));
			for(String name : productGroup2s){
				in.value(name);
			}
			predicates.add(in);			
		}
		
		return predicates;
	}

	public String findDefaultSapPartNumber(String partNumber, String mfr, BizUnit bizUnit){
		if(!QuoteUtil.isEmpty(mfr) && !QuoteUtil.isEmpty(partNumber)){
		  List<Tuple> sapPartNumberTuples  = findSapPartNumbersByPNandMfrName(partNumber, mfr, bizUnit, true);
		  if(!QuoteUtil.isEmpty(mfr) && !QuoteUtil.isEmpty(partNumber)){
			  String sapPartNumberHaveEqual = null;
			  String sapPartNumberNoHaveEqual = null;
			  String mfrPn = mfr + partNumber;
			  for(int i = 0 ; i < sapPartNumberTuples.size() ; i++)
			  {
				  String sapPartNumber = (String) sapPartNumberTuples.get(i).get(0);
				  if(!QuoteUtil.isEmpty(partNumber) && !QuoteUtil.isEmpty(mfr)) {
					  if(sapPartNumber.equals(mfrPn)){
						  sapPartNumberNoHaveEqual = sapPartNumber;
					  }
					  if(sapPartNumberHaveEqual == null && sapPartNumber.startsWith(mfr) && sapPartNumber.contains("=")){
						  sapPartNumberHaveEqual = sapPartNumber;
					  }else if(sapPartNumberNoHaveEqual == null && sapPartNumber.startsWith(mfr)){
						  sapPartNumberNoHaveEqual = sapPartNumber;
					  }
				  }
			  }
			  if(sapPartNumberNoHaveEqual != null){
				  return sapPartNumberNoHaveEqual;
			  } else if(sapPartNumberHaveEqual != null) {
				  return sapPartNumberHaveEqual;
			  }			  
		  }
		}
		return null;
	
	}	
	
	
	
	
	
	
	private List<ProgramPricer> getProgMatListByBizUint(Material material, String bizUnitName)
	{
		List<ProgramPricer> programMaterialList = new ArrayList<ProgramPricer>();
		if(material!=null){
		List<Pricer> materialDetails = findMaterialsDetailByMaterialId(material.getId());
		if(null!=materialDetails && materialDetails.size()>0)
		{
			for(Pricer md : materialDetails)
			{
				if(md instanceof ProgramPricer)
    			{
					if(bizUnitName!=null && md.getBizUnitBean()!=null && bizUnitName.equalsIgnoreCase(md.getBizUnitBean().getName()))
					{
						programMaterialList.add((ProgramPricer)md);
					}
    			}
			}
		}
		}
		return programMaterialList;
	}
	
	private List<ProgramPricer> getValidProgMatListByBizUint(Material material,String bizUnitName)
	{
		Date currentDate = getCurrentDate();
		List<ProgramPricer> returnList = new ArrayList<ProgramPricer>();
		List<ProgramPricer> programMaterialList = getProgMatListByBizUint(material,bizUnitName);
		if(null!=programMaterialList && programMaterialList.size()>0)
		{
			for(ProgramPricer md : programMaterialList)
			{
				if(md.getProgramClosingDate()!=null  && md.getProgramEffectiveDate()!=null && !currentDate.after( md.getProgramClosingDate()) && !currentDate.before(md.getProgramEffectiveDate()))
				{	
					returnList.add(md);
				}
			}
		}
		return returnList;
	}
	
	
	public ProgramPricer getSpecifiedValidProgMatByBizUintAndCostIndicator(Material material,String bizUnitName, String costIndicator)
	{
		List<ProgramPricer> programMaterialList = getValidProgMatListByBizUint(material,bizUnitName);
		if(null!=programMaterialList && programMaterialList.size()>0)
		{
			for(ProgramPricer md : programMaterialList)
			{
				if(md.getCostIndicator() != null && md.getCostIndicator().getName() != null && md.getCostIndicator().getName().equals(costIndicator)){
					return md;
				}
			}
		}
		return null;
	}
	
	public ProgramPricer getOneValidProgMatByBizUint(Material material,String bizUnitName)
	{
		List<ProgramPricer> programMaterialList = getValidProgMatListByBizUint(material,bizUnitName);
		if(null!=programMaterialList && programMaterialList.size()>0)
		{
			return programMaterialList.get(0);
		}
		return null;
	}
	

	public Date getCurrentDate()
	{
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTime();
	}
	

	
	public ProgramPricer getProgramMat(Material material , BizUnit bizUnit,String costIndicator)
	{
		ProgramPricer progMat = null;
		if(bizUnit!=null && bizUnit.getName()!=null && costIndicator!=null)
		{
		   //progMat =  quoteItem.getQuotedMaterial().getSpecifiedValidProgMatByBizUintAndCostIndicator(bizUnit.getName(), quoteItem.getCostIndicator());
			progMat =  getSpecifiedValidProgMatByBizUintAndCostIndicator(material, bizUnit.getName(), costIndicator);
		}
		return progMat;
	}
	
	
	/*
	 * find the closest expired abook cost.
	 * @params: List<MaterialDetail> materialDetailList;
	 * @return: MaterialDetail;
	 */
	
	private NormalPricer findClosestExpiredAbookCost(List<NormalPricer> materialDetails )
	{
		if(materialDetails==null || materialDetails.size() == 0)
			return null;
		
		Date tempDate = QuoteUtil.getCurrentDateZeroHour();
		NormalPricer closestExpiredAbookCost = null;
		for(NormalPricer materialDetail : materialDetails)
		{
			if((materialDetail.getQuotationEffectiveDate()!=null && !materialDetail.getQuotationEffectiveDate().after(tempDate))
				&& (materialDetail.getQuotationEffectiveTo()!=null && !materialDetail.getQuotationEffectiveTo().before(tempDate)))
			{
				continue;
			}
			else
			{
				if(materialDetail.getCostIndicator()!=null && QuoteSBConstant.A_BOOK_COST.equalsIgnoreCase(materialDetail.getCostIndicator().getName()))
				{
					if(closestExpiredAbookCost==null)
					{
						closestExpiredAbookCost = materialDetail;
					}
					else
					{
						if(materialDetail.getQuotationEffectiveTo()!=null && closestExpiredAbookCost.getQuotationEffectiveTo()!=null && 
								materialDetail.getQuotationEffectiveTo().after(closestExpiredAbookCost.getQuotationEffectiveTo()))
						{
							closestExpiredAbookCost = materialDetail;
						}
					}
					
				}
			}
		}
		return closestExpiredAbookCost;
	}
	
	
	
	
	
	public List<Material> partMasterEnquiry(PartMasterCriteria criteria, boolean isOnline) {
	
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Material> c = cb.createQuery(Material.class);
		c.distinct(true);
		Root<Material> material = c.from(Material.class);
		List<Predicate> criterias = new ArrayList<Predicate>();
		Predicate p1 = null;
		
		if (!QuoteUtil.isEmpty(criteria.getMfr())) {
			Join<Material, Manufacturer> manufacturer = material.join("manufacturer");
			Predicate mfrPredicate = cb.equal(cb.upper(manufacturer.<String> get("name")), criteria.getMfr().toUpperCase());
			criterias.add(mfrPredicate);
		}
		if (!QuoteUtil.isEmpty(criteria.getPartNumber())) {
			Predicate pn = cb.like(cb.upper(material.<String> get("fullMfrPartNumber")), "%" + criteria.getPartNumber().toUpperCase()+"%");
			criterias.add(pn);
		}
		
		Join<Material, MaterialRegional> mRegional = material.join("materialRegionals");
		if(!QuoteUtil.isEmpty(criteria.getRegion())) {
			Join<MaterialRegional, BizUnit> mBizUnit = mRegional.join("bizUnit");
			p1 = cb.equal(mBizUnit.<String>get("name"), criteria.getRegion());
			criterias.add(p1);
		}
		if (!QuoteSBConstant.OPTION_ALL.equals(criteria.getSalesCostPart())) {
			Predicate pn = cb.equal(mRegional.<Boolean> get("salesCostFlag"),
					QuoteSBConstant.OPTION_YES.equals(criteria.getSalesCostPart())? true :false);
			criterias.add(pn);
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup1())) {
			Join<MaterialRegional, ProductGroup> productGroup = mRegional.join("productGroup1");
			Predicate pn = cb.like(cb.upper(productGroup.<String> get("name")) , "%" + criteria.getProductGroup1().toUpperCase()+"%");
			criterias.add(pn);
		}
		if (!QuoteUtil.isEmpty(criteria.getProductGroup2())) {
			Join<MaterialRegional, ProductGroup> productGroup2 = mRegional.join("productGroup2");
			Predicate pn = cb.like(cb.upper(productGroup2.<String> get("name")), "%" + criteria.getProductGroup2().toUpperCase()+"%");
			criterias.add(pn);
		}
		if (!QuoteUtil.isEmpty(criteria.getProductGroup3())) {
			Predicate pn = cb.like(cb.upper(mRegional.<String> get("productGroup3")), "%" + criteria.getProductGroup3().toUpperCase()+"%");
			criterias.add(pn);
		}
		if (!QuoteUtil.isEmpty(criteria.getProductGroup4())) {
			Predicate pn = cb.like(cb.upper(mRegional.<String> get("productGroup4")), "%" + criteria.getProductGroup4().toUpperCase()+"%");
			criterias.add(pn);
		}
		
		
		if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}
		c.orderBy(cb.asc(material.get("id")));
		TypedQuery<Material> q = em.createQuery(c);
		if(isOnline){
			q.setFirstResult(0);
			q.setMaxResults(MAX_RESULT_IN_PART_MASTER_DOWNLOAD);
		}
		else
		{
			q.setFirstResult(criteria.getStart());
			q.setMaxResults(criteria.getEnd());
		}

		List<Material> searchResult = q.getResultList();

		//Add to solve Send Off Line Report java.lang.OutOfMemoryError: Java heap space
	   	em.clear();
		return searchResult;
	}
	
	
	public int partMasterEnquiryCount(PartMasterCriteria criteria) 
	{

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> c = cb.createQuery(Long.class);
		c.distinct(true);
		Root<Material> material = c.from(Material.class);
		List<Predicate> criterias = new ArrayList<Predicate>();
		
		if (!QuoteUtil.isEmpty(criteria.getMfr())) {
			Join<Material, Manufacturer> manufacturer = material.join("manufacturer");
			Predicate mfr = cb.equal(manufacturer.<String> get("name"), criteria.getMfr().toUpperCase());
			criterias.add(mfr);
		}
		if (!QuoteUtil.isEmpty(criteria.getPartNumber())) {
			Predicate pn = cb.like(material.<String> get("fullMfrPartNumber"), "%" + criteria.getPartNumber().toUpperCase()+"%");
			criterias.add(pn);
		}
		
		if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}
		
		c.select(cb.count(material));
		
		TypedQuery<Long> q = em.createQuery(c);
		
		Long count = q.getSingleResult();
		if(count!=null)
			return count.intValue();
		else
			return 0;
	}
	
	public void applyAqLogic(List<QuoteItem> items, RFQSubmissionTypeEnum submissionType) {
		for (QuoteItem item : items) {
			RfqItemVO rfItem = item.createRfqItemVO();
			this.applyAQLogic(Arrays.asList(rfItem), submissionType);
			rfItem.fillPriceInfoToQuoteItem(item);
			if (QuoteSBConstant.QUOTE_STAGE_FINISH.equals(item.getStage())
					&& QuoteSBConstant.RFQ_STATUS_AQ.equals(item.getStatus())) {
				item.postProcessAfterFinish();
			}
			
		}
	}
	/*
	 * useNoneSalesCostPricerOnly, use null if need consider both NoneSalesCostPricer and SalesCostPricer
	 * In normal RFQ submission, use null
	 * DP use NoneSalesCostPricer only, so set it as true
	 */
	public void applyAQLogic(List<RfqItemVO> rfqItems, RFQSubmissionTypeEnum submissionType) {
		for(RfqItemVO rfItem : rfqItems) {
			Material material = find(rfItem.getMfr(), rfItem.getRequiredPartNumber());
			
			User sales= userSB.findByEmployeeIdLazily(rfItem.getRfqHeaderVO().getSalesEmployeeNumber());
			List<Customer> allowedCustomers =  new ArrayList<>();
			if (sales != null) {
				allowedCustomers = sales.getCustomers();
			}
			if(material != null) {
				material.applyAQLogic(rfItem, rfItem.getRfqHeaderVO().getBizUnit(), rfItem.getSoldToCustomer(), 
						rfItem.getEndCustomer(), allowedCustomers, submissionType);
			} else {
				//if material is null we have not clear rfqItem, so do it here to confirm
				// that no found pricer let BuyCurr = RfqCurr by function [defaultAfterAqOrCostIndicator] ;
				// let it adjust;
				rfItem.setBuyCurr(null);
			}
			rfItem.adjustAfterNoFoundBuyCurr();
			rfItem.setMaterial(material);
			
			rfItem.calStageStatusWithStatusColor();
				
		}
		
	}
	
	/**   
	* @Description: Capture out  the status color , then deal with the draft quote 
	* @author 042659
	* @param rfItem      
	* @return void    
	* @throws  
	*/ 
	 //commented by Damonchen@20180726
/*	private void handleRfqItemWithStatusColor(RfqItemVO rfqItem) {
		rfqItem=fillStatusColorToRfqItem(rfqItem);
		if(rfqItem.getStatusColor()==3){
			rfqItem.setStage(QuoteSBConstant.QUOTE_STAGE_DRAFT);
			rfqItem.setStatus(QuoteSBConstant.RFQ_STATUS_DQ);
		}else if(rfqItem.getStatusColor()==2){
			rfqItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
			rfqItem.setStatus(QuoteSBConstant.RFQ_STATUS_QC);
		}

		
	}*/
	 //commented by Damonchen@20180726
/*	private RfqItemVO fillStatusColorToRfqItem(RfqItemVO itemVo) {
		BizUnit bu=bizUnitSB.findByPk(itemVo.getRfqHeaderVO().getBizUnit());
		itemVo.setStatusColor(getStatusColor(itemVo.getMfr(), itemVo.getMaterial(),bu,manufacturerSB.isPricerByBizUnitAndMfr(bu, itemVo.getMfr())));
		return itemVo;
	}*/

	public List<Material> getMaterialList(String mfr,String fullPartNumber,String bizUnitName,boolean excatQuery, int firstResult, int maxResults) {
		LOG.fine("call getMaterial");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Material> cq = cb.createQuery(Material.class);

		Root<Material> material = cq.from(Material.class);
		Join<Material, Pricer> pricer = material.join("pricers");
		Join<Pricer, BizUnit> bizUnit = pricer.join("bizUnitBean");

		Expression<String> fullMfrPartNumber = material.get("fullMfrPartNumber");
		Expression<String> mfrName = material.<Manufacturer>get("manufacturer").get("name");
		List<Predicate> subPredicatesWhole = new ArrayList<Predicate>();
		List<Predicate> subPredicates = new ArrayList<Predicate>();
		if (StringUtils.isNotBlank(mfr)
				&& !StringUtils.equalsIgnoreCase(mfr, QuoteSBConstant.ALL)) {
			Predicate predicate = cb.equal(cb.upper(mfrName), mfr.toUpperCase());
			subPredicates.add(predicate);
		}
		if (StringUtils.isNotBlank(fullPartNumber)) {
			if(excatQuery) {
				Predicate predicate = cb.equal(cb.upper(fullMfrPartNumber), fullPartNumber);
				subPredicates.add(predicate);
			}else {
				Predicate predicate = cb.like(cb.upper(fullMfrPartNumber),  QuoteSBConstant.SIGN_LIKE + fullPartNumber +QuoteSBConstant.SIGN_LIKE );
				subPredicates.add(predicate);
			}
			
		}
		subPredicatesWhole.add(cb.and(subPredicates.toArray(new Predicate[0])));
		List<Predicate> predicates = new ArrayList<Predicate>();
		predicates.add(cb.or(subPredicatesWhole.toArray(new Predicate[0])));
		predicates.add(cb.or(cb.isNull(material.get("valid")), cb.equal(material.get("valid"), 1)));
		if(StringUtils.isNotBlank(bizUnitName)) {
			predicates.add(cb.equal(bizUnit.get("name"), bizUnitName));
		}
		
		cq.select(material);
		cq.distinct(true);
		cq.where(predicates.toArray(new Predicate[] {}));

		List<Material> results = null;
		TypedQuery<Material> q = em.createQuery(cq);
		q.setFirstResult(firstResult).setMaxResults(maxResults);
		results = q.getResultList();
		if (results != null && results.size() > 0) {
			for (Material m : results) {
				em.detach(m);
			}
		}
		return results;
	}

	public Material find(String mfr, String fullMfrPartNumber) {
		TypedQuery<Material> query = em.createQuery("select m from Material m where (m.valid is null or m.valid=true) and "
				+ "m.manufacturer.name like :mfr and m.fullMfrPartNumber= :fullMfrPartNumber", Material.class);
		query.setParameter("mfr", mfr);
		query.setParameter("fullMfrPartNumber", fullMfrPartNumber);
		List<Material> materials = query.getResultList();
		if (! materials.isEmpty()) {
			return materials.get(0);
		}
		
		return null;
	}
	
public List<Material> restricFind(String mfr, String fullMfrPartNumber, String region) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Material> cq = cb.createQuery(Material.class);
		List<Predicate> predicates = new ArrayList<>();
		Root<Material> material = cq.from(Material.class);
		Join<Material, SapMaterial> sapMaterial = material.join("sapMaterials", JoinType.LEFT);
		Join<Material, Manufacturer> manufacturer = material.join("manufacturer");
		Join<Material, MaterialRegional> mRegional = material.join("materialRegionals");
		if (!QuoteUtil.isEmpty(region)) {
			Join<MaterialRegional, BizUnit> mBizUnit = mRegional.join("bizUnit");
			predicates.add(cb.equal(mBizUnit.<String>get("name"), region));
		}
		
		Predicate p1 = cb.isNull(material.<Boolean>get("valid"));
		Predicate p2 = cb.equal(material.<Boolean>get("valid"), true);
		predicates.add(cb.or(p1, p2));
		
		Predicate p3 = cb.isNull(sapMaterial.<Boolean>get("deletionFlag"));
		Predicate p4 = cb.equal(sapMaterial.<Boolean>get("deletionFlag"), false);
		predicates.add(cb.or(p3, p4));
		
		
		if (mfr != null && mfr.trim().length()>0 && !org.apache.commons.lang.StringUtils.equalsIgnoreCase(QuoteSBConstant.WILDCARD_ALL,mfr.toUpperCase())) {
			predicates.add(cb.equal(manufacturer.<String>get("name"), mfr.toUpperCase()));
		}

		if (fullMfrPartNumber != null) {
			predicates.add(cb.equal(material.<String>get("fullMfrPartNumber"), fullMfrPartNumber.toUpperCase()));
		}
		cq.where(cb.and(predicates.toArray(new Predicate[0])));
		
		TypedQuery<Material> query = em.createQuery(cq);
		
		//query.setMaxResults(1);

		return query.getResultList();
	}

	public List<Material> wildcardFind(String mfr, String fullMfrPartNumber, String region, int maxResults) {
		PricerInfo pricerInfo =  new PricerInfo();
		pricerInfo.setMfr(mfr);
		pricerInfo.setFullMfrPartNumber(fullMfrPartNumber);
		pricerInfo.setBizUnit(region);
		
		
		return searchForMaterial(pricerInfo, 0, maxResults);

	}
	
	public List<Material> wildcardFind(String mfr, String fullMfrPartNumber) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Material> cq = cb.createQuery(Material.class);
		List<Predicate> predicates = new ArrayList<>();
		Root<Material> material = cq.from(Material.class);
		Join<Material, SapMaterial> sapMaterial = material.join("sapMaterials", JoinType.LEFT);
		Join<Material, Manufacturer> manufacturer = material.join("manufacturer");
		Predicate p1 = cb.isNull(material.<Boolean>get("valid"));
		Predicate p2 = cb.equal(material.<Boolean>get("valid"), true);
		predicates.add(cb.or(p1, p2));
		
		Predicate p3 = cb.isNull(sapMaterial.<Boolean>get("deletionFlag"));
		Predicate p4 = cb.equal(sapMaterial.<Boolean>get("deletionFlag"), false);
		predicates.add(cb.or(p3, p4));
		
		
		if (mfr != null && !org.apache.commons.lang.StringUtils.equalsIgnoreCase(QuoteSBConstant.WILDCARD_ALL,mfr.toUpperCase())) {
			predicates.add(cb.equal(manufacturer.<String>get("name"), mfr.toUpperCase()));
		}

		if (fullMfrPartNumber != null) {
			predicates.add(cb.like(material.<String>get("fullMfrPartNumber"), "%" + fullMfrPartNumber.toUpperCase() + "%"));
		}
		cq.where(cb.and(predicates.toArray(new Predicate[0])));
		
		TypedQuery<Material> query = em.createQuery(cq);
		
		query.setMaxResults(Integer.parseInt(sysConfigSB.getProperyValue("MAX_SEARCH_RESULT")));

		return query.getResultList();
	}

	
	public List<PricerInfo> searchForMaterialPopup(PricerInfo pricerInfo) {
		
		List<PricerInfo> results = new ArrayList<>();
		
		if (pricerInfo.getSalesId() != 0l){
			User sales = em.find(User.class, pricerInfo.getSalesId());
			pricerInfo.setAllowedCustomers(sales.getCustomers());
		} else {
			pricerInfo.setAllowedCustomers(new ArrayList<Customer>());
		}
		/*
		List<Material> materials = this.wildcardFind(pricerInfo.getMfr(), pricerInfo.getFullMfrPartNumber());
		for (Material material : materials) {
			if (! material.isValidForMaterialPoupup(pricerInfo.getBizUnit(), pricerInfo.getSoldToCustomer(), 
					pricerInfo.getEndCustomer(), pricerInfo.getAllowedCustomers())){
				continue;
			}
		
			pricerInfo.setMfr(material.getManufacturer().getName());
			pricerInfo.setFullMfrPartNumber(material.getFullMfrPartNumber());
			
			PricerInfo pricerInfo2 = PricerInfo.createInstance(pricerInfo);
			
			material.applyDefaultCostIndicatorLogic(pricerInfo2, true);
			results.add(pricerInfo2);
		}
		*/
		int i = 0;
		//int j = 0;
		int countOnceSearch = 200;
		do {
			List<Material> materials = searchForMaterial(pricerInfo, i * countOnceSearch, countOnceSearch);
			if (materials.isEmpty()) {
				break;
			}
			for (Material material : materials ) {
				/*
				if (! material.isValidForMaterialPoupup(pricerInfo.getBizUnit(), pricerInfo.getSoldToCustomer(), 
						pricerInfo.getEndCustomer(), pricerInfo.getAllowedCustomers())){
					continue;
				}
				*/
				pricerInfo.setMfr(material.getManufacturer().getName());
				pricerInfo.setFullMfrPartNumber(material.getFullMfrPartNumber());
				
				PricerInfo pricerInfo2 = PricerInfo.createInstance(pricerInfo);
				material.applyDefaultCostIndicatorLogic(pricerInfo2, true);
				//remove this strange logic.
				//pricerInfo2.setPricerId(j++);
				results.add(pricerInfo2);
				if (results.size() >= 50) {
					break;
				}
			}
			//this said no more data in db. no need to do search again.
			if (materials.size() < countOnceSearch) break;
			i++;
		} while(i < 1000); //just a safeguard to avoid infinite loop
			
		return results;
	}	
	
	private List<Material> searchForMaterial(PricerInfo pricerInfo, int first, int max) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Material> c = cb.createQuery(Material.class);
		Root<Material> material = c.from(Material.class);
		Join<Material, SapMaterial> sapMaterial = material.join("sapMaterials", JoinType.LEFT);
		Join<Material, Manufacturer> manufacturer = material.join("manufacturer");
//		Join<Material, MaterialRegional> materialRegional = material.join("materialRegionals", JoinType.LEFT);
		Join<Material, Pricer> pricer = material.join("pricers", JoinType.LEFT);
		Join<Pricer, BizUnit> bizUnit = pricer.join("bizUnitBean", JoinType.LEFT);
		c.distinct(true);
		
		Predicate materialPredicate = null;

		if (! StringUtils.isEmpty(pricerInfo.getMfr()) && ! pricerInfo.getMfr().equalsIgnoreCase(QuoteSBConstant.WILDCARD_ALL)) {
			materialPredicate = cb.like(manufacturer.<String>get("name"), pricerInfo.getMfr());
			materialPredicate = cb.and(materialPredicate, cb.like(material.<String>get("fullMfrPartNumber"), "%" + pricerInfo.getFullMfrPartNumber().toUpperCase() + "%"));
		} else {
			materialPredicate = cb.like(material.<String>get("fullMfrPartNumber"), "%" + pricerInfo.getFullMfrPartNumber().toUpperCase() + "%");
		}
		
//		predicates.add(cb.or(cb.isNull(material.<Boolean>get("valid")), cb.equal(material.<Boolean>get("valid"), true)));
		Predicate deletePredicate = cb.or(cb.isNull(sapMaterial.<Boolean>get("deletionFlag")), cb.equal(sapMaterial.<Boolean>get("deletionFlag"), false));
		
		Predicate pricerPredicate = cb.lessThan(pricer.<Date>get("quotationEffectiveDate"), new Date());
		Calendar cal =  Calendar.getInstance();
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		pricerPredicate = cb.and(pricerPredicate, cb.greaterThanOrEqualTo(pricer.<Date>get("quotationEffectiveTo"), cal.getTime()));
		pricerPredicate = cb.and(pricerPredicate, cb.like(bizUnit.<String>get("name"), pricerInfo.getBizUnit()));
//		pricerPredicate = cb.and(pricerPredicate, cb.like(materialRegional.<BizUnit>get("bizUnit").<String>get("name"), pricerInfo.getBizUnit()));
		
		Join<Manufacturer, BizUnit> mfrBizUnit = manufacturer.join("bizUnits");
		Predicate mfrBizUnitPredicate = cb.like(mfrBizUnit.get("name"), pricerInfo.getBizUnit());
		
		c.where(cb.and(materialPredicate, cb.or(deletePredicate, pricerPredicate), mfrBizUnitPredicate));
		
		TypedQuery<Material> q = em.createQuery(c);
		q.setFirstResult(first);
		q.setMaxResults(max);
		return q.getResultList();

	}	
	
	public List<PricerInfo> searchForPricerPopup(PricerInfo pricerInfo) {
		Material material = this.find(pricerInfo.getMfr(), pricerInfo.getFullMfrPartNumber());
		
		User sales = em.find(User.class, pricerInfo.getSalesId());
		pricerInfo.setAllowedCustomers(sales.getCustomers());
		
		if (material != null) {
		    return material.searchForPricerPopup(pricerInfo);	    	
		}
		
		return new ArrayList<PricerInfo>();
	}
	
	public Material findMaterialByPricerId(long pricerId) {
		TypedQuery<Pricer> query = em.createQuery("select p from Pricer p where p.id = :pricerId ", Pricer.class);
		query.setParameter("pricerId", pricerId);
		List<Pricer> pricers = query.getResultList();
		if (! pricers.isEmpty()) {
			return pricers.get(0).getMaterial();
		}
		return null;
	}
	
	public Pricer findPricerById(long pricerId) {
		return em.find(Pricer.class,pricerId);
	}

	/**   
	* @Description: call  this  function if it is not last submited step on rfq submisiton MB
	* @author 042659
	* @param pRfqItems
	* @param normalrfqsubmission      
	* @return void    
	* @throws  
	*/  
	public void applyDefaultCostIndictorLogic(List<RfqItemVO> pRfqItems) {
		// TODO Auto-generated method stub
		for(RfqItemVO rfItem : pRfqItems) {
			Material material = find(rfItem.getMfr(), rfItem.getRequiredPartNumber());
			
			User sales= userSB.findByEmployeeIdLazily(rfItem.getRfqHeaderVO().getSalesEmployeeNumber());
			List<Customer> allowedCustomers =  new ArrayList<>();
			if (sales != null) {
				allowedCustomers = sales.getCustomers();
			}
			
			if(material != null) {
				material.applyDefaultCostIndictorLogic(rfItem, rfItem.getRfqHeaderVO().getBizUnit(), rfItem.getSoldToCustomer(), 
						rfItem.getEndCustomer(), allowedCustomers);
			} else {
				rfItem.clearPricingInfo();
			}
			
			rfItem.setMaterial(material);
			//rfItem=fillStatusColorToRfqItem(rfItem);
//			handleRfqItemWithStatusColor(rfItem);
			rfItem.calStatusColor();
		}
		
	}
	
	
	public List<Material> materialFindForCataLogSearch(String mfr, String fullMfrPartNumber, String region, int maxResults) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Material> c = cb.createQuery(Material.class);
		Root<Material> material = c.from(Material.class);
		Join<Material, Manufacturer> manufacturer = material.join("manufacturer");
		Join<Material, MaterialRegional> materialRegional = material.join("materialRegionals", JoinType.INNER);
		//c.distinct(true);
		Predicate p0 = cb.like(manufacturer.<String>get("name"), "%" + mfr.toUpperCase() + "%");
		p0 = cb.and(p0, cb.like(material.<String>get("fullMfrPartNumber"), "%" + fullMfrPartNumber.toUpperCase() + "%"));
		Join<Manufacturer, BizUnit> mfrBizUnit = manufacturer.join("bizUnits");
		Predicate p1 = cb.equal(mfrBizUnit.get("name"), region);
		Join<MaterialRegional, BizUnit> mBizUnit = materialRegional.join("bizUnit");
		Predicate p2 = cb.equal(mBizUnit.<String>get("name"), region);
		
		
		c.where(cb.and(p0, p1, p2));
		
		TypedQuery<Material> q = em.createQuery(c);
		q.setFirstResult(0);
		q.setMaxResults(maxResults);
		return q.getResultList();

	}
	
	
/*	public Pricer findPricerById(Long Id) {
		LOG.info("MaterailSB.findPricerById: Begin");

		TypedQuery<Pricer> querySib = em
				.createQuery("SELECT a FROM Pricer a where a.id = :Id", Pricer.class);
		querySib.setParameter("Id", Id);
		List<Pricer> sibLst = querySib.getResultList();

		LOG.info("MaterailSB.findPricerById: End,pricerList size is:"+sibLst.size());
		if(sibLst.isEmpty()){
			return null;
		}

		return sibLst.get(0);
	}*/
}
