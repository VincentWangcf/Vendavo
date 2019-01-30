package com.avnet.emasia.webquote.utilities;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;

import com.avnet.emasia.webquote.entity.AppMessages;
import com.avnet.emasia.webquote.entity.AppProperty;
import com.avnet.emasia.webquote.entity.LocaleMaster;
import com.avnet.emasia.webquote.entity.util.EntityManagerUtils;
import com.avnet.emasia.webquote.exception.WebQuoteException;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class LocaleMessageServices {
	
	@EJB
	private LocaleMessageServices localeMessageServices;
	
	public static LocaleMessageServices INSTANCE = new LocaleMessageServices();
	private static final Logger LOG = Logger.getLogger(LocaleMessageServices.class.getName());
	
	public static LocaleMessageServices getInstance(){
		return INSTANCE;
	}

	public List<LocaleMaster> findAll() throws WebQuoteException {
		EntityManager em = EntityManagerUtils.getEntityManager();
		Query query = em.createQuery("SELECT e FROM LocaleMaster e");
		return (List<LocaleMaster>) query.getResultList();
	}
	
	public List<AppMessages> getMessageByLocale(String language) throws WebQuoteException {
			EntityManager em = EntityManagerUtils.getEntityManager();
		  String strQuery="SELECT e1.ID, e1.MESSAGE_CODE, e1."+language+" FROM APP_MESSAGES e1";
		  Query query =em.createNativeQuery(strQuery, AppMessages.class);
		  List<AppMessages> objects= (List<AppMessages>)query.getResultList();
		  return objects;
	}
	
	public List<AppMessages> getMessageByLocale() throws WebQuoteException {
		EntityManager em = EntityManagerUtils.getEntityManager();
		  String strQuery="SELECT e1.ID, e1.MESSAGE_CODE, e1.EN, e1.JA FROM APP_MESSAGES e1";
		  Query query =em.createNativeQuery(strQuery, AppMessages.class);
		  List<AppMessages> objects= (List<AppMessages>)query.getResultList();
		  return objects;
	}
	
	public LocaleMaster findByPk(Long localeId) throws WebQuoteException {
		EntityManager em = EntityManagerUtils.getEntityManager();
		return em.find(LocaleMaster.class, localeId);
	}
	
	
	public void updateProperty(AppProperty appProperty) throws WebQuoteException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException, NamingException  {
		EntityManager em = EntityManagerUtils.getEntityManager();
		UserTransaction transaction = null;
		try {
			transaction = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
		}catch(Exception e){
			//this caller must be ejb ,so no get the transaction.
			transaction = null;
			//e.printStackTrace();
			return;
		}
		
		if(transaction == null){
			LOG.log(Level.INFO, "transaction is null");
		}else{
			LOG.log(Level.INFO, "transaction is notnull");
		}
		if(transaction !=null) {
			transaction.begin();
		}
		
		AppProperty prop = em.find(AppProperty.class, appProperty.getPropKey());
		if(prop!=null){
			prop.setPropKey(appProperty.getPropKey());
			prop.setPropValue(appProperty.getPropValue());
			prop.setPropDesc(appProperty.getPropDesc());
			prop.setPropReserved(appProperty.getPropReserved());
			em.merge(prop);
		}else{
			em.persist(appProperty);
		}
		if(transaction !=null) {
			transaction.commit();
		}
		
	}
	
	private List<AppProperty> getSystemProperty() throws WebQuoteException{
		EntityManager em = EntityManagerUtils.getEntityManager();
		Query query = em.createQuery("SELECT a FROM AppProperty a");
		if (query.getResultList() instanceof List<?>) {
			return (List<AppProperty>) query.getResultList();
		}
		return null;
	}
	
	public  boolean needsReload(String propretyKey) throws WebQuoteException{
		for (AppProperty appPrty : getSystemProperty()) {
			if (propretyKey.equals(appPrty.getPropKey())) {
				if(null!=appPrty.getPropValue()){
					return Boolean.valueOf(appPrty.getPropValue()); 
				}
			}
		}
		return false;
	}
	
	public  long getReloadTime(String propretyKey) throws WebQuoteException{
		for (AppProperty appPrty : getSystemProperty()) {
			if (propretyKey.equals(appPrty.getPropKey())) {
				if(null!=appPrty.getPropValue()){
					return Long.valueOf(appPrty.getPropValue()); 
				}
			}
		}
		return 10*1000;
	}
}