package com.scurab.gwt.anuitor.client.util;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public class PBarHelper {

    private static PopupPanel sPopupPanel;

    public static void show() {
        hide();
        sPopupPanel = new PopupPanel(false, false);
        sPopupPanel.setWidget(new Image("/loader.gif"));
        //sPopupPanel.center();        
        int left = (Window.getClientWidth()) >> 1;        
        sPopupPanel.setPopupPosition(left, 10);
        sPopupPanel.show();
    }

    public static void hide() {
        if (sPopupPanel != null) {
            sPopupPanel.hide();
            sPopupPanel = null;
        }
    }
}
