package com.scurab.android.anuitorsample;

import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.multidex.MultiDexApplication;

import com.scurab.android.anuitor.extract2.BaseExtractor;
import com.scurab.android.anuitor.extract2.DetailExtractor;
import com.scurab.android.anuitor.extract2.ExtractingContext;
import com.scurab.android.anuitor.hierarchy.IdsHelper;
import com.scurab.android.anuitor.service.AnUitorClientConfig;
import com.scurab.android.anuitor.service.AnUitorService;
import com.scurab.android.anuitorsample.common.BaseFragment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by jbruchanov@gmail.com
 *
 * @since 2014-05-15.
 */
public class SampleApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        AnUitorClientConfig.addTypeHighlighting(Button.class, "rgba(255, 0, 255, 0.15)");
        AnUitorClientConfig.addPropertyHighlighting("text.*|.*TextColor.*|CanResolveText.*", "rgba(255, 0, 255, 1)");
        AnUitorClientConfig.addPointerIgnoreViewId(R.id.pointer_ignore);
        AnUitorService.startService(this, 8081, 0, true, null);
        IdsHelper.loadValues(R.class);

        DetailExtractor.registerRenderArea(DrawOutsideBoundsFragment.HelpTextView.class,
                (view, outRect) -> view.getDrawingSize(outRect));

        DetailExtractor.registerExtractor(BaseFragment.class, new BaseExtractor() {

            @Nullable
            @Override
            public Class<?> getParent() {
                return Fragment.class;
            }

            @Override
            protected void onFillValues(@NotNull Object item, @NotNull ExtractingContext context) {
                BaseFragment baseFragment = (BaseFragment) item;
                context.put("FakePresenter", baseFragment.getFakePresenter());
            }
        });
    }
}
