
package com.sap.document.sap.soap.functions.mc_style;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ZwqCustomer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ZwqCustomer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Kunnr" type="{urn:sap-com:document:sap:rfc:functions}char100"/>
 *         &lt;element name="Name" type="{urn:sap-com:document:sap:rfc:functions}char30"/>
 *         &lt;element name="Ktokd" type="{urn:sap-com:document:sap:rfc:functions}char100"/>
 *         &lt;element name="City" type="{urn:sap-com:document:sap:rfc:functions}char25"/>
 *         &lt;element name="Land" type="{urn:sap-com:document:sap:rfc:functions}char3"/>
 *         &lt;element name="Vkorg" type="{urn:sap-com:document:sap:rfc:functions}char100"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ZwqCustomer", propOrder = {
    "kunnr",
    "name",
    "ktokd",
    "city",
    "land",
    "vkorg"
})
public class ZwqCustomer {

    @XmlElement(name = "Kunnr", required = true)
    protected String kunnr;
    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "Ktokd", required = true)
    protected String ktokd;
    @XmlElement(name = "City", required = true)
    protected String city;
    @XmlElement(name = "Land", required = true)
    protected String land;
    @XmlElement(name = "Vkorg", required = true)
    protected String vkorg;

    /**
     * Gets the value of the kunnr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKunnr() {
        return kunnr;
    }

    /**
     * Sets the value of the kunnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKunnr(String value) {
        this.kunnr = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the ktokd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKtokd() {
        return ktokd;
    }

    /**
     * Sets the value of the ktokd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKtokd(String value) {
        this.ktokd = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the land property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLand() {
        return land;
    }

    /**
     * Sets the value of the land property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLand(String value) {
        this.land = value;
    }

    /**
     * Gets the value of the vkorg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVkorg() {
        return vkorg;
    }

    /**
     * Sets the value of the vkorg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVkorg(String value) {
        this.vkorg = value;
    }

}
