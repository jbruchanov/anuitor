package com.scurab.gwt.anuitor.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.scurab.gwt.anuitor.client.model.FSItemJSO;
import com.scurab.gwt.anuitor.client.model.ObjectJSO;
import com.scurab.gwt.anuitor.client.model.ResourceDetailJSO;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;

/**
 * Base class for downloading JSON data from server side
 * 
 * @author jbruchanov
 * 
 */
public class DataProvider {

    /* Little different pathes for demo */
    static final boolean DEMO = false;    
    
    private static final String SAMPLE_DATA = "sampledata";
    private static final String VIEW_TREE_HIERARCHY = (DEMO ? SAMPLE_DATA : "") + "/viewhierarchy.json";    
    private static final String RESOURCES = "/resources.json";
    private static final String RESOURCE_ID_X = "/resources.json?id=";
    private static final String STORAGE = "/storage.json?path=";
    private static final String SCREENS = "/screens.json";
    private static final int HTTP_OK = 200;
    
    public static final String SCREEN_INDEX = "screenIndex";
    public static final String SCREEN_INDEX_QRY = "?" + SCREEN_INDEX + "=";
    public static final String SCREEN = (DEMO ? SAMPLE_DATA : "") + "/screen.png";
    public static final String SCREEN_SCTRUCTURE = (DEMO ? SAMPLE_DATA : "") + "/screenstructure.json";     

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
    public static void getTreeHierarchy(int screenIndex, final AsyncCallback<ViewNodeJSO> callback) {
        sendRequest(VIEW_TREE_HIERARCHY + SCREEN_INDEX_QRY + screenIndex, callback);
    }

    public static void getResources(final AsyncCallback<ObjectJSO> callback) {
        sendRequest(RESOURCES, callback);
    }

    public static void getResource(int id, final AsyncCallback<ResourceDetailJSO> callback) {        
        sendRequest(RESOURCE_ID_X + id, callback);
    }
    
    public static void getFiles(String folder, AsyncCallback<JsArray<FSItemJSO>> asyncCallback) {        
        sendRequest(STORAGE + folder, asyncCallback);
    }
    
    public static void getScreens(AsyncCallback<JsArrayString> asyncCallback) {
        sendRequest(SCREENS, asyncCallback);
    }
    
    public static void getViewProperty(final AsyncCallback<ObjectJSO> callback) {
        sendRequest(RESOURCES, callback);
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
    
    /**
     * Return proper link for image view
     * @param position
     * @return
     */
    public static String getViewImageLink(int position, int screenIndex){
        return DEMO
                ? SAMPLE_DATA + "/imageview_" + position + ".png"
                : "/view.png?position=" + position + "&screenIndex=" + screenIndex;
                
    }
}
