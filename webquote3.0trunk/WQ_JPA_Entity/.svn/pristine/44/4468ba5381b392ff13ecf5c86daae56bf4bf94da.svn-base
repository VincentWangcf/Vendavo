package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.*;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.vo.PricerInfo;
import com.avnet.emasia.webquote.vo.RfqItemVO;

/**
 * The persistent class for the COST_INDICATOR database table.
 * 
 */
@Entity
@Table(name = "COST_INDICATOR")
public class CostIndicator implements Serializable {
	private static final Logger logger =  Logger.getLogger(CostIndicator.class.getName());
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false, length = 30)
	private String name;

	@Column(length = 100)
	private String description;

	@Column(nullable = false, precision = 1)
	private Boolean status;

	public CostIndicator() {
	}

	@Column(name = "LEAD_TIME", nullable = false)
	private Integer leadTime;

	@Column(nullable = false)
	private Integer moq;

	@Column(name = "MOQ_VALUE", length = 20)
	private String moqValue;

	@Column(nullable = false)
	private Integer mov;

	@Column(name = "MOV_VALUE", length = 20)
	private String movValue;

	@Column(nullable = false)
	private Integer mpq;

	@Column(name = "MPQ_VALUE", length = 20)
	private String mpqValue;

	@Column(name = "PRICE_VALIDITY")
	private Integer priceValidity;

	@Column(name = "SHIPMENT_VALIDITY")
	private Integer shipmentValidity;

	@Column(name = "PRICE_LIST_REMARKS")
	private Integer priceListRemarks;

	@Column(name = "PART_DESCRIPTION")
	private Integer partDescription;

	public Integer getLeadTime() {
		return this.leadTime;
	}

	public void setLeadTime(Integer leadTime) {
		this.leadTime = leadTime;
	}

	public Integer getMoq() {
		return this.moq;
	}

	public void setMoq(Integer moq) {
		this.moq = moq;
	}

	public String getMoqValue() {
		return this.moqValue;
	}

	public void setMoqValue(String moqValue) {
		this.moqValue = moqValue;
	}

	public Integer getMov() {
		return this.mov;
	}

	public void setMov(Integer mov) {
		this.mov = mov;
	}

	public String getMovValue() {
		return this.movValue;
	}

	public void setMovValue(String movValue) {
		this.movValue = movValue;
	}

	public Integer getMpq() {
		return this.mpq;
	}

	public void setMpq(Integer mpq) {
		this.mpq = mpq;
	}

	public String getMpqValue() {
		return this.mpqValue;
	}

	public void setMpqValue(String mpqValue) {
		this.mpqValue = mpqValue;
	}

	public Integer getPriceValidity() {
		return priceValidity;
	}

	public void setPriceValidity(Integer priceValidity) {
		this.priceValidity = priceValidity;
	}

	public Integer getShipmentValidity() {
		return shipmentValidity;
	}

	public void setShipmentValidity(Integer shipmentValidity) {
		this.shipmentValidity = shipmentValidity;
	}

	public Integer getPriceListRemarks() {
		return priceListRemarks;
	}

	public void setPriceListRemarks(Integer priceListRemarks) {
		this.priceListRemarks = priceListRemarks;
	}

	public Integer getPartDescription() {
		return partDescription;
	}

	public void setPartDescription(Integer partDescription) {
		this.partDescription = partDescription;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getStatus() {
		return this.status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CostIndicator [name=" + name + ", description=" + description + ", status=" + status + ", leadTime="
				+ leadTime + ", moq=" + moq + ", moqValue=" + moqValue + ", mov=" + mov + ", movValue=" + movValue
				+ ", mpq=" + mpq + ", mpqValue=" + mpqValue + ", priceValidity=" + priceValidity + ", shipmentValidity="
				+ shipmentValidity + ", priceListRemarks=" + priceListRemarks + ", partDescription=" + partDescription
				+ "]";
	}

	void apply(NormalPricer aBookCostPricer, PricerInfo pricerInfo) {
		
		if (aBookCostPricer == null) {
			logger.log(Level.INFO, "ABookCostPricer is not found.");
			return;
		}
		
		logger.log(Level.INFO, "aBookCostPricer[" + aBookCostPricer.getId() + "], CostIndicator[" + name + "] is used.");
		
		if (StringUtils.isEmpty(pricerInfo.getTermsAndConditions()) && aBookCostPricer.isEffectivePricer()) {
			pricerInfo.setTermsAndConditions(aBookCostPricer.getTermsAndConditions());
		}
		
		if (getMpq() != null) {
			if (getMpq() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND) {
				if (pricerInfo.getMpq() == null) {
					if (getMpqValue() != null) {
						if (getMpqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MPQ)) {
							pricerInfo.setMpq(aBookCostPricer.getMpq());
						} else if (getMpqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOQ)) {
							pricerInfo.setMpq(aBookCostPricer.getMoq());
						} else if (getMpqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOV)) {
							pricerInfo.setMpq(aBookCostPricer.getMov());
						}
					}
				}
			} else if (getMpq() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_DIRECTLY) {
				if (getMpqValue() != null) {
					if (getMpqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MPQ)) {
						pricerInfo.setMpq(aBookCostPricer.getMpq());
					} else if (getMpqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOQ)) {
						pricerInfo.setMpq(aBookCostPricer.getMoq());
					} else if (getMpqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOV)) {
						pricerInfo.setMpq(aBookCostPricer.getMov());
					}
				}

			} else if (getMpq() == QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND) {
				// no action needed

			} else if (getMpq() == QuoteSBConstant.COST_INDICATOR_RULE_BLANK_DIRECTLY) {
				pricerInfo.setMpq(null);
			}
		}
		/*
		 ************************************************
		 * MOQ
		 */
		if (getMoq() != null) {
			if (getMoq() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND) {
				if (pricerInfo.getMoq() == null) {
					if (getMoqValue() !=  null) {
						if (getMoqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MPQ)) {
							pricerInfo.setMoq(aBookCostPricer.getMpq());
						} else if (getMoqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOQ)) {
							pricerInfo.setMoq(aBookCostPricer.getMoq());
						} else if (getMoqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOV)) {
							pricerInfo.setMoq(aBookCostPricer.getMov());
						}
					}
				}
			} else if (getMoq() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_DIRECTLY) {
				if (getMoqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MPQ)) {
					pricerInfo.setMoq(aBookCostPricer.getMpq());
				} else if (getMoqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOQ)) {
					pricerInfo.setMoq(aBookCostPricer.getMoq());
				} else if (getMoqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOV)) {
					pricerInfo.setMoq(aBookCostPricer.getMov());
				}
			} else if (getMoq() == QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND) {

			} else if (getMoq() == QuoteSBConstant.COST_INDICATOR_RULE_BLANK_DIRECTLY) {
				pricerInfo.setMoq(null);
			}
		}

		/*
		 ************************************************
		 * MOV
		 */
		if (getMov() != null) {
			if (getMov() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND) {
				if (pricerInfo.getMov() == null ) {
					if (getMovValue() != null) {
						if (getMovValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MPQ)) {
							pricerInfo.setMov(aBookCostPricer.getMpq());
						} else if (getMovValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOQ)) {
							pricerInfo.setMov(aBookCostPricer.getMoq());
						} else if (getMovValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOV)) {
							pricerInfo.setMov(aBookCostPricer.getMov());
						}
						
					}
				}
			} else if (getMov() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_DIRECTLY) {
				if (getMovValue() != null) {
					if (getMovValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MPQ)) {
						pricerInfo.setMov(aBookCostPricer.getMpq());
					} else if (getMovValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOQ)) {
						pricerInfo.setMov(aBookCostPricer.getMoq());
					} else if (getMovValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOV)) {
						pricerInfo.setMov(aBookCostPricer.getMov());
					}
				}
			} else if (getMov() == QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND) {

			} else if (getMov() == QuoteSBConstant.COST_INDICATOR_RULE_BLANK_DIRECTLY) {
				pricerInfo.setMov(null);
			}
		}
		/*
		 ************************************************
		 * Lead Time
		 */
		if (getLeadTime() != null) {

			if (getLeadTime() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND) {
				if (pricerInfo.getLeadTime() == null) {
					pricerInfo.setLeadTime(aBookCostPricer.getLeadTime());
				}
			} else if (getLeadTime() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_DIRECTLY) {
				pricerInfo.setLeadTime(aBookCostPricer.getLeadTime());
			} else if (getLeadTime() == QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND) {

			} else if (getLeadTime() == QuoteSBConstant.COST_INDICATOR_RULE_BLANK_DIRECTLY) {
				pricerInfo.setLeadTime(null);
			}
		}

		/*
		 ************************************************
		 * pricer list remarks 1-4
		 */
		if (getPriceListRemarks() != null) {
			if (getPriceListRemarks() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND) {
				if (pricerInfo.getPriceListRemarks1() == null) {
					pricerInfo.setPriceListRemarks1(aBookCostPricer.getPriceListRemarks1());
				}
				if (pricerInfo.getPriceListRemarks2() == null) {
					pricerInfo.setPriceListRemarks2(aBookCostPricer.getPriceListRemarks2());
				}
				if (pricerInfo.getPriceListRemarks3() == null) {
					pricerInfo.setPriceListRemarks3(aBookCostPricer.getPriceListRemarks3());
				}
				if (pricerInfo.getPriceListRemarks4() == null) {
					pricerInfo.setPriceListRemarks4(aBookCostPricer.getPriceListRemarks4());
				}

			} else if (getPriceListRemarks() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_DIRECTLY) {
				pricerInfo.setPriceListRemarks1(aBookCostPricer.getPriceListRemarks1());
				pricerInfo.setPriceListRemarks2(aBookCostPricer.getPriceListRemarks2());
				pricerInfo.setPriceListRemarks3(aBookCostPricer.getPriceListRemarks3());
				pricerInfo.setPriceListRemarks4(aBookCostPricer.getPriceListRemarks4());
			} else if (getPriceListRemarks() == QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND) {

			} else if (getPriceListRemarks() == QuoteSBConstant.COST_INDICATOR_RULE_BLANK_DIRECTLY) {
				pricerInfo.setPriceListRemarks1(null);
				pricerInfo.setPriceListRemarks2(null);
				pricerInfo.setPriceListRemarks3(null);
				pricerInfo.setPriceListRemarks4(null);

			}
		}
		/*
		 ************************************************
		 * part description
		 */
		if (getPartDescription() != null) {
			if (getPartDescription() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND) {
				if (pricerInfo.getDescription() == null) {
					pricerInfo.setDescription(aBookCostPricer.getPartDescription());
				}
			} else if (getPartDescription() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_DIRECTLY) {
				pricerInfo.setDescription(aBookCostPricer.getPartDescription());
			} else if (getPartDescription() == QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND) {

			} else if (getPartDescription() == QuoteSBConstant.COST_INDICATOR_RULE_BLANK_DIRECTLY) {
				pricerInfo.setDescription(null);
			}

		}
		// Price Validity
		if (getPriceValidity() != null) {
			if (getPriceValidity() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND) {
				if (pricerInfo.getPriceValidity() == null) {
					pricerInfo.setPriceValidity(aBookCostPricer.getPriceValidity());
				}
			} else if (getPriceValidity() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_DIRECTLY) {
				pricerInfo.setPriceValidity(aBookCostPricer.getPriceValidity());
			} else if (getPriceValidity() == QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND) {

			} else if (getPriceValidity() == QuoteSBConstant.COST_INDICATOR_RULE_BLANK_DIRECTLY) {
				pricerInfo.setPriceValidity(null);
			}
		}
		// Shipment validity
		if (getShipmentValidity() != null) {
			if (getShipmentValidity() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND) {
				if (pricerInfo.getShipmentValidity() == null) {
					pricerInfo.setShipmentValidity(aBookCostPricer.getShipmentValidity());
				}
			} else if (getShipmentValidity() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_DIRECTLY) {
				pricerInfo.setShipmentValidity(aBookCostPricer.getShipmentValidity());
			} else if (getShipmentValidity() == QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND) {

			} else if (getPriceValidity() == QuoteSBConstant.COST_INDICATOR_RULE_BLANK_DIRECTLY) {
				pricerInfo.setShipmentValidity(null);

			}
		}
	}

	void apply(NormalPricer aBookCostPricer, RfqItemVO pricerInfo) {
		if (aBookCostPricer == null) {
			logger.log(Level.INFO, "ABookCostPricer is not found.");
			return;
		}
		
		logger.log(Level.INFO, "aBookCostPricer[" + aBookCostPricer.getId() + "], CostIndicator[" + name + "] is used.");
		
		if (StringUtils.isEmpty(pricerInfo.getTermsAndCondition()) && aBookCostPricer.isEffectivePricer()) {
			pricerInfo.setTermsAndCondition(aBookCostPricer.getTermsAndConditions());
		}

		
		if (getMpq() != null) {
			if (getMpq() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND) {
				if (pricerInfo.getMpq() == null) {
					if (getMpqValue() != null) {
						if (getMpqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MPQ)) {
							pricerInfo.setMpq(aBookCostPricer.getMpq());
						} else if (getMpqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOQ)) {
							pricerInfo.setMpq(aBookCostPricer.getMoq());
						} else if (getMpqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOV)) {
							pricerInfo.setMpq(aBookCostPricer.getMov());
						}
					}
				}
			} else if (getMpq() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_DIRECTLY) {
				if (getMpqValue() != null) {
					if (getMpqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MPQ)) {
						pricerInfo.setMpq(aBookCostPricer.getMpq());
					} else if (getMpqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOQ)) {
						pricerInfo.setMpq(aBookCostPricer.getMoq());
					} else if (getMpqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOV)) {
						pricerInfo.setMpq(aBookCostPricer.getMov());
					}
				}

			} else if (getMpq() == QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND) {
				// no action needed

			} else if (getMpq() == QuoteSBConstant.COST_INDICATOR_RULE_BLANK_DIRECTLY) {
				pricerInfo.setMpq(null);
			}
		}
		/*
		 ************************************************
		 * MOQ
		 */
		if (getMoq() != null) {
			if (getMoq() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND) {
				if (pricerInfo.getMoq() == null) {
					if (getMoqValue() !=  null) {
						if (getMoqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MPQ)) {
							pricerInfo.setMoq(aBookCostPricer.getMpq());
						} else if (getMoqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOQ)) {
							pricerInfo.setMoq(aBookCostPricer.getMoq());
						} else if (getMoqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOV)) {
							pricerInfo.setMoq(aBookCostPricer.getMov());
						}
					}
				}
			} else if (getMoq() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_DIRECTLY) {
				if (getMoqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MPQ)) {
					pricerInfo.setMoq(aBookCostPricer.getMpq());
				} else if (getMoqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOQ)) {
					pricerInfo.setMoq(aBookCostPricer.getMoq());
				} else if (getMoqValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOV)) {
					pricerInfo.setMoq(aBookCostPricer.getMov());
				}
			} else if (getMoq() == QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND) {

			} else if (getMoq() == QuoteSBConstant.COST_INDICATOR_RULE_BLANK_DIRECTLY) {
				pricerInfo.setMoq(null);
			}
		}

		/*
		 ************************************************
		 * MOV
		 */
		if (getMov() != null) {
			if (getMov() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND) {
				if (pricerInfo.getMov() == null ) {
					if (getMovValue() != null) {
						if (getMovValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MPQ)) {
							pricerInfo.setMov(aBookCostPricer.getMpq());
						} else if (getMovValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOQ)) {
							pricerInfo.setMov(aBookCostPricer.getMoq());
						} else if (getMovValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOV)) {
							pricerInfo.setMov(aBookCostPricer.getMov());
						}
						
					}
				}
			} else if (getMov() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_DIRECTLY) {
				if (getMovValue() != null) {
					if (getMovValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MPQ)) {
						pricerInfo.setMov(aBookCostPricer.getMpq());
					} else if (getMovValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOQ)) {
						pricerInfo.setMov(aBookCostPricer.getMoq());
					} else if (getMovValue().equalsIgnoreCase(QuoteSBConstant.COST_INDICATOR_RULE_MOV)) {
						pricerInfo.setMov(aBookCostPricer.getMov());
					}
				}
			} else if (getMov() == QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND) {

			} else if (getMov() == QuoteSBConstant.COST_INDICATOR_RULE_BLANK_DIRECTLY) {
				pricerInfo.setMov(null);
			}
		}
		/*
		 ************************************************
		 * Lead Time
		 */
		if (getLeadTime() != null) {

			if (getLeadTime() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND) {
				if (pricerInfo.getLeadTime() == null) {
					pricerInfo.setLeadTime(aBookCostPricer.getLeadTime());
				}
			} else if (getLeadTime() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_DIRECTLY) {
				pricerInfo.setLeadTime(aBookCostPricer.getLeadTime());
			} else if (getLeadTime() == QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND) {

			} else if (getLeadTime() == QuoteSBConstant.COST_INDICATOR_RULE_BLANK_DIRECTLY) {
				pricerInfo.setLeadTime(null);
			}
		}

		/*
		 ************************************************
		 * pricer list remarks 1-4
		 */
		if (getPriceListRemarks() != null) {
			if (getPriceListRemarks() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND) {
				if (pricerInfo.getPriceListRemarks1() == null) {
					pricerInfo.setPriceListRemarks1(aBookCostPricer.getPriceListRemarks1());
				}
				if (pricerInfo.getPriceListRemarks2() == null) {
					pricerInfo.setPriceListRemarks2(aBookCostPricer.getPriceListRemarks2());
				}
				if (pricerInfo.getPriceListRemarks3() == null) {
					pricerInfo.setPriceListRemarks3(aBookCostPricer.getPriceListRemarks3());
				}
				if (pricerInfo.getPriceListRemarks4() == null) {
					pricerInfo.setPriceListRemarks4(aBookCostPricer.getPriceListRemarks4());
				}

			} else if (getPriceListRemarks() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_DIRECTLY) {
				pricerInfo.setPriceListRemarks1(aBookCostPricer.getPriceListRemarks1());
				pricerInfo.setPriceListRemarks2(aBookCostPricer.getPriceListRemarks2());
				pricerInfo.setPriceListRemarks3(aBookCostPricer.getPriceListRemarks3());
				pricerInfo.setPriceListRemarks4(aBookCostPricer.getPriceListRemarks4());
			} else if (getPriceListRemarks() == QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND) {

			} else if (getPriceListRemarks() == QuoteSBConstant.COST_INDICATOR_RULE_BLANK_DIRECTLY) {
				pricerInfo.setPriceListRemarks1(null);
				pricerInfo.setPriceListRemarks2(null);
				pricerInfo.setPriceListRemarks3(null);
				pricerInfo.setPriceListRemarks4(null);

			}
		}
		/*
		 ************************************************
		 * part description
		 */
		if (getPartDescription() != null) {
			if (getPartDescription() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND) {
				if (pricerInfo.getDescription() == null) {
					pricerInfo.setDescription(aBookCostPricer.getPartDescription());
				}
			} else if (getPartDescription() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_DIRECTLY) {
				pricerInfo.setDescription(aBookCostPricer.getPartDescription());
			} else if (getPartDescription() == QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND) {

			} else if (getPartDescription() == QuoteSBConstant.COST_INDICATOR_RULE_BLANK_DIRECTLY) {
				pricerInfo.setDescription(null);
			}

		}
		// Price Validity
		if (getPriceValidity() != null) {
			if (getPriceValidity() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND) {
				if (pricerInfo.getPriceValidity() == null) {
					pricerInfo.setPriceValidity(aBookCostPricer.getPriceValidity());
				}
			} else if (getPriceValidity() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_DIRECTLY) {
				pricerInfo.setPriceValidity(aBookCostPricer.getPriceValidity());
			} else if (getPriceValidity() == QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND) {

			} else if (getPriceValidity() == QuoteSBConstant.COST_INDICATOR_RULE_BLANK_DIRECTLY) {
				pricerInfo.setPriceValidity(null);
			}
		}
		// Shipment validity
		if (getShipmentValidity() != null) {
			if (getShipmentValidity() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND) {
				if (pricerInfo.getShipmentValidity() == null) {
					pricerInfo.setShipmentValidity(aBookCostPricer.getShipmentValidity());
				}
			} else if (getShipmentValidity() == QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_DIRECTLY) {
				pricerInfo.setShipmentValidity(aBookCostPricer.getShipmentValidity());
			} else if (getShipmentValidity() == QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND) {

			} else if (getPriceValidity() == QuoteSBConstant.COST_INDICATOR_RULE_BLANK_DIRECTLY) {
				pricerInfo.setShipmentValidity(null);

			}
		}
	}

}