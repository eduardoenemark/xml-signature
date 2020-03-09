package org.apache.xml.security.hsm.brb;

import br.com.brb.hsm.api.security.DinamoGenericException;
import br.com.brb.hsm.api.security.DinamoManager;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.spi.brb.BrbHsmRsaSha256SignatureAlgorithmSpi;
import static org.apache.xml.security.test.SupportTest.getFileBody;
import static org.apache.xml.security.test.SupportTest.getRsaPrivateKeyFromPemFile;
import org.apache.xml.security.transforms.Transforms;
import static org.apache.xml.security.util.Documents.newDocument;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

        Element root = pibr001Doc.getDocumentElement();

        XMLSignature signature = new XMLSignature(pibr001Doc, "", XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256/*, Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS*/);

        Transforms transforms = new Transforms(pibr001Doc);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

        signature.addDocument("", transforms, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
//        signature.addDocument("/Envelope/Document", transforms, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
        signature.addKeyInfo(certificate);
        root.appendChild(signature.getElement());
        signature.sign(privateKey);

//            Path filepath = Paths.get(USER_DIR + "/src/test/resources/spi.1.2/pibr.001.spi.1.0_msg.signed.xml");
//            Files.deleteIfExists(filepath);
//            Files.write(filepath, XMLUtils.getFullTextChildrenFromNode(doc).getBytes());
        XMLUtils.outputDOMc14nWithComments(pibr001Doc, System.out);

        millisEnd = System.currentTimeMillis();
        LOGGER.info("\n\nTIMES: {} -> (start: {}, end: {})", (millisEnd - millisStart), millisStart, millisEnd);
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
            pibr001Doc = newDocument(getFileBody(USER_DIR + "/src/test/resources/spi.1.2/pibr.001.spi.1.0_msg.xml"));
        }
    }

    @AfterClass
    public static void end() throws DinamoGenericException {
        dinamoManager.closeSession();
    }

    private static X509Certificate certificate;
    private static DinamoManager dinamoManager;
    private static PrivateKey privateKey;
    private static Document pibr001Doc;

    static final String USER_DIR = System.getProperty("user.dir");
    private static Logger LOGGER = LoggerFactory.getLogger(BrbHsmScenarioTest.class);
}
