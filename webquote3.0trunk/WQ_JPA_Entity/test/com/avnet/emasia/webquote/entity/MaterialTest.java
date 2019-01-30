package com.avnet.emasia.webquote.entity;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.avnet.emasia.webquote.constants.Constants;
import com.avnet.emasia.webquote.vo.RfqItemVO;

@Test
public class MaterialTest {
	Logger log = Logger.getLogger(MaterialTest.class.getName());
	private Material material;;
	private Customer soldTo;
	private Customer end;
	private List<Customer> allowedCustomers;

	private NormalPricer np;
	private ContractPricer cp;
	private ProgramPricer pp;
	private SalesCostPricer sp;
	private RFQSubmissionTypeEnum submissionType;

	private RfqItemVO rfqItem;

	private String region;

	@BeforeMethod
	public void setup() {
		material = MaterialFactory.getInstance().createMaterial();

		np = (NormalPricer) material.getPricers().get(0);
		cp = (ContractPricer) material.getPricers().get(1);
		pp = (ProgramPricer) material.getPricers().get(2);
		sp = (SalesCostPricer) material.getPricers().get(3);

		soldTo = new Customer();
		soldTo.setCustomerNumber("soldTo");

		end = new Customer();
		end.setCustomerNumber("end");

		allowedCustomers = new ArrayList<>();
		allowedCustomers.add(soldTo);

		rfqItem = new RfqItemVO();
		rfqItem.setBuyCurr("USD");
		region = "China";

		submissionType = RFQSubmissionTypeEnum.NormalRFQSubmission;

		Money.setCacheUtil(new CacheUtilTest());
	}

	public void getMaterialRegaionalTest() {
		MaterialRegional mr1 = material.getMaterialRegaional("CHINA");
		assertEquals(mr1.getBizUnit().getName(), "CHINA");
		MaterialRegional mr2 = material.getMaterialRegaional("TW");
		assertEquals(mr2.getBizUnit().getName(), "TW");

		MaterialRegional mr3 = material.getMaterialRegaional("ASEAN");
		assertNull(mr3);
	}

	public void getCandidatePricersForAQTest1() {
		material.getMaterialRegaional("China").setSalesCostFlag(false);
		List<Pricer> pricers = material.getCandidatePricersForAQ("China", Currency.USD, soldTo, end, allowedCustomers,
				new Date());
		assertEquals(pricers.size(), 3);
	}

	public void getCandidatePricersForAQTest2() {
		ContractPricer cp = (ContractPricer) material.getPricers().get(1);
		cp.setOverrideFlag(false);
		cp.setCost(100d);
		List<Pricer> pricers = material.getCandidatePricersForAQ("China", Currency.USD, soldTo, end, allowedCustomers,
				new Date());

		assertEquals(pricers.size(), 2);
		assertTrue(pricers.get(0) instanceof ContractPricer);
	}

	public void getCandidatePricersForAQTest3() {
		material.getMaterialRegaional("China").setSalesCostFlag(false);
		np.setCost(1d);
		cp.setCost(1.05d);
		pp.setCost(1.1d);

		List<Pricer> pricers = material.getCandidatePricersForAQ("China", Currency.USD, soldTo, end, allowedCustomers,
				new Date());
		assertEquals(pricers.size(), 3);
		assertTrue(pricers.get(0) instanceof NormalPricer);
		assertTrue(pricers.get(1) instanceof ContractPricer);
		assertTrue(pricers.get(2) instanceof ProgramPricer);

	}

	public void getCandidatePricersForAQTest4() {
		material.getMaterialRegaional("China").setSalesCostFlag(false);
		np.setCost(1d);
		cp.setCost(1d);
		pp.setCost(1d);

		List<Pricer> pricers = material.getCandidatePricersForAQ("China", Currency.RMB, soldTo, end, allowedCustomers,
				new Date());
		assertEquals(pricers.size(), 3);
		assertTrue(pricers.get(0) instanceof ContractPricer);
		assertTrue(pricers.get(1) instanceof ProgramPricer);
		assertTrue(pricers.get(2) instanceof NormalPricer);

	}

	public void getCandidatePricersForAQTest5() {
		material.getMaterialRegaional("China").setSalesCostFlag(true);
		List<Pricer> pricers = material.getCandidatePricersForAQ("China", Currency.RMB, soldTo, end,
				new ArrayList<Customer>(), null);
		assertEquals(pricers.size(), 1);
	}

	public void getCandidatePricersForAQTest6() {
		material.getMaterialRegaional("China").setSalesCostFlag(false);
		np.setCost(1d);
		cp.setCost(1.05d);
		pp.setCost(1.1d);

		List<Pricer> pricers = material.getCandidatePricersForAQ("China", Currency.RMB, soldTo, end, allowedCustomers,
				new Date());
		assertEquals(pricers.size(), 3);
		assertTrue(pricers.get(0) instanceof NormalPricer);
		assertTrue(pricers.get(1) instanceof ContractPricer);
		assertTrue(pricers.get(2) instanceof ProgramPricer);

	}

	// no good
	public void getCandidatePricersForAQTest7() {
		material.getMaterialRegaional("China").setSalesCostFlag(false);
		np.setCost(1d);
		cp.setCost(1.0000001d);
		pp.setCost(1.0000001d);

		List<Pricer> pricers = material.getCandidatePricersForAQ("China", Currency.RMB, soldTo, end, allowedCustomers,
				new Date());
		log.info(pricers.stream().map(p -> p.getClass().getSimpleName()).collect(Collectors.joining(",")));
		assertEquals(pricers.size(), 3);
		assertTrue(pricers.get(0) instanceof NormalPricer);
		assertTrue(pricers.get(1) instanceof ContractPricer);
		assertTrue(pricers.get(2) instanceof ProgramPricer);

	}

	// GOOD
	public void getCandidatePricersForAQTest8() {
		material.getMaterialRegaional("China").setSalesCostFlag(false);
		np.setCost(1d);
		np.setCurrency(Currency.RMB);
		cp.setCost(1.00001d);
		cp.setCurrency(Currency.RMB);
		pp.setCost(1.00001d);
		pp.setCurrency(Currency.RMB);

		BigDecimal d1 = Money.of(new BigDecimal(1.000001d), Currency.RMB)
				.convertToRfq(Currency.USD, new BizUnit("CHINA"), null, new Date(), Constants.SCALE_FOR_COMPARE)
				.getAmount();
		BigDecimal d2 = Money.of(new BigDecimal(1d), Currency.RMB)
				.convertToRfq(Currency.USD, new BizUnit("CHINA"), null, new Date(), Constants.SCALE_FOR_COMPARE)
				.getAmount();
		assertTrue(0 < d1.compareTo(d2));
		List<Pricer> pricers = material.getCandidatePricersForAQ("China", Currency.USD, soldTo, end, allowedCustomers,
				new Date());
		log.info(pricers.stream().map(p -> p.getClass().getSimpleName()).collect(Collectors.joining(",")));
		assertEquals(pricers.size(), 3);
		assertTrue(pricers.get(0) instanceof NormalPricer);
		assertTrue(pricers.get(1) instanceof ContractPricer);
		assertTrue(pricers.get(2) instanceof ProgramPricer);

	}

	// GOOD
	public void getCandidatePricersForAQTest9() {
		material.getMaterialRegaional("JAPAN").setSalesCostFlag(false);
		np.setCost(1d);
		np.setCurrency(Currency.JPY);
		cp.setCost(1.0000001d);
		cp.setCurrency(Currency.JPY);
		pp.setCost(1.0000001d);
		pp.setCurrency(Currency.JPY);
		pp.setBizUnitBean(new BizUnit("JAPAN"));
		np.setBizUnitBean(new BizUnit("JAPAN"));
		cp.setBizUnitBean(new BizUnit("JAPAN"));
		List<Pricer> pricers = material.getCandidatePricersForAQ("JAPAN", Currency.JPY, soldTo, end, allowedCustomers,
				new Date());
		log.info(pricers.stream().map(p -> p.getClass().getSimpleName()).collect(Collectors.joining(",")));
		assertEquals(pricers.size(), 3);
		assertTrue(pricers.get(0) instanceof NormalPricer);
		assertTrue(pricers.get(1) instanceof ContractPricer);
		assertTrue(pricers.get(2) instanceof ProgramPricer);

	}

	public void getCandidatePricersForAQTest10() {
		material.getMaterialRegaional("China").setSalesCostFlag(false);
		np.setCost(1d);
		np.setCurrency(Currency.RMB);
		cp.setCost(1.00001d);
		cp.setCurrency(Currency.RMB);
		pp.setCost(1.00001d);
		pp.setCurrency(Currency.RMB);
		assertTrue(Money.of(new BigDecimal(5.59), Currency.RMB).convertToRfq(Currency.EUR, new BizUnit("CHINA"), null , 
				new Date())!=null);
		//no get money will be fitered;
	/*	List<Pricer> pricers = material.getCandidatePricersForAQ("China", Currency.EUR, soldTo, end, allowedCustomers,
				new Date());
		log.info(pricers.stream().map(p -> p.getClass().getSimpleName()).collect(Collectors.joining(",")));
		assertEquals(pricers.size(), 1);*/

	}

	public void fillInPricerInfoTest() {

		RfqItemVO expected = rfqItem.copy();

		material.applyAQLogic(rfqItem, region, soldTo, end, allowedCustomers, submissionType);
		material.getMaterialRegaional(region).getManufacturerDetail().fillIn(expected);

		assertEquals(rfqItem.getCancellationWindow(), expected.getCancellationWindow());
		assertEquals(rfqItem.getReschedulingWindow(), expected.getReschedulingWindow());
		assertEquals(rfqItem.getMultiUsage(), expected.getMultiUsage());

	}

	public void fillInPricerInfoTest2() {
		RfqItemVO expected = rfqItem.copy();
		material.applyAQLogic(rfqItem, region, soldTo, end, allowedCustomers, submissionType);

		MaterialRegional mr = material.getMaterialRegaional(region);
		mr.fillInPricer(expected, RFQSubmissionTypeEnum.NormalRFQSubmission);

		assertEquals(rfqItem.getProductGroup1(), expected.getProductGroup1());
		assertEquals(rfqItem.getProductGroup2(), expected.getProductGroup2());
		assertEquals(rfqItem.getProductGroup3(), expected.getProductGroup3());
		assertEquals(rfqItem.getProductGroup4(), expected.getProductGroup4());
		assertEquals(rfqItem.isSalesCostFlag(), expected.isSalesCostFlag());
	}

	public void fillInPricerInfoTest3() {
		material.getMaterialRegaional("China").setSalesCostFlag(false);
		np.setCost(1.05d);
		np.setMinSellPrice(1.2d);
		cp.setCost(1.06d);
		cp.setMinSellPrice(1.1d);
		pp.setCost(1.07d);
		pp.getQuantityBreakPrices().get(0).setQuantityBreakPrice(1.2d);

		rfqItem.setSpecialPriceIndicator(false);
		rfqItem.setTargetResale("1.15");
		rfqItem.setRequiredQty("100");
		material.applyAQLogic(rfqItem, region, soldTo, end, allowedCustomers, submissionType);
		assertEquals(rfqItem.matchAQConditions(), true);
		assertEquals(rfqItem.getCost().doubleValue(), 1.06d);

	}

	public void fillInPricerInfoTest30() {
		material.getMaterialRegaional("China").setSalesCostFlag(false);
		np.setCost(1.05d);
		np.setMinSellPrice(1.2d);
		cp.setCost(1.06d);
		cp.setMinSellPrice(1.1d);
		pp.setCost(1.07d);
		pp.getQuantityBreakPrices().get(0).setQuantityBreakPrice(1.2d);

		rfqItem.setSpecialPriceIndicator(false);
		rfqItem.setTargetResale("1.15");
		rfqItem.setRequiredQty("100");

		material.applyAQLogic(rfqItem, region, soldTo, end, allowedCustomers, submissionType);
		// assertEquals(rfqItem.matchAQConditions(), false);
		assertEquals(rfqItem.getCost().doubleValue(), 1.06d);
	}

	public void fillInPricerInfoTest4() {
		material.getMaterialRegaional("China").setSalesCostFlag(false);
		np.setCost(1.05d);
		np.setMinSellPrice(1.2d);
		cp.setCost(1.06d);
		cp.setMinSellPrice(1.1d);
		pp.setCost(1.07d);
		pp.getQuantityBreakPrices().get(0).setQuantityBreakPrice(1.2d);

		rfqItem.setTargetResale("0.9");
		rfqItem.setRequiredQty("100");
		rfqItem.setRfqCurr("USD");
		material.applyAQLogic(rfqItem, region, soldTo, end, allowedCustomers, submissionType);
		assertEquals(rfqItem.matchAQConditions(), false);
		assertEquals(rfqItem.getCost().doubleValue(), 1.05d);

	}
}