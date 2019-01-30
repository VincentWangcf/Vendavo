package com.avnet.emasia.webquote.masterData.ejb.job;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.Timer;

import org.jboss.ejb3.annotation.TransactionTimeout;

import com.avnet.emasia.webquote.entity.MaterialRegional;
import com.avnet.emasia.webquote.masterData.dao.MaterialRegionalDAO;
import com.avnet.emasia.webquote.masterData.dao.SapInterfaceBatchesDAO;
import com.avnet.emasia.webquote.masterData.dto.Config;
import com.avnet.emasia.webquote.masterData.ejb.SapJob;
import com.avnet.emasia.webquote.masterData.ejb.ScheduleConfigSB;
import com.avnet.emasia.webquote.masterData.ejb.remote.SalesCostFlagToSapRemote;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.util.SftpUtil;


/**
 * @author Bryan,Toh
 * @Created on 2017-11-14
 */
@Stateless
@Remote(SalesCostFlagToSapRemote.class)
public class SalesCostFlagToSapJob extends SapJob implements SalesCostFlagToSapRemote
{
	private static Logger logger = Logger.getLogger(SalesCostFlagToSapJob.class.getName());
	//private static final String SALES_COST_FLAG_BATCH = "SC_FLAG";
	private static final String PROCESS_ID = "SC_FLAG";
	private static final String JOB_NAME = "Sales Cost Flag Job";
	private Config commonConfig;
 

    @EJB
    private ScheduleConfigSB schedConfSB;
    
    @EJB
    private SapInterfaceBatchesDAO sapInterfacesBatchesDAO;
    
    @EJB
    private MaterialRegionalDAO materialRegionalDAO;
    
    public SalesCostFlagToSapJob(){
    	this.processId = PROCESS_ID;
    	this.jobName = JOB_NAME;
    }
    
    //For schedule job
    /* (non-Javadoc)
     * @see com.avnet.emasia.webquote.masterData.ejb.job.SalesCostFlagToSapJob#executeTask(javax.ejb.Timer)
     */
    @Asynchronous
    @TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS) 
    public void executeTask(Timer timer)
    {
        try
        {
            run();	
        }
        catch(Exception e)
        {
        	String msg = MessageFormatorUtil.getParameterizedStringFromException(e);
        	logger.log(Level.SEVERE, "Exception occured in CustomerLoadingJob#executeTask for Timer: "+timer.getInfo()+", Task: "+this.processId + "(" + this.jobName +")" +", Reason for failure: "+msg, e);
        }
    }
    
    //For control-M
    @TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS) 
    @Override
    public int executeTask()
    {
        int returnInt = 1; 
    	try
        {
	        run();
        }
    	catch(Exception e)
        {
    		logger.log(Level.SEVERE, "Exception occured in executing task: "+this.processId + "(" + this.jobName +")" + ", Reason for failure: "+e.getMessage(), e);
        	returnInt = 0 ;
        }
        return returnInt;
    }
    
    public void process() throws CheckedException{
    	
    	try{
    		//setup config
    		this.commonConfig = schedConfSB.getCommonConfig();
    		logger.info("File path: " + commonConfig.getJobTempOutputPath());

    		//Insert record to SAP_INTERFACE_BATCHES table
    		long recordId = sapInterfacesBatchesDAO.createBatch(this.processId, this.processId);

    		//Update MATERIAL_REGIONAL.BATCH_STATUS to be process eg 11 or 12
    		materialRegionalDAO.batchStartUpdate();

    		//Select all the record to be process from MATERIAL_REGIONAL.BATCH_STATUS
    		List<MaterialRegional> materialRegionalList = materialRegionalDAO.getProcessingData();

    		//Output 'materialRegionalList' to a DAT file.
    		logger.info("materialRegionalList.size(): " + materialRegionalList.size());
    		File fileName = outputFile(materialRegionalList);
    		
    		//SFTP to SAP server
    		copyFileToSAP(fileName.getAbsolutePath());
    		
    		//Move output file to backup folder
			moveFile(fileName.getAbsolutePath(), this.commonConfig.getBackupPath());		
    		
    		//Update processed data to completed status
    		materialRegionalDAO.batchEndUpdate();
    		
    		//Update Batch status to completed
    		sapInterfacesBatchesDAO.updateBatchEnd(recordId, 1);
    		
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
    
    private File outputFile(List<MaterialRegional> materialRegionalList) throws CheckedException{
    	File file =  null;
    	String fileName = "";
    	try{
    		String pattern = "yyyyMMddHHmmss";
    		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//SAP_SC_MATyyyymmddhhmmss.DAT
    		Date now = DateUtils.getCurrentAsiaDateObj();
    		String formattedDate = simpleDateFormat.format(DateUtils.getCurrentAsiaDateObj());
    		//String formattedDate = simpleDateFormat.format(now);
    		fileName = "SAP_SC_MAT" +formattedDate + commonConfig.getDataFilePostfix();
//    		fileName = "SAP_SC_MAT_FLAG_" +formattedDate + commonConfig.getDataFilePostfix();
    		String datFile = this.commonConfig.getJobTempOutputPath() + fileName; //"C:\\Users\\P02811\\temp\\SAP_SC_MAT_FLAG_" + formattedDate + ".DAT";
    		file = new File(datFile);

    		// creates the file
    		file.createNewFile();
    		FileWriter writer = new FileWriter(file);
    		logger.info("Created file: " + file.getAbsoluteFile());

    		//Write file header
    		CSVUtils.writeLine(writer, Arrays.asList("REGION", "MAT_SC_IND", "APN", "MFR", "MPN"), '|');
    		//Write file content
    		for(MaterialRegional materialRegional : materialRegionalList){

    			CSVUtils.writeLine(writer, Arrays.asList(
    					materialRegional.getBizUnit().getName().equals("ASIA")?"": materialRegional.getBizUnit().getName(), 
    					materialRegional.isSalesCostFlag()?"X":"",
    					"",
    					materialRegional.getMaterial().getManufacturer().getName(), 
    					materialRegional.getMaterial().getFullMfrPartNumber()), '|');

    		}
    		logger.info("Completed output content to file : " + file.getAbsolutePath());

    		writer.flush();
    		writer.close();

    	}catch(Exception e){

    		Writer result = new StringWriter();
    		PrintWriter printWriter = new PrintWriter(result);
    		e.printStackTrace(printWriter);

    		CheckedException ce = new CheckedException(e);
    		ce.setMessage(e.getMessage() + ": \n" + result);
    		throw ce;
    	}
    	
    	return file;
    }
	
	@Override
	public boolean isRanOnThisServer(String serviceName) {
		return true;
	}
	
	
}
