package com.scurab.android.anuitor.extract;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;

import com.scurab.android.anuitor.reflect.FragmentManagerReflector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jbruchanov on 24/06/2014.
 */
public class ActivityExtractor extends BaseExtractor<Activity> {

    FragmentExtractor mFragmentExtractor = new FragmentExtractor();
    IntentExtractor mIntentExtractor = new IntentExtractor();

    @Override
    public HashMap<String, Object> fillValues(Activity activity, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        data.put("Type", activity.getClass().getCanonicalName());
        data.put("Intent", mIntentExtractor.fillValues(activity.getIntent(), new HashMap<String, Object>(), data));
        data.put("StringValue", String.valueOf(activity));
        data.put("Parent", activity.getParent() != null ? activity.getParent() : null);
        data.put("TaskID", activity.getTaskId());
        data.put("Title", activity.getTitle());
        data.put("TitleColor", getStringColor(activity.getTitleColor()));
        data.put("HasWindowFocus", activity.hasWindowFocus());
        data.put("IsChild", activity.isChild());
        data.put("IsDestroyed", activity.isFinishing());
        data.put("IsTaskRoot", activity.isTaskRoot());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            FragmentManager fragmentManager = activity.getFragmentManager();
            List<Fragment> fragments = new FragmentManagerReflector(fragmentManager).getFragments();
            List<HashMap<String, Object>> fragmentsData = handleFragments(fragments, new HashMap<String, Object>());
            data.put("Fragments", fragmentsData);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            data.put("ParentActivityIntent", mIntentExtractor.fillValues(activity.getParentActivityIntent(), new HashMap<String, Object>(), data));
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            data.put("IsDestroyed", activity.isDestroyed());
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            data.put("IsImmersive", activity.isImmersive());
        }

        return data;
    }

    protected HashMap<String, Object> extractFragments(Fragment fragment, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        if (fragment != null) {
            mFragmentExtractor.fillValues(fragment, data, contextData);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                FragmentManager childFragmentManager = fragment.getChildFragmentManager();

                List<Fragment> childFragments = new FragmentManagerReflector(childFragmentManager).getFragments();
                if (childFragments != null && childFragments.size() > 0) {
                    List<HashMap<String, Object>> fragmentsData = handleFragments(childFragments, new HashMap<String, Object>());
                    data.put("ChildFragments", fragmentsData);
                }
            }
        }
        return data;
    }

    protected List<HashMap<String, Object>> handleFragments(List<Fragment> fragments, HashMap<String, Object> data) {
        List<HashMap<String, Object>> fragmentsData = new ArrayList<HashMap<String, Object>>();
        if(fragments != null) {
            for (Fragment fragment : fragments) {
                fragmentsData.add(extractFragments(fragment, new HashMap<String, Object>(), data));
            }
        }
        return fragmentsData;
    }
}
