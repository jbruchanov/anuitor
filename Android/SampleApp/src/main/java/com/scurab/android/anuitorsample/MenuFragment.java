package com.scurab.android.anuitorsample;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by jbruchanov on 26/06/2014.
 */
public class MenuFragment extends Fragment {

    private ListView mListView;
    private ArrayAdapter<String> mMenuAdapter;

    public static final Class<?>[] itemFragments = {
            ComponentsFragment.class,
            ListViewFragment.class,
            ScrollViewFragment.class,
            ViewPagerFragment.class,
            WidgetsFragment.class,
            CustomWidgetsFragment.class,
            DrawOutsideBoundsFragment.class,
            RecyclerViewFragment.class,
            CoordinatorLayoutFragment.class,
            NavigationViewFragment.class
    };

    public static final String[] itemFragmentNames;

    static {
        itemFragmentNames = new String[itemFragments.length];
        for (int i = 0; i < itemFragmentNames.length; i++) {
            itemFragmentNames[i] = itemFragments[i].getSimpleName().replace("Fragment", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        String[] items = itemFragmentNames;
        mMenuAdapter = new ArrayAdapter<>(getActivity(), R.layout.menu_list_item, items);
        mListView.setAdapter(mMenuAdapter);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Class<?> clz = itemFragments[i];
                try {
                    Fragment f = (Fragment) clz.newInstance();
                    final BaseActivity activity = getBaseActivity();
                    activity.openFragment(f, activity instanceof AnotherActivity);
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}
