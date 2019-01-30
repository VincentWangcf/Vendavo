package com.avnet.emasia.webquote.entity;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

@Test
public class ManufacturerTest {

	private Manufacturer mfr; 
	
	@BeforeMethod
	public void setUp(){
		mfr =  MaterialFactory.getInstance().createMaterial().getManufacturer();
	}
	
	public void getManufacturerDetailTest() {
		
		ProductGroup productGroup2 =  new ProductGroup();
		productGroup2.setName("CP_DIODE");
		
		ProductCategory productCategory = new ProductCategory();
		productCategory.setCategoryCode("SEMI");


		ManufacturerDetail mfrDetail = mfr.getManufacturerDetail(productGroup2, productCategory, "China");
		assertEquals(mfrDetail, mfr.getManufacturerDetails().get(1));
	}

}
