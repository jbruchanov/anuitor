package com.scurab.android.uitorsample;

import android.content.Context;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scurab.android.uitorsample.common.BaseFragment;
import com.scurab.android.uitorsample.widget.SimplePagerAdapter;

public class ViewPagerFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewPager vp = new ViewPager(inflater.getContext());
        vp.setAdapter(new SampleAdapter(inflater.getContext()));
        vp.setPageMargin((int) getResources().getDimension(R.dimen.view_pager_item_margin));
        vp.setOffscreenPageLimit(2);
        return vp;
    }

    private static class SampleAdapter extends SimplePagerAdapter {

        private SampleAdapter(Context context) {
            super(context);
        }

        @Override
        public View onCreateView(final int position, View container) {
            return View.inflate(getContext(), R.layout.tile, null);
        }

        @Override
        public int getCount() {
            return 10;
        }
    }
}
