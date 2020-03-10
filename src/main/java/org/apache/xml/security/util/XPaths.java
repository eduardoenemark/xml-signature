package org.apache.xml.security.util;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

/**
 *
 */
public final class XPaths {

    public static XPath newXPath() {
        return xPathFactory.newXPath();
    }

    static {
        xPathFactory = XPathFactory.newInstance();
    }

    private static final XPathFactory xPathFactory;
}
