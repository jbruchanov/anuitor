package com.scurab.android.anuitor.tools;

import java.io.IOException;
import java.io.InputStream;

public class IOUtils {

    public static final int BUFFER_SIZE = 8 * 1024;

    public static String readAsString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();

        byte[] buffer = new byte[BUFFER_SIZE];
        int len = -1;
        while ((len = is.read(buffer, 0, buffer.length)) != -1) {
            sb.append(new String(buffer, 0, len));
        }
        return sb.toString();
    }
}
