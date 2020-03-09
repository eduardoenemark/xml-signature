package org.apache.xml.security.spi.brb;

import br.com.brb.hsm.api.security.DinamoManager;
import org.apache.xml.security.spi.*;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Map;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.algorithms.implementations.SignatureBaseRSA;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.xml.security.signature.XMLSignature;
import static org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256;
import org.apache.xml.security.signature.XMLSignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author eduardo
 */
public class BrbHsmRsaSha256SignatureSpi extends HsmRsaSha256SignatureSpi {

    public BrbHsmRsaSha256SignatureSpi(DinamoManager dinamoManager) throws XMLSignatureException {
        super();
        canonicalNameClassForRegister = BrbHsmRsaSha256SignatureSpi.class.getCanonicalName();
        this.dinamoManager = dinamoManager;
    }

    @Override
    protected byte[] engineSign() {
        try {
            return dinamoManager.signWithSHA256RSA(dinamoManager.getOurPrivateKeyObjectName(), outputStream.toByteArray());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                this.outputStream.close();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private DinamoManager dinamoManager;
    private Logger LOGGER = LoggerFactory.getLogger(HsmRsaSha256SignatureSpi.class);
}
