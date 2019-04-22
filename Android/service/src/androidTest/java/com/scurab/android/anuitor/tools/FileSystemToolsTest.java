package com.scurab.android.anuitor.tools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.scurab.android.anuitor.model.FSItem;

import java.io.File;
import java.util.List;

/**
 * Created by jbruchanov on 19/05/2014.
 */
public class FileSystemToolsTest extends AndroidTestCase {

    public void testGetAppRoot() {
        String appLocation = String.format("/data/data/%s/", getContext().getPackageName());
        List<FSItem> fsItems = FileSystemTools.get(new File(appLocation));
        assertNotNull(fsItems);
        assertFalse(fsItems.isEmpty());
    }

    public void ignore_testGetAppRootCache() {
        String appLocation = String.format("/data/data/%s/cache", getContext().getPackageName());
        List<FSItem> fsItems = FileSystemTools.get(new File(appLocation));
        assertNotNull(fsItems);
        assertFalse(fsItems.isEmpty());
    }

    public void testGetSdCard() {
        Context c = getContext();
        if ((c.checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            String location = Environment.getExternalStorageDirectory().getAbsolutePath();
            List<FSItem> fsItems = FileSystemTools.get(new File(location));
            assertNotNull(fsItems);
            assertFalse(fsItems.isEmpty());
        }
    }

    public void testGetStorages() {
        List<FSItem> root = FileSystemTools.get(getContext());
        assertNotNull(root);
        assertFalse(root.isEmpty());
    }
}
