package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


/**
 * @SqlResultSetMappings<br/>
 * @ColumnResult <br/>
 * @Customizer history <br/>
 * 
 * @AttributeOverride
 * @ExcludeSuperclassListeners
 * @author 041863
 */
@MappedSuperclass
//@EntityListeners(value = { AbstractEntityListeners.class })
//@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.INVALIDATE, type = CacheType.FULL, size = 999999)
public abstract class AbstractEntity implements Cloneable, BaseEntity, Serializable {
	private static final Logger LOG = Logger.getLogger(AbstractEntity.class.getName());
	private static final long serialVersionUID = 1L;
	@Transient
	private Boolean rendered;
	
	@Column(name="CREATED_BY")
	private String createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_TIME")
	private Date createdTime;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_TIME")
	private Date updateTime;

	@Version
	@Column(name="\"VERSION\"")
	private Integer version;

	@Override
	public Object getId() {
		Object outcome = "";
		Field[] fields = this.getClass().getDeclaredFields();
		try {
			Field idField = null;
			for (Field field : fields) {
				field.setAccessible(true);
				Id ann = field.getAnnotation(Id.class);
				if (ann != null) {
					idField = field;
					break;
				}
				EmbeddedId eid = field.getAnnotation(EmbeddedId.class);
				if (eid != null) {
					idField = field;
					break;
				}
			}
			
			if (idField != null) {
				outcome = idField.get(this);
			}
		} catch (Exception e) {
//			Logger logger = Logger.getLogger(this.getClass().getName());
//			logger.error(e.getMessage());
//			e.printStackTrace();

			LOG.log(Level.SEVERE, "Exception in getting object of 'id class' in class"+this.getClass().getSimpleName()+" , Exception message : "+e.getMessage(), e);
		}
		return outcome;
	}

	public Boolean getRendered() {
		if(rendered == null){
			rendered = Boolean.TRUE;
		}
		return rendered;
	}

	public void setRendered(Boolean rendered) {
		this.rendered = rendered;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof BaseEntity)) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final BaseEntity other = (BaseEntity) obj;
		return getId().equals(other.getId());
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 67 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		Class<?> obj = this.getClass();
		Field[] fields = obj.getDeclaredFields();
		StringBuffer sb = new StringBuffer(this.getClass().getName()).append("\n[");
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				sb.append(field.getName()).append(": ");
				if (field.getType().equals("")) {
					sb.append("{is list}");
				} else {
					sb.append(field.get(this));
				}
				sb.append("\n");
			} catch (Exception e) {
//				Logger logger = Logger.getLogger(this.getClass().getName());
//				logger.error(e.getMessage());
//				e.printStackTrace();

				LOG.log(Level.SEVERE, "Exception in getting fields of 'id class' in class"+this.getClass().getSimpleName()+" , Exception message : "+e.getMessage(), e);
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public Object clone() throws CloneNotSupportedException {
		Object o = null;
		o = super.clone();
		return o;
	}
	
	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedTime() {
		return this.createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
