package com.avnet.emasia.webquote.entity;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.avnet.emasia.webquote.vo.RfqItemVO;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;


@Test
public class ManufacturerDetailTest {
	private Material material;
	private MaterialRegional mr;
	private ManufacturerDetail mfrDetail;
	
	@BeforeMethod
	public void setUp(){
		material =  MaterialFactory.getInstance().createMaterial();
		mr =  material.getMaterialRegaional("China");
		mfrDetail =  mr.getManufacturerDetail();
	}

	public void fillInTest() {
		RfqItemVO rfqItem =  new RfqItemVO();
		mfrDetail.fillIn(rfqItem );
		assertEquals(rfqItem.getCancellationWindow(), mfrDetail.getCancellationWindow());
		assertEquals(rfqItem.getReschedulingWindow(), mfrDetail.getReschedulingWindow());
		assertEquals(rfqItem.getMultiUsage(), mfrDetail.getMultiUsage());

	}
}
