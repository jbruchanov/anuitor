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
    public static List<FSItem> get(Context context) {
        ArrayList<FSItem> result = new ArrayList<FSItem>();
        File f = new File(String.format("/data/data/%s", context.getPackageName()));
        if (f.exists()) {
            result.add(new FSItem(f.getAbsolutePath(), FSItem.TYPE_FOLDER, 0));
        }
        if (context.checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) { //better way if you have multiple storages
                try {
                    File[] fs = new File("/storage").listFiles();
                    if (fs != null) {
                        for (File file : fs) {
                            if (file.isDirectory()) {
                                result.add(new FSItem(file.getAbsolutePath(), FSItem.TYPE_FOLDER, 0));
                            }
                        }
                    }
                } catch (Exception e) {
                    //swallow it and just grab default values from Environment class
                }
            }
            String location = Environment.getExternalStorageDirectory().getAbsolutePath();
            result.add(new FSItem(location, FSItem.TYPE_FOLDER, 0));
        }
        //TODO: add / for rooted devices ?
        return result;
    }

    /**
     * Get items for specific location
     *
     * @param location
     * @return
     */
    public static List<FSItem> get(File location) {
        File[] files = location.listFiles();

        ArrayList<FSItem> result = new ArrayList<FSItem>();
        try {
            if (files != null) { //if we don't have access it's null
                for (File f : files) {
                    int type = f.isFile() ? FSItem.TYPE_FILE : FSItem.TYPE_FOLDER;
                    FSItem fi = new FSItem(f.getName(), type, type == FSItem.TYPE_FILE ? f.length() : 0);
                    result.add(fi);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(result);
        return result;
    }

    @SuppressWarnings("UnusedDeclaration")
    private static String[] getSDCardFolders() {
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

    /**
     * Delete folder including internal files/folders
     * @param folder
     */
    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    /**
     * Returns file extension or null if there is no extension
     * @param f
     * @return
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}
