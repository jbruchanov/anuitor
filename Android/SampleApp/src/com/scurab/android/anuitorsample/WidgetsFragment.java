package com.scurab.android.anuitorsample;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * Created by jbruchanov on 26/06/2014.
 */
public class WidgetsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.widgets, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view){
        Context context = view.getContext();
        initSpinner(view, context);
        initAutoCompleteTextView(view, context);
    }

    private void initAutoCompleteTextView(View view, Context context) {
        AutoCompleteTextView actv = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
        String[] data = context.getResources().getStringArray(R.array.planets);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, data);
        actv.setAdapter(adapter);
    }

    private void initSpinner(View view, Context context) {
        String[] data = new String[20];
        data[0] = context.getString(R.string.lorem_ipsum);
        data[1] = context.getString(R.string.lorem_ipsum_short);
        data[2] = context.getString(R.string.lorem_ipsum_long);
        for (int i = 3; i < data.length; i++) {
            data[i] = data[i % 3];
        }

        Spinner sp = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
    }
}
