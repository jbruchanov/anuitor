package com.scurab.gwt.anuitor.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.scurab.gwt.anuitor.client.DataProvider.AsyncCallback;
import com.scurab.gwt.anuitor.client.model.FSItemJSO;
import com.scurab.gwt.anuitor.client.model.ResourceDetailJSO;
import com.scurab.gwt.anuitor.client.model.ResourcesJSO;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;

/**
 * Base class for downloading JSON data from server side
 * 
 * @author jbruchanov
 * 
 */
public class DataProvider {

    private static final String VIEW_TREE_HIERARCHY = "/viewhierarchy.json";
    private static final String RESOURCES = "/resources.json";
    private static final String RESOURCE_ID_X = "/resources.json?id=";
    private static final String STORAGE = "/storage.json?path=";
    private static final int HTTP_OK = 200;

    /**
     * Generic callback
     * 
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
     * 
     * @param callback
     */
    public static void getTreeHierarchy(final AsyncCallback<ViewNodeJSO> callback) {
        sendRequest(VIEW_TREE_HIERARCHY, callback);
    }

    public static void getResources(final AsyncCallback<ResourcesJSO> callback) {
        sendRequest(RESOURCES, callback);
    }

    public static void getResource(int id, final AsyncCallback<ResourceDetailJSO> callback) {        
        sendRequest(RESOURCE_ID_X + id, callback);
    }
    
    public static void getFiles(String folder, AsyncCallback<JsArray<FSItemJSO>> asyncCallback) {        
        sendRequest(STORAGE + folder, asyncCallback);
    }   
    
    /**
     * Send GET request
     * @param url
     * @param callback
     */
    private static <T extends JavaScriptObject> void sendRequest(String url, final AsyncCallback<T> callback){
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.sendRequest(null, new ReqCallback<T>(callback));                      
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    /**
     * Generic Request callback
     * @author jbruchanov
     *
     * @param <T>
     */
    private static class ReqCallback<T extends JavaScriptObject> implements RequestCallback {

        private AsyncCallback<T> mCallback;

        public ReqCallback(AsyncCallback<T> callback) {
            mCallback = callback;
        }

        @Override
        public void onResponseReceived(Request request, Response response) {
            if (HTTP_OK == response.getStatusCode()) {
                try {
                    String value = response.getText();
                    T t = JsonUtils.safeEval(value);
                    mCallback.onDownloaded(t);
                } catch (Exception e) {
                    mCallback.onError(request, e);
                }
            } else {
                mCallback.onError(request, new Exception("ErrCode:" + response.getStatusCode()));
            }
        }

        @Override
        public void onError(Request request, Throwable exception) {
            if(mCallback != null){
                mCallback.onError(request, exception);
            }
        }
    }
}
