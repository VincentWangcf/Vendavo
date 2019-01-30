
package com.avnet.emasia.webquote.webservice.zwqrmtosap;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.avnet.emasia.webquote.webservice.zwqrmtosap package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.avnet.emasia.webquote.webservice.zwqrmtosap
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ZsdRecvWqmrpblkctlResponse }
     * 
     */
    public ZsdRecvWqmrpblkctlResponse createZsdRecvWqmrpblkctlResponse() {
        return new ZsdRecvWqmrpblkctlResponse();
    }

    /**
     * Create an instance of {@link TableOfZquoteMsg }
     * 
     */
    public TableOfZwqrmtosapMsg createTableOfZquoteMsg() {
        return new TableOfZwqrmtosapMsg();
    }

    /**
     * Create an instance of {@link TableOfZquoteBlklst }
     * 
     */
    public TableOfZquoteBlklst createTableOfZquoteBlklst() {
        return new TableOfZquoteBlklst();
    }

    /**
     * Create an instance of {@link ZsdRecvWqmrpblkctl }
     * 
     */
    public ZsdRecvWqmrpblkctl createZsdRecvWqmrpblkctl() {
        return new ZsdRecvWqmrpblkctl();
    }

    /**
     * Create an instance of {@link ZquoteBlklst }
     * 
     */
    public ZquoteBlklst createZquoteBlklst() {
        return new ZquoteBlklst();
    }

    /**
     * Create an instance of {@link ZquoteMsg }
     * 
     */
    public ZquoteMsg createZquoteMsg() {
        return new ZquoteMsg();
    }

}
