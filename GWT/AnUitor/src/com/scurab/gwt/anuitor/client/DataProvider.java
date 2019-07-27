package com.scurab.gwt.anuitor.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.scurab.gwt.anuitor.client.model.DataResponseJSO;
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
    private static final String CONFIG = "/config.json";
    private static final String VIEW_PROPERTY = "/viewproperty.json";
    private static final String GROOVY = "/groovy";
    
    public static final int HTTP_OK = 200;
    public static final int HTTP_NOT_FOUND = 404;
    public static final String QRY_PARAM_SCREEN_INDEX = "screenIndex";
    public static final String SCREEN_INDEX_QRY = "?" + QRY_PARAM_SCREEN_INDEX + "=";
    public static final String SCREEN = (DEMO ? SAMPLE_DATA : "") + "/screen.png";
    public static final String SCREEN_SCTRUCTURE = (DEMO ? SAMPLE_DATA : "") + "/screenstructure.json";
    public static final String SCREEN_SCTRUCTURE_SIMPLE = (DEMO ? SAMPLE_DATA : "") + "/screencomponents.json";
    
    public static final String QRY_PARAM_POSITION = "position";
    public static final String QRY_PARAM_PROPERTY = "property";    
    public static final String QRY_MAX_DEPTH = "maxDepth";

    /**
     * Generic callback
     * 
     * @author jbruchanov
     * 
     * @param <T>
     */
    public interface AsyncCallback<T> {

        public void onDownloaded(T result);

        public void onError(Request req, Response res, Throwable t);
    }

    /**
     * Download view tree hierarchy from server
     * 
     * @param callback
     */
    public static void getTreeHierarchy(int screenIndex, final AsyncCallback<ViewNodeJSO> callback) {
        sendRequest(VIEW_TREE_HIERARCHY + SCREEN_INDEX_QRY + screenIndex, callback);
    }

    public static void getResources(int screenIndex, final AsyncCallback<ObjectJSO> callback) {
        sendRequest(RESOURCES + "?" + QRY_PARAM_SCREEN_INDEX + "=" + screenIndex, callback);
    } 

    public static void getResource(int id, int screenIndex, final AsyncCallback<ResourceDetailJSO> callback) {               
        String url = RESOURCE_ID_X + id;
        if (screenIndex >= 0) {
            url += "&" + QRY_PARAM_SCREEN_INDEX + "=" + screenIndex;
        }
        sendRequest(url, callback);
    }
    
    public static void getFiles(String folder, AsyncCallback<JsArray<FSItemJSO>> asyncCallback) {        
        sendRequest(STORAGE + folder, asyncCallback);
    }
    
    public static void getScreens(AsyncCallback<JsArrayString> asyncCallback) {
        sendRequest(SCREENS, asyncCallback);
    }
    
    public static void getConfig(AsyncCallback<JSONValue> callback) {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, CONFIG);
        try {
            builder.sendRequest(null, new ReqJsonCallback(callback));                      
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void getViewProperty(int screen, int position, String property, final AsyncCallback<DataResponseJSO> callback) {
        sendRequest(buildViewPropertyUrl(screen, position, property), callback);
    }
    
    public static void executeGroovyCode(String code, final AsyncCallback<String> callback){
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, GROOVY);
        try {
            builder.setHeader("Content-Type", "text/plain");
            builder.setHeader("Content-Length", Integer.toString(code.length()));
            builder.sendRequest(code, new RequestCallback() {
                
                @Override
                public void onResponseReceived(Request request, Response response) {
                    callback.onDownloaded(response.getText());
                    
                }
                
                @Override
                public void onError(Request request, Throwable exception) {
                    callback.onError(request, null, exception);                    
                }
            });                      
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String buildViewPropertyUrl(int screen, int position, String property) {
        return new StringBuilder(VIEW_PROPERTY)
                .append("?").append(QRY_PARAM_SCREEN_INDEX).append("=").append(screen)
                .append("&").append(QRY_PARAM_POSITION).append("=").append(position)
                .append("&").append(QRY_PARAM_PROPERTY).append("=").append(property)                
                .toString();
    }
    
    /**
     * Send GET request
     * @param url
     * @param callback
     */
    private static <T extends JavaScriptObject> void sendRequest(String url, final AsyncCallback<T> callback){
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        builder.setHeader("Cache-Control", "no-cache");
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
            final int code = response.getStatusCode();
            if (HTTP_OK == code) {
                try {
                    String value = response.getText();                 
                    T t = JsonUtils.safeEval(value);
                    mCallback.onDownloaded(t);
                } catch (Exception e) {
                    mCallback.onError(request, response, e);
                }
            } else if (HTTP_NOT_FOUND == code) {
                mCallback.onError(request, response, new Exception("Not found, Is selected Activity running?"));                                          
            } else {
                mCallback.onError(request, response, new Exception("ErrCode:" + response.getStatusCode()));
            }
        }

        @Override
        public void onError(Request request, Throwable exception) {
            if(mCallback != null){
                mCallback.onError(request, null, exception);
            }
        }
    }
    
    /**
     * Generic Request callback
     * @author jbruchanov
     *
     * @param <T>
     */
    private static class ReqJsonCallback implements RequestCallback {

        private AsyncCallback<JSONValue> mCallback;

        public ReqJsonCallback(AsyncCallback<JSONValue> callback) {
            mCallback = callback;
        }

        @Override
        public void onResponseReceived(Request request, Response response) {
            if (HTTP_OK == response.getStatusCode()) {
                try {
                    String json = response.getText();                 
                    JSONValue t = JSONParser.parseStrict(json);
                    mCallback.onDownloaded(t);
                } catch (Exception e) {
                    mCallback.onError(request, null, e);
                }
            } else {
                mCallback.onError(request, null, new Exception("ErrCode:" + response.getStatusCode()));
            }
        }

        @Override
        public void onError(Request request, Throwable exception) {
            if(mCallback != null){
                mCallback.onError(request, null, exception);
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
