package com.scurab.android.anuitorsample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.FrameLayout;

/**
 * Created by JBruchanov on 27/03/2017.
 */

public class AnotherActivity extends BaseActivity {

    private static final String ARG_INIT_FRAGMENT = "ARG_INIT_FRAGMENT";

    public static Intent intent(Context context, Class<? extends Fragment> initFragment) {
        Intent intent = new Intent(context, AnotherActivity.class);
        intent.putExtra(ARG_INIT_FRAGMENT, initFragment.getName());
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout fl = new FrameLayout(this);
        fl.setId(R.id.fragment_container);
        setContentView(fl);

        try {
            openFragment(getInitFragment().newInstance(), false);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    protected Class<Fragment> getInitFragment() throws ClassNotFoundException {
        return (Class<Fragment>) Class.forName(getIntent().getStringExtra(ARG_INIT_FRAGMENT));
    }
}
