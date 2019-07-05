package com.scurab.android.anuitorsample;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.scurab.android.anuitorsample.common.BaseFragment;

/**
 * Created by jbruchanov on 26/06/2014.
 */
public class ScrollViewFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = inflater.getContext();
        ScrollView sv = new ScrollView(context);
        sv.addView(buildContent(context));
        return sv;
    }

    private LinearLayout buildContent(Context context) {
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ListViewFragment.SampleAdapter adapter = new ListViewFragment.SampleAdapter(context);
        for (int i = 0, n = adapter.getCount(); i < n; i++) {
            ll.addView(adapter.getView(i, null, null));
        }
        return ll;
    }
}
