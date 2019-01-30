package com.sap.document.sap.soap.functions.mc_style;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.4.6
 * 2013-04-10T20:44:47.365+08:00
 * Generated source version: 2.4.6
 * 
 */
@WebService(targetNamespace = "urn:sap-com:document:sap:soap:functions:mc-style", name = "ZWQTOSAP")
@XmlSeeAlso({ObjectFactory.class})
public interface ZWQTOSAP {

    @RequestWrapper(localName = "ZsdRecvWebQuote", targetNamespace = "urn:sap-com:document:sap:soap:functions:mc-style", className = "com.sap.document.sap.soap.functions.mc_style.ZsdRecvWebQuote")
    @WebMethod(operationName = "ZsdRecvWebQuote")
    @ResponseWrapper(localName = "ZsdRecvWebQuoteResponse", targetNamespace = "urn:sap-com:document:sap:soap:functions:mc-style", className = "com.sap.document.sap.soap.functions.mc_style.ZsdRecvWebQuoteResponse")
    public void zsdRecvWebQuote(
        @WebParam(mode = WebParam.Mode.INOUT, name = "Return", targetNamespace = "")
        javax.xml.ws.Holder<TableOfZquoteMsg> _return,
        @WebParam(mode = WebParam.Mode.INOUT, name = "ZquoteItm", targetNamespace = "")
        javax.xml.ws.Holder<TableOfZquoteLst> zquoteItm
    );
}
