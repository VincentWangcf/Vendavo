package com.avnet.emasia.webquote.commodity.web.timersrvice;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.data.FilterEvent;

import com.avnet.emasia.webquote.entity.TaskInfo;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilities.ejb.TaskInfoSB;
import com.avnet.emasia.webquote.utilities.schedule.ClusterTimerService;
import com.avnet.emasia.webquote.web.datatable.BaseLazyDataMB;
import com.avnet.emasia.webquote.web.datatable.LazyEntityDataModel;
import com.avnet.emasia.webquote.web.quote.FacesUtil;
import com.avnet.emasia.webquote.web.user.UserInfo;

@ManagedBean
@ViewScoped
public class TaskInfoMB extends BaseLazyDataMB<TaskInfo> implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private TaskInfoSB taskInfoSB;
	private TaskInfo taskInfo;
	private List<TaskInfo> selectedTaskInfos;
 

	private LazyEntityDataModel<TaskInfo> taskInfos;
 
	@PostConstruct
	public void init() {
		taskInfos = new LazyEntityDataModel<>(taskInfoSB);
	}

	public void create() {
		try {
			taskInfo.setCreatedBy(UserInfo.getUser().getEmployeeId());
			taskInfoSB.create(taskInfo);
			taskInfo = null;
			FacesUtil.showInfoMessage(ResourceMB.getText("wq.message.addTask"), ResourceMB.getText("wq.message.successful")+".");
			FacesUtil.updateUI(":growl");
		} catch (Exception e) {
			FacesUtil.showErrorMessage(ResourceMB.getText("wq.message.addTask"), ResourceMB.getText("wq.message.contactHelpDesk")+".");
			LOGGER.log(Level.SEVERE, "Error in creating task : "+taskInfo.getTaskName()+" , Exception Message  : "+e.getMessage(), e);
		}
	}

	public void remove(TaskInfo taskInfo) {
		try {
			if (taskInfo.getEnable()) {
				FacesUtil.showInfoMessage(ResourceMB.getText("wq.message.removeTask"), ResourceMB.getText("wq.message.disableTaskFirst")+".");
			} else {
				taskInfoSB.remove(taskInfo);
				FacesUtil.showInfoMessage(ResourceMB.getText("wq.message.removeTask"), ResourceMB.getText("wq.message.successful")+".");
			}
		} catch (Exception e) {
			FacesUtil.showErrorMessage(ResourceMB.getText("wq.message.removeTask"), ResourceMB.getText("wq.message.contactHelpDesk")+".");
			LOGGER.log(Level.SEVERE, "Error in removing task : "+taskInfo.getTaskName()+" , Exception Message  : "+e.getMessage(), e);
		}
	}

	public void onCellEdit(CellEditEvent event) {
		try {
//			String key = event.getColumn().getColumnKey();
//			LOGGER.info("[cellEdit] " + key);
			DataTable table = (DataTable) event.getSource();
			TaskInfo o = (TaskInfo) table.getRowData();
			if(o.getAllNode()){
				o.setNode("ALL");
				o.setNextTimeout(null);
				o.setStatus("Update");
			}
			taskInfoSB.update(o);

			LOGGER.info("[cellEdit] " + o);
		} catch (OptimisticLockException e) {
			FacesUtil.showErrorMessage(ResourceMB.getText("wq.message.updateTask"), ResourceMB.getText("wq.message.dataExpired")+".");	
			LOGGER.log(Level.SEVERE, "Error in updating task : "+taskInfo.getTaskName()+" , Exception Message  : "+e.getMessage(), e);
		} catch (Exception e) {
			FacesUtil.showErrorMessage(ResourceMB.getText("wq.message.updateTask"), e.getMessage());
			LOGGER.log(Level.SEVERE, "Error in updating task : "+taskInfo.getTaskName()+" , Exception Message  : "+e.getMessage(), e);
		}
	}

	public void updateHATimer() {
		try {
			ClusterTimerService.updateTaskService();
			FacesUtil.showErrorMessage(ResourceMB.getText("wq.message.updateHATimer"), ResourceMB.getText("wq.message.successful")+".");
		} catch (Exception e) {
			FacesUtil.showErrorMessage(ResourceMB.getText("wq.message.updateHATimer"), ResourceMB.getText("wq.message.failed")+"."+e.getMessage());
			LOGGER.log(Level.SEVERE, "Update HA Timer failed. , Exception Message  : "+e.getMessage(), e);
		}
	}

	@Override
	protected LazyEntityDataModel<TaskInfo> getLazyData() {
		return taskInfos;
	}

	public TaskInfo getTaskInfo() {
		if (taskInfo == null) {
			taskInfo = new TaskInfo();
		}
		return taskInfo;
	}

	public void setTaskInfo(TaskInfo taskInfo) {
		this.taskInfo = taskInfo;
	}

	public LazyEntityDataModel<TaskInfo> getTaskInfos() {
		return taskInfos;
	}

	public void setTaskInfos(LazyEntityDataModel<TaskInfo> taskInfos) {
		this.taskInfos = taskInfos;
	}

	public List<TaskInfo> getSelectedTaskInfos() {
		return selectedTaskInfos;
	}

	public void setSelectedTaskInfos(List<TaskInfo> selectedTaskInfos) {
		this.selectedTaskInfos = selectedTaskInfos;
	}

	@Override
	public void cellChangeListener(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFilter(FilterEvent event) {
		// TODO Auto-generated method stub
		
	}

}
