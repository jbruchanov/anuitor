package com.scurab.android.anuitorsample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by jbruchanov on 26/06/2014.
 */
public class MenuFragment extends Fragment {

    private ListView mListView;
    private ArrayAdapter<String> mMenuAdapter;

    public static final Class<?>[] itemFragments = {
            ListViewFragment.class,
            ScrollViewFragment.class,
            ViewPagerFragment.class,
            WidgetsFragment.class
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
        mMenuAdapter = new ArrayAdapter<String>(getActivity(), R.layout.menu_list_item, items);
        mListView.setAdapter(mMenuAdapter);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Class<?> clz = itemFragments[i];
                try {
                    Fragment f = (Fragment) clz.newInstance();
                    getMainActivity().openFragment(f);
                } catch (Exception e) {
                    getMainActivity().showToast(e);
                }
            }
        });
        return view;
    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }
}
