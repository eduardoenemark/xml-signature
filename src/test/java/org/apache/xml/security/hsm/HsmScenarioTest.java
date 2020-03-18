//package org.apache.xml.security.hsm;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.security.GeneralSecurityException;
//import java.security.PrivateKey;
//import java.security.cert.X509Certificate;
//import java.util.Iterator;
//import javax.xml.namespace.NamespaceContext;
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
//import org.apache.xml.security.spi.HsmRsaSha256SignatureAlgorithmSpi;
//import static org.apache.xml.security.test.SupportTest.getFileBody;
//import static org.apache.xml.security.test.SupportTest.getRsaPrivateKeyFromPemFile;
//import static org.apache.xml.security.test.SupportTest.getX509CertificateFromFile;
//import org.apache.xml.security.transforms.Transforms;
//import static org.apache.xml.security.util.Documents.newDocument;
//import static org.apache.xml.security.util.XPaths.newXPath;
//import org.apache.xml.security.utils.UniversalNamespaceCache;
//import org.apache.xml.security.utils.XMLUtils;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.xml.sax.SAXException;
//
///**
// * UTILIZANDO CHAMADA MODIFICADA PARA O APACHE SANTUARIO.
// *
// */
//public class HsmScenarioTest {
//
//    @Test
//    public void pibr001Spi1_0Sign() throws Exception {
//        long millisStart, millisEnd;
//        millisStart = System.currentTimeMillis();
//
//        Init.init();
//        HsmRsaSha256SignatureAlgorithmSpi.register();
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
//        xPath.setNamespaceContext(new UniversalNamespaceCache(pibr001Doc, false));
//        XPathExpression expression = xPath.compile("/:Envelope/:AppHdr/:Sgntr");
//        ElementImpl sgntr = (ElementImpl) expression.evaluate(pibr001Doc, NODE);
//        sgntr.appendChild(signature.getElement());
//
//        // ------------ AppHdr ------------
//        Transforms transformsAppHdr = new Transforms(pibr001Doc);
//        transformsAppHdr.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
//        transformsAppHdr.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
//        signature.addDocument("/:Envelope/:AppHdr", TAG_AND_EMPTY_VALUE, transformsAppHdr, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
//
//        // ------------ Document ------------
//        Transforms transformsDocument = new Transforms(pibr001Doc);
//        transformsDocument.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
//        signature.addDocument("/:Envelope/:Document", NOT_SHOW, transformsDocument, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
//
//        // ------------ sign ------------
//        signature.sign(privateKey);
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
//    @BeforeClass
//    public static void init() throws IOException, GeneralSecurityException, SAXException {
//        certificate = getX509CertificateFromFile(USER_DIR + "/src/test/resources/keys/certificate.01.cer");
//        privateKey = getRsaPrivateKeyFromPemFile(USER_DIR + "/src/test/resources/keys/private-key.01.key");
//        pibr001Doc = (DocumentImpl) newDocument(getFileBody(USER_DIR + "/src/test/resources/spi.1.2/brb-pibr.001.spi.1.0_msg.xml"));
//    }
//   
//
//    private static X509Certificate certificate;
//    private static PrivateKey privateKey;
//    private static DocumentImpl pibr001Doc;
//
//    static final String USER_DIR = System.getProperty("user.dir");
//    private static Logger LOGGER = LoggerFactory.getLogger(HsmScenarioTest.class);
//}
