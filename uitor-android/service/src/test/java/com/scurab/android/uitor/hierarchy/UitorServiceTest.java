package com.scurab.android.uitor.hierarchy;

import com.scurab.android.uitor.R;
import com.scurab.android.uitor.service.UitorService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@SuppressWarnings("KotlinInternalInJava")
@RunWith(RobolectricTestRunner.class)
public class UitorServiceTest {

    @Test
    public void testLoadsIdsOnCreate() {
        UitorService ser = spy(new UitorService());
        String name = R.class.getName();
        //remove '.R' at the end
        name = name.substring(0, name.length() - 2);
        doReturn(name).when(ser).getPackageName();
        //noinspection KotlinInternalInJava
        IdsHelper./*protected*/getData$service_debug().clear();
        ser.onCreate();
        assertTrue(IdsHelper.getData$service_debug().size() > 0);
    }
}
