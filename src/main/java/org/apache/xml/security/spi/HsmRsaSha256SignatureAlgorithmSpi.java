//package org.apache.xml.security.spi;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import static java.lang.System.out;
//import java.security.InvalidKeyException;
//import java.security.Key;
//import java.security.NoSuchAlgorithmException;
//import java.security.PrivateKey;
//import java.security.Signature;
//import java.security.SignatureException;
//import lombok.Getter;
//import lombok.Setter;
//import org.apache.xml.security.algorithms.SignatureAlgorithm;
//import org.apache.xml.security.algorithms.implementations.SignatureBaseRSA;
//import org.apache.xml.security.signature.XMLSignature;
//import static org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256;
//import org.apache.xml.security.signature.XMLSignatureException;
//
///**
// *
// *
// */
//public class HsmRsaSha256SignatureAlgorithmSpi extends SignatureBaseRSA {
//
//    public HsmRsaSha256SignatureAlgorithmSpi() throws XMLSignatureException {
//        super();
//        this.setOutputStream(new ByteArrayOutputStream());
//    }
//
//    @Override
//    protected void engineUpdate(byte[] input, int offset, int len) throws XMLSignatureException {
//        this.outputStream.write(input, offset, len);
//    }
//
//    @Override
//    protected void engineUpdate(byte[] input) throws XMLSignatureException {
//        this.outputStream.reset();
//        this.outputStream.write(input, 0, input.length - 1);
//    }
//
//    @Override
//    protected void engineUpdate(byte input) throws XMLSignatureException {
//        this.outputStream.write(input);
//    }
//
//    @Override
//    protected byte[] engineSign() {
//        try {
//            Signature rsa = Signature.getInstance("SHA256withRSA");
//            rsa.initSign(this.privateKey);
//            rsa.update(this.outputStream.toByteArray());
//            out.println("####\n" + new String(this.outputStream.toByteArray())+"\n####");
//            return rsa.sign();
//        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException ex) {
//            throw new RuntimeException(ex);
//        } finally {
//            try {
//                this.outputStream.close();
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        }
//    }
//
//    @Override
//    protected void engineInitSign(Key privateKey) throws XMLSignatureException {
//        this.privateKey = (PrivateKey) privateKey;
//    }
//
//    @Override
//    protected void engineInitSign() throws XMLSignatureException {
//    }
//
//    @Override
//    public String engineGetURI() {
//        return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256;
//    }
//
//    public static void register() {
//        SignatureAlgorithm.overlapOfRegister(ALGO_ID_SIGNATURE_RSA_SHA256, HsmRsaSha256SignatureAlgorithmSpi.class);
//    }
//
//    private PrivateKey privateKey;
//
//    @Setter
//    @Getter
//    private ByteArrayOutputStream outputStream;
//}
