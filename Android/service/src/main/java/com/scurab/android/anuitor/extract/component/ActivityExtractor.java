package com.scurab.android.anuitor.extract.component;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import com.scurab.android.anuitor.extract.BaseExtractor;
import com.scurab.android.anuitor.extract.DetailExtractor;
import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.reflect.FragmentManagerReflector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jbruchanov on 24/06/2014.
 */
public class ActivityExtractor extends BaseExtractor<Activity> {

    public ActivityExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(Activity activity, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        BaseExtractor<Intent> extractor = DetailExtractor.getExtractor(Intent.class);

        data.put("Type", activity.getClass().getName());
        data.put("Intent", extractor.fillValues(activity.getIntent(), new HashMap<String, Object>(), data));
        data.put("StringValue", String.valueOf(activity));
        data.put("Parent:", activity.getParent() != null ? activity.getParent() : null);
        data.put("TaskID", activity.getTaskId());
        data.put("Title", activity.getTitle());
        data.put("TitleColor", getStringColor(activity.getTitleColor()));
        data.put("HasWindowFocus", activity.hasWindowFocus());
        data.put("IsChild", activity.isChild());
        data.put("IsDestroyed", activity.isFinishing());
        data.put("IsTaskRoot", activity.isTaskRoot());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            android.app.FragmentManager fragmentManager = activity.getFragmentManager();
            List<android.app.Fragment> fragments = new FragmentManagerReflector(fragmentManager).getFragments();
            List<HashMap<String, Object>> fragmentsData = handleFragments(fragments, new HashMap<String, Object>());
            data.put("Fragments", fragmentsData);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            data.put("ParentActivityIntent", extractor.fillValues(activity.getParentActivityIntent(), new HashMap<String, Object>(), data));
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            data.put("IsDestroyed", activity.isDestroyed());
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            data.put("IsImmersive", activity.isImmersive());
        }

        return data;
    }

    protected HashMap<String, Object> extractFragments(android.app.Fragment fragment, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        if (fragment != null) {
            BaseExtractor<android.app.Fragment> extractor = DetailExtractor.getExtractor(android.app.Fragment.class);
            extractor.fillValues(fragment, data, contextData);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                android.app.FragmentManager childFragmentManager = fragment.getChildFragmentManager();

                List<android.app.Fragment> childFragments = new FragmentManagerReflector(childFragmentManager).getFragments();
                if (childFragments != null && childFragments.size() > 0) {
                    List<HashMap<String, Object>> fragmentsData = handleFragments(childFragments, new HashMap<String, Object>());
                    data.put("ChildFragments", fragmentsData);
                }
            }
        }
        return data;
    }

    protected List<HashMap<String, Object>> handleFragments(List<android.app.Fragment> fragments, HashMap<String, Object> data) {
        List<HashMap<String, Object>> fragmentsData = new ArrayList<>();
        if(fragments != null) {
            for (android.app.Fragment fragment : fragments) {
                fragmentsData.add(extractFragments(fragment, new HashMap<String, Object>(), data));
            }
        }
        return fragmentsData;
    }
}
