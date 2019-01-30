package com.avnet.emasia.webquote.utilities.schedule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.DuplicateKeyException;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.TaskInfoBean;

/**
 * 
 * @author 914975
 */
@LocalBean
@Singleton
@Startup
public class TaskScheduleManager {

	private static final Logger LOG = Logger.getLogger(TaskScheduleManager.class.getName());
	@Resource
	private TimerService timerService;
	
	@PersistenceContext(unitName = "Server_Source")
	private EntityManager em;
	
	@PostConstruct
	public void init(){
		createNonePersitentTimer();
	}
	
	
    @Schedule(second="17", minute="*/17", hour="*", persistent=false,info="TimerForRefreshTimer")
    private void createNonePersitentTimer(){
    	
    	LOG.info("recreate NonePersitentTimer");
    	Collection<Timer> timers = timerService.getAllTimers();
    	
    	for (Timer timer : timers){
    		if ((! timer.isPersistent()) && (timer.getInfo() instanceof TaskInfoBean) ){
    			timer.cancel();
    		}
    	}
    	
    	List<TaskInfoBean> tasks = findAllNonePersistentTask();
    	for (TaskInfoBean task : tasks){
    		TimerConfig timerConfig = new TimerConfig(task, false);
    		timerService.createCalendarTimer(createScheduleExpression(task), timerConfig);
    	}
    	LOG.info("recreate NonePersitentTimer done");
    	
    }		
	
    @Timeout
    public void timeout(Timer timer){
    	if (timer.getInfo() instanceof TaskInfoBean){
    		
    		LOG.info("Timer: " + timer.getInfo() + " timeout.");
    		try {
    			TaskInfoBean taskInfoBean = (TaskInfoBean) timer.getInfo();
    			IScheduleTask batchTask = (IScheduleTask) InitialContext.doLookup(taskInfoBean.getTaskClassName());
    			batchTask.executeTask(timer);
    			LOG.info("timer execute successfully!");
    		} catch (Exception e) {
    			e.printStackTrace();
    		}    		
    	} 
    }	


	/**
	 * create a task.
	 * 
	 * @param taskInfoBean
	 * @return
	 * @throws Exception
	 */
	public TaskInfoBean createTask(TaskInfoBean taskInfoBean) throws Exception {
		if (getTimer(taskInfoBean) != null) {
			LOG.severe("task already exist!");
			throw new DuplicateKeyException("task already exist!");
		}
		TimerConfig config = new TimerConfig(taskInfoBean, taskInfoBean.isPersistent());
		ScheduleExpression scheduExp = createScheduleExpression(taskInfoBean);

		Timer newTimer = timerService.createCalendarTimer(scheduExp, config);
		taskInfoBean.setNextTimeout(newTimer.getNextTimeout());
		LOG.info("create a timer successfully! scheduler expression: " + scheduExp.toString());
		
		return taskInfoBean;
	}
	
    private ScheduleExpression createScheduleExpression(TaskInfoBean taskInfoBean){
    	ScheduleExpression se  = new ScheduleExpression();
//		if enable below two lines, the timer cannot be recreate after jboss restart. could be a jboss bug.
//		scheduExp.start(taskInfoBean.getStartDate());
//		scheduExp.end(taskInfoBean.getEndDate());    	
    	se.second(taskInfoBean.getSecond());
    	se.minute(taskInfoBean.getMinute());
    	se.hour(taskInfoBean.getHour());
    	se.dayOfMonth(taskInfoBean.getDayOfMonth());
    	se.dayOfWeek(taskInfoBean.getDayOfWeek());
    	se.year(taskInfoBean.getYear());
    	
    	return se;
    }	

	/*
	 * Returns a list of taskInfoBean for the active timers
	 */
	public List<TaskInfoBean> getTaskList() {
		
		List<TaskInfoBean> taskList = new ArrayList<>();
		//List<TaskInfoBean> taskListOutPut = new ArrayList<>();
		for (Timer t : timerService.getAllTimers()) {
			if (!(t.getInfo() instanceof TaskInfoBean)){
				continue;
			}
			TaskInfoBean taskInfoBean = (TaskInfoBean) t.getInfo();
			if (t.isPersistent()){
				taskInfoBean.setPersistent(true);
				taskInfoBean.setNextTimeout(t.getNextTimeout());
				taskList.add(taskInfoBean);
			}
		}
		
		taskList.addAll(findAllNonePersistentTask());
		LOG.info("get all exist task successfully!");
		return taskList;
	}
	/*****
	public void callPersist() {
		List<TaskInfoBean> taskList = new ArrayList<>();
		//List<TaskInfoBean> taskListOutPut = new ArrayList<>();
		for (Timer t : timerService.getAllTimers()) {
			if (!(t.getInfo() instanceof TaskInfoBean)){
				continue;
			}
			TaskInfoBean taskInfoBean = (TaskInfoBean) t.getInfo();
			if (t.isPersistent()){
				taskInfoBean.setPersistent(true);
				taskInfoBean.setNextTimeout(t.getNextTimeout());
				taskList.add(taskInfoBean);
			}
		}
		LOG.info("callPersist()::" + taskList.size());
		persist(taskList);
	}
	
	public void callByCreate() {
		List<TaskInfoBean> taskList =  deSerilize();
		LOG.info("callByCreate()::" + taskList.size());
		for(TaskInfoBean t : taskList) {
			try {
				t.setPersistent(true);
				this.updateTask(t);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	static final String folder = "/app/eap62/webquote/b2bfiles/TIS/exception";
	private void persist(List<TaskInfoBean> taskList) {
		
		try {
			ByteArrayOutputStream bo1=new ByteArrayOutputStream();    
			ObjectOutputStream oo1=new ObjectOutputStream(bo1);    
			oo1.writeObject(taskList);    
			ByteArrayInputStream bi1=new ByteArrayInputStream(bo1.toByteArray());    
			ObjectInputStream oi1=new ObjectInputStream(bi1);    
			taskList = ((List<TaskInfoBean>)oi1.readObject());
			 
			taskList.stream().forEach(p -> {
				if(p.getTaskClassName() !=null)
				{
					String d = p.getTaskClassName().replaceFirst("WebQuoteEar", "WebQuote_jpEar");
					p.setTaskClassName(d);
				}
				
			});
			FileOutputStream bo =  new FileOutputStream(new File(folder + File.separator  + "task.txt"));
			ObjectOutputStream oo=new ObjectOutputStream(bo);    
			oo.writeObject(taskList); 
		} catch(Exception e){
			e.printStackTrace();
			LOG.info("Customer deepClone() failed::" + e.getMessage() + e.getStackTrace());
		}
		LOG.info("persist()::" + taskList.size());
		LOG.info("get all exist task successfullyew!");
	}
	
	private List<TaskInfoBean> deSerilize() {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					                 new File(folder + File.separator  + "task.txt")));
			List<TaskInfoBean> taskList = (List<TaskInfoBean>) ois.readObject();
			
			System.out.println("fa c ::" + taskList.size());
			return taskList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Collections.EMPTY_LIST;  
		
		

	}
	*/
	/*
	 * Updates a timer with the given taskInfo
	 */
	public TaskInfoBean updateTask(TaskInfoBean taskInfoBean) throws Exception {
		
		if (taskInfoBean.isPersistent()){
			deleteTask(taskInfoBean);
			TaskInfoBean taskInfo = createTask(taskInfoBean);
			LOG.info("update task successfully!");
			return taskInfo;
		} else {
			return em.merge(taskInfoBean);
		}		
		

	}

	/*
	 * Remove a timer with the given TaskInfo
	 */
	public void deleteTask(TaskInfoBean taskInfoBean) {
		
		if (taskInfoBean.isPersistent()){
			Timer t = getTimer(taskInfoBean);
			if (t != null) {
				t.cancel();
			}
			
		} else {
			taskInfoBean  =em.merge(taskInfoBean);
			em.remove(taskInfoBean);
		}
	}
	
	public List<TaskInfoBean> findAllNonePersistentTask() {
		String sql = "select o from " + TaskInfoBean.class.getSimpleName() + " o ";
		return em.createQuery(sql, TaskInfoBean.class).getResultList();
	}
	

    
	private Timer getTimer(TaskInfoBean taskInfoBean) {
		for (Timer timer : timerService.getTimers()) {
			if (timer.getInfo() instanceof TaskInfoBean){
				if (taskInfoBean.equals((TaskInfoBean) timer.getInfo())) {
					LOG.info("get the indicated timer successfully!");
					return timer;
				}
			}
		}

		return null;
	}
    

    
    
	
}