package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the AUDIT_FIELD_MAPPING database table.
 * 
 */
//@Entity
@Table(name="AUDIT_FIELD_MAPPING")
public class AuditFieldMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;

	@Column(name="\"KEY\"", length=20)
	private String key;

	@Column(name="\"TYPE\"", length=20)
	private String type;

	@Column(name="\"VALUE\"", length=20)
	private String value;

	public AuditFieldMapping() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
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