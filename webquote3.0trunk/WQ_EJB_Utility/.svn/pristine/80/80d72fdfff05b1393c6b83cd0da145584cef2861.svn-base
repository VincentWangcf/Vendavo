package com.avnet.emasia.webquote.utilities.ejb;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.eclipse.persistence.exceptions.OptimisticLockException;

import com.avnet.emasia.webquote.entity.TaskInfo;
import com.avnet.emasia.webquote.utilities.common.BaseSB;

@Stateless
public class TaskInfoSB extends BaseSB<TaskInfo> implements Serializable{

	private static final long serialVersionUID = 1L;

	public TaskInfoSB() {
	}

	public List<TaskInfo> findEnableTask() {
		String jpql = "select o from TaskInfo o where o.enable = 1 and o.allNode = 0";
		return em.createQuery(jpql, TaskInfo.class).getResultList();
	}
	
	public List<TaskInfo> findEnableAllNodeTask() {
		String jpql = "select o from TaskInfo o where o.enable = 1 and o.allNode = 1";
		return em.createQuery(jpql, TaskInfo.class).getResultList();
	}

	@Override
	public TaskInfo update(TaskInfo t) throws OptimisticLockException {
		t.setUpdateTime(new Date());
		t =super.update(t);
		em.flush();
		return t;
	}
	
	@Override
	public void create(TaskInfo t) {
		t.setCreatedTime(new Date());
		super.create(t);
	}

	public List<TaskInfo> findAll() {
		String sql = "select o from " + TaskInfo.class.getSimpleName() + " o ";
		return em.createQuery(sql, TaskInfo.class).getResultList();
	}

	public void setAllStatusToStoped() {
		String updateJpql = "update TaskInfo o set o.lastRunStatus = o.status,o.status = 'Stoped' where 1=1 ";
		em.createQuery(updateJpql).executeUpdate();		
	}
}
