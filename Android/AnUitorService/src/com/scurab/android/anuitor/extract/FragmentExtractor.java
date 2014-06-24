package com.scurab.android.anuitor.extract;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;

import com.scurab.android.anuitor.hierarchy.IdsHelper;

import java.util.HashMap;

/**
 * Created by jbruchanov on 24/06/2014.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FragmentExtractor extends BaseExtractor<Fragment> {

    private BundleExtractor mBundleExtractor = new BundleExtractor();

    @Override
    public HashMap<String, Object> fillValues(Fragment fragment, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        data.put("Type", fragment.getClass().getCanonicalName());
        data.put("IDi", fragment.getId());
        data.put("IDs", IdsHelper.getNameForId(fragment.getId()));
        data.put("Tag", fragment.getTag());

        data.put("TargetFragment", fragment.getTargetFragment() != null ? String.valueOf(fragment.getTargetFragment()) : null);
        data.put("TargetRequestCode", fragment.getTargetRequestCode());
//            data.put("HasOptionsMenu", fragment.hasOptionsMenu());
        data.put("IsAdded", fragment.isAdded());

        data.put("IsHidden", fragment.isHidden());
        data.put("IsInLayout", fragment.isInLayout());
//            data.put("IsMenuVisible", fragment.isMenuVisible());
        data.put("IsRemoving", fragment.isRemoving());
        data.put("IsResumed", fragment.isResumed());
        data.put("IsVisible", fragment.isVisible());
        data.put("Arguments", mBundleExtractor.fillValues(fragment.getArguments(), new HashMap<String, Object>(), data));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            data.put("IsDetached", fragment.isDetached());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            data.put("ParentFragment", String.valueOf(fragment.getParentFragment()));
        }
        return data;
    }
}
