package com.scurab.android.anuitorsample;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by jbruchanov on 27/06/2014.
 */
public class DialogsFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return buildView(inflater.getContext());
    }

    private View buildView(Context context){
        LinearLayout ll = new LinearLayout(context);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);
        ll.setOrientation(LinearLayout.VERTICAL);

        Button alertDialog = new Button(context);
        alertDialog.setText("Alert");
        alertDialog.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { onShowAlertDialog(); }
        });
        ll.addView(alertDialog);

        Button datePicker = new Button(context);
        datePicker.setText("DatePicker");
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { onShowDatePickerDialog(); }
        });
        ll.addView(datePicker);

        Button dialogFragment = new Button(context);
        dialogFragment.setText("DialogFragment");
        dialogFragment.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { onShowDialogFragment(); }
        });
        ll.addView(dialogFragment);

        Button pBarDialog = new Button(context);
        pBarDialog.setText("ProgressBar");
        pBarDialog.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { onShowProgressDialog(); }
        });
        ll.addView(pBarDialog);

        Button timeDialog = new Button(context);
        timeDialog.setText("TimerPicker");
        timeDialog.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { onShowTimePickerDialog(); }
        });
        ll.addView(timeDialog);

        return ll;
    }

    protected void onShowDialogFragment() {
        DialogsFragment df = new DialogsFragment();
        df.show(getActivity().getSupportFragmentManager(), "Dialog");
    }

    protected void onShowProgressDialog() {
        ProgressDialog.show(getActivity(), "Title", "Message", true, true);
    }

    protected void onShowAlertDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Title")
                .setMessage("Message")
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .setIcon(R.drawable.ic_launcher).show();
    }

    protected void onShowDatePickerDialog() {
        new DatePickerDialog(getActivity(), null, 2012, 3, 3).show();
    }

    protected void onShowTimePickerDialog() {
        new TimePickerDialog(getActivity(), null, 10, 10, true);
    }
}
