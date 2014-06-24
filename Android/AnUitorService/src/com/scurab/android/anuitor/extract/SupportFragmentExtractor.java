package com.scurab.android.anuitor.extract;

import android.support.v4.app.Fragment;

import com.scurab.android.anuitor.hierarchy.IdsHelper;
import com.scurab.android.anuitor.reflect.SupportFragmentReflector;

import java.util.HashMap;

/**
 * Created by jbruchanov on 24/06/2014.
 */
public class SupportFragmentExtractor extends BaseExtractor<Fragment> {

    private BundleExtractor mBundleExtractor = new BundleExtractor();

    @Override
    public HashMap<String, Object> fillValues(Fragment fragment, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        data.put("Type", fragment.getClass().getCanonicalName());
        data.put("IDi", fragment.getId());
        data.put("IDs", IdsHelper.getNameForId(fragment.getId()));
        data.put("Tag", fragment.getTag());
        data.put("ParentFragment", fragment.getParentFragment() != null ? String.valueOf(fragment.getParentFragment()) : null);
        data.put("TargetFragment", fragment.getTargetFragment() != null ? String.valueOf(fragment.getTargetFragment()) : null);
        data.put("TargetRequestCode", fragment.getTargetRequestCode());
        data.put("HasOptionsMenu", fragment.hasOptionsMenu());
        data.put("IsAdded", fragment.isAdded());
        data.put("IsDetached", fragment.isDetached());
        data.put("IsHidden", fragment.isHidden());
        data.put("IsInLayout", fragment.isInLayout());
        data.put("IsMenuVisible", fragment.isMenuVisible());
        data.put("IsRemoving", fragment.isRemoving());
        data.put("IsResumed", fragment.isResumed());
        data.put("IsVisible", fragment.isVisible());
        data.put("Arguments", mBundleExtractor.fillValues(fragment.getArguments(), new HashMap<String, Object>(), data));

        SupportFragmentReflector sfr = new SupportFragmentReflector(fragment);
        data.put("State", Translator.fragmentState(sfr.getState()));
        data.put("Who", sfr.getWho());
        data.put("Index", sfr.getIndex());
        return data;
    }
}
