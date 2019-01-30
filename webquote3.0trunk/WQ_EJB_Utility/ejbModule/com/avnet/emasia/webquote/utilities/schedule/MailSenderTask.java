package com.avnet.emasia.webquote.utilities.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.mail.MessagingException;
import javax.naming.NamingException;

import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;

/**
 * remark by Jason: this class only for test
 * 
 * @author 914975
 */
@Stateless
public class MailSenderTask implements IScheduleTask {

	private static final Logger LOG = Logger.getLogger(MailSenderTask.class.getName());

	@EJB
	private MailUtilsSB mailUtilsSB;

	/**
	 * send mail schedule class.
	 */
	@Override
	@Asynchronous
	public void executeTask(Timer timer) {
		LOG.info("send mail beginning...");
		MailInfoBean taskInfoBean = new MailInfoBean();
		taskInfoBean.setMailFrom("041863@AVNET.COM");
		List<String> mailTo = new ArrayList<String>();
		mailTo.add("041863@AVNET.COM");
		taskInfoBean.setMailTo(mailTo);
		taskInfoBean.setMailSubject("HA singleton timer test form:"+System.getProperty(HATimerService.JBOSS_NODE_NAME));
		taskInfoBean.setMailContent("nothing to do.");
		try {
			mailUtilsSB.sendTextMail(taskInfoBean);
		} catch (Exception e) {
			
			LOG.log(Level.SEVERE, "Send email failed! , Exception Message : "+e.getMessage(), e);
		}

		LOG.info("send mail ended");
	}
}
