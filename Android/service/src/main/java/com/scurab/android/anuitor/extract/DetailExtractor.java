package com.scurab.android.anuitor.extract;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.scurab.android.anuitor.extract.component.ActivityExtractor;
import com.scurab.android.anuitor.extract.component.BitmapDrawableExtractor;
import com.scurab.android.anuitor.extract.component.BitmapExtractor;
import com.scurab.android.anuitor.extract.component.BundleExtractor;
import com.scurab.android.anuitor.extract.component.DrawableExtractor;
import com.scurab.android.anuitor.extract.component.FragmentActivityExtractor;
import com.scurab.android.anuitor.extract.component.FragmentExtractor;
import com.scurab.android.anuitor.extract.component.IntentExtractor;
import com.scurab.android.anuitor.extract.component.LayoutParamsExtractor;
import com.scurab.android.anuitor.extract.component.PaintExtractor;
import com.scurab.android.anuitor.extract.component.SupportFragmentExtractor;
import com.scurab.android.anuitor.extract.view.AbsListViewExtractor;
import com.scurab.android.anuitor.extract.view.AbsSeekBarExtractor;
import com.scurab.android.anuitor.extract.view.AdapterViewExtractor;
import com.scurab.android.anuitor.extract.view.CalendarViewExtractor;
import com.scurab.android.anuitor.extract.view.CardViewExtractor;
import com.scurab.android.anuitor.extract.view.CheckedTextViewExtractor;
import com.scurab.android.anuitor.extract.view.CompoundButtonExtractor;
import com.scurab.android.anuitor.extract.view.DrawerLayoutExtractor;
import com.scurab.android.anuitor.extract.view.GridLayoutExtractor;
import com.scurab.android.anuitor.extract.view.ImageViewExtractor;
import com.scurab.android.anuitor.extract.view.LinearLayoutExtractor;
import com.scurab.android.anuitor.extract.view.ListViewExtractor;
import com.scurab.android.anuitor.extract.view.ProgressBarExtractor;
import com.scurab.android.anuitor.extract.view.RecyclerViewExtractor;
import com.scurab.android.anuitor.extract.view.ScrollViewExtractor;
import com.scurab.android.anuitor.extract.view.SlidingPaneLayoutExtractor;
import com.scurab.android.anuitor.extract.view.SwitchExtractor;
import com.scurab.android.anuitor.extract.view.TextViewExtractor;
import com.scurab.android.anuitor.extract.view.ViewExtractor;
import com.scurab.android.anuitor.extract.view.ViewGroupExtractor;
import com.scurab.android.anuitor.extract.view.ViewPagerExtractor;
import com.scurab.android.anuitor.extract.view.ViewStubExtractor;
import com.scurab.android.anuitor.extract.view.WebViewExtractor;
import com.scurab.android.anuitor.model.ViewNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 13:25
 * <p/>
 * Base class for working with Extractors.
 */
public final class DetailExtractor {

    static final HashMap<String, BaseExtractor<?>> MAP;
    static final HashMap<String, RenderAreaWrapper<?>> RENDER_AREA_MAP;
    static final HashSet<String> VIEWGROUP_IGNORE;

    static {
        MAP = new HashMap<>();
        RENDER_AREA_MAP = new HashMap<>();
        VIEWGROUP_IGNORE = new HashSet<>();
        resetToDefault();
    }

    /**
     * Reinitialize extractor set to default state
     */
    public static void resetToDefault() {
        MAP.clear();
        VIEWGROUP_IGNORE.clear();

        Translator translator = new Translator();
        //region views
        registerExtractor(AbsListView.class, new AbsListViewExtractor(translator));
        registerExtractor(AbsSeekBar.class, new AbsSeekBarExtractor(translator));
        registerExtractor(AdapterView.class, new AdapterViewExtractor(translator));
        registerExtractor(CheckedTextView.class, new CheckedTextViewExtractor(translator));
        registerExtractor(CompoundButton.class, new CompoundButtonExtractor(translator));
        registerExtractor(DrawerLayout.class, new DrawerLayoutExtractor(translator));
        registerExtractor(ImageView.class, new ImageViewExtractor(translator));
        registerExtractor(ListView.class, new ListViewExtractor(translator));
        registerExtractor(ProgressBar.class, new ProgressBarExtractor(translator));
        registerExtractor(ScrollView.class, new ScrollViewExtractor(translator));
        registerExtractor(SlidingPaneLayout.class, new SlidingPaneLayoutExtractor(translator));
        registerExtractor(TextView.class, new TextViewExtractor(translator));
        registerExtractor(View.class, new ViewExtractor(translator));
        registerExtractor(ViewGroup.class, new ViewGroupExtractor(translator));
        registerExtractor(LinearLayout.class, new LinearLayoutExtractor(translator));
        registerExtractor(ViewPager.class, new ViewPagerExtractor(translator));
        registerExtractor(ViewStub.class, new ViewStubExtractor(translator));
        registerExtractor(WebView.class, new WebViewExtractor(translator));

        //optionals
        try {
            registerExtractor(android.support.v7.widget.RecyclerView.class, new RecyclerViewExtractor(translator));
        } catch (Throwable e) { /*not included in project*/ }
        try {
            registerExtractor(android.support.v7.widget.CardView.class, new CardViewExtractor(translator));
        } catch (Throwable e) { /*not included in project*/ }
        try {
            registerExtractor(android.support.v7.widget.GridLayout.class, new GridLayoutExtractor(translator));
        } catch (Throwable e) { /*not included in project*/ }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            registerExtractor(android.widget.CalendarView.class, new CalendarViewExtractor(translator));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerExtractor(android.widget.Switch.class, new SwitchExtractor(translator));
        }

        VIEWGROUP_IGNORE.add(WebView.class.getCanonicalName());
        //endregion views

        registerExtractor(Activity.class, new ActivityExtractor(translator));
        registerExtractor(Bundle.class, new BundleExtractor(translator));
        registerExtractor(FragmentActivity.class, new FragmentActivityExtractor(translator));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            registerExtractor(android.app.Fragment.class, new FragmentExtractor(translator));
        }
        registerExtractor(android.support.v4.app.Fragment.class, new SupportFragmentExtractor(translator));
        registerExtractor(Intent.class, new IntentExtractor(translator));
        registerExtractor(Paint.class, new PaintExtractor(translator));
        registerExtractor(Drawable.class, new DrawableExtractor(translator));
        registerExtractor(BitmapDrawable.class, new BitmapDrawableExtractor(translator));
        registerExtractor(Bitmap.class, new BitmapExtractor(translator));
        registerExtractor(ViewGroup.LayoutParams.class, new LayoutParamsExtractor(translator));
    }

    /**
     * Register extractor for particular class.<br/>
     * Older is overwritten if exists
     *
     * @param clz
     * @param extractor
     * @return overwritten extractor
     */
    public static <T> BaseExtractor<?> registerExtractor(Class<? extends T> clz, BaseExtractor<T> extractor) {
        return registerExtractor(clz.getCanonicalName(), extractor);
    }

    /**
     * Register extractor for particular class.<br/>
     * Older is overwritten if exists
     *
     * @param className
     * @param extractor
     * @param <T>
     * @return
     */
    public static <T> BaseExtractor<?> registerExtractor(String className, BaseExtractor<T> extractor) {
        return MAP.put(className, extractor);
    }

    /**
     * Register wrapper for particular class.<br/>
     * Older is overwritten if exists
     *
     * @param clz
     * @param wrapper
     * @return overwritten wrapper
     */
    public static <T extends View> RenderAreaWrapper<?> registerRenderArea(Class<? extends T> clz, RenderAreaWrapper<T> wrapper) {
        return registerRenderArea(clz.getCanonicalName(), wrapper);
    }

    /**
     * Register wrapper for particular class.<br/>
     * Older is overwritten if exists
     *
     * @param className
     * @param wrapper
     * @param <T>
     * @return
     */
    public static <T extends View> RenderAreaWrapper<?> registerRenderArea(String className, RenderAreaWrapper<T> wrapper) {
        return RENDER_AREA_MAP.put(className, wrapper);
    }

    /**
     * Unregister extractor
     *
     * @param className
     * @return removed extractor
     */
    public static BaseExtractor<?> unregisterExtractor(String className) {
        return MAP.remove(className);
    }

    /**
     * Unregister extractor
     *
     * @param clz
     * @return removed extractor
     */
    public static RenderAreaWrapper<?> unregisterRenderArea(Class<?> clz) {
        return unregisterRenderArea(clz.getCanonicalName());
    }

    /**
     * Unregister extractor
     *
     * @param className
     * @return removed extractor
     */
    public static RenderAreaWrapper<?> unregisterRenderArea(String className) {
        return RENDER_AREA_MAP.remove(className);
    }

    /**
     * Unregister extractor
     *
     * @param clz
     * @return removed extractor
     */
    public static BaseExtractor<?> unregisterExtractor(Class<?> clz) {
        return unregisterExtractor(clz.getCanonicalName());
    }

    /**
     * Flag particular class which is {@link android.view.ViewGroup} to behave as like simple {@link android.view.View}<br/>
     * Currently useful only for {@link android.webkit.WebView}
     *
     * @param className
     * @return
     */
    public static boolean excludeViewGroup(String className) {
        return VIEWGROUP_IGNORE.add(className);
    }

    /**
     * {@link #excludeViewGroup(String)}
     *
     * @param className
     * @return
     */
    public static boolean removeExcludeViewGroup(String className) {
        return VIEWGROUP_IGNORE.remove(className);
    }

    /**
     * {@link #excludeViewGroup(String)}
     *
     * @param className
     * @return
     */
    public static boolean isExcludedViewGroup(String className) {
        return VIEWGROUP_IGNORE.contains(className);
    }

    /**
     * Traverse whole view tree hierarchy and extract data
     *
     * @param rootView
     * @param lazy     if true, childs are ignored
     * @return
     */
    public static ViewNode parse(View rootView, boolean lazy) {
        int[] counter = {0};
        ViewNode vn = new ViewNode(rootView.getId(), 0, counter[0], lazy
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
                        lazy ? null : getExtractor(child).fillValues(child, new HashMap<String, Object>(), parentData)
                );

                root.addChild(vn);
                position[0]++;
                parse(child, vn, level + 1, position, lazy, vn.getData());
            }
        }
    }

    /**
     * Find view by Position item from json
     *
     * @param rootView
     * @param position
     * @return
     */
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

    /**
     * Get extractor for view
     *
     * @param object
     * @return
     */
    public static BaseExtractor<View> getExtractor(View object) {
        Class<? extends View> clz = object.getClass();
        return (BaseExtractor<View>) getExtractor(clz);
    }

    /**
     * Get render size for view if exists
     *
     * @param object
     * @return
     */
    @Nullable
    public static RenderAreaWrapper<View> getRenderArea(View object) {
        return (RenderAreaWrapper<View>) findItemByClassInheritance(object.getClass(), RENDER_AREA_MAP);
    }

    /**
     * Find generic extractor for particular class
     *
     * @param clazz
     * @param <T>
     * @return
     * @throws IllegalStateException if no extractor is found
     */
    @NonNull
    public static <T> BaseExtractor<T> getExtractor(final Class<T> clazz) {
        final BaseExtractor<?> be = findExtractor(clazz);
        if (be == null) {
            throw new IllegalStateException("Not found extractor for type:" + clazz.getCanonicalName());
        }
        //noinspection unchecked
        return (BaseExtractor<T>) be;
    }

    /**
     * Find generic extractor for particular class
     *
     * @param clazz
     * @return
     * @throws IllegalStateException if no extractor is found
     */
    @Nullable
    public static BaseExtractor<?> findExtractor(final Class clazz) {
        return findItemByClassInheritance(clazz, MAP);
    }

    @Nullable
    private static <T, R> R findItemByClassInheritance(final Class<T> clazz, Map<String, R> data) {
        Class<?> clz = clazz;
        R ve = data.get(clz.getCanonicalName());
        while (ve == null && clz != Object.class) {//object just for sure that View is unregistered
            clz = clz.getSuperclass();
            ve = data.get(clz.getCanonicalName());
        }
        return ve;
    }
}
