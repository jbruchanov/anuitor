package com.scurab.android.anuitor.extract2;

import android.view.View;
import android.view.ViewGroup;

import com.scurab.android.anuitor.extract.RenderAreaWrapper;
import com.scurab.android.anuitor.model.ViewNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 13:25
 * <p/>
 * Base class for working with Extractors.
 */
public final class DetailExtractor {

    static final HashMap<String, BaseExtractor> MAP;
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

        ExtractorsRegister.INSTANCE.register();
    }

    /**
     * Register extractor for particular class.<br/>
     * Older is overwritten if exists
     *
     * @param clz
     * @param extractor
     * @return overwritten extractor
     */
    public static BaseExtractor registerExtractor(Class<?> clz, BaseExtractor extractor) {
        return registerExtractor(clz.getCanonicalName(), extractor);
    }

    /**
     * Register extractor for particular class.<br/>
     * Older is overwritten if exists
     *
     * @param className
     * @param extractor
     * @return
     */
    public static BaseExtractor registerExtractor(String className, BaseExtractor extractor) {
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
    public static BaseExtractor unregisterExtractor(String className) {
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
    public static BaseExtractor unregisterExtractor(Class<?> clz) {
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
                : getExtractor(rootView).fillValues(rootView, new HashMap<>(), null, 0));


        counter[0]++;
        parse(rootView, vn, 1, counter, lazy, vn.getData());
        return vn;
    }

    private static void parse(View rootView, ViewNode root, int level, int[] position, boolean lazy, Map<String, Object> parentData) {
        if (rootView instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) rootView;
            for (int i = 0, n = group.getChildCount(); i < n; i++) {
                View child = group.getChildAt(i);
                BaseExtractor extractor = getExtractor(child);
                Map<String, Object> result = new HashMap<>();

                result = lazy ? null : extractor.fillValues(child, result, parentData, 0);

                ViewNode vn = new ViewNode(
                        child.getId(),
                        level,
                        position[0],
                        result);

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
    public static BaseExtractor getExtractor(View object) {
        return getExtractor(object.getClass());
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
     * @return
     * @throws IllegalStateException if no extractor is found
     */
    @NonNull
    public static BaseExtractor getExtractor(final Class clazz) {
        final BaseExtractor be = findExtractor(clazz);
        if (be == null) {
            throw new IllegalStateException("Not found extractor for type:" + clazz.getCanonicalName());
        }
        //noinspection unchecked
        return be;
    }

    /**
     * Find generic extractor for particular class
     *
     * @param clazz
     * @return
     * @throws IllegalStateException if no extractor is found
     */
    @Nullable
    public static BaseExtractor findExtractor(final Class clazz) {
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
