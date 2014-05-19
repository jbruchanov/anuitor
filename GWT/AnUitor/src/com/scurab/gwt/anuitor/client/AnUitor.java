package com.scurab.gwt.anuitor.client;

import com.google.gwt.core.client.EntryPoint;
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
        TestPage tp = new TestPage();
        RootPanel.get().add(tp);
    }
}
