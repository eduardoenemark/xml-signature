package org.apache.xml.security.spi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
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
 *
 */
public class HsmRsaSha256SignatureAlgorithmSpi extends SignatureBaseRSA {

    public HsmRsaSha256SignatureAlgorithmSpi() throws XMLSignatureException {
        super();
        this.setOutputStream(new ByteArrayOutputStream());
    }

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

    @Override
    protected byte[] engineSign() {
        try {
            Signature rsa = Signature.getInstance("SHA256withRSA");
            rsa.initSign(this.privateKey);
            rsa.update(this.outputStream.toByteArray());
            return rsa.sign();
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                this.outputStream.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    protected void engineInitSign(Key privateKey) throws XMLSignatureException {
        this.privateKey = (PrivateKey) privateKey;
//        super.engineInitSign(privateKey);
    }

    @Override
    protected void engineInitSign(Key privateKey, AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        this.privateKey = (PrivateKey) privateKey;
        this.algorithmParameterSpec = algorithmParameterSpec;
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
        SignatureAlgorithm.register(ALGO_ID_SIGNATURE_RSA_SHA256, canonicalNameClassForRegister);
    }

    public void setCanonicalNameClassForRegister(Class clazz) {
        canonicalNameClassForRegister = clazz.getCanonicalName();
    }

    private PrivateKey privateKey;
    private AlgorithmParameterSpec algorithmParameterSpec;

    @Setter
    @Getter
    private ByteArrayOutputStream outputStream;
    private static String canonicalNameClassForRegister = HsmRsaSha256SignatureAlgorithmSpi.class.getCanonicalName();

    private final Logger LOGGER = LoggerFactory.getLogger(HsmRsaSha256SignatureAlgorithmSpi.class);
}
