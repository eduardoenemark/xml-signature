package org.apache.xml.security.hsm;

import java.io.IOException;
import java.io.InputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.IOUtils;
import static org.apache.xml.security.hsm.HsmScenarioTest.Documents.newDocument;
import static org.apache.xml.security.hsm.HsmScenarioTest.SupportTest.getFileBody;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author eduardo
 */
public class HsmScenarioTest {

    @Test
    public void pibr001Spi1_0Sign() throws Exception {
        final String pibr001String = getFileBody(
                System.getProperty("user.dir") + "/src/test/resources/spi.1.2/pibr.001.spi.1.0_msg.xml"
        );
        final Document pibr001Doc = newDocument(pibr001String);

    }

    public static final class XmlSecurity {

        public void lookupByKeyPair() {
        }
    }

    public static final class Documents {

        public static Document newDocument(String documentString)
                throws SAXException, IOException {
            return newDocument(IOUtils.toInputStream(documentString, UTF_8));
        }

        public static Document newDocument(InputStream inputStream)
                throws SAXException, IOException {
            return documentBuilder.parse(inputStream);
        }

        public static Document newDocument() {
            return documentBuilder.newDocument();
        }

        static {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            try {
                documentBuilder = documentBuilderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
                throw new RuntimeException(ex);
            }
        }

        private static final DocumentBuilder documentBuilder;
    }

    public static final class SupportTest {

        public static String getFileBody(final String filepath) throws IOException {
            return new String(Files.readAllBytes(Paths.get(filepath)), UTF_8);
        }
    }
}
