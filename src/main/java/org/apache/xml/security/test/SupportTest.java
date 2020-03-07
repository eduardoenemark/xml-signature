package org.apache.xml.security.test;

import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.Paths;

/*
 *
 *
 */
public final class SupportTest {

    public static String getFileBody(final String filepath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filepath)), UTF_8);
    }
}
