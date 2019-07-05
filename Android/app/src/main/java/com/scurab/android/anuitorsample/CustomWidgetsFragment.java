package com.scurab.android.anuitorsample;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.scurab.android.anuitorsample.common.BaseFragment;
import com.scurab.android.anuitorsample.widget.CustomButton;
import com.scurab.android.anuitorsample.widget.CustomTextView;
import com.scurab.android.anuitorsample.widget.SubCustomButton;
import com.scurab.android.anuitorsample.widget.SubCustomTextView;

/**
 * Created by jbruchanov on 01/07/2014.
 */
public class CustomWidgetsFragment extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return buildContent();
    }

    private View buildContent() {
        Context context = getActivity();
        ScrollView sv = new ScrollView(context);
        LinearLayout ll = new LinearLayout(context);
        sv.addView(ll);
        ll.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, context.getResources().getDisplayMetrics());

        CustomTextView ctv = new CustomTextView(context);
        ctv.setText("CustomTextView");
        ctv.setTextSize(size);
        ctv.setPadding(padding,padding,padding,padding);
        ll.addView(ctv);

        SubCustomTextView sctv = new SubCustomTextView(context);
        sctv.setText("SubCustomTextView");
        sctv.setTextSize(size);
        sctv.setPadding(padding,padding,padding,padding);
        ll.addView(sctv);


        CustomButton cb = new CustomButton(context);
        cb.setText("CustomButton");
        cb.setTextSize(size);
        cb.setPadding(padding,padding,padding,padding);
        ll.addView(cb);

        SubCustomButton scb = new SubCustomButton(context);
        scb.setText("SubCustomButton");
        scb.setTextSize(size);
        scb.setPadding(padding,padding,padding,padding);
        ll.addView(scb);

        return sv;
    }
}
