package com.avnet.emasia.webquote.entity;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.avnet.emasia.webquote.vo.RfqItemVO;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;


@Test
public class MaterialRegionalTest {

	private Material material;
	private MaterialRegional mr;
	
	@BeforeMethod
	public void setUp(){
		material =  MaterialFactory.getInstance().createMaterial();
		mr = material.getMaterialRegaional("China");
	}

	public void getMaterialRegaionalTest() {
		assertNotNull(mr);
	}
	
	public void getMaterialRegaionalTest2() {
		mr = material.getMaterialRegaional("france");
		assertNull(mr);
	}

	public void fillInPricerTest() {
		RfqItemVO rfqItem =  new RfqItemVO();
		mr.fillInPricer(rfqItem, RFQSubmissionTypeEnum.NormalRFQSubmission );
		assertEquals(rfqItem.getProductGroup1(), mr.getProductGroup1());
		assertEquals(rfqItem.getProductGroup2(), mr.getProductGroup2());
		assertEquals(rfqItem.getProductGroup3(), mr.getProductGroup3());
		assertEquals(rfqItem.getProductGroup4(), mr.getProductGroup4());
		assertEquals(rfqItem.isSalesCostFlag(), mr.isSalesCostFlag());
	}
	
	public void getManufacturerDetailTest() {
		ManufacturerDetail mfrDetail = mr.getManufacturerDetail();
		assertEquals(mfrDetail.getCancellationWindow().intValue(), 30);
		assertEquals(mfrDetail.getReschedulingWindow().intValue(), 40);
		assertEquals(mfrDetail.getMultiUsage().booleanValue(), true);
	}
	
	public void getManufacturerDetailTest2() {
		ManufacturerDetail mfrDetail = mr.getManufacturerDetail();
		assertEquals(mfrDetail.getProductGroup(), mr.getProductGroup2());
		assertEquals(mfrDetail.getProductCategory(), mr.getProductCategory());
	}
}
