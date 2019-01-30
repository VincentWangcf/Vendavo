package com.avnet.emasia.webquote.masterData.dao.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

import org.jboss.ejb3.annotation.TransactionTimeout;

//import com.avnet.emasia.webquote.commodity.bean.PricerUploadParametersBean;
//import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;
import com.avnet.emasia.webquote.entity.CostIndicator;
import com.avnet.emasia.webquote.entity.DataSyncError;
import com.avnet.emasia.webquote.entity.SapInterfaceBatches;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.masterData.util.StringUtils;

import com.avnet.emasia.webquote.masterData.dao.SapInterfaceBatchesDAO;
import com.avnet.emasia.webquote.masterData.dto.Config;
import com.avnet.emasia.webquote.masterData.dto.FileContext;
import com.avnet.emasia.webquote.masterData.ejb.DataSyncErrorSB;
import com.avnet.emasia.webquote.masterData.ejb.MasterDataSB;
import com.avnet.emasia.webquote.masterData.ejb.ScheduleConfigSB;
import com.avnet.emasia.webquote.masterData.ejb.StandardJob;
import com.avnet.emasia.webquote.masterData.ejb.remote.PosRemote;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.masterData.util.Constants;
import com.avnet.emasia.webquote.masterData.util.FunctionUtils;
import com.avnet.emasia.webquote.masterData.util.ResourceLoader;
import com.avnet.emasia.webquote.quote.ejb.PosSB;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;


@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SapInterfaceBatchesDAOImpl implements SapInterfaceBatchesDAO {
	@Resource
	UserTransaction ut;

	@PersistenceContext(unitName = "Server_Source")
	private EntityManager em;

	@EJB
	private DataSyncErrorSB errorSB;

	
	private static Logger logger = Logger.getLogger(MasterDataSB.class.getName());

	private final static int SESSION_TIME_OUT = 36000;
	private final static String MATERIAL_TYPE_NORMAL = "NORMAL";

	@Override
	public Long createBatch(String createdBy, String processID) throws CheckedException {
		// TODO Auto-generated method stub
		logger.info("SapInterfaceBatchesDAOImpl.createBatch: Begin");
		//List<DataSyncError> errorList = new ArrayList<DataSyncError>();
		int errorPosition = 0;
		String errorFileName = "";
		String errorFunction = "INSERT INTO SAP_INTERFACE_BATCHES";
		SapInterfaceBatches sapInterfaceBatches = new SapInterfaceBatches();
		

		try {
			ut.setTransactionTimeout(SESSION_TIME_OUT);
			ut.begin();
			
			sapInterfaceBatches.setCreatedBy(createdBy);
			sapInterfaceBatches.setCreatedOn(DateUtils.getCurrentAsiaDateObj());
			sapInterfaceBatches.setProcessID(processID);
			sapInterfaceBatches.setStartTime(DateUtils.getCurrentAsiaDateObj());
			sapInterfaceBatches.setProcessStatus(0);

			em.persist(sapInterfaceBatches);
			em.flush();
			em.clear();
			ut.commit();
		}
    	catch(Exception e)
    	{
    		e.printStackTrace();
	    	logger.log(Level.SEVERE, "createBatch", e);

			CheckedException cheEx = new CheckedException("[errorFunction:" + errorFunction + "] [errorFileName:"
					+ errorFileName + "] [errorPosition:" + errorPosition + "] [errorMsg: " + e.getMessage() + "]");
			throw cheEx;

		} finally {

		}

		logger.info("SapInterfaceBatchesDAOImpl.createBatch: End");
		return sapInterfaceBatches.getId();
	}

	@Override
	public void updateBatchEnd(Long Id, Integer processStatus) throws CheckedException {
		// TODO Auto-generated method stub
		logger.info("SapInterfaceBatchesDAOImpl.updateBatchEnd: Begin");
		//List<DataSyncError> errorList = new ArrayList<DataSyncError>();
		int errorPosition = 0;
		String errorFileName = "";
		String errorFunction = "UPDATE SAP_INTERFACE_BATCHES";
		List<SapInterfaceBatches> sapInterfaceBatches; // = new SapInterfaceBatches();
		

		try {
			sapInterfaceBatches = findById(Id);
			
			if (sapInterfaceBatches!=null && !sapInterfaceBatches.isEmpty()) {
				ut.setTransactionTimeout(SESSION_TIME_OUT);
				ut.begin();
				
				//sapInterfaceBatches.get(0)
				//sapInterfaceBatches.setId(Id);
				sapInterfaceBatches.get(0).setProcessStatus(processStatus);
				sapInterfaceBatches.get(0).setEndTime(DateUtils.getCurrentAsiaDateObj());

				em.merge(sapInterfaceBatches.get(0));
				em.flush();
				em.clear();
				ut.commit();				
			}

			logger.info("SapInterfaceBatchesDAOImpl.updateBatchEnd: End");
		}
    	catch(Exception e)
    	{
    		e.printStackTrace();
	    	logger.log(Level.SEVERE, "updateBatchEnd", e);

			CheckedException cheEx = new CheckedException("[errorFunction:" + errorFunction + "] [errorFileName:"
					+ errorFileName + "] [errorPosition:" + errorPosition + "] [errorMsg: " + e.getMessage() + "]");
			throw cheEx;

		} finally {

		}
		
	}

	public List<SapInterfaceBatches> findById(Long Id) {
		logger.info("SapInterfaceBatchesDAOImpl.findById: Begin");

		TypedQuery<SapInterfaceBatches> querySib = em
				.createQuery("SELECT a FROM SapInterfaceBatches a where a.id = :Id", SapInterfaceBatches.class);
		querySib.setParameter("Id", Id);
		List<SapInterfaceBatches> sibLst = querySib.getResultList();

		logger.info("SapInterfaceBatchesDAOImpl.findById: End");
		return sibLst;
	}
}
