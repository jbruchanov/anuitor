package com.scurab.android.anuitor.tools;

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

    public static List<FSItem> get(String location) {
        File[] files = new File(location).listFiles();

        ArrayList<FSItem> result = new ArrayList<FSItem>();
        try {
            if(files != null){ //if we don't have access it's null
                for (File f : files) {
                    int type = f.isFile() ? FSItem.TYPE_FILE : FSItem.TYPE_FOLDER;
                    FSItem fi = new FSItem(f.getName(), type == FSItem.TYPE_FILE ?  f.length() : 0, type);
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
