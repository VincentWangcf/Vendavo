//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.04.13 at 03:43:52 PM CST 
//


package com.avnet.emasia.webquote.dp.xml.responsequote;

import java.sql.Timestamp;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.avnet.emasia.webquote.dp.xml.requestquote.WebQuoteLineRequest;
import com.avnet.emasia.webquote.entity.DpRfqItem;


/**
 * <p>Java class for AvnQuoteIdentifierType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AvnQuoteIdentifierType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="referenceLineId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lineItemNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="quoteLineStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lostSaleReasonCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lostSaleReasonDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="noBidReasonCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="noBidReasonDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestedManufacturer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestedPart" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="quantity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="quotedResale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lineItemComments" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="currency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="remarks" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="expiryDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rejectionReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AvnQuoteIdentifierType", propOrder = {
    "referenceLineId",
    "lineItemNumber",
    "quoteLineStatus",
    "lostSaleReasonCode",
    "lostSaleReasonDescription",
    "noBidReasonCode",
    "noBidReasonDescription",
    "requestedManufacturer",
    "requestedPart",
    "quantity",
    "quotedResale",
    "lineItemComments",
    "currency",
    "remarks",
    "expiryDate",
    "rejectionReason"
})
public class AvnQuoteIdentifierType {

    @XmlElement(required = true)
    protected String referenceLineId;
    @XmlElement(required = true)
    protected String lineItemNumber;
    @XmlElement(required = true)
    protected String quoteLineStatus;
    protected String lostSaleReasonCode;
    protected String lostSaleReasonDescription;
    protected String noBidReasonCode;
    protected String noBidReasonDescription;
    protected String requestedManufacturer;
    protected String requestedPart;
    protected String quantity;
    protected String quotedResale;
    protected String lineItemComments;
    protected String currency;
    protected String remarks;
    protected String expiryDate;
    protected String rejectionReason;
    
    public static AvnQuoteIdentifierType createInstance(DpRfqItem dpRfqItem){
    	
    	ObjectFactory factory = new ObjectFactory();
    	AvnQuoteIdentifierType item = factory.createAvnQuoteIdentifierType();
    	
    	item.setReferenceLineId(dpRfqItem.getReferenceLineId());
    	item.setLineItemNumber("1");
    	item.setQuoteLineStatus(dpRfqItem.getQuoteLineStatus());
    	item.setRequestedManufacturer(dpRfqItem.getRequestedManufacturer());
    	item.setRequestedPart(dpRfqItem.getRequestedPart());
    	item.setQuantity(dpRfqItem.getQuantity() == null ? "" : dpRfqItem.getQuantity().toString());
    	item.setQuotedResale(dpRfqItem.getQuotedResale() == null ? "" : dpRfqItem.getQuotedResale().toString());
    	item.setRejectionReason(dpRfqItem.getRejectionReason());
    	item.setRemarks(dpRfqItem.getRemarks());
    	item.setCurrency(dpRfqItem.getCurrency());
    	
    	Timestamp expiryDate = dpRfqItem.getExpiryDate();
    	if(expiryDate != null){
    		item.setExpiryDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(expiryDate));
    	}else{
    		item.setExpiryDate("");
    	}
    	return item;
    }
    
    
    public static AvnQuoteIdentifierType createInstance(WebQuoteLineRequest webQuoteLineRequest){
    	
    	ObjectFactory factory = new ObjectFactory();
    	AvnQuoteIdentifierType item = factory.createAvnQuoteIdentifierType();
    	
    	item.setReferenceLineId(webQuoteLineRequest.getReferenceLineID());
    	item.setLineItemNumber("1");
    	item.setRequestedManufacturer(webQuoteLineRequest.getRequestedManufacturer());
    	item.setRequestedPart(webQuoteLineRequest.getRequestedPart());
    	item.setCurrency(webQuoteLineRequest.getCurrency());
    	
    	return item;
    }
    

    /**
     * Gets the value of the referenceLineId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferenceLineId() {
        return referenceLineId;
    }

    /**
     * Sets the value of the referenceLineId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenceLineId(String value) {
        this.referenceLineId = value;
    }

    /**
     * Gets the value of the lineItemNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineItemNumber() {
        return lineItemNumber;
    }

    /**
     * Sets the value of the lineItemNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineItemNumber(String value) {
        this.lineItemNumber = value;
    }

    /**
     * Gets the value of the quoteLineStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuoteLineStatus() {
        return quoteLineStatus;
    }

    /**
     * Sets the value of the quoteLineStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuoteLineStatus(String value) {
        this.quoteLineStatus = value;
    }

    /**
     * Gets the value of the lostSaleReasonCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLostSaleReasonCode() {
        return lostSaleReasonCode;
    }

    /**
     * Sets the value of the lostSaleReasonCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLostSaleReasonCode(String value) {
        this.lostSaleReasonCode = value;
    }

    /**
     * Gets the value of the lostSaleReasonDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLostSaleReasonDescription() {
        return lostSaleReasonDescription;
    }

    /**
     * Sets the value of the lostSaleReasonDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLostSaleReasonDescription(String value) {
        this.lostSaleReasonDescription = value;
    }

    /**
     * Gets the value of the noBidReasonCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNoBidReasonCode() {
        return noBidReasonCode;
    }

    /**
     * Sets the value of the noBidReasonCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNoBidReasonCode(String value) {
        this.noBidReasonCode = value;
    }

    /**
     * Gets the value of the noBidReasonDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNoBidReasonDescription() {
        return noBidReasonDescription;
    }

    /**
     * Sets the value of the noBidReasonDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNoBidReasonDescription(String value) {
        this.noBidReasonDescription = value;
    }

    /**
     * Gets the value of the requestedManufacturer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestedManufacturer() {
        return requestedManufacturer;
    }

    /**
     * Sets the value of the requestedManufacturer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestedManufacturer(String value) {
        this.requestedManufacturer = value;
    }

    /**
     * Gets the value of the requestedPart property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestedPart() {
        return requestedPart;
    }

    /**
     * Sets the value of the requestedPart property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestedPart(String value) {
        this.requestedPart = value;
    }

    /**
     * Gets the value of the quantity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuantity(String value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the quotedResale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuotedResale() {
        return quotedResale;
    }

    /**
     * Sets the value of the quotedResale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuotedResale(String value) {
        this.quotedResale = value;
    }

    /**
     * Gets the value of the lineItemComments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineItemComments() {
        return lineItemComments;
    }

    /**
     * Sets the value of the lineItemComments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineItemComments(String value) {
        this.lineItemComments = value;
    }

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrency(String value) {
        this.currency = value;
    }

    /**
     * Gets the value of the remarks property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets the value of the remarks property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemarks(String value) {
        this.remarks = value;
    }

    /**
     * Gets the value of the expiryDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the value of the expiryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpiryDate(String value) {
        this.expiryDate = value;
    }

    /**
     * Gets the value of the rejectionReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRejectionReason() {
        return rejectionReason;
    }

    /**
     * Sets the value of the rejectionReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRejectionReason(String value) {
        this.rejectionReason = value;
    }

}
