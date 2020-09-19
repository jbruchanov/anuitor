package com.scurab.android.uitorsample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.android.material.navigation.NavigationView;
import com.scurab.android.uitorsample.common.BaseFragment;

public class NavigationViewFragment extends BaseFragment {

    private NavigationView mNavigationView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mNavigationView = (NavigationView) inflater.inflate(R.layout.fragment_navigationview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavigationView.setNavigationItemSelectedListener(item -> true);
    }
}
