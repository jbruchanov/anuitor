package groovy.lang;

import android.app.Activity;
import android.app.Application;
import android.view.View;

import com.scurab.android.anuitor.extract2.DetailExtractor;
import com.scurab.android.anuitor.hierarchy.IdsHelper;
import com.scurab.android.anuitor.reflect.ActivityThreadReflector;
import com.scurab.android.anuitor.reflect.Reflector;
import com.scurab.android.anuitor.reflect.WindowManagerProvider;

import java.util.List;

/**
 * Created by JBruchanov on 20/01/2017.
 * Help class for used in groovy scripts
 */

@SuppressWarnings("unused")//used indirectly via groovy scripts
public class GroovyHelper {

    /**
     * Get resource id value from string
     *
     * @param id string value of id, e.g. "R.id.myButton"
     * @return*
     */
    public static int id(String id) {
        int index = id.lastIndexOf(".");
        String fieldName = id.substring(index + 1);
        String clsName = id.substring(1, index).replace('.', '$');//$id ,R removed
        try {
            Class clz = Class.forName(IdsHelper.getRClass().getName() + clsName);
            return clz.getField(fieldName).getInt(null);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Get Application object instance
     *
     * @return
     */
    public static Application getApplication() {
        return new ActivityThreadReflector().getApplication();
    }

    /**
     * Get list of created activities
     *
     * @return
     */
    public static List<Activity> getActivities() {
        return new ActivityThreadReflector().getActivities();
    }

    /**
     * Get Root view of particular screen
     *
     * @param index
     * @return
     */
    public static View getRootView(int index) {
        return WindowManagerProvider.getManager().getRootView(index);
    }

    /**
     * Get any view of particular screen
     *
     * @param rootViewIndex
     * @param position
     * @return
     */
    public static View getView(int rootViewIndex, int position) {
        final View rootView = WindowManagerProvider.getManager().getRootView(rootViewIndex);
        return DetailExtractor.findViewByPosition(rootView, position);
    }

    /**
     * Get value for any field of object
     *
     * @param src
     * @param name
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static Object field(Object src, String name) throws NoSuchFieldException, IllegalAccessException {
        return Reflector.getFieldValue(src, name);
    }

    /**
     * Get value for any field of object
     *
     * @param clz
     * @param name
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static Object field(Class<?> clz, String name) throws NoSuchFieldException, IllegalAccessException {
        return Reflector.getFieldValue(null, clz, name, true);
    }
}
