package org.apache.xml.security.hsm;

import static org.apache.xml.security.test.SupportTest.getFileBody;
import static org.apache.xml.security.util.Documents.newDocument;
import org.junit.Test;
import org.w3c.dom.Document;

/*
 *
 *
 */
public class HsmScenarioTest {

    @Test
    public void pibr001Spi1_0Sign() throws Exception {
        final String pibr001String = getFileBody(
                System.getProperty("user.dir") + "/src/test/resources/spi.1.2/pibr.001.spi.1.0_msg.xml"
        );
        final Document pibr001Doc = newDocument(pibr001String);

    }

}
