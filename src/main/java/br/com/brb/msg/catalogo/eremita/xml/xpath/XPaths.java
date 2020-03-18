package br.com.brb.msg.catalogo.eremita.xml.xpath;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 */
public final class XPaths {

    public XPaths() {
        this(null, null);
    }

    @SuppressWarnings("")
    public XPaths(final XPath xpath, final Document document) {
        this.init(xpath);
        this.init(document);
    }

    public void init(final Document document) {
        this.setDocument(document);
    }

    public void init(final XPath xpath) {
        this.setXPath(xpath);
    }

    public void init(NamespaceContext namespaceContext) {
        this.getXPath().setNamespaceContext(namespaceContext);
    }

    public static Object compileAndEvaluate(
            final XPath xpath,
            final String expression,
            final Document document,
            final QName returnType
    ) throws XPathExpressionException {
        return xpath.compile(expression).evaluate(document, returnType);
    }

    public Object compileAndEvaluate(String expression, QName returnType)
            throws XPathExpressionException {
        return compileAndEvaluate(this.getXPath(), expression, this.getDocument(), returnType);
    }

    public String compileAndEvaluateToString(String expression)
            throws XPathExpressionException {
        return (String) compileAndEvaluate(this.getXPath(), expression, this.getDocument(), XPathConstants.STRING);
    }

    public NodeList compileAndEvaluateToNodeList(String expression)
            throws XPathExpressionException {
        return (NodeList) compileAndEvaluate(this.getXPath(), expression, this.getDocument(), XPathConstants.NODESET);
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public XPath getXPath() {
        if (this.xpath == null) {
            this.xpath = getxPathFactory().newXPath();
        }
        return xpath;
    }

    public void setXPath(XPath xpath) {
        this.xpath = xpath;
    }

    public static synchronized XPathFactory getxPathFactory() {
        if (xPathFactory == null) {
            xPathFactory = XPathFactory.newInstance();
        }
        return xPathFactory;
    }

    private Document document;
    private XPath xpath;
    private static XPathFactory xPathFactory;
}
