package com.avnet.emasia.webquote.user.ejb;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import com.avnet.emasia.webquote.entity.AppMessages;
import com.avnet.emasia.webquote.entity.LocaleMaster;

@Stateless
@LocalBean
public class LocaleMessagesSB {

	@PersistenceContext(unitName = "Server_Source")
	EntityManager em;

	public List<LocaleMaster> findAll() {
		Query query = em.createQuery("SELECT e FROM LocaleMaster e");
		return (List<LocaleMaster>) query.getResultList();
	}
	public List<AppMessages> getMessageByLocale(String language) {
		  String strQuery="SELECT e1.ID, e1.MESSAGE_CODE, e1."+language+" FROM APP_MESSAGES e1";
		  Query query =em.createNativeQuery(strQuery, AppMessages.class);
		  List<AppMessages> objects= (List<AppMessages>)query.getResultList();
		  return objects;
	}
	
	public List<AppMessages> getMessageByLocale() {
		  String strQuery="SELECT e1.ID, e1.MESSAGE_CODE, e1.EN, e1.JA FROM APP_MESSAGES e1";
		  Query query =em.createNativeQuery(strQuery, AppMessages.class);
		  List<AppMessages> objects= (List<AppMessages>)query.getResultList();
		  return objects;
	}
	
	public LocaleMaster findByPk(Long localeId) {
		return em.find(LocaleMaster.class, localeId);
	}
}