package com.scurab.android.anuitorsample;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by jbruchanov on 27/06/2014.
 */
public class ComponentsFragment extends DialogFragment {

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
        alertDialog.setTag("SimpleTag");
        alertDialog.setTag(R.id.tag_test1, "ComplexTag1");
        alertDialog.setTag(R.id.tag_test2, alertDialog);
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

        Button bottomSheetDialog = new Button(context);
        bottomSheetDialog.setText("BottomSheetDialog");
        bottomSheetDialog.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { onShowBottomSheetDialog(); }
        });
        ll.addView(bottomSheetDialog);

        Button snackBar = new Button(context);
        snackBar.setText("SnackBar");
        snackBar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { onShowSnackBar(); }
        });
        ll.addView(snackBar);

        Button anotherActivity = new Button(context);
        anotherActivity.setText("AnotherActivity");
        anotherActivity.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { onOpenAnotherActivity(); }
        });
        ll.addView(anotherActivity);

        return ll;
    }

    protected void onOpenAnotherActivity() {
        final BaseActivity activity = (BaseActivity) getActivity();
        if (activity instanceof MainActivity) {
            activity.startActivity(AnotherActivity.intent(getContext(), MenuFragment.class));
        } else {
            activity.openFragment(new MenuFragment(), true);
        }
    }

    protected void onShowDialogFragment() {
        ComponentsFragment df = new ComponentsFragment();
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
        new TimePickerDialog(getActivity(), null, 10, 10, true).show();
    }

    protected void onShowBottomSheetDialog() {
        final FragmentActivity context = getActivity();
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        final AppCompatTextView textViewCompat = new AppCompatTextView(context);
        textViewCompat.setText(R.string.lorem_ipsum_huge);
        int gap = context.getResources().getDimensionPixelSize(R.dimen.gap_normal);
        textViewCompat.setPadding(gap, gap, gap, gap);
        textViewCompat.setTextColor(ContextCompat.getColor(context, R.color.black));
        bottomSheetDialog.setContentView(textViewCompat);
        bottomSheetDialog.show();
    }

    protected void onShowSnackBar() {
        Snackbar.make(getView(), R.string.app_name, Snackbar.LENGTH_LONG)
                .setAction(R.string.title_home, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "Click", Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }
}
