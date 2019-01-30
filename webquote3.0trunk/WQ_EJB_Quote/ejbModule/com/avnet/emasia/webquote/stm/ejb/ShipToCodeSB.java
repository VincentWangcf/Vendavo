package com.avnet.emasia.webquote.stm.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.avnet.emasia.webquote.entity.ShipToCodeMapping;
import com.avnet.emasia.webquote.entity.Team;
import com.avnet.emasia.webquote.stm.dto.ShipToCodeMappingVo;
import com.avnet.emasia.webquote.user.ejb.TeamSB;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;

@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class ShipToCodeSB {
	
	@PersistenceContext(unitName = "Server_Source")
	private EntityManager em;

	@EJB
	private TeamSB teamSB;
	
	@Resource
	private UserTransaction ut;
	
	private static Logger logger = Logger.getLogger(ShipToCodeSB.class.getName());

	public String verifyTeamExist(List<ShipToCodeMappingVo> list, String language){
		ArrayList<String> errlist = new ArrayList<String>();

		for(ShipToCodeMappingVo bean:list){
			String teamName = bean.getTeamName();

			List<Team> teams = teamSB.findByName(teamName);
			if(teams.size()==0){
				Object arr[] = {bean.getXlsLineSeq(),teamName};
				errlist.add(MessageFormatorUtil.getParameterizedString(new Locale(language), "wq.message.notExistInSystem", arr)+" <br>");
			}else{
				bean.setTeam(teams.get(0));
			}
		}
		return errlist.toString();
	}
	
	public boolean deleteAndSaveToDB(List<ShipToCodeMappingVo> list){
		
		Boolean bool = false;
		try {
			ut.setTransactionTimeout(100000);
			ut.begin();
			
			//before save into DB, to do remove all firstly
			logger.info("remove all BPM records from DB!");
			Query delete = em.createQuery("DELETE FROM ShipToCodeMapping");
			delete.executeUpdate();
			em.flush();
			em.clear();
			logger.info("Finish remove all ShipToCodeMapping records from DB!");
			
			logger.info("Begin save ShipToCodeMapping  to Db!");

			for(ShipToCodeMappingVo bean:list){
				ShipToCodeMapping sga = new ShipToCodeMapping();
				sga.setTeam(bean.getTeam());
				sga.setShipToCode(bean.getSgashipToCode());
				sga.setType("SGA");
				em.persist(sga);
				
				ShipToCodeMapping sada = new ShipToCodeMapping();
				sada.setTeam(bean.getTeam());
				sada.setShipToCode(bean.getSadashipToCode());
				sada.setType("SADA");
				em.persist(sada);
				
			}
			ut.commit();
			bool = true;
		
		} catch (NotSupportedException|SystemException|SecurityException|IllegalStateException|RollbackException|HeuristicMixedException|HeuristicRollbackException e) {
			logger.log(Level.SEVERE,"NotSupportedException occurs in deleteAndSaveToDB "+ e.getMessage(),e);
		} 
		
		
		logger.info("End Save ShipToCodeMapping  to Db!");
		return bool;
	}
}
