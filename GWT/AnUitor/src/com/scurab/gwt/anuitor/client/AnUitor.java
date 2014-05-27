package com.scurab.gwt.anuitor.client;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import thothbot.parallax.core.client.RenderingPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.scurab.gwt.anuitor.client.ui.TestPage;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AnUitor implements EntryPoint {
    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {        
//        TestPage tp = new TestPage();
//        RootPanel.get().add(tp);
        RenderingPanel renderingPanel = new RenderingPanel();
//        renderingPanel.setWidth("200px");
//        renderingPanel.setHeight("200px");
        // Background color
        renderingPanel.setBackground(0x111111);
        renderingPanel.setAnimatedScene(new MyScene());

        RootLayoutPanel.get().add(renderingPanel);
    }
}
