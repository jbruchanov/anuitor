package com.scurab.android.anuitor.extract;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsSeekBar;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.scurab.android.anuitor.model.ViewNode;

import java.util.HashMap;
import java.util.HashSet;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 13:25
 */
public final class ViewDetailExtractor {

    static final HashMap<Class<?>, ViewExtractor> MAP;
    static final HashSet<Class<?>> VIEWGROUP_IGNORE;

    static {
        MAP = new HashMap<Class<?>, ViewExtractor>();
        registerExtractor(AbsListView.class, new AbsListViewExtractor());
        registerExtractor(AbsSeekBar.class, new AbsSeekBarExtractor());
        registerExtractor(AdapterView.class, new AdapterViewExtractor());
        registerExtractor(CheckedTextView.class, new CheckedTextViewExtractor());
        registerExtractor(CompoundButton.class, new CompoundButtonExtractor());
        registerExtractor(DrawerLayout.class, new DrawerLayoutExtractor());
        registerExtractor(ImageView.class, new ImageViewExtractor());
        registerExtractor(ListView.class, new ListViewExtractor());
        registerExtractor(ProgressBar.class, new ProgressBarExtractor());
        registerExtractor(ScrollView.class, new ScrollViewExtractor());
        registerExtractor(TextView.class, new TextViewExtractor());
        registerExtractor(View.class, new ViewExtractor());
        registerExtractor(ViewGroup.class, new ViewGroupExtractor());
        registerExtractor(ViewPager.class, new ViewPagerExtractor());
        registerExtractor(ViewStub.class, new ViewStubExtractor());
        registerExtractor(WebView.class, new WebViewExtrator());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerExtractor(Switch.class, new SwitchExtractor());
        }



        VIEWGROUP_IGNORE = new HashSet<Class<?>>();
        VIEWGROUP_IGNORE.add(WebView.class);
    }

    public static void registerExtractor(Class<?> clz, ViewExtractor extractor) {
        MAP.put(clz, extractor);
    }

    public static void unregisterExtractor(Class<?> clz){
        MAP.remove(clz);
    }

    public static boolean excludeViewGroup(Class<?> clz){
        return VIEWGROUP_IGNORE.add(clz);
    }

    public static boolean removeExcludeViewGroup(Class<?> clz){
        return VIEWGROUP_IGNORE.remove(clz);
    }

    public static boolean isExcludedViewGroup(Class<?> clz){
        return VIEWGROUP_IGNORE.contains(clz);
    }

    public static ViewNode parse(View rootView, boolean lazy) {
        int[] counter = {0};
        ViewNode vn = new ViewNode(rootView.getId(), 0 , counter[0], lazy
                                                         ? null
                                                         : getExtractor(rootView).fillValues(rootView, new HashMap<String, Object>(), null));


        counter[0]++;
        parse(rootView, vn, 1, counter, lazy, vn.getData());
        return vn;
    }

    private static void parse(View rootView, ViewNode root, int level, int[] position, boolean lazy, HashMap<String, Object> parentData) {
        if (rootView instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) rootView;
            for (int i = 0, n = group.getChildCount(); i < n; i++) {
                View child = group.getChildAt(i);
                ViewNode vn = new ViewNode(
                        child.getId(),
                        level,
                        position[0],
                        lazy ? null : getExtractor(child).fillValues(child, new HashMap<String, Object>(),parentData)
                );

                root.addChild(vn);
                position[0]++;
                parse(child, vn, level + 1, position, lazy, vn.getData());
            }
        }
    }

    public static View findViewByPosition(View rootView, int position) {
        return findViewByPosition(rootView, position, new int[1]);
    }

    private static View findViewByPosition(View rootView, int position, int[] counter) {
        if (position == counter[0]) {
            return rootView;
        }

        if (rootView instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) rootView;
            for (int i = 0, n = group.getChildCount(); i < n; i++) {
                counter[0]++;
                View v = findViewByPosition(group.getChildAt(i), position, counter);
                if (v != null) {
                    return v;
                }
            }
        }
        return null;
    }

    static ViewExtractor getExtractor(View v) {
        Class<?> clz = v.getClass();
        ViewExtractor ve = MAP.get(clz);
        while (ve == null && clz != Object.class) {//object just for sure that View is unregistered
            clz = clz.getSuperclass();
            ve = MAP.get(clz);
        }
        if (ve == null) {
            ve = MAP.get(View.class);
        }
        return ve;
    }
}
