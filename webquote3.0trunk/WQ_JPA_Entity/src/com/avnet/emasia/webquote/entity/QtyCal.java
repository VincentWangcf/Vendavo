package com.avnet.emasia.webquote.entity;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.vo.PricerInfo;
import com.avnet.emasia.webquote.vo.RfqItemVO;

public class QtyCal {
	
	private static final Logger logger = Logger.getLogger(QtyCal.class.getName());
	
	private Integer requiredQty;
	private Integer mpq, moq, mov, calMoq;
	private Double cost;
	private String qtyIndicator;
	private Integer quotedQty;
	private String pmoq;
	
	
	public QtyCal(){
	}
	
	public QtyCal(PricerInfo pricerInfo) {
		requiredQty =  pricerInfo.getRequestedQty();
		mpq = pricerInfo.getMpq();
		moq = pricerInfo.getMoq();
		mov = pricerInfo.getMov();
		cost = pricerInfo.getCost();
		qtyIndicator = pricerInfo.getQtyIndicator();
	}
	
	public QtyCal(QuoteItem quoteItem) {
		requiredQty =  quoteItem.getRequestedQty();
		mpq = quoteItem.getMpq();
		moq = quoteItem.getMoq();
		mov = quoteItem.getMov();
		cost = quoteItem.getCost();
		qtyIndicator = quoteItem.getQtyIndicator();
		quotedQty = quoteItem.getQuotedQty();
	}
	
	
	public QtyCal(Integer requiredQty, Integer mpq, Integer moq, Integer mov, Double cost, String qtyIndicator) {
		this.requiredQty = requiredQty;
		this.mpq = mpq;
		this.moq = moq;
		this.mov = mov;
		this.cost = cost;
		this.qtyIndicator = qtyIndicator;
	}
	
	public Integer getRequiredQty() {
		return requiredQty;
	}

	public void setRequiredQty(Integer requiredQty) {
		this.requiredQty = requiredQty;
	}

	public Integer getMpq() {
		return mpq;
	}

	public void setMpq(Integer mpq) {
		this.mpq = mpq;
	}

	public Integer getMoq() {
		return moq;
	}

	public void setMoq(Integer moq) {
		this.moq = moq;
	}

	public Integer getMov() {
		return mov;
	}

	public void setMov(Integer mov) {
		this.mov = mov;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String getQtyIndicator() {
		return qtyIndicator;
	}

	public void setQtyIndicator(String qtyIndicator) {
		this.qtyIndicator = qtyIndicator;
	}
	
	public void cal(){
		qtyIndicator =  calQtyIndicator();
		calMoq = calMoq();
		quotedQty = calQuotedQty();
		pmoq =  calPmoq();
	}
	
	public void fill(QuoteItem quoteItem) {
		quoteItem.setQtyIndicator(qtyIndicator);
		quoteItem.setMoq(calMoq);
		quoteItem.setQuotedQty(quotedQty);
		quoteItem.setPmoq(pmoq);
	}

	String calQtyIndicator() {
		if ( ! StringUtils.isEmpty(qtyIndicator)) {
			return qtyIndicator;
		}
		if(mov != null && cost != null && cost != 0 && moq != null){
			if (mov/cost > moq) {
				return "MOV";
			} else {
				return "MOQ";
			}
		} else if ((mov == null || cost == null || cost == 0) && moq != null) {
			return "MOQ";
		} else if (mov != null && cost != null && cost != 0 && moq == null){
			return "MOV";
		} else {
			return null;
		}
	}	
	
	
	Integer calMoq() {
		
		/*
		if (calQtyIndicator().equalsIgnoreCase("MOQ")) {
			if (moq != null) {
				return moq;
			} else if (mov != null && cost != null){
				return roundUp((int)Math.ceil(mov / cost));
			}
		} else if (calQtyIndicator().equalsIgnoreCase("MOV") && mov != null && cost != null) {
			return roundUp((int)Math.ceil(mov / cost));
		}
		*/
		if (moq != null) {
			return moq;
		}
		//moq!=null condition is duplicate below. keep it here at the moment.  
		if(mov != null && cost != null && cost != 0 && moq != null){
			if (mov/cost > moq) {
				return roundUp((int)Math.ceil(mov / cost));
			} else {
				return moq;
			}
		} else if ((mov == null || cost == null || cost == 0) && moq != null) {
			return moq;
		} else if (mov != null && cost != null && cost != 0 && moq == null){
			return roundUp((int)Math.ceil(mov / cost));
		} else {
			return null;
		}
	}	
	
	Integer calQuotedQty() {
		
		if (quotedQty != null) {
			return quotedQty;
		}
		
		if (requiredQty == null || calQtyIndicator() == null) {
			return null;
		}
		
		if (calQtyIndicator().equalsIgnoreCase("MOQ")) {
			if (getMoq() != null) {
				quotedQty = calQuotedQty(requiredQty, getMoq(), null, null);
			} else if (getMov() != null && getCost() != null && getMpq() != null) {
				quotedQty = calQuotedQty(requiredQty, null, getMov(), getCost());
			}
			
		} else if (calQtyIndicator().equalsIgnoreCase("MOV")) {
			if (getMov() != null && getCost() != null && getMpq() != null) {
				quotedQty = calQuotedQty(requiredQty, null, getMov(), getCost());
			}
			
		} else if (calQtyIndicator().equalsIgnoreCase("EXACT")) {
			quotedQty = requiredQty;
			
		} else if (calQtyIndicator().equalsIgnoreCase("LIMIT")) {
			quotedQty = roundUp(requiredQty);

		} else if (calQtyIndicator().contains("%") || calQtyIndicator().equalsIgnoreCase("MPQ")) {
			quotedQty = calQuotedQty(requiredQty, getMoq(), getMov(), getCost());
			
		}
		return quotedQty;
	}
	
	private Integer calQuotedQty(Integer requiredQty, Integer moq, Integer mov, Double cost) {
		if (mov == null || cost == null || cost==0d) {
			if (moq == null) {
				return null;
			} else if (moq > requiredQty) {
				return moq;
			} else {
				return roundUp(requiredQty);
			}
		} else {
			if (moq == null) {
				if (mov/cost > requiredQty) {
					return roundUp((int)Math.ceil(getMov()/getCost()));
				} else {
					return roundUp(requiredQty);
				}
			} else {
				if (moq >= mov/cost && moq >= requiredQty) {
					return moq;
				} else if (mov/cost >= moq && mov/cost >= requiredQty) {
					return roundUp((int)Math.ceil(getMov()/getCost()));
				} else if (requiredQty >= moq && requiredQty >= mov/cost) {
					return roundUp(requiredQty);
				}
			}
			
		}
		return null;
	}
	
	String calPmoq() {
		
		String pmoq = null;
		
		if (StringUtils.isEmpty(qtyIndicator)) {
			return pmoq;
		}
		
		if (qtyIndicator.equalsIgnoreCase("MOQ")) {
			if (moq != null) {
				pmoq = moq + "+";
			} else if (calMoq != null){
				pmoq = calMoq + "+";
			}
		} else if (qtyIndicator.equalsIgnoreCase("MOV")) {
			if (mov != null && cost != null && mpq != null) {
				pmoq = roundUp((int)Math.ceil(getMov()/getCost())) + "+";
			}
		} else if (qtyIndicator.equalsIgnoreCase("MPQ")) {
			pmoq = mpq + "+";
		} else if (qtyIndicator.contains("%")) {
			try {
				pmoq = roundUp((int)Math.ceil(quotedQty * Double.parseDouble(qtyIndicator.substring(0, qtyIndicator.length()-1)) / 100)) + "+";
			} catch (Exception e) {
				logger.log(Level.WARNING, "error when calPmoq", e);
			}
			
		} else if (qtyIndicator.equalsIgnoreCase("EXACT")) {
			//no need roundUp
			if (quotedQty != null) {
				pmoq = String.valueOf(quotedQty);
			}
			
		} else if (qtyIndicator.equalsIgnoreCase("LIMIT")) {
			//need pricer to get result, cannot cal here
		}
		return pmoq;
	}

	
	
	Integer roundUp(Integer qty){
		if(qty == null || mpq == null || mpq == 0) {
			return null;
		}
		return ((int)Math.ceil(qty * 1.0d / mpq)) * mpq;
	}

	Integer roundDown(Integer qty){
		if(qty == null || mpq == null || mpq == 0) {
			return null;
		}
		if (qty % mpq == 0) {
			return (qty - mpq) < 0 ? 0 : (qty - mpq);
		} else {
			return ((int)Math.floor(qty * 1.0d / mpq) * mpq);
		}
	}

}