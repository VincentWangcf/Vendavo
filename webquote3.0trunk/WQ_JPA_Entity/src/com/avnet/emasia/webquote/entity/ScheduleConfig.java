package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The persistent class for the SCHEDULE_CONFIG database table.
 * 
 */
@Entity
@Table(name="SCHEDULE_CONFIG")
@XmlRootElement
@NamedQueries(
{
   @NamedQuery(name= "SCHEDULE_CONFIG.findByType", query ="SELECT a FROM ScheduleConfig a where a.type in(:type1 ,:type2)")
})
 
public class ScheduleConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="SCHEDULE_CONFIG_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SCHEDULE_CONFIG_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="\"KEY\"")
	private String key;

	@Column(name="\"TYPE\"")
	private String type;

	@Column(name="\"VALUE\"")
	private String value;

	public ScheduleConfig() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}