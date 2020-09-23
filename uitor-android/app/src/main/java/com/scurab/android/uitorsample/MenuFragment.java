package com.scurab.android.uitorsample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.scurab.android.uitorsample.common.BaseFragment;

public class MenuFragment extends BaseFragment {

    private ListView mListView;
    private ArrayAdapter<String> mMenuAdapter;

    public static final Class<?>[] itemFragments = {
            ComponentsFragment.class,
            ListViewFragment.class,
            ScrollViewFragment.class,
            ViewPagerFragment.class,
            ViewPager2Fragment.class,
            WidgetsFragment.class,
            WidgetsAndroidXFragment.class,
            CustomWidgetsFragment.class,
            DrawOutsideBoundsFragment.class,
            RecyclerViewFragment.class,
            ConstraintLayoutFragment.class,
            CoordinatorLayoutFragment.class,
            NavigationViewFragment.class,
            ChildFragments.class,
            WebViewFragment.class,
    };

    public static final String[] itemFragmentNames;

    static {
        itemFragmentNames = new String[itemFragments.length];
        for (int i = 0; i < itemFragmentNames.length; i++) {
            String fragment = itemFragments[i].getSimpleName();
            if (fragment.endsWith("Fragment")) {
                fragment = fragment.replace("Fragment", "");
            }
            itemFragmentNames[i] = fragment;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu, container, false);
        mListView = view.findViewById(android.R.id.list);
        String[] items = itemFragmentNames;
        mMenuAdapter = new ArrayAdapter<>(getActivity(), R.layout.menu_list_item, items);
        mListView.setAdapter(mMenuAdapter);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener((adapterView, view1, i, l) -> {
            Class<?> clz = itemFragments[i];
            try {
                Fragment f = (Fragment) clz.newInstance();
                final BaseActivity activity = getBaseActivity();
                activity.openFragment(f, activity instanceof AnotherActivity);
            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}
