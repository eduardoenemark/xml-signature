package org.apache.xml.security.spi;

import java.security.Provider;

/*
 *
 * 
 */
public final class HsmProvider extends Provider {

    public HsmProvider() {
        super("HSM", 1.0, "HSM Provider");
        put("Signature.SHA256WithRSA", "org.apache.xml.security.spi.HsmSignatureSpi");
        put("Signature.SHA256WITHRSA", "org.apache.xml.security.spi.HsmSignatureSpi");
        put("Signature.rsa-sha256", "org.apache.xml.security.spi.HsmSignatureSpi");
        put("Signature.RSA-SHA256", "org.apache.xml.security.spi.HsmSignatureSpi");
        put("Signature.RSA", "org.apache.xml.security.spi.HsmSignatureSpi");
        put("Signature.rsa-sha111", "org.apache.xml.security.spi.HsmSignatureSpi");
    }
}
