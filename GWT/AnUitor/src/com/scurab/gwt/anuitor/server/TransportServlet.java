package com.scurab.gwt.anuitor.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is simple fake servlet class to cross-site image loading
 * 
 * @author jbruchanov
 * 
 */
public class TransportServlet extends HttpServlet {

    private static final String CONTENT_TYPE = "Content-Type";
    /**
     * 
     */
    private static final long serialVersionUID = 1020491090626232287L;

    public String getContentURL(String path) {
        return String.format("http://%s%s", getDeviceAddress(), path);
    }

    public String getDeviceAddress() {
        return "testanuitor:8081";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp, "GET");
    }      
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp, "POST"); 
    }
    
    private void handleRequest(HttpServletRequest req, HttpServletResponse resp, String method){
        try {
            String qs = req.getQueryString();            
            String newUrl = getContentURL(req.getRequestURI() + (qs == null || qs.length() == 0 ? "" : "?" + req.getQueryString()));
            URL url = new URL(newUrl);
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            String contentType = req.getHeader(CONTENT_TYPE);
            if(contentType != null){
                urlc.setRequestProperty(CONTENT_TYPE, contentType);
            }
            urlc.setRequestMethod(method);
            urlc.setConnectTimeout(15000);
            urlc.setReadTimeout(50000);
            
            urlc.setDoInput(true);
            urlc.setDoOutput(true);
            urlc.connect();                     
            if ("POST".equals(method)) {               
                copy(req.getInputStream(), urlc.getOutputStream());
            }
            copy(urlc.getInputStream(), resp.getOutputStream());                    
        } catch (Exception e) {            
            e.printStackTrace();
        }
    }
    
    private static void copy(InputStream is, OutputStream os) throws IOException{
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[1024];
        int len = is.read(buffer);
        while (len != -1) {
            os.write(buffer, 0, len);
            sb.append(new String(buffer, 0, len));
            len = is.read(buffer);
        }        
        System.out.println(sb.toString());
    }
}


