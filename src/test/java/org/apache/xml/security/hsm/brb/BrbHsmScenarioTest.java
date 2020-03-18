//package org.apache.xml.security.hsm.brb;
//
//import br.com.brb.hsm.api.security.DinamoGenericException;
//import br.com.brb.hsm.api.security.DinamoManager;
//import java.io.IOException;
//import java.io.OutputStream;
//import static java.lang.System.out;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.security.GeneralSecurityException;
//import java.security.cert.X509Certificate;
//import javax.xml.xpath.XPath;
//import static javax.xml.xpath.XPathConstants.NODE;
//import javax.xml.xpath.XPathExpression;
//import org.apache.xerces.dom.DocumentImpl;
//import org.apache.xerces.dom.ElementImpl;
//import org.apache.xml.security.Init;
//import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
//import org.apache.xml.security.keys.content.X509Data;
//import static org.apache.xml.security.signature.Reference.UriHowShow.NOT_SHOW;
//import static org.apache.xml.security.signature.Reference.UriHowShow.TAG_AND_EMPTY_VALUE;
//import org.apache.xml.security.signature.XMLSignature;
//import org.apache.xml.security.spi.brb.BrbHsmRsaSha256SignatureAlgorithmSpi;
//import static org.apache.xml.security.test.SupportTest.getFileBody;
//import org.apache.xml.security.transforms.Transforms;
//import static org.apache.xml.security.util.Documents.newDocument;
//import static org.apache.xml.security.util.XPaths.newXPath;
//import org.apache.xml.security.utils.Constants;
//import org.apache.xml.security.utils.XMLUtils;
//import org.apache.xpath.XPathAPI;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.w3c.dom.Element;
//import org.xml.sax.SAXException;
//
///**
// * UTILIZANDO CHAMADA MODIFICADA PARA O APACHE SANTUARIO.
// *
// */
//public class BrbHsmScenarioTest {
//
//    @Test
//    public void pibr001Spi1_0Sign() throws Exception {
//        long millisStart, millisEnd;
//        millisStart = System.currentTimeMillis();
//
//        Init.init();
//        BrbHsmRsaSha256SignatureAlgorithmSpi.register(dinamoManager);
//
//        XMLSignature signature = new XMLSignature(
//                pibr001Doc, "pibr.001.spi.1.0.xsd", XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256, Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS
//        );
//
//        // ------------ KeyInfo ------------=
//        Transforms transformsKeyInfo = new Transforms(pibr001Doc);
//        transformsKeyInfo.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
//        signature.addDocument("#KeyInfo", transformsKeyInfo, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
//
//        X509Data x509Data = new X509Data(pibr001Doc);
//        x509Data.addIssuerSerial(certificate.getIssuerX500Principal().getName(), certificate.getSerialNumber());
//        signature.getKeyInfo().add(x509Data);
//
//        signature.getKeyInfo().setId("KeyInfo");
//        XPath xPath = newXPath();
//        XPathExpression expression = xPath.compile("/bc:Envelope/AppHdr/Sgntr");
//        ElementImpl sgntr = (ElementImpl) expression.evaluate(pibr001Doc, NODE);
//        sgntr.appendChild(signature.getElement());
//
//        // ------------ AppHdr ------------
//        Transforms transformsAppHdr = new Transforms(pibr001Doc);
//        transformsAppHdr.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
//        transformsAppHdr.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
//        signature.addDocument("/bc:Envelope/AppHdr", TAG_AND_EMPTY_VALUE, transformsAppHdr, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
//
//        // ------------ Document ------------
//        Transforms transformsDocument = new Transforms(pibr001Doc);
//        transformsDocument.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
//        signature.addDocument("/bc:Envelope/Document", NOT_SHOW, transformsDocument, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
//
//        // ------------ sign ------------
//        signature.sign();
//
//        // ------------ output ------------
//        XMLUtils.outputDOMc14nWithComments(pibr001Doc, System.out);
//
//        millisEnd = System.currentTimeMillis();
//        System.out.println(String.format("\n\nTIMES: %d -> (start: %d, end: %d)", (millisEnd - millisStart), millisStart, millisEnd));
//
//        {
//            System.out.println("Gerando arquivo....");
//            OutputStream os = Files.newOutputStream(Paths.get(USER_DIR + "/src/test/resources/spi.1.2/brb-pibr.001.spi.1.0_msg.signed.xml"));
//            XMLUtils.outputDOMc14nWithComments(pibr001Doc, os);
//            System.out.println("Gerado arquivo!");
//        }
//    }
//
////    @Test
//    public void pibr001Spi1_0Verify() throws Exception {
//        {
//            this.pibr001Spi1_0Sign();
//        }
//        Init.init();
//        DocumentImpl pibr001DocSigned = (DocumentImpl) newDocument(
//                getFileBody(USER_DIR + "/src/test/resources/spi.1.2/brb-pibr.001.spi.1.0_msg.signed.xml")
//        );
//
//        Element nscontext = XMLUtils.createDSctx(pibr001DocSigned, "ds", Constants.SignatureSpecNS);
//        Element sigElement = (Element) XPathAPI.selectSingleNode(pibr001DocSigned, "//ds:Signature", nscontext);
//
//        XMLSignature signature = new XMLSignature(sigElement, "");
//        signature.addResourceResolver(new org.apache.xml.security.test.dom.utils.resolver.OfflineResolver());
//
////        KeyInfo ki = signature.getKeyInfo();
////        PublicKey pk = signature.getKeyInfo().getPublicKey();
//        boolean result = signature.checkSignatureValue(certificate.getPublicKey());
//        out.println("VALID: " + result);
//    }
//
//    @BeforeClass
//    public static void init() throws DinamoGenericException, IOException, GeneralSecurityException, SAXException {
//        {
//            dinamoManager = new DinamoManager();
//            dinamoManager.setHostname("hsmspi.brb.com.br");
//            dinamoManager.setAcceptExpiredCertificate(true);
//            dinamoManager.setApplyCompactation(true);
//            dinamoManager.setUsername("usrgpih");
//            dinamoManager.setPassword("P@gpih070");
//            dinamoManager.setOurPrivateKeyObjectName("usrspih/PKJZXAQ2A79T6D0WLKOSJE6ZVWJ0OXA");
//            dinamoManager.setOurX509CertificateObjectName("usrspih/c_00000208_DOM10_20200226153641");
//            dinamoManager.setTheirX509CertificateObjectName("usrspih/c_00000208_DOM11_20200226153701");
//            dinamoManager.setApplyCompactation(false);
//        }
//        {
//            certificate = dinamoManager.getOurX509Certificate();
//            pibr001Doc = (DocumentImpl) newDocument(
//                    getFileBody(USER_DIR + "/src/test/resources/spi.1.2/brb-pibr.001.spi.1.0_msg.xml")
//            );
//        }
//    }
//
//    @AfterClass
//    public static void end() throws DinamoGenericException {
//        dinamoManager.closeSession();
//    }
//
//    private static X509Certificate certificate;
//    private static DocumentImpl pibr001Doc;
//    private static DinamoManager dinamoManager;
//
//    static final String USER_DIR = System.getProperty("user.dir");
//    private static Logger LOGGER = LoggerFactory.getLogger(BrbHsmScenarioTest.class);
//}
