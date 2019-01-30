package com.avnet.emasia.webquote.utilites.web.schedule;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.avnet.emasia.webquote.entity.TaskInfoBean;
import com.avnet.emasia.webquote.utilities.schedule.TaskScheduleManager;

/**
 * 
 * @author 914975
 */
@ManagedBean(name = "TaskMB")
@SessionScoped
public class TaskMB implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(TaskMB.class.getName());
	@EJB
	private TaskScheduleManager taskScheduleManager;
	
	private TaskInfoBean selectedTask;
	
	public TaskMB() {
	}

	public List<TaskInfoBean> getTaskList(){
		return taskScheduleManager.getTaskList();
	}
	
	public TaskInfoBean getSelectedTask() {
		return selectedTask;
	}

	
	public void setSelectedTask(TaskInfoBean selectedTask) {
		this.selectedTask = selectedTask;

	}
	
	public String editTask(TaskInfoBean taskInfoBean){
		selectedTask = taskInfoBean;
		selectedTask.setNewRecord(false);
		return "TaskDetails";		
	}

	/*
	 * Action handler for New Task button
	 */
	public String createNewTask() {
		selectedTask = new TaskInfoBean();
//		selectedTask.setNewRecord(true);
		selectedTask.setDeafaultValues();
		LOG.info("create new task.");
		return "TaskDetails";
	}

	/*
	 * Action handler for Update button in the Details page
	 */
	public String updateTask() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {

			taskScheduleManager.updateTask(selectedTask);

//			selectedTask = taskScheduleManager.updateTask(selectedTask);
			
			LOG.info("task successfully updated!");
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Success",
					"task successfully updated!"));
		} catch (Exception ex) {
			LOG.severe("task successfully Failed!" + ex);
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Failed", ex.getCause()
							.getMessage()));
		}
		return null;
	}

	/*
	 * Action handler for Delete button in the Details page
	 */
	public String deleteTask() {
		taskScheduleManager.deleteTask(selectedTask);
		return "TaskList";
	}

	/*
	 * Action handler for Create button in the New page
	 */
	//wait to remove with TaskNew.xhtml
	@Deprecated
	public String createTask() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			selectedTask = taskScheduleManager.createTask(selectedTask);
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucess",	"task successfully created!"));
			LOG.info("task successfully created!");
			return "TaskDetails";
		} catch (Exception ex) {
			LOG.severe("task successfully failed!");
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Failed", ex.getCause()
							.getMessage()));
		}
		return null;
	}
}