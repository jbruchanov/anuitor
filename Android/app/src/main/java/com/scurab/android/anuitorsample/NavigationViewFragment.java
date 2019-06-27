package com.scurab.android.anuitorsample;

import android.os.Bundle;

import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by JBruchanov on 12/03/2017.
 */

public class NavigationViewFragment extends Fragment {

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
