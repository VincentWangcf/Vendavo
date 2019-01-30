package com.avnet.emasia.webquote.masterData.ejb.scheduleJob;

//import java.io.File;
//import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
//import java.math.BigDecimal;
//import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;

import javax.ejb.Stateless;
import javax.ejb.Timer;

import org.jboss.ejb3.annotation.TransactionTimeout;

//import com.avnet.emasia.webquote.entity.QuantityBreakPrice;
////import com.avnet.emasia.webquote.entity.MaterialRegional;
//import com.avnet.emasia.webquote.entity.SalesCostPricer;
//import com.avnet.emasia.webquote.entity.PricerDeleted;
//import com.avnet.emasia.webquote.masterData.dao.SalesCostPricerDAO;
//import com.avnet.emasia.webquote.masterData.dao.PricerDeletedDAO;
//import com.avnet.emasia.webquote.masterData.dao.SapInterfaceBatchesDAO;
//import com.avnet.emasia.webquote.masterData.dto.Config;
import com.avnet.emasia.webquote.masterData.ejb.SapJob;
import com.avnet.emasia.webquote.masterData.ejb.ScheduleConfigSB;
import com.avnet.emasia.webquote.masterData.ejb.job.CSVUtils;
import com.avnet.emasia.webquote.masterData.ejb.remote.SalesCostFlagToSapRemote;
import com.avnet.emasia.webquote.masterData.ejb.remote.SalesCostPricerToSapRemote;
//import com.avnet.emasia.webquote.masterData.ejb.StandardJob;
//import com.avnet.emasia.webquote.masterData.ejb.remote.SalesCostFlagToSapRemote;
//import com.avnet.emasia.webquote.masterData.ejb.remote.SalesCostPricerToSapRemote;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.util.SftpUtil;


/**
 * @author WingHong, Wong
 * @Created on 2017-11-21
 */
@Stateless
public class ScToSapScheduleJob extends SapJob
{
	private static Logger logger = Logger.getLogger(ScToSapScheduleJob.class.getName());
	//private static final String SALES_COST_FLAG_BATCH = "SC_PRICER";
	private static final String PROCESS_ID = "SC_PRICER";
	private static final String JOB_NAME = "Sales Cost Pricer Job";
	//private Config commonConfig;
 

/*	@EJB
	SalesCostFlagToSapScheduleJob salesCostFlagToSapScheduleJob;

	@EJB
	SalesCostPricerToSapScheduleJob salesCostPricerToSapScheduleJob;
*/
	@EJB
	SalesCostPricerToSapRemote salesCostPricerToSapRemote;

	@EJB
	SalesCostFlagToSapRemote salesCostFlagToSapRemote;


	/*	@EJB
    private ScheduleConfigSB schedConfSB;
    
    @EJB
    private SapInterfaceBatchesDAO sapInterfacesBatchesDAO;
    
    @EJB
    private SalesCostPricerDAO salesCostPricerDAO;
    //private MaterialRegionalDAO materialRegionalDAO;

    @EJB
    private PricerDeletedDAO pricerDeletedDAO;
*/    
    public ScToSapScheduleJob(){
    	this.processId = PROCESS_ID;
    	this.jobName = JOB_NAME;
    }
    
    //For schedule job
    /* (non-Javadoc)
     * @see com.avnet.emasia.webquote.masterData.ejb.job.SalesCostPricerToSapJob#executeTask(javax.ejb.Timer)
     */
    @Asynchronous
    @TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS) 
    public void executeTask(Timer timer)
    {
        try
        {
        	//setType("SCP_TO_SAP");
            run();	
        }
        catch(Exception e)
        {
        	String msg = MessageFormatorUtil.getParameterizedStringFromException(e);
        	logger.log(Level.SEVERE, "Exception occured in CustomerLoadingJob#executeTask for Timer: "+timer.getInfo()+", Task: "+this.processId + "(" + this.jobName +")" +", Reason for failure: "+msg, e);
        }
    }
    
    //For control-M
//    @TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS) 
//    @Override
//    public int executeTask()
//    {
//        int returnInt = 1; 
//    	try
//        {
//	        run();
//        }
//    	catch(Exception e)
//        {
//    		logger.log(Level.SEVERE, "Exception occured in executing task: "+this.processId + "(" + this.jobName +")" + ", Reason for failure: "+e.getMessage(), e);
//        	returnInt = 0 ;
//        }
//        return returnInt;
//    }
    
    @Override
    public void process() throws CheckedException{
    	System.out.println("SalesCostPricerToSapScheduleJob.process(): Begin");    	
    	try{
    		//1) call SalesCostFlagToSapJob
	    	if(salesCostFlagToSapRemote != null) {
	    		salesCostFlagToSapRemote.executeTask();
	    	} else {
	    	    CheckedException ce = new CheckedException();
	    		ce.setMessage("ERROR: salesCostFlagToSapRemote is NULL");
	    		throw ce;
	    	}
    		
    		
//2) call SalesCostPricerToSapJob
	    	if(salesCostPricerToSapRemote != null) {
	    		salesCostPricerToSapRemote.executeTask();
	    	} else {
	    	    CheckedException ce = new CheckedException();
	    		ce.setMessage("ERROR: salesCostPricerToSapRemote is NULL");
	    		throw ce;
	    	}

/* 
//Below code will run @Asynchronous, which not prefer in this job
 			if(salesCostFlagToSapScheduleJob != null) {
			      Timer tt = null;// = new Timer;
			      
			      salesCostFlagToSapScheduleJob.executeTask(tt);
	    	} else {
	    	    CheckedException ce = new CheckedException();
	    		ce.setMessage("ERROR: salesCostFlagToSapScheduleJob is NULL");
	    		throw ce;
	    	}
    		
			
			if(salesCostPricerToSapScheduleJob != null) {
			      Timer tt = null;// = new Timer;
			      
			      salesCostPricerToSapScheduleJob.executeTask(tt);
	    	} else {
	    	    CheckedException ce = new CheckedException();
	    		ce.setMessage("ERROR: salesCostPricerToSapScheduleJob is NULL");
	    		throw ce;
	    	}
*/  		
    	}catch(CheckedException e){
    		Writer result = new StringWriter();
    	    PrintWriter printWriter = new PrintWriter(result);
    	    e.printStackTrace(printWriter);
    	    
    	    CheckedException ce = new CheckedException(e);
    		ce.setMessage(e.getMessage() + ": \n" + result);
    		throw ce;
    		
    	}catch(Exception e){
    	
    		Writer result = new StringWriter();
    	    PrintWriter printWriter = new PrintWriter(result);
    	    e.printStackTrace(printWriter);
    	    
    	    CheckedException ce = new CheckedException(e);
    		ce.setMessage(e.getMessage() + ": \n" + result);
    		throw ce;
    	}
    }
    
	
}
