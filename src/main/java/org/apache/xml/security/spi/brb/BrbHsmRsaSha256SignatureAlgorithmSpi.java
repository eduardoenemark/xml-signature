//package org.apache.xml.security.spi.brb;
//
//import br.com.brb.hsm.api.security.DinamoGenericException;
//import br.com.brb.hsm.api.security.DinamoManager;
//import java.io.IOException;
//import static java.lang.System.out;
//import org.apache.xml.security.algorithms.SignatureAlgorithm;
//import static org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256;
//import org.apache.xml.security.signature.XMLSignatureException;
//import org.apache.xml.security.spi.HsmRsaSha256SignatureAlgorithmSpi;
//
///**
// *
// *
// */
//public class BrbHsmRsaSha256SignatureAlgorithmSpi extends HsmRsaSha256SignatureAlgorithmSpi {
//
//    public BrbHsmRsaSha256SignatureAlgorithmSpi() throws XMLSignatureException {
//        super();
//    }
//
//    @Override
//    protected byte[] engineSign() {
//        try {
//            out.println("[engineSign]\n#####\n" + new String(getOutputStream().toByteArray())+"\n#####\n\n");
//            return dinamoManager.signWithSHA256RSA(dinamoManager.getOurPrivateKeyObjectName(), getOutputStream().toByteArray());
//        } catch (DinamoGenericException ex) {
//            throw new RuntimeException(ex);
//        } finally {
//            try {
//                this.getOutputStream().close();
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        }
//    }
//
//    public static void register(DinamoManager _dinamoManager) {
//        dinamoManager = _dinamoManager;
//        SignatureAlgorithm.overlapOfRegister(ALGO_ID_SIGNATURE_RSA_SHA256, BrbHsmRsaSha256SignatureAlgorithmSpi.class);
//    }
//
//    private static DinamoManager dinamoManager;
//}
