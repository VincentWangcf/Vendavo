package com.avnet.emasia.webquote.user.ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.DataAccess;
import com.avnet.emasia.webquote.entity.Group;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.MaterialType;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.ProgramType;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.Team;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.user.vo.UserSearchCriteria;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

/**
 * Session Bean implementation class UserSB
 */
@Stateless
@LocalBean
public class UserSB {
	private static final Logger LOGGER = Logger.getLogger(UserSB.class.getName());

	@PersistenceContext(unitName = "Server_Source")
	public EntityManager em;

	@EJB
	RoleSB roleSB;

	private static final Logger LOG = Logger.getLogger(UserSB.class.getName());

	public boolean isValidUser(String employeeId) {
		return true;
	}

	public boolean hasAccessToRegion(String employeeId, String region) {
		return true;
	}

	public List<User> findAll() {
		TypedQuery<User> query = em.createQuery("select u from User u order by u.name", User.class);
		return query.getResultList();
	}

	public User findByEmployeeIdLazily(String employeeId) {

		Query query = em.createQuery("select u from User u where u.employeeId=:employeeId");
		query.setParameter("employeeId", employeeId);
		User user = null;

		try {
			user = (User) query.getSingleResult();

		} catch (NoResultException e) {

			LOGGER.log(Level.SEVERE, "Failed for getting the information for employee id : " + employeeId
					+ ", message : " + e.getMessage(), e);
		}
		return user;
	}

	public List<User> findByEmployeeIds(List<String> employeeIds) {

		Query query = em.createQuery("select u from User u where u.employeeId in :employeeIds order by u.name");
		query.setParameter("employeeIds", employeeIds);

		return query.getResultList();
	}

	public User findByEmployeeIdWithAllRelation(String employeeId) {

		/*
		 * Query query =
		 * em.createQuery("select u from User u left join fetch u.roles " +
		 * "left join fetch u.groups left join fetch u.bizUnits " +
		 * "left join fetch u.csForSales left join fetch u.salesForCs " +
		 * "left join fetch u.reportTo left join fetch u.subordinates " +
		 * "left join fetch u.dataAccesses " +
		 * "where u.employeeId=:employeeId");
		 */
		TypedQuery<User> query = em.createQuery("select u from User u where u.employeeId=:employeeId", User.class);
		query.setParameter("employeeId", employeeId);
		User user = null;

		try {
			List<User> users=query.getResultList();
			if(users.size()<1) {
				return null;
			}
			user = users.get(0);
			user.getRoles().size();
			user.getGroups().size();
			user.getBizUnits().size();
			user.getCsForSales().size();
			user.getSalesForCs().size();
			user.getReportTo();
			user.getSubordinates().size();
			user.getSalesOrgs().size();
			user.getCustomers().size();
			user.getDataAccesses().size();

			user.setCurrenBizUnit(user.getDefaultBizUnit());
		} catch (NoResultException e) {
			LOGGER.log(Level.SEVERE, "Exception occured while getting the information for employee id : " + employeeId
					+ ", Reason for failure : " + MessageFormatorUtil.getParameterizedStringFromException(e), e);

		}

		return user;
	}

	public User findByEmployeeIdAndBizUnits(String employeeId, List<BizUnit> bizUnits) {

		List<String> bu = new ArrayList<String>();
		for (BizUnit bizUnit : bizUnits) {
			bu.add(bizUnit.getName());
		}

		TypedQuery<User> query = em.createQuery(
				"select u from User u join u.bizUnits b where b in :bu and u.employeeId=:employeeId order by u.name",
				User.class);

		query.setParameter("bu", bu);
		query.setParameter("employeeId", employeeId);
		User user = null;

		List<User> users = query.getResultList();
		if (users.size() != 0) {
			user = users.get(0);
		}

		return user;
	}

	// recursively find all subordinates
	public List<User> findAllSubordinates(User user, int depth) {

		LOG.finer("Getting Subordinates for " + user.getEmployeeId() + ", depth: " + depth);

		List<User> allSubordinates = new ArrayList<User>();

		if (depth <= 0) {
			return allSubordinates;
		}

		depth--;

		if (user.getDataSupervisor() == false) {
			return allSubordinates;
		}

		// List<User> directSubordinates = user.getSubordinates();

		List<User> directSubordinates = findSubordinates(user);

		if (directSubordinates.size() == 0) {

			return directSubordinates;

		} else {

			allSubordinates.addAll(directSubordinates);

			for (User subordinate : directSubordinates) {
				allSubordinates.addAll(findAllSubordinates(subordinate, depth));
			}

		}

		return allSubordinates;

	}

	public List<User> removeDuplicateUser(List<User> users) {
		List<User> results = new ArrayList<User>();

		for (User user : users) {

			for (User result : results) {
				if (user.getId() == result.getId()) {

					break;
				}
			}

			results.add(user);

		}

		return results;
	}

	public List<User> findAllSalesByBizUnit(String roleName, BizUnit bizUnit) {
		return wFindAllSalesByBizUnit(null, null, roleName, bizUnit);
	}

	public List<User> findWorkingPlatformEmailListByBizUnit(List<String> roleNames, BizUnit bizUnit) {
		long start = System.currentTimeMillis();

		String sql = "select distinct u from User u join fetch  u.roles r join fetch  u.bizUnits b where u.active = true "
				+ " and r.name in :roleNames and b = :bizUnit order by u.name";

		Query query = em.createQuery(sql);

		query.setParameter("roleNames", roleNames);
		query.setParameter("bizUnit", bizUnit);

		List<User> users = query.getResultList();

		// enforce loading the roles
		for (User user : users) {
			user.getRoles().size();
		}

		long end = System.currentTimeMillis();
		LOG.info("findWorkingPlatformEmailListByBizUnit size: " + users.size() + ", takes " + (end - start) + " ms");

		return users;
	}

	public List<User> findAllQcps(String[] roleNames, BizUnit bizUnit) {
		return wFindAllSalesByBizUnit(null, null, roleNames, bizUnit);
	}

	public List<User> wFindAllSalesByBizUnit(String keyOfName, String keyOfNumber, String[] roleNames,
			BizUnit bizUnit) {
		List<String> roleList = new ArrayList<String>();
		for (String ro : roleNames) {
			roleList.add(ro);
		}
		String sql = "select u from User u join u.roles r join u.bizUnits b where u.active = true and r.name in :roleName and b = :bizUnit";
		if (keyOfName != null)
			sql += " and upper(u.name) like :name";
		if (keyOfNumber != null)
			sql += " and upper(u.employeeId) like :number";

		Query query = em.createQuery(sql);

		query.setParameter("roleName", roleList);
		query.setParameter("bizUnit", bizUnit);

		if (keyOfName != null)
			query.setParameter("name", "%" + keyOfName.toUpperCase() + "%");
		if (keyOfNumber != null)
			query.setParameter("number", "%" + keyOfNumber.toUpperCase() + "%");

		return query.getResultList();
	}

	public List<User> findAllQcpsForReports(String[] roleNames, List<BizUnit> bizUnits) {
		return wFindAllSalesByBizUnits(null, null, roleNames, bizUnits);
	}

	public List<User> wFindAllSalesByBizUnits(String keyOfName, String keyOfNumber, String[] roleNames,
			List<BizUnit> bizUnits) {
		List<String> roleList = new ArrayList<String>();
		for (String ro : roleNames) {
			roleList.add(ro);
		}
		String sql = "select u from User u join u.roles r join u.bizUnits b where u.active = true and r.name in :roleName and b in :bizUnits";
		if (keyOfName != null)
			sql += " and upper(u.name) like :name";
		if (keyOfNumber != null)
			sql += " and upper(u.employeeId) like :number";

		Query query = em.createQuery(sql);

		query.setParameter("roleName", roleList);
		query.setParameter("bizUnits", bizUnits);

		if (keyOfName != null)
			query.setParameter("name", "%" + keyOfName.toUpperCase() + "%");
		if (keyOfNumber != null)
			query.setParameter("number", "%" + keyOfNumber.toUpperCase() + "%");

		return query.getResultList();
	}

	public User findSalesByEmployeeId(String employeeId, String[] roleNames) {
		List<String> roleList = new ArrayList<String>();
		for (String ro : roleNames) {
			roleList.add(ro);
		}
		String sql = "select u from User u join u.roles r where u.active = true and r.name in :roleName and upper(u.employeeId) like :employeeId";

		Query query = em.createQuery(sql);
		query.setParameter("roleName", roleList);
		query.setParameter("employeeId", "%" + employeeId.toUpperCase() + "%");

		User user = null;
		List<User> users = query.getResultList();
		if (users.size() != 0) {
			user = users.get(0);
		}

		return user;
	}

	public List<User> wFindAllSalesByBizUnit(String keyOfName, String keyOfNumber, String roleName, BizUnit bizUnit) {
		String sql = "select u from User u join u.roles r join u.bizUnits b where u.active = true and r.name = :roleName and b = :bizUnit";
		if (keyOfName != null)
			sql += " and upper(u.name) like :name";
		if (keyOfNumber != null)
			sql += " and upper(u.employeeId) like :number";

		Query query = em.createQuery(sql);

		query.setParameter("roleName", roleName);
		query.setParameter("bizUnit", bizUnit);

		if (keyOfName != null)
			query.setParameter("name", "%" + keyOfName.toUpperCase() + "%");
		if (keyOfNumber != null)
			query.setParameter("number", "%" + keyOfNumber.toUpperCase() + "%");

		return query.getResultList();
	}

	public List<User> findAllSalesForCs(User user) {

		return wFindAllSalesForCs(null, null, user);

	}

	public List<User> wFindAllSalesForCs(String keyOfName, String keyOfNumber, User user) {
		return wFindAllSalesForCs(keyOfName, keyOfNumber, user, true);
	}

	/*
	 * @param: all : true, all the sales for the related CS; false : only the
	 * active sales for the related CS.
	 */
	public List<User> wFindAllSalesForCs(String keyOfName, String keyOfNumber, User user, boolean all) {

		List<User> allCs = new ArrayList<User>();

		List<User> subordinates = this.findAllSubordinates(user, 10);

		allCs.addAll(subordinates);

		allCs.add(user);

		List<Long> ids = new ArrayList<Long>();

		for (User cs : allCs) {
			ids.add(cs.getId());
		}
		// Fix 1010
		// add the u.active=1 condition. updated by Tonmy 11 Oct 2013
		String sql = "";
		if (all) {
			sql = "select distinct sales from User u join u.salesForCs sales where u.id in :cs ";
		} else {
			sql = "select distinct sales from User u join u.salesForCs sales where sales.active = true and u.id in :cs ";
		}
		if (keyOfName != null)
			sql += " and upper(sales.name) like :name";
		if (keyOfNumber != null)
			sql += " and upper(sales.employeeId) like :number";

		TypedQuery<User> query = em.createQuery(sql, User.class);
		query.setParameter("cs", ids);
		if (keyOfName != null)
			query.setParameter("name", "%" + keyOfName.toUpperCase() + "%");
		if (keyOfNumber != null)
			query.setParameter("number", "%" + keyOfNumber.toUpperCase() + "%");

		return query.getResultList();

	}

	public List<User> wFindAllSalesForCsForInsideSales(String keyOfName, String keyOfNumber, User user) {

		List<User> allCs = new ArrayList<User>();

		List<User> subordinates = this.findAllSubordinates(user, 10);

		allCs.addAll(subordinates);

		allCs.add(user);

		List<Long> ids = new ArrayList<Long>();

		for (User cs : allCs) {
			ids.add(cs.getId());
		}
		// Fix 1010
		// add the u.active=1 condition. updated by Tonmy 11 Oct 2013
		String sql = "select distinct sales from User u join u.salesForCs sales where sales.active = true and (u.id in :cs or sales.id =:id)";
		if (keyOfName != null)
			sql += " and upper(sales.name) like :name";
		if (keyOfNumber != null)
			sql += " and upper(sales.employeeId) like :number";

		TypedQuery<User> query = em.createQuery(sql, User.class);
		query.setParameter("id", user.getId());
		query.setParameter("cs", ids);
		if (keyOfName != null)
			query.setParameter("name", "%" + keyOfName.toUpperCase() + "%");
		if (keyOfNumber != null)
			query.setParameter("number", "%" + keyOfNumber.toUpperCase() + "%");

		return query.getResultList();

	}

	public List<User> findPmForInternalTransfer(Manufacturer manufacturer, ProductGroup productGroup, Team team,
			String materialType, BizUnit bizUnit) {
		Long mfrId = null;
		if (manufacturer != null)
			mfrId = manufacturer.getId();

		Long productGroupId = null;
		if (productGroup != null)
			productGroupId = productGroup.getId();

		String teamId = null;
		if (team != null)
			teamId = team.getName();

		String materialTypeId = materialType;

		String sql = "select distinct u from User u join u.dataAccesses d left join d.manufacturer m left join d.productGroup pg left join d.team t left join d.materialType mt ";
		sql += "where u.defaultBizUnit = :bizunit and u.active = true and (m.id = :mfrId or m.id is null) and (pg.id = :productGroupId or pg.id is null) and (t.name = :teamId or t.name is null) and (mt.name = :materialTypeId or mt.name is null)";
		TypedQuery<User> query = em.createQuery(sql, User.class);
		query.setParameter("mfrId", mfrId);
		query.setParameter("productGroupId", productGroupId);
		query.setParameter("teamId", teamId);
		query.setParameter("materialTypeId", materialTypeId);
		query.setParameter("bizunit", bizUnit);

		return query.getResultList();
	}

	public List<User> findPmForInternalTransfer(Manufacturer manufacturer, ProductGroup productGroup, Team team,
			MaterialType materialType) {
		Long mfrId = null;
		if (manufacturer != null)
			mfrId = manufacturer.getId();

		Long productGroupId = null;
		if (productGroup != null)
			productGroupId = productGroup.getId();

		String teamId = null;
		if (team != null)
			teamId = team.getName();

		String materialTypeId = null;
		if (materialType != null)
			materialTypeId = materialType.getName();

		String sql = "select distinct u from User u join u.dataAccesses d left join d.manufacturer m left join d.productGroup pg left join d.team t left join d.materialType mt ";
		sql += "where (m.id = :mfrId or m.id is null) and (pg.id = :productGroupId or pg.id is null) and (t.name = :teamId or t.name is null) and (mt.name = :materialTypeId or mt.name is null)";
		TypedQuery<User> query = em.createQuery(sql, User.class);
		query.setParameter("mfrId", mfrId);
		query.setParameter("productGroupId", productGroupId);
		query.setParameter("teamId", teamId);
		query.setParameter("materialTypeId", materialTypeId);

		return query.getResultList();
	}

	public String findPmForInternalTransfer(String forms, String request) {
		Query query = em.createNativeQuery("BEGIN PM_EMAIL_ENQUIRY_PROC('" + forms + "', '" + request + "'); END;");
		Object obj = query.executeUpdate();
		return obj.toString();
	}

	public List<String> getPmForInternalTransfer(String forms, String bizUnit) {
		List<String> list = em.createNativeQuery(
				"select pm_request from pm_grouping where forms = '" + forms + "' and biz_unit = '" + bizUnit + "'")
				.getResultList();
		return list;
	}

	public List<User> findAllCsForSales(User user) {

		List<User> allSales = new ArrayList<User>();

		List<User> subordinates = this.findAllSubordinates(user, 10);

		allSales.addAll(subordinates);

		allSales.add(user);

		List<Long> ids = new ArrayList<Long>();

		for (User sales : allSales) {
			ids.add(sales.getId());
		}

		TypedQuery<User> query = em
				.createQuery("select distinct cs from User u join u.csForSales cs where u.id in :sales", User.class);
		query.setParameter("sales", ids);

		return query.getResultList();

	}

	// inactive CS issue
	public List<User> findAllActiveCsForSales(User user) {

		List<User> allSales = new ArrayList<User>();

		List<User> subordinates = this.findAllSubordinates(user, 10);

		allSales.addAll(subordinates);

		allSales.add(user);

		List<Long> ids = new ArrayList<Long>();

		for (User sales : allSales) {
			ids.add(sales.getId());
		}

		TypedQuery<User> query = em.createQuery(
				"select distinct cs from User u join u.csForSales cs where cs.active = true and u.id in :sales",
				User.class);
		query.setParameter("sales", ids);

		return query.getResultList();

	}

	public List<DataAccess> findAllDataAccesses(User user) {

		List<User> users = new ArrayList<User>();

		List<User> subordinates = this.findAllSubordinates(user, 10);

		users.addAll(subordinates);

		users.add(user);

		List<Long> ids = new ArrayList<Long>();

		for (User user1 : users) {
			ids.add(user1.getId());
		}

		// If one of users has does not have data access, then the supervisor
		// has no data access limit either.
		// TypedQuery<User> query1 = em.createQuery("select u from User u left
		// join u.dataAccesses d where u.id in :ids group by u having COUNT(d) =
		// 0", User.class);
		// query1.setParameter("ids", ids);
		// List<User> result = query1.getResultList();
		// if(result.size() !=0 ){
		// return null;
		// }

		TypedQuery<DataAccess> query = em.createQuery(
				"select distinct d from User u join u.dataAccesses d where u.id in :userId", DataAccess.class);
		query.setParameter("userId", ids);

		List<DataAccess> dataAccesses = query.getResultList();

		dataAccesses = removeDuplicateDataAccess(dataAccesses);

		return dataAccesses;

	}

	public List<DataAccess> removeDuplicateDataAccess(List<DataAccess> dataAccesses) {

		// removed by Alex on 16 Oct 2014 as the remove dup logic has been
		// changed. ALL case has to be taken into consideration...
		/*
		 * List<DataAccess> results = new ArrayList<DataAccess>();
		 * 
		 * for(DataAccess dataAccess : dataAccesses){
		 * 
		 * boolean found = false;
		 * 
		 * for(DataAccess result : results){
		 * 
		 * boolean manufacturerMatched = false; boolean materialTypeMatched =
		 * false; boolean programTypeMatched = false; boolean
		 * productGroupMatched = false; boolean teamMatched = false;
		 * 
		 * if(dataAccess.getManufacturer() == null ^ result.getManufacturer() ==
		 * null){ manufacturerMatched = false; }else
		 * if(dataAccess.getManufacturer() == null && result.getManufacturer()
		 * == null){ manufacturerMatched = true; }else{
		 * if(dataAccess.getManufacturer().getId() ==
		 * result.getManufacturer().getId()){ manufacturerMatched = true; }else{
		 * manufacturerMatched= false; } }
		 * 
		 * if(dataAccess.getMaterialType() == null ^ result.getMaterialType() ==
		 * null){ materialTypeMatched = false; }else
		 * if(dataAccess.getMaterialType() == null && result.getMaterialType()
		 * == null){ materialTypeMatched = true; }else{
		 * if(dataAccess.getMaterialType().getName().equals(result.
		 * getMaterialType().getName())){ materialTypeMatched = true; }else{
		 * materialTypeMatched= false; } }
		 * 
		 * if(dataAccess.getProgramType() == null ^ result.getProgramType() ==
		 * null){ programTypeMatched = false; }else
		 * if(dataAccess.getProgramType() == null && result.getProgramType() ==
		 * null){ programTypeMatched = true; }else{
		 * if(dataAccess.getProgramType().getId() ==
		 * result.getProgramType().getId()){ programTypeMatched = true; }else{
		 * programTypeMatched= false; } }
		 * 
		 * if(dataAccess.getProductGroup() == null ^ result.getProductGroup() ==
		 * null){ productGroupMatched = false; }else
		 * if(dataAccess.getProductGroup() == null && result.getProductGroup()
		 * == null){ productGroupMatched = true; }else{
		 * if(dataAccess.getProductGroup().getId() ==
		 * result.getProductGroup().getId()){ productGroupMatched = true; }else{
		 * productGroupMatched= false; } }
		 * 
		 * if(dataAccess.getTeam() == null ^ result.getTeam() == null){
		 * teamMatched = false; }else if(dataAccess.getTeam() == null &&
		 * result.getTeam() == null){ teamMatched = true; }else{
		 * if(dataAccess.getTeam().getName().equals(result.getTeam().getName()))
		 * { teamMatched = true; }else{ teamMatched= false; } }
		 * 
		 * if(manufacturerMatched && materialTypeMatched && programTypeMatched
		 * && productGroupMatched && teamMatched){ found = true; break; }
		 * 
		 * }
		 * 
		 * if(found == false){ results.add(dataAccess); }
		 * 
		 * }
		 * 
		 * return results;
		 */

		/*---------added by Alex on 16 Oct 2014 for INC0038879 begin------ */

		if (dataAccesses == null || dataAccesses.size() == 0) {
			return dataAccesses;
		}

		List<DataAccess> retList = new ArrayList<DataAccess>();

		Map<String, DataAccess> uniqueDAMap = new HashMap<String, DataAccess>();

		for (DataAccess da : dataAccesses) {
			String mftId = da.getManufacturer() == null ? "" : QuoteUtil.object2String(da.getManufacturer().getId());
			String prdtGroupId = da.getProductGroup() == null ? ""
					: QuoteUtil.object2String(da.getProductGroup().getId());
			String team = da.getTeam() == null ? "" : QuoteUtil.object2String(da.getTeam().getName());
			String materialType = da.getMaterialType() == null ? ""
					: QuoteUtil.object2String(da.getMaterialType().getName());
			String programTypeId = da.getProgramType() == null ? ""
					: QuoteUtil.object2String(da.getProgramType().getId());
			String daKey = mftId + "==" + prdtGroupId + "==" + team + "==" + materialType + "==" + programTypeId;
			// System.out.println(daKey);
			uniqueDAMap.put(daKey, da); // remove duplicated key data access
										// object;
		}

		if (uniqueDAMap.get("========") != null) { // exists the case that all
													// data access elements are
													// ALL values;
			retList.add(uniqueDAMap.get("========"));
			return retList;
		}

		for (Map.Entry<String, DataAccess> entry : uniqueDAMap.entrySet()) {
			DataAccess da = entry.getValue();
			if (!existInDAMap(da, uniqueDAMap)) { // if it is not included in
													// the hasAllDAMap, add it.
				retList.add(da);
			}
		}
		return retList;

	}

	private boolean existInDAMap(DataAccess da, Map<String, DataAccess> uniqueDAMap) {
		boolean existed = false;
		String mftId = da.getManufacturer() == null ? "" : QuoteUtil.object2String(da.getManufacturer().getId());
		String prdtGroupId = da.getProductGroup() == null ? "" : QuoteUtil.object2String(da.getProductGroup().getId());
		String team = da.getTeam() == null ? "" : QuoteUtil.object2String(da.getTeam().getName());
		String materialType = da.getMaterialType() == null ? ""
				: QuoteUtil.object2String(da.getMaterialType().getName());
		String programTypeId = da.getProgramType() == null ? "" : QuoteUtil.object2String(da.getProgramType().getId());
		String daKey = mftId + "==" + prdtGroupId + "==" + team + "==" + materialType + "==" + programTypeId;
		for (DataAccess compDA : uniqueDAMap.values()) {
			boolean incMft = false;
			boolean incPrdtGroup = false;
			boolean incTeam = false;
			boolean incMaterialType = false;
			boolean incProgramType = false;
			String compMftId = compDA.getManufacturer() == null ? ""
					: QuoteUtil.object2String(compDA.getManufacturer().getId());
			String compPrdtGroupId = compDA.getProductGroup() == null ? ""
					: QuoteUtil.object2String(compDA.getProductGroup().getId());
			String compTeam = compDA.getTeam() == null ? "" : QuoteUtil.object2String(compDA.getTeam().getName());
			String compMaterialType = compDA.getMaterialType() == null ? ""
					: QuoteUtil.object2String(compDA.getMaterialType().getName());
			String compProgramTypeId = compDA.getProgramType() == null ? ""
					: QuoteUtil.object2String(compDA.getProgramType().getId());
			String compDAKey = compMftId + "==" + compPrdtGroupId + "==" + compTeam + "==" + compMaterialType + "=="
					+ compProgramTypeId;
			if (daKey.equals(compDAKey)) { // Don't compare with himself so skip
											// it.
				continue;
			}
			if ("".equals(compMftId) || mftId.equals(compMftId)) {
				incMft = true;
			}

			if ("".equals(compPrdtGroupId) || prdtGroupId.equals(compPrdtGroupId)) {
				incPrdtGroup = true;
			}

			if ("".equals(compTeam) || team.equals(compTeam)) {
				incTeam = true;
			}

			if ("".equals(compMaterialType) || materialType.equals(compMaterialType)) {
				incMaterialType = true;
			}

			if ("".equals(compProgramTypeId) || programTypeId.equals(compProgramTypeId)) {
				incProgramType = true;
			}

			if (incMft && incPrdtGroup && incTeam && incMaterialType && incProgramType) {
				existed = true;
				break;
			}
		}
		return existed;
	}

	/*---------added by Alex on 16 Oct 2014 for INC0038879 end------ */

	public List<User> findByPage(int first, int pageSize, String employeeId, String name, String emailAddress,
			String phoneNumber, Boolean active) {

		TypedQuery<User> q = buildQuery(employeeId, name, emailAddress, phoneNumber, active);

		q.setFirstResult(first);
		q.setMaxResults(pageSize);
		return q.getResultList();

	}

	public long getUserCount(String employeeId, String name, String emailAddress, String phoneNumber, Boolean active) {

		TypedQuery<User> q = buildQuery(employeeId, name, emailAddress, phoneNumber, active);

		return q.getResultList().size();

		/*
		 * long count =
		 * (Long)em.createQuery("select count(u) from User u").getSingleResult()
		 * ; return count;
		 */
	}

	public TypedQuery<User> buildQuery(String employeeId, String name, String emailAddress, String phoneNumber,
			Boolean active) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> c = cb.createQuery(User.class);
		Root<User> user = c.from(User.class);
		c.select(user);

		List<Predicate> criteria = new ArrayList<Predicate>();

		if (employeeId != null) {
			ParameterExpression<String> p = cb.parameter(String.class, "employeeId");
			criteria.add(cb.like(user.<String>get("employeeId"), p));
		}

		if (name != null) {
			ParameterExpression<String> p = cb.parameter(String.class, "name");
			criteria.add(cb.equal(user.<String>get("name"), p));
		}

		if (emailAddress != null) {
			ParameterExpression<String> p = cb.parameter(String.class, "emailAddress");
			criteria.add(cb.equal(user.<String>get("emailAddress"), p));
		}

		if (phoneNumber != null) {
			ParameterExpression<String> p = cb.parameter(String.class, "phoneNumber");
			criteria.add(cb.equal(user.<String>get("phoneNumber"), p));
		}

		if (active != null) {
			ParameterExpression<Boolean> p = cb.parameter(Boolean.class, "active");
			criteria.add(cb.equal(user.get("active"), p));
		}

		if (criteria.size() == 0) {
		} else if (criteria.size() == 1) {
			c.where(criteria.get(0));
		} else {
			c.where(cb.and(criteria.toArray(new Predicate[0])));
		}

		TypedQuery<User> q = em.createQuery(c);
		if (employeeId != null) {
			q.setParameter("employeeId", "%" + employeeId + "%");
		}
		if (name != null) {
			q.setParameter("name", "%" + name + "%");
		}
		if (emailAddress != null) {
			q.setParameter("emailAddress", "%" + emailAddress + "%");
		}
		if (phoneNumber != null) {
			q.setParameter("phoneNumber", "%" + phoneNumber + "%");
		}
		if (active != null) {
			q.setParameter("active", active);
		}

		return q;

	}

	public User save(User user) {
		return em.merge(user);
	}

	public User saveWithAllRelation(User user) {

		List<User> oldCss = null;

		User oldUser = em.find(User.class, user.getId());

		if (oldUser != null) {
			oldCss = oldUser.getCsForSales();
		} else {
			oldCss = new ArrayList<User>();
		}
		List<User> newCss = user.getCsForSales();

		for (User oldCs : oldCss) {
			boolean found = false;
			for (User newCs : newCss) {
				if (oldCs.getId() == newCs.getId()) {
					found = true;
					break;
				}
			}
			if (found == false) {
				for (int i = 0; i < oldCs.getSalesForCs().size(); i++) {
					if (user.getId() == oldCs.getSalesForCs().get(i).getId()) {
						oldCs.getSalesForCs().remove(i);
					}
				}

				em.merge(oldCs);

			}
		}

		boolean isNew = true;

		if (user.getId() == 0) {
			user = em.merge(user);
			isNew = false;
		}

		for (User newCs : newCss) {
			boolean found = false;
			for (User oldCs : oldCss) {
				if (oldCs.getId() == newCs.getId()) {
					found = true;
					break;
				}
			}
			if (found == false) {
				newCs = em.find(User.class, newCs.getId());
				List<User> sales = newCs.getSalesForCs();
				sales.add(user);
				em.merge(newCs);
			}
		}

		if (isNew) {
			user = em.merge(user);
		}

		return user;
	}

	public List<User> wfindUserByEmployeeIdAndRole(String partialEmployeeId, String roleName, BizUnit bizUnit) {

		Query query = em.createQuery("select u from User u join u.roles r join u.bizUntis b where u.active = true "
				+ "and u.employeeId like :partialEmployeeId and r.name = :roleName and b = :bizUnit");
		query.setParameter("partialEmployeeId", "%" + partialEmployeeId + "%");
		query.setParameter("roleName", roleName);
		query.setParameter("bizUnit", bizUnit);
		return query.getResultList();

	}

	public List<User> wfindUserByEmployeeNameAndRole(String partialEmployeeName, String roleName, BizUnit bizUnit) {

		Query query = em.createQuery("select u from User u join u.roles r join u.bizUnits b "
				+ "where u.active = true and u.name like :partialEmployeeName and r.name = :roleName and b = :bizUnit");
		query.setParameter("partialEmployeeName", "%" + partialEmployeeName + "%");
		query.setParameter("roleName", roleName);
		query.setParameter("bizUnit", bizUnit);
		return query.getResultList();

	}

	public List<User> wfindUserByEmployeeNameAndRoles(String partialEmployeeName, List<String> roleNameLst,
			BizUnit bizUnit) {

		Query query = em.createQuery("select u from User u join u.roles r join u.bizUnits b "
				+ "where u.active = true and u.name like :partialEmployeeName and r.name in :roleNameLst and b = :bizUnit");
		query.setParameter("partialEmployeeName", "%" + partialEmployeeName + "%");
		query.setParameter("roleNameLst", roleNameLst);
		query.setParameter("bizUnit", bizUnit);
		return query.getResultList();

	}

	public List<User> wFindUserByNameAndRoleWithPage(String name, String role, List<BizUnit> bizUnits, int firstResult,
			int maxResults) {

		List<String> sBizUnits = new ArrayList<String>();
		for (BizUnit bizUnit : bizUnits) {
			sBizUnits.add(bizUnit.getName());
		}

		// TODO add role parameter

		TypedQuery<User> query = em.createQuery("select DISTINCT u from User u join u.bizUnits b "
				+ "where u.active = true and UPPER(u.name) like :name and b.name in :bizUnits", User.class);

		query.setParameter("name", "%" + name.toUpperCase() + "%").setParameter("bizUnits", sBizUnits)
				.setFirstResult(firstResult).setMaxResults(maxResults);

		return query.getResultList();

	}

	public List<User> findByDataAccess(List<Object[]> dataAccessAndBizUnits, List<Role> roles) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<Predicate>();
		CriteriaQuery<User> c = cb.createQuery(User.class);
		Root<User> user = c.from(User.class);
		user.fetch("dataAccesses");
		c.distinct(true);
		Join<User, BizUnit> bizUnit = user.join("bizUnits");
		Join<User, DataAccess> dataAccess = user.join("dataAccesses");
		Join<DataAccess, Manufacturer> manufacturer = dataAccess.join("manufacturer", JoinType.LEFT);
		Join<DataAccess, ProductGroup> productGroup = dataAccess.join("productGroup", JoinType.LEFT);
		Join<DataAccess, MaterialType> materialType = dataAccess.join("materialType", JoinType.LEFT);
		Join<DataAccess, ProgramType> programType = dataAccess.join("programType", JoinType.LEFT);
		Join<DataAccess, Team> team = dataAccess.join("team", JoinType.LEFT);
		Join<User, Role> role = user.join("roles");

		if (dataAccessAndBizUnits.size() != 0) {

			List<Predicate> p0 = new ArrayList<Predicate>();

			for (Object[] dataAccessAndBizUnit : dataAccessAndBizUnits) {

				DataAccess access = (DataAccess) dataAccessAndBizUnit[0];
				BizUnit bu = (BizUnit) dataAccessAndBizUnit[1];

				List<Predicate> p1 = new ArrayList<Predicate>();

				if (access.getManufacturer() != null) {
					Predicate predicate = cb.or(cb.isNull(manufacturer),
							cb.equal(manufacturer, access.getManufacturer()));
					p1.add(predicate);
				} else {
					Predicate predicate = cb.isNull(manufacturer);
					p1.add(predicate);
				}

				if (access.getProductGroup() != null) {
					Predicate predicate = cb.or(cb.isNull(productGroup),
							cb.equal(productGroup, access.getProductGroup()));
					p1.add(predicate);
				} else {
					Predicate predicate = cb.isNull(productGroup);
					p1.add(predicate);
				}

				if (access.getMaterialType() != null) {
					Predicate predicate = cb.or(cb.isNull(materialType),
							cb.equal(materialType, access.getMaterialType()));
					p1.add(predicate);
				} else {
					Predicate predicate = cb.isNull(materialType);
					p1.add(predicate);
				}

				if (access.getProgramType() != null) {
					Predicate predicate = cb.or(cb.isNull(programType), cb.equal(programType, access.getProgramType()));
					p1.add(predicate);
				} else {
					Predicate predicate = cb.isNull(programType);
					p1.add(predicate);
				}

				if (access.getTeam() != null) {
					Predicate predicate = cb.or(cb.isNull(team), cb.equal(team, access.getTeam()));
					p1.add(predicate);
				} else {
					Predicate predicate = cb.isNull(team);
					p1.add(predicate);
				}

				Predicate predicate = cb.equal(bizUnit.<String>get("name"), bu.getName());

				p1.add(predicate);

				p0.add(cb.and(p1.toArray(new Predicate[0])));
			}

			predicates.add(cb.or(p0.toArray(new Predicate[0])));

		}

		if (roles.size() != 0) {
			CriteriaBuilder.In<String> in = cb.in(role.<String>get("name"));
			for (Role r : roles) {
				in.value(r.getName());
			}
			predicates.add(in);
		}

		predicates.add(cb.isTrue(user.<Boolean>get("active")));

		if (predicates.size() == 0) {

		} else if (predicates.size() == 1) {
			c.where(predicates.get(0));
		} else {
			c.where(cb.and(predicates.toArray(new Predicate[0])));
		}

		TypedQuery<User> q = em.createQuery(c);

		List<User> results = q.getResultList();

		LOG.info("size:" + results.size());

		return results;

	}

	public List<Customer> findAllCustomers(User user) {

		List<User> users = new ArrayList<User>();

		List<User> subordinates = this.findAllSubordinates(user, 10);

		users.addAll(subordinates);

		users.add(user);

		List<Long> ids = new ArrayList<Long>();

		for (User u : users) {
			ids.add(u.getId());
		}

		TypedQuery<Customer> query = em
				.createQuery("select distinct c from User u join u.customers c where u.id in :userId", Customer.class);
		query.setParameter("userId", ids);

		List<Customer> customers = query.getResultList();

		return customers;

	}

	/**
	 * Auto complete fill in data maintenance.
	 * 
	 * @param user
	 * @return
	 */
	public List<Customer> findAutoCompleteCustomers(String key) {
		TypedQuery<Customer> query = em.createQuery(
				"select distinct c from Customer c "
						+ " where upper(concat(c.customerName1,' ', c.customerName2,' ', c.customerName3,' ', c.customerName4)) like :name",
				Customer.class);
		query.setParameter("name", "%" + key.toUpperCase() + "%");
		List<Customer> customers = query.setFirstResult(0).setMaxResults(10).getResultList();

		return customers;
	}

	/**
	 * Auto complete fill sale' name.
	 * 
	 * @param key
	 * @return
	 */
	public List<User> findAutoCompleteUser(String key) {
		TypedQuery<User> query = em.createQuery("select distinct a from User a" + " where UPPER(a.name) like :name",
				User.class);
		query.setParameter("name", "%" + key.toUpperCase() + "%");
		List<User> userLst = query.setFirstResult(0).setMaxResults(10).getResultList();

		return userLst;
	}

	/**
	 * Auto complete fill supervisor' name.
	 * 
	 * @param key
	 * @return
	 */
	public List<User> findAutoCompleteUser(String key, List<BizUnit> bizUnits) {
		TypedQuery<User> query = em.createQuery(
				"select distinct a from User a join a.bizUnits b" + " where UPPER(a.name) like :name and b in :bzUts",
				User.class);
		query.setParameter("name", "%" + key.toUpperCase() + "%");
		query.setParameter("bzUts", bizUnits);
		List<User> userLst = query.getResultList();
		return userLst;
	}

	public List<User> findUserByName(String userName) {
		TypedQuery<User> query = em.createQuery("select distinct a from User a" + " where UPPER(a.name) = :name",
				User.class);
		query.setParameter("name", userName.toUpperCase());
		List<User> userLst = query.getResultList();

		return userLst;
	}

	public List<User> findByUserSearchCriteria(UserSearchCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();

		List<Predicate> predicates = new ArrayList<Predicate>();

		CriteriaQuery<User> c = cb.createQuery(User.class);

		c.distinct(true);

		Root<User> user = c.from(User.class);

		Join<User, BizUnit> bizUnit = user.join("bizUnits", JoinType.LEFT);

		Join<User, Group> group = user.join("groups", JoinType.LEFT);

		Join<User, Role> role = user.join("roles", JoinType.LEFT);

		if (criteria.getBizUnits() == null || criteria.getBizUnits().size() == 0) {
			Predicate predicate = cb.like(bizUnit.<String>get("name"), "");
			predicates.add(predicate);
		} else {
			List<Predicate> p0 = new ArrayList<Predicate>();

			for (String bu : criteria.getBizUnits()) {
				CriteriaBuilder.In<String> in = cb.in(bizUnit.<String>get("name"));
				in.value(bu);
				p0.add(in);
			}
			predicates.add(cb.or(p0.toArray(new Predicate[0])));
		}

		if (criteria.getGroups() != null && criteria.getGroups().size() != 0) {

			List<Predicate> p0 = new ArrayList<Predicate>();

			CriteriaBuilder.In<Long> inGroup = cb.in(group.<Long>get("id"));
			for (String groupId : criteria.getGroups()) {
				inGroup.value(Long.parseLong(groupId));
				p0.add(inGroup);
			}
			predicates.add(cb.or(p0.toArray(new Predicate[0])));
		}

		if (criteria.getRoles() == null || criteria.getRoles().size() == 0) {
			Predicate predicate = cb.like(role.<String>get("name"), "");
			predicates.add(predicate);
		} else {
			/***
			 * List<Predicate> p0 = new ArrayList<Predicate>();
			 * 
			 * CriteriaBuilder.In<String> inRole =
			 * cb.in(role.<String>get("name")); for(String roleName :
			 * criteria.getRoles()){ inRole.value(roleName); p0.add(inRole); }
			 * predicates.add(cb.or(p0.toArray(new Predicate[0])));
			 ***/

			CriteriaBuilder.In<String> inRoles = cb.in(role.<String>get("name"));

			for (String employeeId : criteria.getRoles()) {
				inRoles.value(employeeId);
			}
			predicates.add(inRoles);
		}

		if (criteria.getEmployeeId() != null && !criteria.getEmployeeId().equals("")) {
			Predicate predicate = cb.like(user.<String>get("employeeId"), "%" + criteria.getEmployeeId() + "%");
			predicates.add(predicate);

		}

		if (criteria.getEmployeeName() != null && !criteria.getEmployeeName().equals("")) {
			Predicate predicate = cb.like(cb.upper(user.<String>get("name")),
					"%" + criteria.getEmployeeName().toUpperCase() + "%");
			predicates.add(predicate);

		}

		if (predicates.size() == 0) {

		} else if (predicates.size() == 1) {
			c.where(predicates.get(0));
		} else {
			c.where(cb.and(predicates.toArray(new Predicate[0])));
		}

		TypedQuery<User> q = em.createQuery(c);

		List<User> results = q.getResultList();

		LOG.info("Search Criteria:" + criteria);

		LOG.info("size:" + results.size());

		return results;

	}

	// Added by Tonmy on 20130702
	public List<User> findAllPMByBizUnit(BizUnit bizUnit) {
		return wFindAllSalesByBizUnit(null, null, "ROLE_PM", bizUnit);
	}

	public List<User> findAllPMByBizUnit(String roleName, BizUnit bizUnit) {
		return wFindAllSalesByBizUnit(null, null, roleName, bizUnit);
	}

	private List<User> findSubordinates(User user) {
		TypedQuery<User> query = em.createQuery("select distinct u from User u where u.reportTo.id = ?1", User.class);
		query.setParameter(1, user.getId());
		return query.getResultList();
	}

	// Added by Danny on 20130916
	public List<User> findUserWithKeywordAndRoles(String keyOfName, String[] roleNames, BizUnit bizUnit) {
		List<String> roleList = new ArrayList<String>();
		for (String ro : roleNames) {
			roleList.add(ro);
		}

		String sql = "select u from User u join u.roles r join u.bizUnits b where u.active = true and r.name in :roleName and b = :bizUnit";
		if (keyOfName != null)
			sql += " and upper(u.name) like :name";

		Query query = em.createQuery(sql);

		query.setParameter("roleName", roleList);
		query.setParameter("bizUnit", bizUnit);

		if (keyOfName != null)
			query.setParameter("name", "%" + keyOfName.toUpperCase() + "%");

		return query.setFirstResult(0).setMaxResults(10).getResultList();
	}

	public List<User> findUserWithKeywordRolesAndBizUnits(String keyOfName, String[] roleNames,
			List<BizUnit> bizUnits) {
		List<String> roleList = new ArrayList<String>();
		for (String ro : roleNames) {
			roleList.add(ro);
		}

		String sql = "select u from User u join u.roles r join u.bizUnits b where u.active = true and r.name in :roleName and b in :bizUnits";
		if (keyOfName != null)
			sql += " and upper(u.name) like :name";

		Query query = em.createQuery(sql);

		query.setParameter("roleName", roleList);
		query.setParameter("bizUnits", bizUnits);

		if (keyOfName != null)
			query.setParameter("name", "%" + keyOfName.toUpperCase() + "%");

		return query.setFirstResult(0).setMaxResults(10).getResultList();
	}

	/**
	 * Fetch employee id by part of employee id.
	 * 
	 * @param employeeIDpart
	 * @return
	 */
	public List<User> findUserEmployeeIdByPartion(String employeeIDpart) {
		TypedQuery<User> query = em.createQuery("select u from User u where u.employeeId like :emplyeID", User.class);
		query.setParameter("emplyeID", "%" + employeeIDpart + "%");
		return query.setFirstResult(0).setMaxResults(10).getResultList();
	}

	/**
	 * 
	 * @author 916138
	 *
	 * @param roleName
	 * @param bu
	 * @return
	 */
	public List<User> findUserByRoleAndBu(List<String> roleName, String bu) {
		TypedQuery<User> query = em.createQuery(
				"select u from User u join u.roles r join u.bizUnits b where u.active = true and r.name in :roleName and b.name = :bizName",
				User.class);
		query.setParameter("roleName", roleName);
		query.setParameter("bizName", bu);

		return query.getResultList();

	}

	public List<User> findUserByRoleAndBuTeam(List<String> roleName, String bu, String team) {
		TypedQuery<User> query = em.createQuery(
				"select u from User u join u.roles r join u.bizUnits b join u.team t where u.active = true and r.name in :roleName and b.name = :bizName and t.name = :teamName",
				User.class);
		query.setParameter("roleName", roleName);
		query.setParameter("bizName", bu);
		query.setParameter("teamName", team);

		return query.getResultList();

	}

	/**
	 * @author Damon��Chen @date Created Date��2016��9��13��
	 * ����4:28:12 @Description: find all subordinates @param
	 * user @return @return List<User> @throws
	 */

	public List<User> findAllSubordinatesWithoutDeth(User user) {

		LOG.finer("Getting Subordinates for " + user.getEmployeeId() + ", without depth ");

		List<User> allSubordinates = new ArrayList<User>();

		if (user.getDataSupervisor() == false) {
			return allSubordinates;
		}

		// List<User> directSubordinates = user.getSubordinates();

		List<User> directSubordinates = findSubordinates(user);

		if (directSubordinates.size() == 0) {

			return directSubordinates;

		} else {

			allSubordinates.addAll(directSubordinates);

			for (User subordinate : directSubordinates) {
				allSubordinates.addAll(findAllSubordinatesWithoutDeth(subordinate));
			}

		}

		return allSubordinates;

	}
	
	public boolean updateUserLocale(User user,String language){
		boolean isUpdated = false;
    	Query q1 = em.createQuery("UPDATE User v set v.userLocale =:userLocale where v.employeeId=:employeeId");
    	q1.setParameter("userLocale",language);
    	q1.setParameter("employeeId", user.getEmployeeId());
		try {
			q1.executeUpdate();
	    	em.flush();
        	em.clear();
        	isUpdated =true;
		} catch (Exception ex){
			LOG.log(Level.SEVERE, "update prefered user locale  failed for locale id : "+language+" and employee Id "+user.getEmployeeId()+" Message : "+ex.getMessage(),ex);
	    	em.flush();
        	em.clear();
        	isUpdated =false;
		}
		return isUpdated;
	}

}
