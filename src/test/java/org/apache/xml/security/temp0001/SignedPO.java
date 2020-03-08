package org.apache.xml.security.temp0001;

// Import required classes
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 * ARQUIVO DE MODIFICACAO DO WILLIAN. 
 */
public class SignedPO {
    static String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><Envelope><AppHdr><Fr><FIId><FinInstnId><Othr><Id>00000208</Id></Othr></FinInstnId></FIId></Fr><To><FIId><FinInstnId><Othr><Id>00392159</Id></Othr></FinInstnId></FIId></To><BizMsgIdr>M00000208GPI20200306054927000119</BizMsgIdr><MsgDefIdr>pibr.001.spi.1.0</MsgDefIdr><CreDt>2020-03-06T05:49:27.119Z</CreDt><Sgntr></Sgntr></AppHdr><Document><EchoReq><GrpHdr><MsgId>M00000208GPI20200306054927000119</MsgId><CreDtTm>2020-03-06T05:49:27.119Z</CreDtTm></GrpHdr><EchoTxInf><Data>TESTE!</Data></EchoTxInf></EchoReq></Document></Envelope>";
    static Document createDocument() throws Exception {
        // Obtain an instance of Docuement Builder Factory
        javax.xml.parsers.DocumentBuilderFactory dbf =
        javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();

        // Create a new document
        Document doc = db.newDocument();
        
        // Create elements
        Element root = doc.createElementNS(null, "PurchaseOrder");
        Element contents = doc.createElementNS(null, "signedContents");
        doc.appendChild(root);
        root.appendChild(contents);
        contents.appendChild(doc.createTextNode(
        "We request that you EXECUTE the following trades"));
        // Add Trade details
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
        // Add one more Trade
        // ...
        return doc;
    }

    public static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.parse(new ByteArrayInputStream(xml.getBytes()));
    }
    
    public static void main(String unused[]) throws Exception {
        org.apache.xml.security.Init.init();

        // Generate a public/private key pair for temporary use
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = kpg.generateKeyPair();

        // Obtain reference to generated public/private keys
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey pubkey = keyPair.getPublic();

        // Create a po document
        Document doc = loadXMLFromString(xml);//createDocument();
        
        // Obtain the root element
        Element root = doc.getDocumentElement();

        // Create file for writing output
        File f = new File("po.xml");
        
        // Create a XMLSignature instance that uses RSA_SHA1 algorithm
        XMLSignature signature = new XMLSignature(doc,
                                                  f.toURL().toString(),
                                                  XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256,
                                                  Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        
        // Create canonical XML
        Transforms transforms = new Transforms(doc);
        transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        
        // Create canonical XML
        Transforms transforms2 = new Transforms(doc);
        transforms2.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        transforms2.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        
        // Create canonical XML
        Transforms transforms3 = new Transforms(doc);
        transforms3.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        
        // Add canonicalized document to signature
        signature.addDocument("#b2177f73-7685-39ac-83db-fa00ffd2b89c",
                              transforms,
                              MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
        signature.addDocument("",
                              transforms2,
                              MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
        signature.addDocument("",
                              transforms3,
                              MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
        
        // Add the public key information to signature
        signature.addKeyInfo(keyPair.getPublic());
        signature.getKeyInfo().setId("b2177f73-7685-39ac-83db-fa00ffd2b89c");

        if(signature.getKeyInfo().containsX509Data()) {
            System.out.println(signature.getKeyInfo().getX509Certificate().toString());
        }
        
        
        // Add signature itself to the PO document
        root.appendChild(signature.getElement());
        
        // Sign the document
        signature.sign(privateKey);
        
        // Create an output stream
        FileOutputStream fos = new FileOutputStream(f);
        
        // Output the memory document using XMLUtils.
        XMLUtils.outputDOMc14nWithComments(doc, fos);
    }
}