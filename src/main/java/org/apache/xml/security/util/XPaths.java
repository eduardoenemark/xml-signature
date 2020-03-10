package org.apache.xml.security.util;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.apache.xpath.jaxp.XPathFactoryImpl;
/**
 *
 */
public final class XPaths {

    public static XPath newXPath() {
        return xPathFactory.newXPath();
    }

    static {
        xPathFactory = XPathFactoryImpl.newInstance();
    }

    private static final XPathFactory xPathFactory;
}
