package com.scurab.android.anuitor.extract.component;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;

import com.scurab.android.anuitor.extract.BaseExtractor;
import com.scurab.android.anuitor.extract.DetailExtractor;
import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.hierarchy.IdsHelper;
import com.scurab.android.anuitor.reflect.FragmentReflector;

import java.util.HashMap;

/**
 * Created by jbruchanov on 24/06/2014.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FragmentExtractor extends BaseExtractor<Fragment> {

    public FragmentExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(Fragment fragment, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        data.put("Type", fragment.getClass().getName());
        data.put("IDi", fragment.getId());
        data.put("IDs", IdsHelper.getNameForId(fragment.getId()));
        data.put("Tag", fragment.getTag());
        data.put("TargetFragment", fragment.getTargetFragment() != null ? String.valueOf(fragment.getTargetFragment()) : null);
        data.put("TargetRequestCode", fragment.getTargetRequestCode());
        data.put("IsAdded", fragment.isAdded());
        data.put("IsHidden", fragment.isHidden());
        data.put("IsInLayout", fragment.isInLayout());
        data.put("IsRemoving", fragment.isRemoving());
        data.put("IsResumed", fragment.isResumed());
        data.put("IsVisible", fragment.isVisible());
        data.put("Arguments", DetailExtractor.getExtractor(Bundle.class).fillValues(fragment.getArguments(), new HashMap<String, Object>(), data));

        FragmentReflector sfr = new FragmentReflector(fragment);
        data.put("State", getTranslator().fragmentState(sfr.getState()));
        data.put("Who", sfr.getWho());
        data.put("Index", sfr.getIndex());
        data.put("HasOptionsMenu", sfr.hasOptionsMenu());
        data.put("IsMenuVisible", sfr.isMenuVisible());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            data.put("IsDetached", fragment.isDetached());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            data.put("ParentFragment", String.valueOf(fragment.getParentFragment()));
        }
        return data;
    }
}
