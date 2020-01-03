package com.scurab.android.uitor.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 12:03
 */
public class ShellExecutor {

    public String test(String q) throws IOException {
        Process process = Runtime.getRuntime().exec("screencap -p " + q + "/test.png");
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        StringBuilder sb = new StringBuilder();
        String read = br.readLine();
        while(read != null) {
            //System.out.println(read);
            sb.append(read);
            read =br.readLine();
        }

        read = br2.readLine();
        while(read != null) {
            //System.out.println(read);
            sb.append(read);
            read =br2.readLine();
        }

        return sb.toString();
    }
}
