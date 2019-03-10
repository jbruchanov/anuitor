package com.scurab.android.anuitor.extract.component;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.scurab.android.anuitor.extract.BaseExtractor;
import com.scurab.android.anuitor.extract.DetailExtractor;
import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.hierarchy.IdsHelper;
import com.scurab.android.anuitor.reflect.SupportBackStackEntryReflector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jbruchanov on 24/06/2014.
 */
public class FragmentActivityExtractor extends ActivityExtractor {

    public FragmentActivityExtractor(Translator translator) {
        super(translator);
    }

    @Override
    protected HashMap<String, Object> fillValues(Activity fragmentActivity, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        super.fillValues(fragmentActivity, data, contextData);
        if (fragmentActivity != null) {
            FragmentManager supportFragmentManager = ((FragmentActivity)fragmentActivity).getSupportFragmentManager();
            List<Fragment> fragments = supportFragmentManager.getFragments();
            if (fragments != null && !fragments.isEmpty()) {
                List<HashMap<String, Object>> fragmentsData = handleSupportFragments(fragments, new HashMap<String, Object>());
                data.put("SupportFragments", fragmentsData);
            }

            List<HashMap<String, Object>> backStackEntries = handleSupportBackStackEntries(supportFragmentManager, new HashMap<String, Object>());
            data.put("SupportBackStackEntries", backStackEntries);
        }
        return data;
    }

    protected HashMap<String, Object> extractFragments(Fragment fragment, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        if (fragment != null) {
            BaseExtractor<Fragment> extractor = DetailExtractor.getExtractor(Fragment.class);
            extractor.onFillValues(fragment, data, contextData);
            FragmentManager childFragmentManager = fragment.getChildFragmentManager();

            List<Fragment> childFragments = childFragmentManager.getFragments();
            if (childFragments != null && childFragments.size() > 0) {
                List<HashMap<String, Object>> fragmentsData = handleSupportFragments(childFragments, new HashMap<String, Object>());
                data.put("ChildFragments", fragmentsData);
            }
            List<HashMap<String, Object>> backStackEntries = handleSupportBackStackEntries(childFragmentManager, data);
            data.put("SupportBackStackEntries", backStackEntries);
        }
        return data;
    }

    protected List<HashMap<String, Object>> handleSupportBackStackEntries(FragmentManager fragmentManager, HashMap<String, Object> context) {
        List<HashMap<String, Object>> dataSet = new ArrayList<>();
        for (int i = 0, n = fragmentManager.getBackStackEntryCount(); i < n; i++) {
            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(i);
            SupportBackStackEntryReflector sbsef = new SupportBackStackEntryReflector(backStackEntry);
            //TODO own extractor
            HashMap<String, Object> data = new HashMap<>();
            data.put("Name", backStackEntry.getName());
            data.put("ID", backStackEntry.getId());
            data.put("EnterAnim", IdsHelper.getNameForId(sbsef.getEnterAnim()));
            data.put("ExitAnim", IdsHelper.getNameForId(sbsef.getExitAnim()));
            data.put("PopEnterAnim", IdsHelper.getNameForId(sbsef.getPopEnterAnim()));
            data.put("PopExitAnim", IdsHelper.getNameForId(sbsef.getPopExitAnim()));
            data.put("AddToBackStack", sbsef.isAddToBackStack());
            data.put("SharedElementSourceNamesValue", sbsef.getSharedElementSourceNamesValue());
            data.put("SharedElementTargetNamesValue", sbsef.getSharedElementTargetNamesValue());
            dataSet.add(data);
        }
        return dataSet;
    }

    protected List<HashMap<String, Object>> handleSupportFragments(List<Fragment> fragments, HashMap<String, Object> data) {
        List<HashMap<String, Object>> fragmentsData = new ArrayList<>();
        for (Fragment fragment : fragments) {
            fragmentsData.add(extractFragments(fragment, new HashMap<String, Object>(), data));
        }
        return fragmentsData;
    }
}
