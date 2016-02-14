package com.scurab.android.anuitor.tools;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.test.AndroidTestCase;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scurab.android.anuitor.extract.view.ReflectionExtractor;

import junit.framework.TestCase;

import org.apache.commons.io.input.ReaderInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by JBruchanov on 08/02/2016.
 */
public class ExtractTest extends AndroidTestCase {

    private static final boolean CODE_GEN = true;

    public void testGenSampleDataForClass() throws IOException {
        final InputStream is = getContext().getAssets().open("src.txt");
        byte[] data = new byte[is.available()];
        is.read(data, 0, data.length);
        String srcCode = new String(data);

        Class<? extends Object> clz = GridLayout.class;
        Log.v("SampleDataForClass", "---------------" + clz.getName());
        final Method[] declaredMethods = clz.getDeclaredMethods();
        List<Pair<String, Class<?>>> result = new ArrayList<>();
        for (Method m : declaredMethods) {
            boolean isPublic = Modifier.isPublic(m.getModifiers());
            if (!isPublic) {
                continue;
            }
            String name = m.getName();
            if (!ReflectionExtractor.ignoreMethod(name)) {
                Class<?> returnType = m.getReturnType();
                if (m.getParameterTypes().length == 0 && !returnType.equals(Void.TYPE)) {
                    result.add(new Pair<String, Class<?>>(name, returnType));
                }
            }
        }
        Collections.sort(result, new Comparator<Pair<String, Class<?>>>() {
            @Override
            public int compare(Pair<String, Class<?>> lhs, Pair<String, Class<?>> rhs) {
                return lhs.first.compareTo(rhs.first);
            }
        });
        for (Pair<String, Class<?>> name : result) {
            String methodCall = name.first + "()";
            boolean isPrimitive = name.second.isPrimitive();
            final boolean defined = srcCode.contains(methodCall);
            if (CODE_GEN) {
                if (!defined) {
                    String varName = "v";
                    String call = isPrimitive ? String.format("%s.%s()", varName, name.first) : String.format("String.valueOf(%s.%s())", varName, name.first);
                    Log.v("SampleDataForClass", String.format("data.put(\"%s\", %s);", convertName(name.first), call));
                }
            } else {
                Log.v("SampleDataForClass", String.format("%s%s() [%s] public:%s", defined ? "SKIP " : "", name.first, name.second, Modifier.isPublic(name.second.getModifiers())));
            }
        }
    }

    public static String convertName(String name) {
        name = name.replace("get", "");
        name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        return name;
    }

    public static final class Pair<K, V> {
        public K first;
        public V second;

        public Pair(K first, V second) {
            this.first = first;
            this.second = second;
        }

        public Pair() {
        }
    }
}
