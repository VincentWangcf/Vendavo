package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.eclipse.persistence.annotations.Customizer;

//@Table(name = "TASK_INFO")
//@Entity
// @Cacheable(false)
// @Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE,
// size = 999999)
@Customizer(TaskInfoHistroy.class)
public class TaskInfo extends AbstractEntity implements Serializable {

	private static final long serialVersionUID = -1753565990276306628L;
	@Id
	@GeneratedValue(generator = "system-uuid")
	@Column(name = "ID", nullable = false, length = 32)
	private String id;
	@Column(name = "TASK_NAME", length = 100)
	private String taskName;
	@Column(name = "TASK_CLASS_NAME", length = 100)
	private String taskClassName;
	@Column(name = "DESCRIPTION", length = 255)
	private String description;
	@Column(name = "SECOND", length = 10)
	private String second;
	@Column(name = "MINUTE", length = 10)
	private String minute;
	@Column(name = "HOUR", length = 10)
	private String hour;
	@Column(name = "DAY_OF_WEEK", length = 10)
	private String dayOfWeek;
	@Column(name = "DAY_OF_MONTH", length = 10)
	private String dayOfMonth;
	@Column(name = "MONTH", length = 10)
	private String month;
	@Column(name = "YEAR", length = 10)
	private String year;
	@Column(name = "NODE", length = 255)
	private String node;
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Column(name = "NEXT_TIME_OUT")
	private Date nextTimeout;
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Column(name = "START_TIME")
	private Date startTime;
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Column(name = "END_TIME")
	private Date endTime;
	@Column(name = "LAST_RUN_STATUS", length = 200)
	private String lastRunStatus;
 

	public TaskInfo() {
		this.taskName = "Task Name";
		this.taskClassName = "java:global/";
		this.description = "";
		this.startTime = null;
		this.endTime = null;
		this.second = "";
		this.minute = "*/30";
		this.hour = "*";
		this.dayOfMonth = "";
		this.month = "";
		this.year = "";
		this.dayOfWeek = "";
		this.enable = false;
		this.allNode = false;
	}

	@Column(name = "STATUS")
	private String status;
	@Column(name = "ENABLE")
	private Boolean enable;
	@Column(name = "ALL_NODE")
	private Boolean allNode;

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
 
	public Boolean getAllNode() {
		return allNode;
	}

	public void setAllNode(Boolean allNode) {
		this.allNode = allNode;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskClassName() {
		return taskClassName;
	}

	public void setTaskClassName(String taskClassName) {
		this.taskClassName = taskClassName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSecond() {
		return second;
	}

	public void setSecond(String second) {
		this.second = second;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public Date getNextTimeout() {
		return nextTimeout;
	}

	public void setNextTimeout(Date nextTimeout) {
		this.nextTimeout = nextTimeout;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getLastRunStatus() {
		return lastRunStatus;
	}

	public void setLastRunStatus(String lastRunStatus) {
		this.lastRunStatus = lastRunStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
