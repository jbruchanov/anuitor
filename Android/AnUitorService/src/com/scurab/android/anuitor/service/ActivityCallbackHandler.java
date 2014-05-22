package com.scurab.android.anuitor.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import com.scurab.android.anuitor.nanoplugin.KnowsActivity;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 11:39
 *
 * Currently not used as registration for callback handler needs quite high API and it <b>won't catch first start activity</b>
 * {@link com.scurab.android.anuitor.nanoplugin.KnowsActivity} must be used for Application
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class ActivityCallbackHandler implements Application.ActivityLifecycleCallbacks, KnowsActivity {

    private Activity mCurrentActivity;

    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) { }
    @Override public void onActivityStarted(Activity activity) { }
    @Override public void onActivityResumed(Activity activity) { mCurrentActivity = activity; }
    @Override public void onActivityPaused(Activity activity) { mCurrentActivity = null; }
    @Override public void onActivityStopped(Activity activity) { }
    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }
    @Override public void onActivityDestroyed(Activity activity) { }

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    /**
     * Set current activity<br/>
     * It's set only if current activity is null => only on first start!
     * @param currentActivity
     */
    void setCurrentActivity(Activity currentActivity) {
        if (mCurrentActivity == null) {
            mCurrentActivity = currentActivity;
        }
    }
}
