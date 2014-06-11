package com.scurab.android.anuitor.extract;

import android.os.Build;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by jbruchanov on 06/06/2014.
 */
public class WebViewExtrator extends ViewGroupExtractor {


    @Override
    public HashMap<String, Object> fillValues(View v, final HashMap<String, Object> data, final HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        final Object lock = new Object();
        final WebView wv = (WebView) v;

        //few get methods must be called in same thread as WebView lives in
        synchronized (lock) {
            wv.post(new Runnable() {
                @Override
                public void run() {
                    data.put("CanGoBack", wv.canGoBack());
                    data.put("CanGoForward", wv.canGoForward());
                    data.put("OriginalURL", wv.getOriginalUrl());
                    data.put("URL", wv.getUrl());
                    data.put("Title", wv.getTitle());
                    data.put("Progress", wv.getProgress());

                    WebSettings settings = wv.getSettings();
                    fillSettings(settings, data);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        data.put("CanZoomIn", wv.canZoomIn());
                        data.put("CanZoomOut", wv.canZoomOut());
                        data.put("IsPrivateBrowsingEnabled", wv.isPrivateBrowsingEnabled());
                    }

                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            });

            try {
                lock.wait(1000);//wait a sec for getting values from webviews
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * Fill settings from WebSettings, this is running in MainThread, so be careful about speed
     * @param sets
     * @param data
     */
    protected void fillSettings(WebSettings sets, HashMap<String, Object> data) {
        if (sets == null) {
            return;
        }

        Method[] methods = WebSettings.class.getDeclaredMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("get") && method.getParameterTypes().length == 0) {
                method.setAccessible(true);
                try {
                    Object o = method.invoke(sets);
                    data.put("WS_" + name.replace("get", ""), o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
