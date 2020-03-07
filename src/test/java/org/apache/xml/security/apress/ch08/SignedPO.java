package org.apache.xml.security.apress.ch08;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Poornachandra Sarang.
 */
public class SignedPO {

    @Test
    public void main() throws Exception {
        org.apache.xml.security.Init.init();

        /* Generate a public/private key pair for temporary use */
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = kpg.generateKeyPair();

        /* Obtain reference to generated public/private keys */
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey pubkey = keyPair.getPublic();

        /* Create a po document */
        Document doc = createDocument();

        /* Obtain the root element */
        Element root = doc.getDocumentElement();

        /* Create file for writing output */
        File f = Files.createTempFile("signedxml", ".xml").toFile(); //new File("po.xml");
        LOGGER.info("File in {}", f.getCanonicalPath());
        
        /* Create a XMLSignature instance that uses RSA_SHA1 algorithm */
        XMLSignature signature = new XMLSignature(doc, f.toURL().toString(), XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);

        /* Create canonical XML */
        Transforms transforms = new Transforms(doc);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);

        /* Add canonicalized document to signature */
        signature.addDocument("", transforms, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);

        /* Add the public key information to signature */
        signature.addKeyInfo(pubkey);

        /* Add signature itself to the PO document */
        root.appendChild(signature.getElement());

        /* Sign the document */
        signature.sign(privateKey);

        /* Create an output stream */
        FileOutputStream fos = new FileOutputStream(f);

        /* Output the memory document using XMLUtils. */
        XMLUtils.outputDOMc14nWithComments(doc, fos);

        /* XML Signed Pring */
        LOGGER.info(new String(Files.readAllBytes(f.toPath())));
    }

    static Document createDocument() throws Exception {
        /* Obtain an instance of Docuement Builder Factory */
        javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();

        /* Create a new document */
        Document doc = db.newDocument();

        /* Create elements */
        Element root = doc.createElementNS(null, "PurchaseOrder");
        Element contents = doc.createElementNS(null, "signedContents");
        doc.appendChild(root);
        root.appendChild(contents);
        contents.appendChild(doc.createTextNode("\nWe request that you EXECUTE the following trades\n"));

        /* Add Trade details */
        Element stock1 = doc.createElementNS(null, "stock");
        contents.appendChild(stock1);
        stock1.appendChild(doc.createTextNode("GFW"));
        Element quantity1 = doc.createElementNS(null, "quantity");
        contents.appendChild(quantity1);
        quantity1.appendChild(doc.createTextNode("50"));
        Element price1 = doc.createElementNS(null, "price");
        contents.appendChild(price1);
        price1.appendChild(doc.createTextNode("25.35"));
        Element type1 = doc.createElementNS(null, "type");
        contents.appendChild(type1);
        type1.appendChild(doc.createTextNode("B"));

        return doc;
    }

    private static Logger LOGGER = LoggerFactory.getLogger(SignedPO.class);
}
