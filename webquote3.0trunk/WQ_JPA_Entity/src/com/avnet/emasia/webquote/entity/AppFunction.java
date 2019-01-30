package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;


/**
 * The persistent class for the APP_FUNCTION database table.
 * 
 */
@Entity
@Table(name="APP_FUNCTION")
public class AppFunction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="APP_FUNCTION_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="APP_FUNCTION_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=19)
	private long id;

	@Column(nullable=false, length=200)
	private String name;
	
	@ManyToMany(mappedBy="appFunctions")
	List<Role> roles;


	public AppFunction() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

}