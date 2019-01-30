package com.avnet.emasia.webquote.entity;

import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;




@Test
public class BizUnitTest {
	
	BizUnit asia;
	BizUnit china;
	BizUnit asean;
	BizUnit singapore;
	BizUnit australia;
	
	@BeforeMethod
	public void setup() {
		asia = new BizUnit("ASIA");
		china = new BizUnit("CHINA");
		asean = new BizUnit("asean");
		singapore = new BizUnit("SINGAPORE");
		australia = new BizUnit("AUSTRILIA");
		
		asia.setSubBizUnits(new ArrayList<>());
		china.setSubBizUnits(new ArrayList<>());
		asean.setSubBizUnits(new ArrayList<>());
		singapore.setSubBizUnits(new ArrayList<>());
		australia.setSubBizUnits(new ArrayList<>());
		
		asia.getSubBizUnits().add(china);
		asia.getSubBizUnits().add(asean);
		china.setHigherBizUnit(asia);
		asean.setHigherBizUnit(asia);
		
		asean.setSubBizUnits(new ArrayList<>());
		asean.getSubBizUnits().add(singapore);
		asean.getSubBizUnits().add(australia);
		singapore.setHigherBizUnit(asean);
		australia.setHigherBizUnit(asean);
	}

	@Test
	public void findMaterial(){
		Assert.assertEquals(5, asia.getSelfAndAllSubBizUnits().size());
		
	}
	
		
}
