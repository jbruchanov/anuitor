package com.scurab.android.anuitor.hierarchy;

import com.scurab.android.anuitor.R;
import com.scurab.android.anuitor.service.AnUitorService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * Created by jbruchanov on 23/06/2014.
 */
@RunWith(RobolectricTestRunner.class)
public class AnUitorServiceTest {

    @Test
    public void testLoadsIdsOnCreate() {
        AnUitorService ser = spy(new AnUitorService());
        String name = R.class.getName();
        //remove '.R' at the end
        name = name.substring(0, name.length() - 2);
        doReturn(name).when(ser).getPackageName();
        IdsHelper./*protected*/VALUES.clear();
        ser.onCreate();
        assertTrue(IdsHelper.VALUES.size() > 0);
    }
}
