
package com.avnet.emasia.webquote.webservice.zwqrmtosap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ZquoteBlklst complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="ZquoteBlklst">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Zregion" type="{urn:sap-com:document:sap:rfc:functions}char5"/>
 *         &lt;element name="Mfrnr" type="{urn:sap-com:document:sap:rfc:functions}char10"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ZquoteBlklst", propOrder = {
    "zregion",
    "mfrnr"
})
public class ZquoteBlklst {

    @XmlElement(name = "Zregion", required = true)
    protected String zregion;
    @XmlElement(name = "Mfrnr", required = true)
    protected String mfrnr;

    /**
     * ��ȡzregion���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZregion() {
        return zregion;
    }

    /**
     * ����zregion���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZregion(String value) {
        this.zregion = value;
    }

    /**
     * ��ȡmfrnr���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMfrnr() {
        return mfrnr;
    }

    /**
     * ����mfrnr���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMfrnr(String value) {
        this.mfrnr = value;
    }

}
