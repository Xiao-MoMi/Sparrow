package net.momirealms.sparrow.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Commons {

    public static String readInput(InputStream inputStream) throws IOException {
        var input = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        inputStream.close();
        return input;
    }
}
