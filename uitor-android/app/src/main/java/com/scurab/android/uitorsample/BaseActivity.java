package com.scurab.android.uitorsample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.widget.Toast;

public abstract class BaseActivity extends AppCompatActivity {

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
