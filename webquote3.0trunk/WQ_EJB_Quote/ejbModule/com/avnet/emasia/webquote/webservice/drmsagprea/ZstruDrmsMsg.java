
package com.avnet.emasia.webquote.webservice.drmsagprea;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ZstruDrmsMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ZstruDrmsMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Zzproject" type="{urn:sap-com:document:sap:rfc:functions}char20"/>
 *         &lt;element name="Type" type="{urn:sap-com:document:sap:rfc:functions}char1"/>
 *         &lt;element name="Id" type="{urn:sap-com:document:sap:rfc:functions}char20"/>
 *         &lt;element name="Number" type="{urn:sap-com:document:sap:rfc:functions}numeric3"/>
 *         &lt;element name="Message" type="{urn:sap-com:document:sap:rfc:functions}char220"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ZstruDrmsMsg", propOrder = {
    "zzproject",
    "type",
    "id",
    "number",
    "message"
})
public class ZstruDrmsMsg {

    @XmlElement(name = "Zzproject", required = true)
    protected String zzproject;
    @XmlElement(name = "Type", required = true)
    protected String type;
    @XmlElement(name = "Id", required = true)
    protected String id;
    @XmlElement(name = "Number", required = true)
    protected String number;
    @XmlElement(name = "Message", required = true)
    protected String message;

    /**
     * Gets the value of the zzproject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZzproject() {
        return zzproject;
    }

    /**
     * Sets the value of the zzproject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZzproject(String value) {
        this.zzproject = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the number property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumber(String value) {
        this.number = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

}
