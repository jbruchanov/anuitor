package com.scurab.android.anuitor.nanoplugin;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import com.scurab.android.anuitor.extract2.BaseExtractor;
import com.scurab.android.anuitor.extract2.BaseViewExtractorKt;
import com.scurab.android.anuitor.extract2.DetailExtractor;
import com.scurab.android.anuitor.reflect.ActivityThreadReflector;
import com.scurab.android.anuitor.reflect.WindowManager;
import com.scurab.android.anuitor.tools.HttpTools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static com.scurab.android.anuitor.tools.HttpTools.MimeType.APP_JSON;

/**
 * Created by jbruchanov on 24/06/2014.
 */
public class ScreenStructurePlugin extends BasePlugin {

    public static final String FILE = "screenstructure.json";
    public static final String PATH = "/" + FILE;

    private WindowManager mWindowManager;
    private ActivityThreadReflector mActivityThread;

    public ScreenStructurePlugin(WindowManager windowManager) {
        mWindowManager = windowManager;
        mActivityThread = new ActivityThreadReflector();
    }

    @Override
    public String[] files() {
        return new String[]{FILE};
    }

    @Override
    public String mimeType() {
        return HttpTools.MimeType.APP_JSON;
    }

    @Override
    public boolean canServeUri(String uri, File rootDir) {
        return PATH.equals(uri);
    }

    @SuppressWarnings("unchecked")
    @Override
    public NanoHTTPD.Response serveFile(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        String[] viewRootNames = mWindowManager.getViewRootNames();
        List<HashMap<String, Object>> resultDataSet = new ArrayList<>();

        for (String rootName : viewRootNames) {
            View v = mWindowManager.getRootView(rootName);
            Context c = v.getContext();
            HashMap<String, Object> data = new HashMap<>();
            data.put("RootName", rootName);
            resultDataSet.add(data);
            Activity activity = getActivity(v);
            if (activity == null && c instanceof Activity) {
                activity = (Activity) c;
            }

            if (activity != null) {
                fillActivity(activity, data);
            } else {
                BaseExtractor extractor = DetailExtractor.getExtractor(v);
                extractor.fillValues(v, data, null);
                if (c instanceof ContextWrapper) {
                    c = ((ContextWrapper) c).getBaseContext();
                    if (c instanceof Activity) {
                        HashMap<String, Object> sub = new HashMap<>();
                        data.put("OwnerActivity", sub);
                        fillActivity((Activity) c, sub);
                    }
                }
            }
            data.remove(BaseViewExtractorKt.OWNER);
        }

        Collections.reverse(resultDataSet);//stack order
        String json =  JSON.toJson(resultDataSet);
        NanoHTTPD.Response response = new OKResponse(APP_JSON, new ByteArrayInputStream(json.getBytes()));
        return response;
    }

    private void fillActivity(@Nullable Activity activity, HashMap<String, Object> data){
        if (activity != null) {
            BaseExtractor extractor = DetailExtractor.getExtractor(activity.getClass());
            extractor.fillValues(activity, data, null);
        }
    }

    @Nullable
    private Activity getActivity(@NonNull View view) {
        for (Activity activity : mActivityThread.getActivities()) {
            if (activity.getWindow().getDecorView().getRootView() == view.getRootView()) {
                return activity;
            }
        }
        return null;
    }
}
