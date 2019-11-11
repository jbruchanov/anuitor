package com.scurab.android.anuitorsample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.scurab.android.anuitorsample.common.BaseFragment;

/**
 * Created by jbruchanov on 26/06/2014.
 */
public class WidgetsFragment extends BaseFragment {

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

        Resources res = getResources();
        //vector drawable crashing on Kitkat
        ((TextView)view.findViewById(R.id.textView2))
                .setCompoundDrawablesWithIntrinsicBounds(
                        AppCompatResources.getDrawable(requireContext(), R.drawable.ic_comment_24dp),
                        null,
                        res.getDrawable(R.drawable.ic_launcher),
                        null);

        Drawable drawableTop = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_notifications_black_24dp);
        Drawable drawableBottom = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_home_black_24dp);
        DrawableCompat.setTint(drawableTop, res.getColor(R.color.colorAccent));
        DrawableCompat.setTint(drawableBottom, res.getColor(R.color.colorAccent));

        ((TextView)view.findViewById(R.id.textView3))
                .setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        drawableTop,
                        null,
                        drawableBottom);
    }

    private void initAutoCompleteTextView(View view, Context context) {
        AutoCompleteTextView actv = view.findViewById(R.id.autoCompleteTextView);
        String[] data = context.getResources().getStringArray(R.array.planets);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, data);
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

        Spinner sp = view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
    }
}
