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

import android.view.ContextThemeWrapper;
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

        Button alertDialog = createButton("Alert", v -> onShowAlertDialog());
        alertDialog.setTag("SimpleTag");
        alertDialog.setTag(R.id.tag_test1, "ComplexTag1");
        alertDialog.setTag(R.id.tag_test2, alertDialog);
        ll.addView(alertDialog);

        ll.addView(createButton("DatePicker", v -> onShowDatePickerDialog()));
        ll.addView(createButton("DialogFragment", v -> onShowDialogFragment()));
        ll.addView(createButton("ProgressBar", v -> onShowProgressDialog()));
        ll.addView(createButton("TimerPicker", v -> onShowTimePickerDialog()));
        ll.addView(createButton("BottomSheetDialog", v -> onShowBottomSheetDialog()));
        ll.addView(createButton("SnackBar", v -> onShowSnackBar()));
        ll.addView(createButton("AnotherActivity", v -> onOpenAnotherActivity()));

        Button ignoredByPointer = createButton("Ignored by pointer",
                v -> Toast.makeText(requireContext(), "Ignored by pointer Clicked", Toast.LENGTH_LONG).show());
        ignoredByPointer.setId(R.id.pointer_ignore);
        ll.addView(ignoredByPointer);
        ll.addView(createThemedButton("ThemeWrappedButton", v ->
                Toast.makeText(requireContext(), "Theme.AppCompat.Light", Toast.LENGTH_SHORT).show()));

        return ll;
    }

    private Button createButton(String title, View.OnClickListener clickListener) {
        Button button = new Button(requireActivity());
        button.setText(title);
        button.setOnClickListener(clickListener);
        return button;
    }

    private Button createThemedButton(String title, View.OnClickListener clickListener) {
        Button button = new Button(new ContextThemeWrapper(requireActivity(), R.style.Theme_AppCompat_Light));
        button.setText(title);
        button.setOnClickListener(clickListener);
        return button;
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
        ContextThemeWrapper themeWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_AppCompat);
        new DatePickerDialog(themeWrapper, null, 2012, 3, 3).show();
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
                .setAction(R.string.title_home, v -> Toast.makeText(v.getContext(), "Click", Toast.LENGTH_LONG).show())
                .show();
    }
}
