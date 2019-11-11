package com.scurab.android.anuitorsample

import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.multidex.MultiDexApplication
import com.scurab.android.anuitor.Constants
import com.scurab.android.anuitor.extract.RenderAreaWrapper
import com.scurab.android.anuitor.extract2.*
import com.scurab.android.anuitor.hierarchy.IdsHelper
import com.scurab.android.anuitor.service.AnUitorClientConfig
import com.scurab.android.anuitor.service.AnUitorService
import com.scurab.android.anuitorsample.common.BaseFragment

class SampleApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        AnUitorClientConfig.addTypeHighlighting(Button::class.java, "rgba(255, 0, 255, 0.15)")
        AnUitorClientConfig.addPropertyHighlighting("text.*|.*TextColor.*|CanResolveText.*", "rgba(255, 0, 255, 1)")
        AnUitorClientConfig.addPointerIgnoreViewId(R.id.pointer_ignore)
        AnUitorService.startService(this, 8081, true, null)
        IdsHelper.loadValues(R::class.java)

        DetailExtractor.registerRenderArea(DrawOutsideBoundsFragment.HelpTextView::class.java,
                RenderAreaWrapper { view, outRect -> view.getDrawingSize(outRect) })

        DetailExtractor.registerExtractor(BaseFragment::class.java, object : BaseExtractor() {
            override val parent: Class<*> = Fragment::class.java

            override fun onFillValues(item: Any, context: ExtractingContext) {
                val baseFragment = item as BaseFragment
                context.put("FakePresenter", baseFragment.fakePresenter)
            }
        })

        //replace the ViewExtractor with custom one
        DetailExtractor.registerExtractor(View::class.java, object : ViewExtractor() {

            override fun onFillValues(item: Any, context: ExtractingContext) {
                super.onFillValues(item, context)
                context.data[Constants.OWNER]
                        ?.let { (it as? IFragmentDelegate)?.fragment as? BaseFragment }
                        ?.let {
                            context.put("FakePresenter", it.fakePresenter)
                        }
            }
        })
    }
}