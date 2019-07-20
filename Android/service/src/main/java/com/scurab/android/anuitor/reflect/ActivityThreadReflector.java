package com.scurab.android.anuitor.reflect;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JBruchanov on 20/01/2017.
 */

public class ActivityThreadReflector extends Reflector<Object> {

    public ActivityThreadReflector() {
        super(getInstance());
    }

    private static Object getInstance() {
        try {
            Class clz = Class.forName("android.app.ActivityThread");
            return callMethodByReflection(clz, null, "currentActivityThread");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public Application getApplication() {
        return getFieldValue(mReal, "mInitialApplication");
    }

    public List<Activity> getActivities() {
        final ArrayList<Activity> result = new ArrayList<>();
        for (Object mActivityRecord : ((Map<?, Object>) getFieldValue(mReal, "mActivities")).values()) {
            result.add(getFieldValue(mActivityRecord, "activity"));
        }
        return result;
    }
}