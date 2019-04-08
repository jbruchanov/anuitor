package com.scurab.android.anuitor.extract2;

import android.content.Context;
import android.view.View;

import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Modifier;
import java.util.HashMap;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;

/**
 * Created by jbruchanov on 12.6.2014.
 * This is simple dump test testing only copy&paste issues.
 */
public class ViewExtractingTests {

    public void doTests() throws ClassNotFoundException {
        assertTrue(DetailExtractor.MAP.size() > 0);

        for (String className : DetailExtractor.MAP.keySet()) {
            try {
                Class<?> clz = tryGetClass(className);
                if (clz == null) {
                    System.err.println(className);
                    continue;
                }
                if(Modifier.isAbstract(clz.getModifiers()) || !clz.isAssignableFrom(View.class)){
                    continue;
                }
                BaseExtractor ve = DetailExtractor.MAP.get(clz.getName());
                View v = spy(createView((Class<? extends View>) clz));

                HelpHashMap hhm = new HelpHashMap(ve);
                ve.fillValues(v, hhm, null);

                //TODO: is there any way how to test methods have been called at most 1 time ?
                assertTrue(hhm.size() > 0);
            } catch (Throwable e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private Class<?> tryGetClass(String className) {
        String[] names = new String[]{
                className,
                className.substring(0, className.lastIndexOf(".")) + "$" +
                        className.substring(className.lastIndexOf(".") + 1)
        };
        for (String name : names) {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                //just swallow it
                continue;
            }
        }
        return null;
    }

    private static View createView(Class<? extends View> clz) {
        try {
            return clz.getConstructor(Context.class).newInstance(RuntimeEnvironment.application);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return null;
    }

    private static class HelpHashMap<K, V> extends HashMap<K, V> {

        private BaseExtractor mViewExtractor;

        private HelpHashMap(BaseExtractor viewExtractor) {
            mViewExtractor = viewExtractor;
        }

        @Override
        public V put(K key, V value) {
            if (super.containsKey(key)) {
                throw new IllegalStateException(String.format("Already has key:'%s' Extractor:'%s'", key, mViewExtractor.getClass().getName()));
            }
            return super.put(key, value);
        }
    }
}
