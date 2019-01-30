
package com.avnet.emasia.webquote.webservice.drmsagprea;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ZstruDrms complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ZstruDrms">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Zzproject" type="{urn:sap-com:document:sap:rfc:functions}char20"/>
 *         &lt;element name="AuthGpPercent" type="{urn:sap-com:document:sap:rfc:functions}decimal5.2"/>
 *         &lt;element name="AuthGpChgRea" type="{urn:sap-com:document:sap:rfc:functions}char90"/>
 *         &lt;element name="AuthGpChgRem" type="{urn:sap-com:document:sap:rfc:functions}char60"/>
 *         &lt;element name="ExpiryDate" type="{urn:sap-com:document:sap:rfc:functions}date"/>
 *         &lt;element name="Upnam" type="{urn:sap-com:document:sap:rfc:functions}char12"/>
 *         &lt;element name="Mode" type="{urn:sap-com:document:sap:rfc:functions}char1"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ZstruDrms", propOrder = {
    "zzproject",
    "authGpPercent",
    "authGpChgRea",
    "authGpChgRem",
    "expiryDate",
    "upnam",
    "mode"
})
public class ZstruDrms {

    @XmlElement(name = "Zzproject", required = true)
    protected String zzproject;
    @XmlElement(name = "AuthGpPercent", required = true)
    protected BigDecimal authGpPercent;
    @XmlElement(name = "AuthGpChgRea", required = true)
    protected String authGpChgRea;
    @XmlElement(name = "AuthGpChgRem", required = true)
    protected String authGpChgRem;
    @XmlElement(name = "ExpiryDate", required = true)
    protected XMLGregorianCalendar expiryDate;
    @XmlElement(name = "Upnam", required = true)
    protected String upnam;
    @XmlElement(name = "Mode", required = true)
    protected String mode;

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
     * Gets the value of the authGpPercent property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAuthGpPercent() {
        return authGpPercent;
    }

    /**
     * Sets the value of the authGpPercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAuthGpPercent(BigDecimal value) {
        this.authGpPercent = value;
    }

    /**
     * Gets the value of the authGpChgRea property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthGpChgRea() {
        return authGpChgRea;
    }

    /**
     * Sets the value of the authGpChgRea property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthGpChgRea(String value) {
        this.authGpChgRea = value;
    }

    /**
     * Gets the value of the authGpChgRem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthGpChgRem() {
        return authGpChgRem;
    }

    /**
     * Sets the value of the authGpChgRem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthGpChgRem(String value) {
        this.authGpChgRem = value;
    }

    /**
     * Gets the value of the expiryDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the value of the expiryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpiryDate(XMLGregorianCalendar value) {
        this.expiryDate = value;
    }

    /**
     * Gets the value of the upnam property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpnam() {
        return upnam;
    }

    /**
     * Sets the value of the upnam property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpnam(String value) {
        this.upnam = value;
    }

    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMode() {
        return mode;
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMode(String value) {
        this.mode = value;
    }

}
