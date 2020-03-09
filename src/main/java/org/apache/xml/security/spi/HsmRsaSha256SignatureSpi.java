package org.apache.xml.security.spi;

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
public class HsmRsaSha256SignatureSpi extends SignatureBaseRSA {

    public HsmRsaSha256SignatureSpi() throws XMLSignatureException {
        super();
        this.outputStream = new ByteArrayOutputStream();
    }

//    public HsmSignatureSpi(Provider provider) throws XMLSignatureException {
//        super(provider);
//    }
    @Override
    protected void engineUpdate(byte[] input, int offset, int len) throws XMLSignatureException {
        this.outputStream.write(input, offset, len);
//        super.engineUpdate(input, offset, len);
    }

    @Override
    protected void engineUpdate(byte[] input) throws XMLSignatureException {
        this.outputStream.reset();
        this.outputStream.write(input, 0, input.length - 1);
//        super.engineUpdate(input);
    }

    @Override
    protected void engineUpdate(byte input) throws XMLSignatureException {
        this.outputStream.write(input);
//        super.engineUpdate(input);
    }

    /**
     *  METODO PARA O HSM (codigo de exemplo).
     * @return
     */
    @Override
    protected byte[] engineSign() {
        try {
            Signature rsa = Signature.getInstance("SHA256withRSA");
            rsa.initSign(this.privateKey);
            rsa.update(this.outputStream.toByteArray());
            return rsa.sign();
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

    @Override
    protected void engineInitSign(Key privateKey) throws XMLSignatureException {
        this.privateKey = (PrivateKey) privateKey;
//        super.engineInitSign(privateKey);
    }

    protected void engineInitSign(Key signingKey, AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        this.privateKey = privateKey;
        this.algorithmParameterSpec = algorithmParameterSpec;
    }

    protected PrivateKey privateKey;
    protected AlgorithmParameterSpec algorithmParameterSpec;

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
        SignatureAlgorithm.register(ALGO_ID_SIGNATURE_RSA_SHA256, canonicalNameClassForRegister);
    }

    protected static String canonicalNameClassForRegister = HsmRsaSha256SignatureSpi.class.getCanonicalName();
    protected ByteArrayOutputStream outputStream;
    private Logger LOGGER = LoggerFactory.getLogger(HsmRsaSha256SignatureSpi.class);
}
