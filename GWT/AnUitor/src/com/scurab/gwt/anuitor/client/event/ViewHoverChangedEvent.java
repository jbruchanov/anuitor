package com.scurab.gwt.anuitor.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;

public class ViewHoverChangedEvent extends GwtEvent<ViewHoverChangedEventHandler> {

    public static Type<ViewHoverChangedEventHandler> TYPE = new Type<ViewHoverChangedEventHandler>();
    
    private ViewNodeJSO mView;
    private boolean mIsHovered;
    
    public ViewHoverChangedEvent(ViewNodeJSO view, boolean hover) {
        mView = view;
        mIsHovered = hover;
    }
    
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ViewHoverChangedEventHandler> getAssociatedType() {     
        return TYPE;
    }

    @Override
    protected void dispatch(ViewHoverChangedEventHandler handler) {
        handler.onViewHoverChanged(this);
    }
    
    public ViewNodeJSO getView() {
        return mView;
    }
    
    public boolean isMeshHovered() {
        return mIsHovered;
    }
}
