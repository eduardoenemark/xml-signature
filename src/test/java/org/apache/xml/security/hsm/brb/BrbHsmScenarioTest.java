package org.apache.xml.security.hsm.brb;

import br.com.brb.hsm.api.security.DinamoGenericException;
import br.com.brb.hsm.api.security.DinamoManager;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.xml.xpath.XPath;
import static javax.xml.xpath.XPathConstants.NODE;
import javax.xml.xpath.XPathExpression;
import org.apache.xerces.dom.DeepNodeListImpl;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.spi.brb.BrbHsmRsaSha256SignatureAlgorithmSpi;
import static org.apache.xml.security.test.SupportTest.getFileBody;
import static org.apache.xml.security.test.SupportTest.getRsaPrivateKeyFromPemFile;
import org.apache.xml.security.transforms.Transforms;
import static org.apache.xml.security.util.Documents.newDocument;
import static org.apache.xml.security.util.XPaths.newXPath;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * UTILIZANDO CHAMADA MODIFICADA PARA O APACHE SANTUARIO.
 *
 */
public class BrbHsmScenarioTest {

    @Test
    public void pibr001Spi1_0Sign() throws Exception {
        long millisStart, millisEnd;
        millisStart = System.currentTimeMillis();

        Init.init();
        BrbHsmRsaSha256SignatureAlgorithmSpi.register();

        ElementImpl rootElementImpl = (ElementImpl) pibr001Doc.getDocumentElement();
        XMLSignature signature = new XMLSignature(
                pibr001Doc,
                "",
                XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256,
                Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS
        );

        Transforms transformsKeyInfo = new Transforms(pibr001Doc);
        {
            transformsKeyInfo.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
            signature.addDocument("#KeyInfo", transformsKeyInfo, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
        }

//        Transforms transformsAppHdr = new Transforms(appHdrElementImpl, "Transforms:http://www.w3.org/2000/09/xmldsig#");
        ElementImpl appHdrElementImpl = (ElementImpl) ((DeepNodeListImpl) rootElementImpl.getElementsByTagName("AppHdr")).item(0);
        Transforms transformsAppHdr = new Transforms(pibr001Doc);
        {
//            appHdrElementImpl.setIdAttribute("id", true);
            transformsAppHdr.setElement(appHdrElementImpl, "");
            transformsAppHdr.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
            transformsAppHdr.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
            signature.addDocument("", transformsAppHdr, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
        }

        Transforms transformsDocument = new Transforms(pibr001Doc);
        {
//            documentElementImpl.setIdAttribute("id", true);
            ElementImpl documentElementImpl = (ElementImpl) ((DeepNodeListImpl) rootElementImpl.getElementsByTagName("Document")).item(0);
            transformsDocument.setElement(documentElementImpl, "");
            transformsDocument.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
            signature.addDocument("", transformsDocument, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
        }

        X509Data x509Data = new X509Data(pibr001Doc);
        {
            x509Data.addIssuerSerial(certificate.getIssuerX500Principal().getName(), certificate.getSerialNumber());
            signature.getKeyInfo().add(x509Data);
        }

        signature.getKeyInfo().setId("KeyInfo");
        {
//            XPath xPath = newXPath();
//            XPathExpression expression = xPath.compile("//Sgntr");
//            XPathExpression expression = xPath.compile("//*[local-name()='Sgntr']");
//            XPathExpression expression = xPath.compile("/Envelope/AppHdr/Sgntr");
//            XPathExpression expression = xPath.compile("/app:Envelope/app:AppHdr/app:Sgntr");
//            XPathExpression expression = xPath.compile("//AppHdr/Sgntr");

//            ElementImpl sgntr = (ElementImpl) expression.evaluate(pibr001Doc, NODE);
//            ElementImpl sgntr = (ElementImpl)((ElementImpl)((DeepNodeListImpl) rootElementImpl.getElementsByTagName("AppHdr")).item(0)).getElementsByTagName("Sgntr").item(0);

            // ElementImpl appHdrElementImpl = (ElementImpl) ((DeepNodeListImpl) rootElementImpl.getElementsByTagName("AppHdr")).item(0);

            ElementImpl sgntr =(ElementImpl) ((DeepNodeListImpl)  appHdrElementImpl.getElementsByTagName("Sgntr")).item(0);
            sgntr.appendChild(signature.getElement());
        }

        signature.sign(privateKey);

        XMLUtils.outputDOMc14nWithComments(pibr001Doc, System.out);

        millisEnd = System.currentTimeMillis();
        System.out.println(String.format("\n\nTIMES: %d -> (start: %d, end: %d)", (millisEnd - millisStart), millisStart, millisEnd));

        {
            System.out.println("Gerando arquivo....");
            OutputStream os = Files.newOutputStream(Paths.get(USER_DIR + "/src/test/resources/spi.1.2/pibr.001.spi.1.0_msg.signed.xml"));
            XMLUtils.outputDOMc14nWithComments(pibr001Doc, os);
            System.out.println("Gerado arquivo!");
        }
    }

    @BeforeClass
    public static void init() throws DinamoGenericException, IOException, GeneralSecurityException, SAXException {
        {
            dinamoManager = new DinamoManager();
            dinamoManager.setHostname("hsmspi.brb.com.br");
            dinamoManager.setAcceptExpiredCertificate(true);
            dinamoManager.setApplyCompactation(true);
            dinamoManager.setUsername("usrgpih");
            dinamoManager.setPassword("P@gpih070");
            dinamoManager.setOurPrivateKeyObjectName("usrspih/PKJZXAQ2A79T6D0WLKOSJE6ZVWJ0OXA");
            dinamoManager.setOurX509CertificateObjectName("usrspih/c_00000208_DOM10_20200226153641");
            dinamoManager.setTheirX509CertificateObjectName("usrspih/c_00000208_DOM11_20200226153701");
            dinamoManager.setApplyCompactation(false);
        }
        {
            certificate = dinamoManager.getOurX509Certificate();
            privateKey = getRsaPrivateKeyFromPemFile(USER_DIR + "/src/test/resources/keys/private-key.01.key");
            pibr001Doc = (DocumentImpl) newDocument(
                    getFileBody(USER_DIR + "/src/test/resources/spi.1.2/pibr.001.spi.1.0_msg.xml")
            //                            .replaceAll("<Envelope.*>", "<Envelope>")
            //                            .replaceAll("<AppHdr>", "<AppHdr id=\"AppHdr\">")
            //                            .replaceAll("<Document>", "<Document id=\"Document\">")
            //                            .replaceAll("<Sgntr>", "<Sgntr id=\"Sgntr\">")
            );
        }
    }

    @AfterClass
    public static void end() throws DinamoGenericException {
        dinamoManager.closeSession();
    }

    private static X509Certificate certificate;
    private static PrivateKey privateKey;
    private static DocumentImpl pibr001Doc;
    private static DinamoManager dinamoManager;

    static final String USER_DIR = System.getProperty("user.dir");
    private static Logger LOGGER = LoggerFactory.getLogger(BrbHsmScenarioTest.class);
}
