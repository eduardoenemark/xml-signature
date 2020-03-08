package org.apache.xml.security.spi;

import java.lang.reflect.Field;
import java.util.Map;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.algorithms.implementations.SignatureBaseRSA;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.xml.security.signature.XMLSignature;
import static org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256;
import org.apache.xml.security.signature.XMLSignatureException;

/**
 *
 * @author eduardo
 */
public class HsmRsaSha256SignatureSpi extends SignatureBaseRSA {

    public HsmRsaSha256SignatureSpi() throws XMLSignatureException {
        super();
    }

//    public HsmSignatureSpi(Provider provider) throws XMLSignatureException {
//        super(provider);
//    }
    
    @Override
    protected byte[] engineSign() {
        throw new UnsupportedOperationException("Making...");
    }

    @Override
    public String engineGetURI() {
        return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256;
    }

    public static void register()
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException,
            AlgorithmAlreadyRegisteredException, ClassNotFoundException, XMLSignatureException {
        
        Field algorithmHash = SignatureAlgorithm.class.getDeclaredField("algorithmHash");
        algorithmHash.setAccessible(true);
        Map<String, Object> algs = (Map) algorithmHash.get(SignatureAlgorithm.class);
        algs.remove(ALGO_ID_SIGNATURE_RSA_SHA256);
        algorithmHash.set(SignatureAlgorithm.class, algs);
        SignatureAlgorithm.register(ALGO_ID_SIGNATURE_RSA_SHA256, HsmRsaSha256SignatureSpi.class.getCanonicalName());
    }
}
