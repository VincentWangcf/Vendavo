package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="STRATEGY")
public class Strategy implements Serializable{
	
	private static final long serialVersionUID = 2774248963235687539L;

	@Id
	@Column(name = "ID")
	private int id;
	
	@Column(name = "KEY")
	private String key;
	
	@Column(name = "STRATEGY_INTERFACE")
	private String strategyInterface;
	
	@Column(name = "STRATEGY_IMPLEMENTATION")
	private String strategyImplementation;

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getStrategyInterface() {
		return strategyInterface;
	}

	public void setStrategyInterface(String strategyInterface) {
		this.strategyInterface = strategyInterface;
	}

	public String getStrategyImplementation() {
		return strategyImplementation;
	}

	public void setStrategyImplementation(String strategyImplementation) {
		this.strategyImplementation = strategyImplementation;
	}
	
}
