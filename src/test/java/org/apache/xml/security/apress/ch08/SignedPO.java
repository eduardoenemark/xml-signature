package org.apache.xml.security.apress.ch08;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.spi.HsmRsaSha256SignatureSpi;
import static org.apache.xml.security.test.SupportTest.getFileBody;
import static org.apache.xml.security.test.SupportTest.getRsaPrivateKeyFromPemFile;
import static org.apache.xml.security.test.SupportTest.getX509CertificateFromFile;
import org.apache.xml.security.transforms.Transforms;
import static org.apache.xml.security.util.Documents.newDocument;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * UTILIZANDO O APACHE SANTUARIO.
 * EXEMPLO MODIFICADO DO LIVRO Pro Apache XML (Poornachandra Sarang, Ph.D).
 */
public class SignedPO {
    
    @Test
    public void main() throws Exception {
        Init.init();

        X509Certificate cert = getX509CertificateFromFile(USER_DIR + "/src/test/resources/keys/certificate.01.cer");
        PrivateKey privateKey = getRsaPrivateKeyFromPemFile(USER_DIR + "/src/test/resources/keys/private-key.01.key");

        /* Create a po document */
        Document doc = newDocument(getFileBody(USER_DIR + "/src/test/resources/spi.1.2/pibr.001.spi.1.0_msg.xml"));
        
        /* Obtain the root element */
        Element root = doc.getDocumentElement();

        /* Create a XMLSignature instance that uses RSA_SHA1 algorithm */
        XMLSignature signature = new XMLSignature(doc, "", XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256);

        /* Create canonical XML */
        Transforms transforms = new Transforms(doc);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);

        /* Add canonicalized document to signature */
        signature.addDocument("", transforms, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);

        /* Add the public key information to signature */
        signature.addKeyInfo(cert);

        /* Add signature itself to the PO document */
        root.appendChild(signature.getElement());

        /* Sign the document */
        signature.sign(privateKey);

        /* Output the memory document using XMLUtils. */
        XMLUtils.outputDOMc14nWithComments(doc, System.out);
    }

    static final String USER_DIR = System.getProperty("user.dir");
    private static Logger LOGGER = LoggerFactory.getLogger(SignedPO.class);
}
