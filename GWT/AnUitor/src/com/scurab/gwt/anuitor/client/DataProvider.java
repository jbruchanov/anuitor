package com.scurab.gwt.anuitor.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;

/**
 * Base class for downloading JSON data from server side
 * @author jbruchanov
 *
 */
public class DataProvider {

    private static final String VIEW_TREE_HIERARCHY = "/viewhierarchy.json";
    private static final int HTTP_OK = 200;
    
    /**
     * Generic callback
     * @author jbruchanov
     *
     * @param <T>
     */
    public interface AsyncCallback<T extends JavaScriptObject> {
        
        public void onDownloaded(T result);

        public void onError(Request r, Throwable t);
    }

    /**
     * Download view tree hierarchy from server
     * @param callback
     */
    public static void getTreeHierarchy(final AsyncCallback<ViewNodeJSO> callback) {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, VIEW_TREE_HIERARCHY);
        
        try {
            
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (HTTP_OK == response.getStatusCode()) {
                        String value = response.getText();
                        ViewNodeJSO vn = JsonUtils.safeEval(value);
                        callback.onDownloaded(vn);
                    } else {
                        callback.onError(request, new Exception("ErrCode:" + response.getStatusCode()));
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    callback.onError(request, exception);
                }
                
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
