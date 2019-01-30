
package com.sap.document.sap.soap.functions.mc_style;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ZsdWqCrCustomer.RfcExceptions.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ZsdWqCrCustomer.RfcExceptions">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="InvalidCountry"/>
 *     &lt;enumeration value="TaxDataCannotBeDetermined"/>
 *     &lt;enumeration value="InvalidCurrency"/>
 *     &lt;enumeration value="MissingCity"/>
 *     &lt;enumeration value="MissingName"/>
 *     &lt;enumeration value="InvalidRegion"/>
 *     &lt;enumeration value="InvalidSalesOffice"/>
 *     &lt;enumeration value="InvalidSalesOrg"/>
 *     &lt;enumeration value="InvalidSalesOrgOfficeCombi"/>
 *     &lt;enumeration value="ErrorCreatingCustomer"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ZsdWqCrCustomer.RfcExceptions")
@XmlEnum
public enum ZsdWqCrCustomerRfcExceptions {

    @XmlEnumValue("InvalidCountry")
    INVALID_COUNTRY("InvalidCountry"),
    @XmlEnumValue("TaxDataCannotBeDetermined")
    TAX_DATA_CANNOT_BE_DETERMINED("TaxDataCannotBeDetermined"),
    @XmlEnumValue("InvalidCurrency")
    INVALID_CURRENCY("InvalidCurrency"),
    @XmlEnumValue("MissingCity")
    MISSING_CITY("MissingCity"),
    @XmlEnumValue("MissingName")
    MISSING_NAME("MissingName"),
    @XmlEnumValue("InvalidRegion")
    INVALID_REGION("InvalidRegion"),
    @XmlEnumValue("InvalidSalesOffice")
    INVALID_SALES_OFFICE("InvalidSalesOffice"),
    @XmlEnumValue("InvalidSalesOrg")
    INVALID_SALES_ORG("InvalidSalesOrg"),
    @XmlEnumValue("InvalidSalesOrgOfficeCombi")
    INVALID_SALES_ORG_OFFICE_COMBI("InvalidSalesOrgOfficeCombi"),
    @XmlEnumValue("ErrorCreatingCustomer")
    ERROR_CREATING_CUSTOMER("ErrorCreatingCustomer");
    private final String value;

    ZsdWqCrCustomerRfcExceptions(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ZsdWqCrCustomerRfcExceptions fromValue(String v) {
        for (ZsdWqCrCustomerRfcExceptions c: ZsdWqCrCustomerRfcExceptions.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
