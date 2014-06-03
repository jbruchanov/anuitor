package com.scurab.gwt.anuitor.client.style;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface TreeViewResources extends ClientBundle {
    public static final TreeViewResources INSTANCE = GWT.create(TreeViewResources.class);

    @Source("TreeViewResources.css")
    public TreeViewStyle css();
}