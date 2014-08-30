package com.scurab.gwt.anuitor.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;

public class ViewNodeClickEvent extends GwtEvent<ViewNodeClickEventHandler> {        
    
    public static Type<ViewNodeClickEventHandler> TYPE = new Type<ViewNodeClickEventHandler>();
    
    private ViewNodeJSO mView;
    
    public ViewNodeClickEvent(ViewNodeJSO view) {
        mView = view;
    }
    
    public ViewNodeJSO getView() {
        return mView;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ViewNodeClickEventHandler> getAssociatedType() {        
        return TYPE;
    }

    @Override
    protected void dispatch(ViewNodeClickEventHandler handler) {        
        handler.onViewNodeClick(this);
    }
}
