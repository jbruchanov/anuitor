package com.scurab.android.anuitor.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by JBruchanov on 05/02/2016.
 */
public class LogCatProvider {

    public static final String TYPE_MAIN = "main";
    public static final String TYPE_EVENTS = "events";
    public static final String TYPE_RADIO = "radio";

    public static String dumpLogcat(String type) {
        if (type == null) {
            type = TYPE_MAIN;
        }
        StringBuilder log = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(String.format("logcat -d -b %s -v time *:V", type));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line).append("\n");
            }
        } catch (IOException e) {
            log.append(e.getMessage());
            log.append(getStackTrace(e));
        }
        return log.toString();
    }

    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
