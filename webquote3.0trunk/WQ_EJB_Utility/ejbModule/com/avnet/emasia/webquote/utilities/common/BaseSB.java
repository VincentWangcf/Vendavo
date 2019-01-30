package com.avnet.emasia.webquote.utilities.common;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;

import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

/**
 * 
 * @author 041863
 */
public abstract class BaseSB<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	protected static final Logger LOGGER = Logger.getLogger(BaseSB.class.getName());
	private final Class<T> entityClass;

	@SuppressWarnings("unchecked")
	public BaseSB() {
		entityClass = (Class<T>) QuoteUtil.getSuperClassGenricType(getClass(), 0);
	}

	public Class<T> getEntityClass(){
		return entityClass;
	}
	
	@PersistenceContext(unitName = "Server_Source")
	protected EntityManager em;

	public int findLazyDataCount(String filter, List<QueryBean> queryBeans) {
		String sql = formatSql(filter, queryBeans).toString();
		sql = "SELECT COUNT(1) FROM " + sql.split("FROM")[1];
		Query query = em.createQuery(sql);

		setParamters(query, queryBeans);
//		LOGGER.log(Level.INFO, sql);
		return ((Long) query.getSingleResult()).intValue();
	}

	public List<T> findLazyData(int first, int pageSize, String sortField, String sortOrder, String filter, List<QueryBean> queryBeans) {
		StringBuilder sql = formatSql(filter, queryBeans);
		if (sortField != null && !sortField.equals("")) {
			sql.append(" order by ").append(sortField).append(sortOrder);
			//BALANCE THE ORDER FIELD CAN NOT CORRECT ORDER AND SPEED TO ACCESS.
			//(BMT PAGE OPEN SPEED ISSUE)
			if("o.quoteItem.submissionDate".equalsIgnoreCase(sortField)) {
				sql.append(", o.quoteItem.id ").append(sortOrder);
			}
		}
		TypedQuery<T> query = em.createQuery(sql.toString(), entityClass);
		setParamters(query, queryBeans);

		query.setFirstResult(first);
		query.setMaxResults(pageSize);
//		LOGGER.log(Level.INFO, sql.toString());
		return query.getResultList();
	}
	private void setParamters(Query query, List<QueryBean> queryBeans) {
		if (null != queryBeans) {
			for (QueryBean bean : queryBeans) {
				if (bean.getCondition().equals(FilterMatchMode.CONTAINS)) {// like
					if(bean.getVarParam() !=null && "soldToCustomerNameParam".equals(bean.getVarParam().trim())){
						query.setParameter(bean.getVarParam(), "%" + bean.getValue() + "%");
					}else{
					query.setParameter(bean.getParameter(), "%" + bean.getValue() + "%");
					}
				} else if (bean.getCondition().equals(FilterMatchMode.STARTS_WITH)) {//
					query.setParameter(bean.getParameter(), "%" + bean.getValue());
				} else if (bean.getCondition().equals(FilterMatchMode.ENDS_WITH)) {
					query.setParameter(bean.getParameter(), bean.getValue() + "%");
				} else {
					query.setParameter(bean.getParameter(), bean.getValue());
				}
			}
		}
	}

	protected String defaultLazyDataQuery() {
		return "select o FROM " + entityClass.getSimpleName() + " o ";
	}

	private static final String JPQL_FUNCTION_DATE_TO_CHAR = "FUNCTION('TO_CHAR',{key},'DD/MM/YYYY HH24:MI')";
	private static final String JPQL_FUNCTION_KEY = "{key}";
	private static final String JPQL_KEY_AND = " AND ";
	/**
	 * 
	 * @param filters
	 * @param filter
	 *            can't be end with ' AND '
	 * @param queryBeans
	 * @return
	 */
	private StringBuilder formatSql(String filter, List<QueryBean> queryBeans) {
		StringBuilder sql = new StringBuilder(defaultLazyDataQuery());

		if (filter != null && !filter.equals("")) {
			if (!(filter.startsWith("and") || filter.startsWith(" and") || filter.startsWith("AND") || filter.startsWith(" AND"))) {
				sql.append(JPQL_KEY_AND);
			}
			sql.append(filter);
		}

		if (null != queryBeans) {
			for (QueryBean qb : queryBeans) {
				String finalKey = qb.getKey();
				if(qb.getDataType() != null && qb.getDataType().length() > 0 && "datestr".equals(qb.getDataType())) {
					finalKey = JPQL_FUNCTION_DATE_TO_CHAR.replace(JPQL_FUNCTION_KEY, qb.getKey());					
				} else if(qb.getCondition().sqlKey().equals("LIKE") || qb.getCondition().sqlKey().equals("=")){
					finalKey = "UPPER("+finalKey+")";
				}
				//added by damon@20160906
				if(qb.getVarParam() !=null && "soldToCustomerNameParam".equals(qb.getVarParam().trim())){
					sql.append(JPQL_KEY_AND).append(finalKey).append(" ").append(qb.getCondition().sqlKey()).append(" :").append(qb.getVarParam());
				}else{
					sql.append(JPQL_KEY_AND).append(finalKey).append(" ").append(qb.getCondition().sqlKey()).append(" :").append(qb.getParameter());
				}
				
			}
		}
		return sql;
	}

	public void create(T t) {
		em.persist(t);
	}

	public T update(T t) {
		return em.merge(t);
	}

	public <E> E findNoCache(Class<E> c,Object id) {
		TypedQuery<E> query = em.createQuery("select o from "+c.getSimpleName() + " o where o.id = :id",c);
		query.setParameter("id", id);
		query.setHint(QueryHints.CACHE_USAGE, CacheUsage.DoNotCheckCache);
		return query.getSingleResult();
	}
	
	public T find(Object id) {
		return em.find(entityClass, id);
	}

	public void remove(T t) {
		em.remove(em.merge(t));
	}
}
