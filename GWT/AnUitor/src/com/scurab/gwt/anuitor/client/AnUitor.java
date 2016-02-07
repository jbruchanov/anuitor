package com.scurab.gwt.anuitor.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.scurab.gwt.anuitor.client.DataProvider.AsyncCallback;
import com.scurab.gwt.anuitor.client.ui.FileStoragePage;
import com.scurab.gwt.anuitor.client.ui.ResourcesPage;
import com.scurab.gwt.anuitor.client.ui.ScreenPreviewPage;
import com.scurab.gwt.anuitor.client.ui.ThreeDPage;
import com.scurab.gwt.anuitor.client.ui.TreeViewPage;
import com.scurab.gwt.anuitor.client.ui.ViewPropertyPage;
import com.scurab.gwt.anuitor.client.util.PBarHelper;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AnUitor implements EntryPoint {
    
    private ListBox mScreenListBox;
    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {
        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {                
                PBarHelper.hide();
                openScreen(event.getValue());                
            }
        });

        openScreen(History.getToken());
    }      
    
    private static final String SCREEN_INDEX = "_" + DataProvider.QRY_PARAM_SCREEN_INDEX;
    private void openScreen(String screen) {
        boolean updateHistory = true;
        IsWidget toOpen = null;
        int screenIndex = hasIndexInToken() ? getScreenIndexFromToken() : getScreenIndex();        
        if(hasIndexInToken()){
            screen = screen.substring(0, screen.indexOf(SCREEN_INDEX));
        }
        if ("ScreenPreview".equals(screen)) {            
            toOpen = new ScreenPreviewPage(screenIndex);            
        } else if ("3D".equals(screen)) {
            toOpen = new ThreeDPage(screenIndex);
        } else if ("ViewHierarchy".equals(screen)) {
            toOpen = new TreeViewPage(screenIndex);
        } else if ("Resources".equals(screen)) {
            if(sorryDemoNotSupported()){return;}
            toOpen = new ResourcesPage();
        } else if ("FileStorage".equals(screen)) {
            if(sorryDemoNotSupported()){return;}
            toOpen = new FileStoragePage();
        } else if ("Windows".equals(screen)) {
            Window.open(DataProvider.SCREEN_SCTRUCTURE, "_blank", "");
            return;
        } else if ("Screenshot".equals(screen)) {
            Window.open(DataProvider.SCREEN + DataProvider.SCREEN_INDEX_QRY + getScreenIndex(), "_blank", "");
            return;
        } else if (screen != null && screen.startsWith("ViewProperty")) {          
            Map<String, String> queryString = buildHashParameterMap();
            int position = Integer.parseInt(queryString.get(DataProvider.QRY_PARAM_POSITION));
            String property = queryString.get(DataProvider.QRY_PARAM_PROPERTY);
            toOpen = new ViewPropertyPage(screenIndex, position, property);
            updateHistory = false;
        } else {
            screen = "";
            toOpen = createSelectionPane();
            screenIndex = -1;
        }

        openWidget(screen, screenIndex, toOpen, updateHistory);
    }

    private CellPanel createSelectionPane() {
        VerticalPanel hp = new VerticalPanel();
        hp.setWidth("100%");
        hp.setStyleName("mainScreenContent", true);        
        hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            
        hp.add(mScreenListBox = createListBox());
        hp.add(createButton("ScreenPreview"));
        hp.add(createButton("3D"));
        hp.add(createButton("ViewHierarchy"));
        hp.add(createButton("Resources"));
        hp.add(createButton("FileStorage"));
        hp.add(createButton("Windows"));
        hp.add(createButton("Screenshot"));

        return hp;
    }
    
    private Button createButton(String name){
        Button btn = new Button(name);
        btn.setStyleName("mainScreenButton", true);
        btn.addClickHandler(mClickHandler);
        return btn;
    }
    
    private ListBox createListBox(){
        final ListBox lb = new ListBox(false);
        lb.setEnabled(false);
        lb.addItem("Loading", (String)null);
        DataProvider.getScreens(new AsyncCallback<JsArrayString>() {            
            @Override public void onError(Request r, Throwable t) {
                Window.alert("Unable to load screens:" + t.getMessage());
            }            
            @Override
            public void onDownloaded(JsArrayString result) {
                lb.clear();
                lb.setEnabled(true);
                for (int i = 0, n = result.length(); i < n; i++) {                   
                   String value = result.get(i);                   
                   lb.addItem(value, String.valueOf(i));                   
                }
                lb.setSelectedIndex(lb.getItemCount() - 1);
            }
        });
        return lb;
    }

    private IsWidget mLastScreen;

    private void openWidget(String v, int index, IsWidget w, boolean updateHistory) {
        if (updateHistory) {
            History.newItem(v + (index < 0 ? "" : SCREEN_INDEX + index), false);// just add index if it's valid
        }
        if (mLastScreen != null) {
            RootLayoutPanel.get().remove(mLastScreen);
        }
        mLastScreen = w;
        RootLayoutPanel.get().add(mLastScreen);
    }
        
    private boolean hasIndexInToken(){
        String token = History.getToken();
        return token != null && token.contains(SCREEN_INDEX);
    }
    
    private int getScreenIndexFromToken(){
        String token = History.getToken();
        int start = token.indexOf(SCREEN_INDEX) + SCREEN_INDEX.length();
        int end = Math.min(token.indexOf("&", start), token.indexOf("?", start));
        if (end < 0) {            
            end = token.length();
        }
        String index = token != null ? token.substring(start, end) : null;
        return Integer.parseInt(index);
    }
    
    private int getScreenIndex(){
       String v = mScreenListBox == null ? "0" : mScreenListBox.getValue(mScreenListBox.getSelectedIndex());
       return v == null ? 0 : Integer.parseInt(v);
    }
    
    private boolean sorryDemoNotSupported() {
        if (DataProvider.DEMO) {
            Window.alert("Sorry, not supported in DEMO!");
        }
        return DataProvider.DEMO;
    }

    private ClickHandler mClickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            openScreen(((Button) event.getSource()).getText());
        }
    };
    
    private static Map<String, String> buildHashParameterMap() {
        String historyToken = History.getToken();
        historyToken = historyToken.substring(historyToken.indexOf("?") + 1);
        Map<String, String> paramMap = new HashMap<String, String>();
        if (historyToken != null && historyToken.length() > 1) {
            for (String kvPair : historyToken.split("&")) {
                String[] kv = kvPair.split("=", 2);
                if (kv.length > 1) {
                    paramMap.put(kv[0], URL.decodeQueryString(kv[1]));
                } else {
                    paramMap.put(kv[0], "");
                }
            }
        }

        return paramMap;
    }
}
