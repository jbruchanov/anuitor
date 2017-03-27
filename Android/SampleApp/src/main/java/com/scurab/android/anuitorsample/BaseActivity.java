package com.scurab.android.anuitorsample;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

/**
 * Created by JBruchanov on 27/03/2017.
 */

public abstract class BaseActivity extends FragmentActivity {

    public void showToast(Throwable t) {
        showToast(t.getMessage());
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void openFragment(Fragment f, boolean addToBackStack) {
        final FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, f, f.getClass().getSimpleName());
        if (addToBackStack) {
            transaction.addToBackStack(String.valueOf(System.currentTimeMillis()));
        }
        transaction.commit();
    }
}
