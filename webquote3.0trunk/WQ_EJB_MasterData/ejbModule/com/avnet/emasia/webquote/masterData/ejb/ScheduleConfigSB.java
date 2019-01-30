package com.avnet.emasia.webquote.masterData.ejb;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.masterData.dto.Config;
import com.avnet.emasia.webquote.masterData.util.Constants;
import com.avnet.emasia.webquote.masterData.util.StringUtils;
import com.avnet.emasia.webquote.entity.ScheduleConfig;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-23
 * 
 */
@Stateless
public class ScheduleConfigSB {
	
    @PersistenceContext(unitName = "Server_Source")
    private EntityManager em;
    private static Logger logger = Logger.getLogger(ScheduleConfigSB.class.getName());
    public ScheduleConfigSB() {

	}
    
    public String[] getTables(String type)
    {
    	Query query = em.createQuery("SELECT a FROM ScheduleConfig a where a.type=:type and a.key=:key");
    	query.setParameter("type", type.trim());
    	query.setParameter("key", "TABLES");
    	ScheduleConfig result = (ScheduleConfig)query.getSingleResult();
    	String resultStr = result.getValue();
    	logger.info(" Schedule config SB getTables  : "+resultStr);
    	String[] returnStr=StringUtils.splitStringToArray(resultStr, "|");
    	return returnStr;
    }
    public String[] getErrorEmailAddress()
    {
    	Query query = em.createQuery("SELECT a FROM ScheduleConfig a where a.type=:type and a.key=:key");
    	query.setParameter("type", "COMMON");
    	query.setParameter("key", "MAIL_SEND_TO");
    	ScheduleConfig result = (ScheduleConfig)query.getSingleResult();
    	String resultStr = result.getValue();
    	logger.info(" Schedule getErrorEmailAddress : "+resultStr);
    	String[] returnStr=StringUtils.splitStringToArray(resultStr, "|");
    	return returnStr;
    }
    public boolean getSwitch(String type)
    {
    	Query query = em.createQuery("SELECT a FROM ScheduleConfig a where a.type=:type and a.key=:key");
    	query.setParameter("type", type.trim());
    	query.setParameter("key", "ADDI_JOB_SWITCH");
    	ScheduleConfig result = (ScheduleConfig)query.getSingleResult();
    	String resultStr = result.getValue();
    	logger.info(" Schedule config SB getSwitch  : "+resultStr);
    	if(resultStr.equalsIgnoreCase("Y"))
    		return true;
    	else 
    		return false;
    }
    /*
     * Retrieve the schdele config object from db.
     * @return Config : return a config object
     * @Param type : the config type.
     */
    public Config getConfig(String type)
    {
    	logger.info("call getConfig method");
        Query query = em.createNamedQuery("SCHEDULE_CONFIG.findByType"); 
        query.setParameter("type1", "COMMON");
    	query.setParameter("type2", type.trim());
        List<ScheduleConfig> results = query.getResultList();
        Config config = null;
        if(results!=null && results.size()>0)
        {
            config = new Config();
        	for(ScheduleConfig scheConfig: results)
        	{
        		em.refresh(scheConfig);
        		if(Constants.FILE_PATH.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setFilePath(scheConfig.getValue());
        		}	
        		else if(Constants.BACKUP_PATH.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setBackupPath(scheConfig.getValue());
        		}
        		else if(Constants.DATA_FILE_POSTFIX.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setDataFilePostfix(scheConfig.getValue());
        		}
        		else if(Constants.CONTROL_FILE_POSTFIX.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setControlFilePostfix(scheConfig.getValue());
        		}
        		else if(Constants.MAILSERVER.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setMailServer(scheConfig.getValue());
        		}
        		else if(Constants.MAIL_SEND_FROM.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setMailSendFrom(scheConfig.getValue());
        		}
        		else if(Constants.MAIL_SEND_TO.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setMailSendTo(scheConfig.getValue());
        		}
        		else if(Constants.ALLOW_MUTIPLE_LOAD.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setAllowMultipleLoad(scheConfig.getValue());
        		}
        		else if(Constants.BACKUP_FLAG.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setBackupFlag(scheConfig.getValue());
        		}
        		else if(Constants.FILE_NAME.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setFileName(scheConfig.getValue());
        		}
        		else if(Constants.CLASS_NAME.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setClassName(scheConfig.getValue());
        		}
        		else if(Constants.METHODS.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setMethods(StringUtils.splitStringToArray(scheConfig.getValue(), "|", true));
        		}
        		else if(Constants.PARAM_TYPE.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setParamTypes(StringUtils.splitStringToArray(scheConfig.getValue(), "|", true));
        		}
        		else if(Constants.PK_CLASS.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setPkClass(scheConfig.getValue());
        		}
        		else if(Constants.PK_CLASS_METHODS.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setPkClassMethods(StringUtils.splitStringToArray(scheConfig.getValue(), "|", true));
        		}
        		else if(Constants.PK_CLASS_PARAM_TYPE.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setPkClassParamTypes(StringUtils.splitStringToArray(scheConfig.getValue(), "|", true));
        		}
        		else if(Constants.FK_CLASS.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setFkClass(scheConfig.getValue());
        		}
        		else if(Constants.FK_CLASS_METHODS.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setFkClassMethods(StringUtils.splitStringToArray(scheConfig.getValue(), "|", true));
        		}
        		else if(Constants.FK_CLASS_PARAM_TYPE.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setFkClassParamTypes(StringUtils.splitStringToArray(scheConfig.getValue(), "|", true));
        		}
        		else if(Constants.FK1_CLASS.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setFk1Class(scheConfig.getValue());
        		}
        		else if(Constants.FK1_CLASS_METHODS.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setFk1ClassMethods(StringUtils.splitStringToArray(scheConfig.getValue(), "|", true));
        		}
        		else if(Constants.FK1_CLASS_PARAM_TYPE.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setFk1ClassParamTypes(StringUtils.splitStringToArray(scheConfig.getValue(), "|", true));
        		}
        		else if(Constants.FK2_CLASS.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setFk2Class(scheConfig.getValue());
        		}
        		else if(Constants.FK2_CLASS_METHODS.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setFk2ClassMethods(StringUtils.splitStringToArray(scheConfig.getValue(), "|", true));
        		}
        		else if(Constants.FK2_CLASS_PARAM_TYPE.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setFk2ClassParamTypes(StringUtils.splitStringToArray(scheConfig.getValue(), "|", true));
        		}
        		else if(Constants.ROW_LIMIT.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setRowLimit(Long.parseLong(scheConfig.getValue()));
        		}
        	}
        }
        	
        return config;
    }
    
    //Bryan - Start
    /*
     * Retrieve the schdele config object from db.
     * @return Config : return a config object
     * @Param type : the config type.
     */
    public Config getCommonConfig()
    {
    	logger.info("call getConfig method"); 
        Query query = em.createQuery("SELECT a FROM ScheduleConfig a where a.type = :type1");
        query.setParameter("type1", "COMMON");

        List<ScheduleConfig> results = query.getResultList();
        Config config = null;
        
        if(results!=null && results.size()>0)
        {
            config = new Config();
        	for(ScheduleConfig scheConfig: results)
        	{
        		em.refresh(scheConfig);
        		if(Constants.FILE_PATH.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setFilePath(scheConfig.getValue());
        		}	
        		else if(Constants.BACKUP_PATH.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setBackupPath(scheConfig.getValue());
        		}
        		else if(Constants.DATA_FILE_POSTFIX.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setDataFilePostfix(scheConfig.getValue());
        		}
        		else if(Constants.CONTROL_FILE_POSTFIX.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setControlFilePostfix(scheConfig.getValue());
        		}
        		else if(Constants.MAILSERVER.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setMailServer(scheConfig.getValue());
        		}
        		else if(Constants.MAIL_SEND_FROM.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setMailSendFrom(scheConfig.getValue());
        		}
        		else if(Constants.MAIL_SEND_TO.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setMailSendTo(scheConfig.getValue());
        		}
        		else if(Constants.ALLOW_MUTIPLE_LOAD.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setAllowMultipleLoad(scheConfig.getValue());
        		}
        		else if(Constants.JOB_TEMP_OUTPUT_PATH.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setJobTempOutputPath(scheConfig.getValue());
        		}
        		else if(Constants.ROW_LIMIT.equalsIgnoreCase(scheConfig.getKey()))
        		{
        			config.setRowLimit(Long.parseLong(scheConfig.getValue()));
        		}

        	}
        }
        	
        return config;
    }
    //Bryan - End
}
