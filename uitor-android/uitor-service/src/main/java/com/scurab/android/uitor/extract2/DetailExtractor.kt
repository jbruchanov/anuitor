package com.scurab.android.uitor.extract2

import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.scurab.android.uitor.extract.RenderAreaWrapper
import com.scurab.android.uitor.model.ViewNode
import java.util.HashMap

object DetailExtractor {

    private val items = mutableMapOf<String, BaseExtractor>()
    private val renderAreaItems = mutableMapOf<String, RenderAreaWrapper<*>>()
    private val viewGroupIgnoreItems = mutableSetOf<String>()

    init {
        resetToDefault()
    }

    /**
     * Reinitialize extractor set to default state
     */
    @JvmStatic
    fun resetToDefault() {
        items.clear()
        viewGroupIgnoreItems.clear()
        // WebView is not typical viewgroup, let's treat it as a view
        viewGroupIgnoreItems.add(WebView::class.java.name)
        ExtractorsRegister.register()
    }

    /**
     * Register extractor for particular class.<br></br>
     * Older is overwritten if exists
     *
     * @param clz
     * @param extractor
     * @return overwritten extractor
     */
    @JvmStatic
    fun registerExtractor(clz: Class<*>, extractor: BaseExtractor): BaseExtractor? {
        return registerExtractor(clz.requireCanonicalName(), extractor)
    }

    /**
     * Register extractor for particular class.<br></br>
     * Older is overwritten if exists
     *
     * @param className
     * @param extractor
     * @return
     */
    @JvmStatic
    fun registerExtractor(className: String, extractor: BaseExtractor): BaseExtractor? {
        return items.put(className, extractor)
    }

    /**
     * Register wrapper for particular class.<br></br>
     * Older is overwritten if exists
     *
     * @param clz
     * @param wrapper
     * @return overwritten wrapper
     */
    @JvmStatic
    fun <T : View> registerRenderArea(clz: Class<out T>, wrapper: RenderAreaWrapper<T>): RenderAreaWrapper<*>? {
        return registerRenderArea(clz.requireCanonicalName(), wrapper)
    }

    /**
     * Register wrapper for particular class.<br></br>
     * Older is overwritten if exists
     *
     * @param className
     * @param wrapper
     * @param <T>
     * @return
     </T> */
    @JvmStatic
    fun <T : View> registerRenderArea(className: String, wrapper: RenderAreaWrapper<T>): RenderAreaWrapper<*>? {
        return renderAreaItems.put(className, wrapper)
    }

    /**
     * Unregister extractor
     *
     * @param className
     * @return removed extractor
     */
    @JvmStatic
    fun unregisterExtractor(className: String): BaseExtractor? {
        return items.remove(className)
    }

    /**
     * Unregister extractor
     *
     * @param clz
     * @return removed extractor
     */
    @JvmStatic
    fun unregisterRenderArea(clz: Class<*>): RenderAreaWrapper<*>? {
        return unregisterRenderArea(clz.requireCanonicalName())
    }

    /**
     * Unregister extractor
     *
     * @param className
     * @return removed extractor
     */
    @JvmStatic
    fun unregisterRenderArea(className: String): RenderAreaWrapper<*>? {
        return renderAreaItems.remove(className)
    }

    /**
     * Unregister extractor
     *
     * @param clz
     * @return removed extractor
     */
    @JvmStatic
    fun unregisterExtractor(clz: Class<*>): BaseExtractor? {
        return unregisterExtractor(clz.requireCanonicalName())
    }

    /**
     * Flag particular class which is [android.view.ViewGroup] to behave as like simple [android.view.View]<br></br>
     * Currently useful only for [android.webkit.WebView]
     *
     * @param className
     * @return
     */
    @JvmStatic
    fun excludeViewGroup(className: String): Boolean {
        return viewGroupIgnoreItems.add(className)
    }

    /**
     * [.excludeViewGroup]
     *
     * @param className
     * @return
     */
    @JvmStatic
    fun removeExcludeViewGroup(className: String): Boolean {
        return viewGroupIgnoreItems.remove(className)
    }

    /**
     * [.excludeViewGroup]
     *
     * @param className
     * @return
     */
    @JvmStatic
    fun isExcludedViewGroup(className: String): Boolean {
        return viewGroupIgnoreItems.contains(className)
    }

    /**
     * Traverse whole view tree hierarchy and extract data
     *
     * @param rootView
     * @param lazy     if true, childs are ignored
     * @return
     */
    @JvmStatic
    fun parse(rootView: View, lazy: Boolean): ViewNode {
        val counter = intArrayOf(0)
        val vn = ViewNode(
            rootView.id, 0, counter[0],
            if (lazy) null
            else getExtractor(rootView).fillValues(rootView, ExtractingContext())
        )
        counter[0]++
        parse(rootView, vn, 1, counter, lazy, vn.data)
        return vn
    }

    private fun parse(
        rootView: View,
        root: ViewNode,
        level: Int,
        position: IntArray,
        lazy: Boolean,
        parentData: MutableMap<String, Any>
    ) {
        if (rootView is ViewGroup) {
            var i = 0
            val n = rootView.childCount
            while (i < n) {
                val child = rootView.getChildAt(i)
                val extractor = getExtractor(child)
                var result: MutableMap<String, Any>? = HashMap()

                result = if (lazy) null
                else extractor.fillValues(child, ExtractingContext(result!!, parentData, 0, mutableSetOf()))

                val vn = ViewNode(
                    child.id,
                    level,
                    position[0],
                    result!!
                )

                root.addChild(vn)
                position[0]++
                parse(child, vn, level + 1, position, lazy, vn.data)
                i++
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
    @JvmStatic
    fun findViewByPosition(rootView: View, position: Int): View? {
        return findViewByPosition(rootView, position, IntArray(1))
    }

    private fun findViewByPosition(rootView: View, position: Int, counter: IntArray): View? {
        if (position == counter[0]) {
            return rootView
        }

        if (rootView is ViewGroup) {
            var i = 0
            val n = rootView.childCount
            while (i < n) {
                counter[0]++
                val v = findViewByPosition(rootView.getChildAt(i), position, counter)
                if (v != null) {
                    return v
                }
                i++
            }
        }
        return null
    }

    /**
     * Get extractor for view
     *
     * @param item
     * @return
     */
    @JvmStatic
    fun getExtractor(item: View): BaseExtractor {
        return getExtractor(item.javaClass)
    }

    /**
     * Get render size for view if exists
     *
     * @param item
     * @return
     */
    @JvmStatic
    fun getRenderArea(item: View): RenderAreaWrapper<View>? {
        @Suppress("UNCHECKED_CAST")
        return findItemByClassInheritance(item.javaClass, renderAreaItems) as RenderAreaWrapper<View>?
    }

    /**
     * Find generic extractor for particular class
     *
     * @param clazz
     * @return
     * @throws IllegalStateException if no extractor is found
     */
    @JvmStatic
    fun getExtractor(clazz: Class<*>): BaseExtractor {
        return findExtractor(clazz)
            ?: throw IllegalStateException("Not found extractor for type:" + clazz.canonicalName)
    }

    /**
     * Find generic extractor for particular class
     *
     * @param clazz
     * @return
     * @throws IllegalStateException if no extractor is found
     */
    @JvmStatic
    fun findExtractor(clazz: Class<*>): BaseExtractor? {
        return findItemByClassInheritance(clazz, items)
    }

    private fun <T, R> findItemByClassInheritance(clazz: Class<T>, data: Map<String, R>): R? {
        var clz: Class<*> = clazz
        var ve: R? = data[clz.requireCanonicalName()]
        while (ve == null && clz != Any::class.java) { // object just for sure that View is unregistered
            clz = clz.superclass as Class<*>
            ve = data[clz.requireCanonicalName()]
        }
        return ve
    }

    private fun Class<*>.requireCanonicalName(): String {
        return this.canonicalName
            ?: throw NullPointerException("${this.name}, canonicalName is null")
    }
}
