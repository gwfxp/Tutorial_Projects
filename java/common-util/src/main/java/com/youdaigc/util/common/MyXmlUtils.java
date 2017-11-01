package com.youdaigc.util.common;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.Serializable;
import java.io.StringReader;

/**
 * Created by GaoWeiFeng on 2017-06-22.
 */
public class MyXmlUtils implements Serializable{
    /** The logger. */
    transient private static final Logger logger = LoggerFactory.getLogger(MyXmlUtils.class);
    private static final long serialVersionUID = 2580358125511171501L;

    /**
     * Format XML String
     *
     * @param xml
     * @return
     */
    public static String formatXML(String xml) {
        if(xml == null) {
            return null;
        }

        try {
            final InputSource src = new InputSource(new StringReader(xml));
            final Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
            final Boolean keepDeclaration = Boolean.valueOf(xml.startsWith("<?xml"));


            //May need this: System.setProperty(DOMImplementationRegistry.PROPERTY,"com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");
            final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            final LSSerializer writer = impl.createLSSerializer();

            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE); // Set this to true if the output needs to be beautified.
            writer.getDomConfig().setParameter("xml-declaration", keepDeclaration); // Set this to true if the declaration is needed to be outputted.

            return writer.writeToString(document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    public static void main(String[] args) {
//        String unformattedXml =
//                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><QueryMessage" +
//                        " xmlns=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/message\"" +
//                        "  xmlns:query=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/query\">" +
//                        "<Query>" +
//                        "<query:CategorySchemeWhere>" +
//                        "<query:AgencyID>ECB</query:AgencyID>" +
//                        "</query:CategorySchemeWhere>" +
//                        "</Query>" +
//                        "</QueryMessage>";
//
//        System.out.println(unformattedXml);
//        System.out.println(new MyXmlUtils().formatXML(unformattedXml));
//    }
}
