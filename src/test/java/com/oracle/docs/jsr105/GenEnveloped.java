package com.oracle.docs.jsr105;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import static org.apache.xml.security.test.SupportTest.getFileBody;
import static org.apache.xml.security.test.SupportTest.getRsaPrivateKeyFromPemFile;
import static org.apache.xml.security.test.SupportTest.getX509CertificateFromFile;
import static org.apache.xml.security.util.Documents.newDocument;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * UTILIZANDO A JSR 105.
 */
public class GenEnveloped {

    @Test
    public void main() throws Exception {
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        Reference ref = fac.newReference(
                "", 
                fac.newDigestMethod(DigestMethod.SHA256, null),
                Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                null, null
        );

        SignedInfo si = fac.newSignedInfo(
                fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null),
                fac.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null),
                Collections.singletonList(ref)
        );

        final String USER_DIR = System.getProperty("user.dir");
        
        X509Certificate cert = getX509CertificateFromFile(USER_DIR + "/src/test/resources/keys/certificate.01.cer");
        PrivateKey pk = getRsaPrivateKeyFromPemFile(USER_DIR + "/src/test/resources/keys/private-key.01.key");

        KeyInfoFactory kif = fac.getKeyInfoFactory();
        KeyValue kv = kif.newKeyValue(cert.getPublicKey());

        List keyInfoList = new ArrayList<>();
        keyInfoList.add(kif.newKeyInfo(Collections.singletonList(kv)));
        keyInfoList.add(kif.newX509Data(Collections.singletonList(cert)));
       
        KeyInfo ki = kif.newKeyInfo(keyInfoList);


        // Instantiate the document to be signed
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = newDocument(getFileBody(USER_DIR + "/src/test/resources/spi.1.2/pibr.001.spi.1.0_msg.xml"));

        DOMSignContext dsc = new DOMSignContext(pk, doc.getDocumentElement());
        
        XMLSignature signature = fac.newXMLSignature(si, ki);
        
        signature.sign(dsc);
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(System.out));
    }
}
