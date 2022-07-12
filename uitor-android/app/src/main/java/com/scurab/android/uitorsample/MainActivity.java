package com.scurab.android.uitorsample;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.scurab.android.uitor.extract2.DetailExtractor;
import com.scurab.android.uitorsample.extract.CustomTextViewExtractor;
import com.scurab.android.uitorsample.widget.CustomTextView;

public class MainActivity extends BaseActivity {

    private DrawerLayout mPaneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //register own extractor for specific type
        DetailExtractor.registerExtractor(CustomTextView.class, new CustomTextViewExtractor());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mPaneLayout = findViewById(R.id.sliding_pane_layout);
        mPaneLayout.post(() -> mPaneLayout.open());

        View v = findViewById(R.id.txt_sample);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale);
        v.setAnimation(animation);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sample_menu, menu);
        return true;
    }

    @Override
    public void openFragment(Fragment f, boolean add) {
        super.openFragment(f, add);
        mPaneLayout.close();
    }

    @Override
    public void onBackPressed() {
        if (mPaneLayout.isOpen()) {
            mPaneLayout.close();
            return;
        }
        super.onBackPressed();
    }
}
