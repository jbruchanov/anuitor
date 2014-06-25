package com.scurab.android.anuitor.extract;

import android.content.Context;
import android.view.View;

import org.robolectric.Robolectric;

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

    public void doTests() {
        assertTrue(DetailExtractor.MAP.size() > 0);

        for (Class<?> clz : DetailExtractor.MAP.keySet()) {
            if(Modifier.isAbstract(clz.getModifiers()) || !clz.isAssignableFrom(View.class)){
                continue;
            }
            BaseExtractor<View> ve = (BaseExtractor<View>) DetailExtractor.MAP.get(clz);
            View v = spy(createView((Class<? extends View>) clz));

            HelpHashMap hhm = new HelpHashMap(ve);
            ve.fillValues(v, hhm, null);

            //TODO: is there any way how to test methods have been called at most 1 time ?
            assertTrue(hhm.size() > 0);
        }
    }

    private static View createView(Class<? extends View> clz) {
        try {
            return clz.getConstructor(Context.class).newInstance(Robolectric.application);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return null;
    }

    private static class HelpHashMap<K, V> extends HashMap<K, V> {

        private BaseExtractor<View> mViewExtractor;

        private HelpHashMap(BaseExtractor<View> viewExtractor) {
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
