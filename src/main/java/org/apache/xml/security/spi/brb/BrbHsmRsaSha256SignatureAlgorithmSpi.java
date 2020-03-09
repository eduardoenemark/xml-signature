package org.apache.xml.security.spi.brb;

import lombok.Setter;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.spi.HsmRsaSha256SignatureAlgorithmSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class BrbHsmRsaSha256SignatureAlgorithmSpi extends HsmRsaSha256SignatureAlgorithmSpi {

    public BrbHsmRsaSha256SignatureAlgorithmSpi(DinamoManager dinamoManager) throws XMLSignatureException {
        super();
        super.setCanonicalNameClassForRegister(this.getClass());
        this.setDinamoManager(dinamoManager);
    }

    @Override
    protected byte[] engineSign() {
        try {
            return dinamoManager.signWithSHA256RSA(dinamoManager.getOurPrivateKeyObjectName(), outputStream.toByteArray());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                this.getOutputStream().close();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Setter
    private DinamoManager dinamoManager;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
}
