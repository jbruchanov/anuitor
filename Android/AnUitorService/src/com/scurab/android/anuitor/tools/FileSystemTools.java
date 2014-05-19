package com.scurab.android.anuitor.tools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import com.scurab.android.anuitor.model.FSItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: jbruchanov
 * Date: 15/05/2014
 * Time: 14:18
 */
public class FileSystemTools {

    /**
     * Get root folders
     *
     * @param context
     * @return
     */
    public static List<FSItem> getRoot(Context context) {
        ArrayList<FSItem> result = new ArrayList<FSItem>();
        File f = new File(String.format("/data/data/%s", context.getPackageName()));
        if (f.exists()) {
            result.add(new FSItem(f.getAbsolutePath(), FSItem.TYPE_FOLDER, 0));
        }
        if (context.checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            String location = Environment.getExternalStorageDirectory().getAbsolutePath();
            result.add(new FSItem(location, FSItem.TYPE_FOLDER, 0));
        }
        return result;
    }


    @SuppressWarnings("UnusedDeclaration")
    private static String[] getMemoryFolders() {
        if (Build.VERSION.SDK_INT > 15) {
            try {
                File[] fs = new File("/storage").listFiles();
                if (fs.length > 0) {
                    List<String> dirs = new ArrayList<String>();
                    for (File f : fs) {
                        dirs.add(f.getName());
                    }
                    Collections.sort(dirs);
                    return dirs.toArray(new String[dirs.size()]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<FSItem> get(String location) {
        File[] files = new File(location).listFiles();

        ArrayList<FSItem> result = new ArrayList<FSItem>();
        try {
            if (files != null) { //if we don't have access it's null
                for (File f : files) {
                    int type = f.isFile() ? FSItem.TYPE_FILE : FSItem.TYPE_FOLDER;
                    FSItem fi = new FSItem(f.getName(), type == FSItem.TYPE_FILE ? f.length() : 0, type);
                    result.add(fi);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(result);
        return result;
    }
}
