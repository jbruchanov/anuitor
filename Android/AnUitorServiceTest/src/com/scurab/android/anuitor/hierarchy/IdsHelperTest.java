package com.scurab.android.anuitor.hierarchy;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.scurab.android.anuitor.C;
import com.scurab.android.anuitor.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by jbruchanov on 12.6.2014.
 */
@Config(manifest = C.MANIFEST, emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class IdsHelperTest {

    @Test
    public void testLoadValues() throws NoSuchFieldException, ClassNotFoundException {
        IdsHelper.VALUES.clear();
        assertEquals(0, IdsHelper.VALUES.size());
        IdsHelper.loadValues(R.class);
        assertTrue(IdsHelper.VALUES.size() > 0);
        int count = 0;
        boolean hasAndroid = false;
        boolean hasOwn = false;
        for (String s : IdsHelper.VALUES.keySet()) {
            count += IdsHelper.VALUES.get(s).size();
            hasAndroid |= s.contains("android.R$");
            hasOwn |= s.startsWith("R$");
        }

        assertTrue(count > 0);
        assertTrue(hasAndroid);
        assertTrue(hasOwn);
    }

    @Test
    public void testGetJson() throws NoSuchFieldException, ClassNotFoundException {
        IdsHelper.loadValues(R.class);
        String s = IdsHelper.toJson();
        HashMap hashMap = new Gson().fromJson(s, HashMap.class);
        assertNotNull(hashMap);
        assertEquals(hashMap.size(), IdsHelper.VALUES.size());
    }

    @Test
    public void testGetJsonWithResources() throws NoSuchFieldException, ClassNotFoundException {
        IdsHelper.loadValues(com.scurab.android.anuitorsample.R.class);
        String s = IdsHelper.toJson(Robolectric.application.getResources());
        HashMap hashMap = new Gson().fromJson(s, HashMap.class);
        List<LinkedTreeMap> list = (List<LinkedTreeMap>) hashMap.get("R$layout");

        for (LinkedTreeMap v : list) {
            assertNotNull(v.get("Key"));
            assertNotNull(v.get("Value"));
        }
    }
}
