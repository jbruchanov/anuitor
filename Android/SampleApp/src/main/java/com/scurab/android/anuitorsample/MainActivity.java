package com.scurab.android.anuitorsample;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.scurab.android.anuitor.extract2.DetailExtractor;
import com.scurab.android.anuitor.service.AnUitorClientConfig;
import com.scurab.android.anuitor.service.AnUitorService;
import com.scurab.android.anuitorsample.extract.CustomTextViewExtractor;
import com.scurab.android.anuitorsample.widget.CustomTextView;

/**
 * Created by jbruchanov on 15.5.14.
 */
public class MainActivity extends BaseActivity {

    private SlidingPaneLayout mPaneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //register own extractor for specific type
        DetailExtractor.registerExtractor(CustomTextView.class, new CustomTextViewExtractor());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mPaneLayout = findViewById(R.id.sliding_pane_layout);
        Resources res = getResources();
        mPaneLayout.setParallaxDistance(res.getDimensionPixelSize(R.dimen.left_menu_parallax_distance));
        mPaneLayout.setSliderFadeColor(Color.TRANSPARENT);
        //needs to be posted or invalid size
        mPaneLayout.post(() -> mPaneLayout.openPane());

        View v = findViewById(R.id.txt_sample);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotation);
        v.setAnimation(animation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sample_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        AnUitorService.startService(this, 8081, 0, true, null);
        AnUitorClientConfig.addTypeHighlighting(Button.class, "rgba(255, 0, 255, 0.15)");
        AnUitorClientConfig.setSelectionColor("#FFFF00");
    }

    @Override
    public void openFragment(Fragment f, boolean add) {
        super.openFragment(f, add);
        mPaneLayout.closePane();
    }

    @Override
    public void onBackPressed() {
        if (!mPaneLayout.isOpen()) {
            mPaneLayout.openPane();
            return;
        }
        super.onBackPressed();
    }
}
