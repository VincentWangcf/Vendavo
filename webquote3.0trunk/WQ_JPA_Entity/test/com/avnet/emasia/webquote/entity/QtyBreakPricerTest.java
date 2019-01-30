package com.avnet.emasia.webquote.entity;


import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class QtyBreakPricerTest {
	
	private Material material;
	private NormalPricer np;
	private ContractPricer cp;
	private ProgramPricer pp;
	private SalesCostPricer sp;
	
	private Customer soldTo;
	private Customer end;
	private List<Customer> allowedCustomers;

	
	@BeforeMethod
	public void setup() {
		material =  MaterialFactory.getInstance().createMaterial();
		np =  (NormalPricer) material.getPricers().get(0);
		cp =  (ContractPricer) material.getPricers().get(1);
		pp = (ProgramPricer) material.getPricers().get(2);
		sp = (SalesCostPricer) material.getPricers().get(3);
		
		soldTo = new Customer()	;
		soldTo.setCustomerNumber("soldTo");
		
		end = new Customer();
		end.setCustomerNumber("end");
		
		allowedCustomers = new ArrayList<>();
		allowedCustomers.add(soldTo);

	}
	
	
	public void calQtyBreakRangeTest() {
		List<QuantityBreakPrice> qtyBreakPrices = sp.calQtyBreakRange();
	
		assertEquals(qtyBreakPrices.size(),3 );
		assertEquals(qtyBreakPrices.get(0).getQtyRange(), "300-900");
		assertEquals(qtyBreakPrices.get(1).getQtyRange(), "1200-2700");
		assertEquals(qtyBreakPrices.get(2).getQtyRange(), "3000+");
	}

	public void calQtyBreakRangeTest2() {
		sp.getQuantityBreakPrices().get(2).setSalesCost(null);
		List<QuantityBreakPrice> qtyBreakPrices = sp.calQtyBreakRange();
		assertEquals(qtyBreakPrices.size(),2 );
		assertEquals(qtyBreakPrices.get(0).getQtyRange(), "300-900");
		assertEquals(qtyBreakPrices.get(1).getQtyRange(), "1200-2700");
	}
	
	public void getQtyBreakPriceTest() {
		QuantityBreakPrice qtyBreakPrice = sp.getQtyBreakPrice(1);
		
		assertEquals(qtyBreakPrice.getSalesCost(), new BigDecimal(0.99d));
		
		qtyBreakPrice = sp.getQtyBreakPrice(999);
		assertEquals(qtyBreakPrice.getSalesCost(), new BigDecimal(0.99d));
		
		qtyBreakPrice = sp.getQtyBreakPrice(1000);
		assertEquals(qtyBreakPrice.getSalesCost(), new BigDecimal(0.98d));
		
		qtyBreakPrice = sp.getQtyBreakPrice(3000);
		assertEquals(qtyBreakPrice.getSalesCost(), new BigDecimal(0.97d));
		
		qtyBreakPrice = sp.getQtyBreakPrice(300000);
		assertEquals(qtyBreakPrice.getSalesCost(), new BigDecimal(0.97d));

	
	}

	
	

} 
