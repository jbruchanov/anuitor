package com.scurab.android.anuitorsample;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.scurab.android.anuitor.extract.DetailExtractor;
import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.extract.view.TextViewExtractor;
import com.scurab.android.anuitor.service.AnUitorService;
import com.scurab.android.anuitorsample.extract.CustomTextViewExtractor;
import com.scurab.android.anuitorsample.widget.CustomTextView;
import com.scurab.android.anuitorsample.widget.SlidingPaneLayout;

/**
 * Created by jbruchanov on 15.5.14.
 */
public class MainActivity extends FragmentActivity {

    private SlidingPaneLayout mPaneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //register own extractor for specific type
        DetailExtractor.registerExtractor(CustomTextView.class, new CustomTextViewExtractor(new Translator()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mPaneLayout = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout);
        Resources res = getResources();
        mPaneLayout.setParallaxDistance((int) res.getDimension(R.dimen.left_menu_parallax_distance));
        mPaneLayout.setSliderFadeColor(Color.TRANSPARENT);
        mPaneLayout.openPane();

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

    public void showToast(Throwable t) {
        showToast(t.getMessage());
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void openFragment(Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, f, f.getClass().getSimpleName())
                .commit();
        mPaneLayout.closePane();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AnUitorService.startService(this, 8081, 0, true, null);
    }
}
