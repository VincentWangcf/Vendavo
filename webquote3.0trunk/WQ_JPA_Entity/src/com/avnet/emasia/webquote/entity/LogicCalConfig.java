package com.avnet.emasia.webquote.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Predicate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.avnet.emasia.webquote.entity.LeafLogicCalConfig.ConditionType;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Table(name="LOGIC_CAL_CONFIG")
@DiscriminatorColumn(name="CONFIG_TYPE")
public class LogicCalConfig {
	@Id
	@SequenceGenerator(name = "CONFIG_GENERATOR", sequenceName = "WQ_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONFIG_GENERATOR")
	@Column(unique = true, nullable = false)
	protected long id;

	@Column(name = "LOGIC_RESULT_REVERSE")
	protected boolean logicResultReverse;
	
	@Column(name = "CONFIG_TYPE")
	protected String configType;
	
	@ManyToOne
	@JoinColumn(name = "PARENT_ID")
	protected LogicCalConfig parent;
	
	@OneToMany(mappedBy = "logicCalConfig")
	protected List<PageComponentItem> pageComponentItems;
	 
	public String getConfigType() {
		return configType;
	}

	public void setConfigType(String configType) {
		this.configType = configType;
	}

	public List<PageComponentItem> getPageComponentItems() {
		return pageComponentItems;
	}

	public void setPageComponentItems(List<PageComponentItem> pageComponentItems) {
		this.pageComponentItems = pageComponentItems;
	}

	
	 
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isLogicResultReverse() {
		return logicResultReverse;
	}

	public void setLogicResultReverse(boolean logicResultReverse) {
		this.logicResultReverse = logicResultReverse;
	}
	
	public LogicCalConfig getParent() {
		return parent;
	}

	public void setParent(LogicCalConfig parent) {
		this.parent = parent;
	}
	
	//must override in subClass
	/*protected Predicate<Boolean> isRegionAndRoleMatched(List<String> regions, List<String> roles){
		return (Boolean b)-> false;
		
	};*/
	
	protected Predicate<Boolean> isMatched(EnumMap<ConditionType,Collection<String>> mapParam){
		return (Boolean b)-> false;
		
	};
	
	
}
