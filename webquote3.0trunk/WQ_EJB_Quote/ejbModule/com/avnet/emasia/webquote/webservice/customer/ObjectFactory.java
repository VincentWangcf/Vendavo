
package com.avnet.emasia.webquote.webservice.customer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.avnet.emasia.webquote.webservice.customer package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName ZSDWQCR_CUSTOMER_EXCEPTION_QNAME = new QName("urn:sap-com:document:sap:soap:functions:mc-style", "ZsdWqCrCustomer.Exception");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.avnet.emasia.webquote.webservice.customer
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ZsdWqCrCustomerResponse }
     * 
     */
    public ZsdWqCrCustomerResponse createZsdWqCrCustomerResponse() {
        return new ZsdWqCrCustomerResponse();
    }

    /**
     * Create an instance of {@link ZwqCustomer }
     * 
     */
    public ZwqCustomer createZwqCustomer() {
        return new ZwqCustomer();
    }

    /**
     * Create an instance of {@link ZsdWqCrCustomerRfcException }
     * 
     */
    public ZsdWqCrCustomerRfcException createZsdWqCrCustomerRfcException() {
        return new ZsdWqCrCustomerRfcException();
    }

    /**
     * Create an instance of {@link ZsdWqCrCustomer }
     * 
     */
    public ZsdWqCrCustomer createZsdWqCrCustomer() {
        return new ZsdWqCrCustomer();
    }

    /**
     * Create an instance of {@link RfcExceptionMessage }
     * 
     */
    public RfcExceptionMessage createRfcExceptionMessage() {
        return new RfcExceptionMessage();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ZsdWqCrCustomerRfcException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:sap-com:document:sap:soap:functions:mc-style", name = "ZsdWqCrCustomer.Exception")
    public JAXBElement<ZsdWqCrCustomerRfcException> createZsdWqCrCustomerException(ZsdWqCrCustomerRfcException value) {
        return new JAXBElement<ZsdWqCrCustomerRfcException>(ZSDWQCR_CUSTOMER_EXCEPTION_QNAME, ZsdWqCrCustomerRfcException.class, null, value);
    }

}
