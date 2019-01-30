package com.avnet.emasia.webquote.entity;


import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;
import java.util.logging.Logger;


/**
 * The persistent class for the APP_USER database table.
 * 
 */
@Entity
@Table(name="SPECIAL_CUSTOMER")
public class SpecialCustomer implements Serializable {

	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger("SpecialCustomer");
	@Id
	@SequenceGenerator(name="SPECIAL_CUSTOMER_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SPECIAL_CUSTOMER_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=19)
	private long id;

	@Column(name="\"SOLD_TO_CUSTOMER_NUMBER\"", nullable=false, length=10)
	private String soldToCustomerNumber;
	
	@Column(name="\"END_CUSTOMER_NUMBER\"", nullable=false, length=10)
	private String endCustomerNumber;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="VALID_FROM", nullable=false)
	private Date validFrom;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="VALID_TO", nullable=false)
	private Date validTo;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSoldToCustomerNumber() {
		return soldToCustomerNumber;
	}

	public void setSoldToCustomerNumber(String soldToCustomerNumber) {
		this.soldToCustomerNumber = soldToCustomerNumber;
	}

	public String getEndCustomerNumber() {
		return endCustomerNumber;
	}

	public void setEndCustomerNumber(String endCustomerNumber) {
		this.endCustomerNumber = endCustomerNumber;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}



	
}