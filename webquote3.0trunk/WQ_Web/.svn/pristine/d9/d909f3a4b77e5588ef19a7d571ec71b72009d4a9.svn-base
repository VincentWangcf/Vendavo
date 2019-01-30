package com.avnet.emasia.webquote.web.stm.job;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.faces.bean.ManagedProperty;

import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.msc.service.ServiceController;

import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.stm.constant.StmConstant;
import com.avnet.emasia.webquote.stm.dto.ExceptionVo;
import com.avnet.emasia.webquote.stm.dto.StmConfigInfo;
import com.avnet.emasia.webquote.stm.ejb.STMExceptionReportSB;
import com.avnet.emasia.webquote.stm.ejb.VendorQuotationSB;
import com.avnet.emasia.webquote.user.ejb.BizUnitSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.utilities.schedule.IScheduleTask;
import com.avnet.emasia.webquote.web.quote.job.EnvironmentService;
import com.avnet.emasia.webquote.web.stm.util.FTPClientException;
import com.avnet.emasia.webquote.web.stm.util.FTPClientUtil;
import com.avnet.emasia.webquote.web.stm.util.StmUtil;

@Stateless 
public class STMOutBoundTask implements IScheduleTask {

	private static final Logger LOG = Logger.getLogger(STMOutBoundTask.class.getName());
	@EJB
	private SystemCodeMaintenanceSB systemCodeMaintenanceSB;
    @EJB
    private BizUnitSB bizutSB;	
    @EJB
    private UserSB userSb;
    @EJB
    private MailUtilsSB mailUtilsSB;
	@EJB
	private VendorQuotationSB vendorQuotationSB;
    @EJB  
    private STMExceptionReportSB  stmExceptionReportSB;
    
    @EJB
	protected EJBCommonSB ejbCommonSB;
    
	@Override
	@Asynchronous
	public void executeTask(Timer timer) {
		LOG.info("STM project outBound job beginning...");
		try {
			
					StmConfigInfo stmConfigInfo = vendorQuotationSB.getStmSystemCode();
					File dirFile = new File(stmConfigInfo.getStmoutBoundpath());
					File files[] =dirFile.listFiles();
					if(files.length==0) return;
					FTPClientUtil ftp = new FTPClientUtil(stmConfigInfo.getStmediftpconfig());
					String backupFilePath = stmConfigInfo.getStmfilebackuppath() ;
					
					for(int i=0;i<files.length;i++){
						File file = files[i];
						String sendFileName = file.getName();
						String serverFilePath = stmConfigInfo.getEdireceivepath() + sendFileName;						
						boolean isSuccess = ftp.put(serverFilePath, file.getPath());
						
						if(isSuccess){								
							StmUtil.copyFileToBackUpDir(file, backupFilePath);
						}else{
							String stmExceptionPath = systemCodeMaintenanceSB.getStmExceptionPath();							
							StmUtil.copyFileToExceptionDir(file,stmExceptionPath);
							sendExceptionMail(file.getName(),"ftp put("+sendFileName+") failed");
						}
					}
		} catch (Exception e) {
			sendExceptionMail(null,StmUtil.printStackTrace2String(e));
			LOG.log(Level.SEVERE, "Exception in executing task STM project outBound job , Ëxception message : "+e.getMessage(), e);
		}

		LOG.info("STM project outBound job ended");
	}
	
	private void sendExceptionMail(String fileName, String message){
		ExceptionVo exception = new ExceptionVo();
		exception.setAction(StmConstant.ACTION_OUTBOUND);
		exception.setFileName(fileName);
		exception.setInBoundVo(null);
		exception.setOutBoundVo(null);
		exception.setMessage(message);
		exception.setUser(null);
		exception.setRegion(null);
		stmExceptionReportSB.sendException(exception);
	}
	
}
