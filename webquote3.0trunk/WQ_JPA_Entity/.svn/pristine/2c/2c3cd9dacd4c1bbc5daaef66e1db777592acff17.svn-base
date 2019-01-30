package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;



/**
 * The persistent class for the QUOTE_NUMBER database table.
 * 
 */
@Entity
@Table(name="QUOTE_NUMBER")
public class QuoteNumber implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private QuoteNumberPK id;

	@Column(length=50)
	private String description;

	@Column(name="NEXT_NUMBER", nullable=false, precision=12)
	private int nextNumber;

	@Column(name="NUMBER_PATTERN", nullable=false, length=30)
	private String numberPattern;
	

	

	public QuoteNumber() {
	}

	public QuoteNumberPK getId() {
		return this.id;
	}

	public void setId(QuoteNumberPK id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getNextNumber() {
		return this.nextNumber;
	}

	public void setNextNumber(int nextNumber) {
		this.nextNumber = nextNumber;
	}

	public String getNumberPattern() {
		return this.numberPattern;
	}

	public void setNumberPattern(String numberPattern) {
		this.numberPattern = numberPattern;
	}



}