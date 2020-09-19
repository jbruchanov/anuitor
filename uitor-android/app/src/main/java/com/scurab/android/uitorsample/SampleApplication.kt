package com.scurab.android.uitorsample

import android.content.Context
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.multidex.MultiDexApplication
import com.scurab.android.uitor.Constants
import com.scurab.android.uitor.extract.RenderAreaWrapper
import com.scurab.android.uitor.extract2.BaseExtractor
import com.scurab.android.uitor.extract2.DetailExtractor
import com.scurab.android.uitor.extract2.ExtractingContext
import com.scurab.android.uitor.extract2.IFragmentDelegate
import com.scurab.android.uitor.extract2.ViewExtractor
import com.scurab.android.uitor.hierarchy.IdsHelper
import com.scurab.android.uitor.service.UitorClientConfig
import com.scurab.android.uitor.service.UitorService
import com.scurab.android.uitorsample.common.BaseFragment
import java.util.Date

class SampleApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        UitorClientConfig.addTypeHighlighting(Button::class.java, "rgba(255, 0, 255, 0.5)")
        UitorClientConfig.addPropertyHighlighting("text.*|.*TextColor.*|CanResolveText.*", "rgba(255, 0, 255, 1)")
        UitorClientConfig.addPointerIgnoreViewId(R.id.pointer_ignore)
        UitorClientConfig.setResourcesInSnapshot(true)
        UitorService.startService(this, 8081)
        IdsHelper.loadValues(R::class.java)

        DetailExtractor.registerRenderArea(
            DrawOutsideBoundsFragment.HelpTextView::class.java,
            RenderAreaWrapper { view, outRect -> view.getDrawingSize(outRect) }
        )

        DetailExtractor.registerExtractor(
            BaseFragment::class.java,
            object : BaseExtractor() {
                override val parent: Class<*> = Fragment::class.java

                override fun onFillValues(item: Any, context: ExtractingContext) {
                    val baseFragment = item as BaseFragment
                    context.put("FakePresenter", baseFragment.fakePresenter)
                }
            }
        )

        // replace the ViewExtractor with custom one
        DetailExtractor.registerExtractor(
            View::class.java,
            object : ViewExtractor() {

                override fun onFillValues(item: Any, context: ExtractingContext) {
                    super.onFillValues(item, context)
                    context.data[Constants.OWNER]
                        ?.let { (it as? IFragmentDelegate)?.fragment as? BaseFragment }
                        ?.let {
                            context.put("FakePresenter", it.fakePresenter)
                        }
                }
            }
        )

        getSharedPreferences("UIToSample", Context.MODE_PRIVATE).edit().apply {
            putLong("Now", System.currentTimeMillis())
            putString("Test", Date().toGMTString())
            putStringSet("Set", setOf("1", "A"))
            apply()
        }
    }
}
