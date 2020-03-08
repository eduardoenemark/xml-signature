package org.apache.xml.security.spi;

import org.apache.xml.security.Init;
import org.junit.Test;

/**
 * 
 */
public class HsmRsaSha256SignatureSpiTest {

    @Test
    public void init() throws Exception {
        Init.init();
        HsmRsaSha256SignatureSpi.register();
    }
    
}
