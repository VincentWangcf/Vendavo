
package com.avnet.emasia.webquote.webservice.drmsagprea;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Return" type="{urn:sap-com:document:sap:soap:functions:mc-style}TableOfZstruDrmsMsg"/>
 *         &lt;element name="Zdrms" type="{urn:sap-com:document:sap:soap:functions:mc-style}TableOfZstruDrms"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "_return",
    "zdrms"
})
@XmlRootElement(name = "ZsdWqDrAgpResponse")
public class ZsdWqDrAgpResponse {

    @XmlElement(name = "Return", required = true)
    protected TableOfZstruDrmsMsg _return;
    @XmlElement(name = "Zdrms", required = true)
    protected TableOfZstruDrms zdrms;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link TableOfZstruDrmsMsg }
     *     
     */
    public TableOfZstruDrmsMsg getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link TableOfZstruDrmsMsg }
     *     
     */
    public void setReturn(TableOfZstruDrmsMsg value) {
        this._return = value;
    }

    /**
     * Gets the value of the zdrms property.
     * 
     * @return
     *     possible object is
     *     {@link TableOfZstruDrms }
     *     
     */
    public TableOfZstruDrms getZdrms() {
        return zdrms;
    }

    /**
     * Sets the value of the zdrms property.
     * 
     * @param value
     *     allowed object is
     *     {@link TableOfZstruDrms }
     *     
     */
    public void setZdrms(TableOfZstruDrms value) {
        this.zdrms = value;
    }

}
