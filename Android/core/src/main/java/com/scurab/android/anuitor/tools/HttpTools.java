package com.scurab.android.anuitor.tools;

import java.io.File;
import java.util.HashMap;

import static com.scurab.android.anuitor.tools.FileSystemTools.getExtension;
import static com.scurab.android.anuitor.tools.HttpTools.MimeType.APP_OCTET;

/**
 * Created by jbruchanov on 27/05/2014.
 */
public class HttpTools {


    public static HashMap<String, String> parseQueryString(String query) {
        HashMap<String, String> result = new HashMap<>();
        if (isEmpty(query)) {
            return result;
        }

        String[] items = query.split("&");
        for (String item : items) {
            String[] kv = item.split("=");
            String key = kv[0];
            String value = kv.length > 1 ? kv[1] : "";
            result.put(key, value);
        }
        return result;
    }

    /**
     * Convert int value into hex #AARRGGBB format
     *
     * @param value
     * @return
     */
    public static String getStringColor(int value) {
        return String.format("#%08X", (0xFFFFFFFF & value));
    }

    /**
     * Return mime type based on file extension<br/>
     * Defined myme types are {@link MimeType}
     *
     * @param f
     * @return
     */
    public static String getMimeType(File f) {
        String ext = getExtension(f);
        if ("json".equals(ext)) {
            return MimeType.APP_JSON;
        } else if ("xml".equals(ext)) {
            return MimeType.APP_XML;
        } else if ("txt".equals(ext)) {
            return MimeType.TEXT_PLAIN;
        } else if ("jpeg".equals(ext) || "jpg".equals(ext)) {
            return MimeType.IMAGE_JPG;
        } else if ("png".equals(ext)) {
            return MimeType.IMAGE_PNG;
        } else if ("gif".equals(ext)) {
            return MimeType.IMAGE_GIF;
        } else {
            return APP_OCTET;
        }
    }

    public static final class MimeType {
        public static final String APP_JSON = "application/json";
        public static final String APP_XML = "application/xml";
        public static final String APP_OCTET = "application/octet-stream";
        public static final String TEXT_PLAIN = "text/plain";
        public static final String IMAGE_PNG = "image/png";
        public static final String IMAGE_JPG = "image/jpeg";
        public static final String IMAGE_GIF = "image/gif";
    }

    private static boolean isEmpty(String value){
        return value == null || value.length() == 0;
    }
}
