package com.scurab.android.anuitor.tools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.test.AndroidTestCase;

import com.scurab.android.anuitor.model.FSItem;

import java.util.List;

/**
 * Created by jbruchanov on 19/05/2014.
 */
public class FileSystemToolsTest extends AndroidTestCase {

    public void testGetAppRoot() {
        String appLocation = String.format("/data/data/%s/", getContext().getPackageName());
        List<FSItem> fsItems = FileSystemTools.get(appLocation);
        assertNotNull(fsItems);
        assertFalse(fsItems.isEmpty());
    }

    public void testGetAppRootCache() {
        String appLocation = String.format("/data/data/%s/cache", getContext().getPackageName());
        List<FSItem> fsItems = FileSystemTools.get(appLocation);
        assertNotNull(fsItems);
        assertFalse(fsItems.isEmpty());
    }

    public void testGetSdCard() {
        Context c = getContext();
        if ((c.checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            String location = Environment.getExternalStorageDirectory().getAbsolutePath();
            List<FSItem> fsItems = FileSystemTools.get(location);
            assertNotNull(fsItems);
            assertFalse(fsItems.isEmpty());
        }
    }

    public void testGetStorages() {
        List<FSItem> root = FileSystemTools.getRoot(getContext());
        assertNotNull(root);
        assertFalse(root.isEmpty());
    }
}
