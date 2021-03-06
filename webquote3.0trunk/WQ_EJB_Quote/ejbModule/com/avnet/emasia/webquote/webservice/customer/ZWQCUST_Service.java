package com.avnet.emasia.webquote.webservice.customer;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.4.6
 * 2014-05-26T16:37:42.923+08:00
 * Generated source version: 2.4.6
 * 
 */
@WebServiceClient(name = "ZWQCUST", 
                  wsdlLocation = "file:/D:/Avnet/Workspace/workspace_webquote2_truck/WQ_EJB_Quote/ejbModule/CreateProspectiveCustomerNew.wsdl",
                  targetNamespace = "urn:sap-com:document:sap:soap:functions:mc-style") 
public class ZWQCUST_Service extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("urn:sap-com:document:sap:soap:functions:mc-style", "ZWQCUST");
    public final static QName ZWQCUST = new QName("urn:sap-com:document:sap:soap:functions:mc-style", "ZWQCUST");
    public final static QName ZWQCUSTSOAP12 = new QName("urn:sap-com:document:sap:soap:functions:mc-style", "ZWQCUST_SOAP12");
    static {
        URL url = null;
        try {
            //url = new URL("file:/D:/Avnet/Workspace/workspace_webquote2_truck/WQ_EJB_Quote/ejbModule/CreateProspectiveCustomerNew.wsdl");
            url = ZWQCUST_Service.class.getClassLoader().getResource("CreateProspectiveCustomerNew.wsdl");
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(ZWQCUST_Service.class.getName())
                .log(java.util.logging.Level.WARNING, 
                     "Can not initialize the default wsdl from {0}", "file:/D:/Avnet/Workspace/workspace_webquote2_truck/WQ_EJB_Quote/ejbModule/CreateProspectiveCustomerNew.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public ZWQCUST_Service(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public ZWQCUST_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ZWQCUST_Service() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public ZWQCUST_Service(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public ZWQCUST_Service(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public ZWQCUST_Service(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     *
     * @return
     *     returns ZWQCUST
     */
    @WebEndpoint(name = "ZWQCUST")
    public ZWQCUST getZWQCUST() {
        return super.getPort(ZWQCUST, ZWQCUST.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ZWQCUST
     */
    @WebEndpoint(name = "ZWQCUST")
    public ZWQCUST getZWQCUST(WebServiceFeature... features) {
        return super.getPort(ZWQCUST, ZWQCUST.class, features);
    }
    /**
     *
     * @return
     *     returns ZWQCUST
     */
    @WebEndpoint(name = "ZWQCUST_SOAP12")
    public ZWQCUST getZWQCUSTSOAP12() {
        return super.getPort(ZWQCUSTSOAP12, ZWQCUST.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ZWQCUST
     */
    @WebEndpoint(name = "ZWQCUST_SOAP12")
    public ZWQCUST getZWQCUSTSOAP12(WebServiceFeature... features) {
        return super.getPort(ZWQCUSTSOAP12, ZWQCUST.class, features);
    }

}
