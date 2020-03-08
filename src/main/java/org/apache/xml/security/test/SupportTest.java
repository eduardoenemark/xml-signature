package org.apache.xml.security.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;

/*
 *
 *
 */
public final class SupportTest {

    public static String getFileBody(final String filepath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filepath)), UTF_8);
    }

    public static X509Certificate getX509CertificateFromFile(final String filepath)
            throws CertificateException, FileNotFoundException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        FileInputStream is = new FileInputStream(filepath);
        return (X509Certificate) fact.generateCertificate(is);
    }

    public static PrivateKey getRsaPrivateKeyFromPemFile(final String filepath)
            throws IOException, GeneralSecurityException {
        String privateKeyPEM = new String(Files.readAllBytes(Paths.get(filepath)));
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
        byte[] encoded = Base64.decodeBase64(privateKeyPEM); //Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
        return privKey;
    }
}
