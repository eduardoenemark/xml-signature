package org.apache.xml.security.util;

import java.io.IOException;
import java.io.InputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.IOUtils;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/*
 *
 *
 */
public final class Documents {

    public static Document newDocument(String documentString)
            throws SAXException, IOException {
        return newDocument(IOUtils.toInputStream(documentString, UTF_8));
    }

    public static Document newDocument(InputStream inputStream)
            throws SAXException, IOException {
        return documentBuilder.parse(inputStream);
    }

    public static Document newDocument() {
        return documentBuilder.newDocument();
    }

    static {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactoryImpl.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setIgnoringComments(true);
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static final DocumentBuilder documentBuilder;
}
