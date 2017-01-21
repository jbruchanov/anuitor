package groovy.lang;

import android.app.Activity;
import android.app.Application;
import android.view.View;

import com.scurab.android.anuitor.extract.DetailExtractor;
import com.scurab.android.anuitor.hierarchy.IdsHelper;
import com.scurab.android.anuitor.reflect.ActivityThreadReflector;
import com.scurab.android.anuitor.reflect.Reflector;
import com.scurab.android.anuitor.reflect.WindowManagerProvider;

import java.util.List;

/**
 * Created by JBruchanov on 20/01/2017.
 */

@SuppressWarnings("unused")//used indirectly via groovy scripts
public class GroovyHelper {

    public static int id(String id) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        int index = id.lastIndexOf(".");
        String fieldName = id.substring(index + 1);
        String clsName = id.substring(1, index).replace('.', '$');//$id ,R removed
        Class clz = Class.forName(IdsHelper.RClass.getName() + clsName);
        return clz.getField(fieldName).getInt(null);
    }

    public static Application getApplication() {
        return new ActivityThreadReflector().getApplication();
    }

    public static List<Activity> getActivities() {
        return new ActivityThreadReflector().getActivities();
    }

    public static View getRootView(int index) {
        return WindowManagerProvider.getManager().getRootView(index);
    }

    public static View getView(int rootViewIndex, int position) {
        final View rootView = WindowManagerProvider.getManager().getRootView(rootViewIndex);
        return DetailExtractor.findViewByPosition(rootView, position);
    }

    public static Object field(Object src, String name) throws NoSuchFieldException, IllegalAccessException {
        return Reflector.getFieldValue(src, name);
    }

    public static Object field(Class<?> clz, String name) throws NoSuchFieldException, IllegalAccessException {
        return Reflector.getFieldValue(null, clz, name);
    }
}
