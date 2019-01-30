package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the APP_RESOURCE database table.
 * 
 */
@Entity
@Table(name="APP_RESOURCE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="RESOURCE_TYPE")

public abstract class Resource implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="APP_RESOURCE_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="APP_RESOURCE_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=19)
	private long id;

	@Column(name="\"PATH\"", length=200)
	private String path;


	@Column(name="\"VERSION\"", precision=10)
	private int version;

	//uni-directional many-to-one association to AppFunction
	@ManyToOne
	@JoinColumn(name="APP_FUNCTION_ID")
	private AppFunction appFunction;

	public Resource() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}


	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public AppFunction getAppFunction() {
		return this.appFunction;
	}

	public void setAppFunction(AppFunction appFunction) {
		this.appFunction = appFunction;
	}

}