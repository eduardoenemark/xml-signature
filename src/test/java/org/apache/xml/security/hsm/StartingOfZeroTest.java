package org.apache.xml.security.hsm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.stream.Stream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import static org.apache.xml.security.test.SupportTest.getFileBody;
import static org.apache.xml.security.test.SupportTest.getRsaPrivateKeyFromPemFile;
import static org.apache.xml.security.test.SupportTest.getX509CertificateFromFile;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 *
 */
public class StartingOfZeroTest {

    @Test
    public void test() throws Exception {
        long millisStart, millisEnd;
        millisStart = System.currentTimeMillis();

        // ------= =------
        Canonicalizer.registerDefaultAlgorithms();

        // ------= =------
        final String APPHDR_TAG = "AppHdr";
        final String DOCUMENT_TAG = "Document";
        final String SGNTR_TAG = "Sgntr";

        // ------= =------
        String uri = extractUri(docStringBuilder);

        // ------= =------
        String keyInfoTagbody = printKeyInfoTagbody(certificate);
        String appHdrTagbody = applyNamespaceInTag(uri, canonicalize(extractTag(APPHDR_TAG, docStringBuilder).getBytes()));
        String documentTagbody = applyNamespaceInTag(uri, canonicalize(extractTag(DOCUMENT_TAG, docStringBuilder).getBytes()));
        String signedInfoTagbody = printSignedInfoTagbody(
                sha256DigestBase64Result(keyInfoTagbody.getBytes()),
                sha256DigestBase64Result(appHdrTagbody.getBytes()),
                sha256DigestBase64Result(documentTagbody.getBytes())
        );
        String signatureValueTagbody = printSignatureValueTagbody(signBase64Result(signedInfoTagbody));
        String signatureTagBody = printOpenAndCloseSignatureTag(
                new StringBuilder()
                        .append(signedInfoTagbody)
                        .append("\n")
                        .append(signatureValueTagbody)
                        .append("\n")
                        .append(keyInfoTagbody)
                        .toString()
        );

        // ------= =------
        String sgntrXmlTag = "<" + SGNTR_TAG + ">";

        int startInsercaoAssinatura = docStringBuilder.indexOf(sgntrXmlTag) + sgntrXmlTag.length();
        docStringBuilder.insert(startInsercaoAssinatura, signatureTagBody);

        // ------= =------
        millisEnd = System.currentTimeMillis();
        System.out.println(docStringBuilder);
        System.out.println(String.format("\n\nTIMES: %d -> (start: %d, end: %d)", (millisEnd - millisStart), millisStart, millisEnd));
    }

    private static String extractUri(StringBuilder tagBody) {
        int start = tagBody.indexOf("xmlns");
        return tagBody.substring(start, tagBody.indexOf(">", start));
    }

    private static String extractTag(String tagname, StringBuilder body) {
        int start = body.indexOf("<" + tagname);
        int end = body.indexOf("</" + tagname, start) + ("</" + tagname + ">").length();
        return body.substring(start, end);
    }

    private static String applyNamespaceInTag(String uri, String tagBody) {
        return applyNamespaceInTag(uri, new StringBuilder(tagBody));
    }

    private static String applyNamespaceInTag(String uri, StringBuilder tagBody) {
        if (uri != null) {
            int start = tagBody.indexOf(">");
            tagBody.insert(start, " " + uri);
            return tagBody.toString();
        }
        return tagBody.toString();
    }

    private static String createDefaultNamespace(String uri) {
        return (uri == null) ? "" : String.format("xmlns=\"%s\"", uri);
    }

    private static String signBase64Result(String payload) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return signBase64Result(payload.getBytes());
    }

    private static String signBase64Result(byte[] payload) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initSign(privateKey);
        rsa.update(payload);
        return Base64.getEncoder().encodeToString(rsa.sign());
    }

    private static String canonicalize(byte[] payload)
            throws InvalidCanonicalizerException, ParserConfigurationException, IOException, SAXException, CanonicalizationException {
        Canonicalizer canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        return new String(canonicalizer.canonicalize(payload), UTF_8);
    }

    private static String sha256DigestBase64Result(byte[] payload) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(payload);
        return Base64.getEncoder().encodeToString(md.digest());
    }

    private static String printKeyInfoTagbody(X509Certificate certificate) {
        final String template = "<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Id=\"%s\">\n"
                + "<ds:X509Data>\n"
                + "<ds:X509IssuerSerial>\n"
                + "<ds:X509IssuerName>%s</ds:X509IssuerName>\n"
                + "<ds:X509SerialNumber>%s</ds:X509SerialNumber>\n"
                + "</ds:X509IssuerSerial>\n"
                + "</ds:X509Data>\n"
                + "</ds:KeyInfo>";
        return String.format(template,
                "KeyInfo",
                certificate.getIssuerDN().getName(),//certificate.getIssuerX500Principal().getName(),
                certificate.getSerialNumber()
        );
    }

    private static String printSignedInfoTagbody(String digestValueKeyInfo, String digestValueAppHdr, String digestValueDocument) {
        final String template = "<ds:SignedInfo>\n"
                + "<ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\n"
                + "<ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/>\n"
                + "<ds:Reference URI=\"#KeyInfo\">\n"
                + "<ds:Transforms>\n"
                + "<ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\n"
                + "</ds:Transforms>\n"
                + "<ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>\n"
                + "<ds:DigestValue>%s</ds:DigestValue>\n"
                + "</ds:Reference>\n"
                + "<ds:Reference URI=\"\">\n"
                + "<ds:Transforms>\n"
                + "<ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/>\n"
                + "<ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\n"
                + "</ds:Transforms>\n"
                + "<ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>\n"
                + "<ds:DigestValue>%s</ds:DigestValue>\n"
                + "</ds:Reference>\n"
                + "<ds:Reference>\n"
                + "<ds:Transforms>\n"
                + "<ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\n"
                + "</ds:Transforms>\n"
                + "<ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>\n"
                + "<ds:DigestValue>%s</ds:DigestValue>\n"
                + "</ds:Reference>\n"
                + "</ds:SignedInfo>";
        return String.format(template, digestValueKeyInfo, digestValueAppHdr, digestValueDocument);
    }

    private static String printOpenAndCloseSignatureTag(String coreTagsOfSignature) {
        return "<ds:Signature>" + coreTagsOfSignature + "</ds:Signature>";
    }

    private static String printSignatureValueTagbody(String signatureValueText) {
        return "<ds:SignatureValue>" + signatureValueText + "</ds:SignatureValue>";
    }

    @BeforeClass
    public static void init() throws Exception {
        Canonicalizer.registerDefaultAlgorithms();
        certificate = getX509CertificateFromFile(USER_DIR + "/src/test/resources/keys/certificate.01.cer");
        privateKey = getRsaPrivateKeyFromPemFile(USER_DIR + "/src/test/resources/keys/private-key.01.key");
        docStringBuilder = new StringBuilder(getFileBody(USER_DIR + "/src/test/resources/spi.1.2/brb-pibr.001.spi.1.0_msg.xml"));
    }

    private static StringBuilder docStringBuilder;
    private static X509Certificate certificate;
    private static PrivateKey privateKey;

    private static final String USER_DIR = System.getProperty("user.dir");

}
