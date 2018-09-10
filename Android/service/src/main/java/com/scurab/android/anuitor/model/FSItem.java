package com.scurab.android.anuitor.model;

import com.google.gson.annotations.Expose;

/**
 * User: jbruchanov
 * Date: 15/05/2014
 * Time: 14:13
 */
public class FSItem implements Comparable<FSItem> {

    public static int TYPE_PARENT_FOLDER = -1;
    public static int TYPE_FILE = 1;
    public static int TYPE_FOLDER = 2;

    private String Name;
    private String Size;
    @Expose
    private long mSize;
    private int Type;

    public FSItem(String name, int type, long size) {
        if (name == null) {
            throw new IllegalArgumentException("name is null!");
        }
        Name = name;
        mSize = size;
        Size = String.valueOf(size);
        Type = type;
    }

    public String getName() {
        return Name;
    }

    public long getSize() {
        return mSize;
    }

    public int getType() {
        return Type;
    }

    @Override
    public int compareTo(FSItem o) {
        if (Type == o.Type) {
            return Name.compareTo(o.Name);
        } else {
            return -(Type - o.Type);
        }
    }
}
