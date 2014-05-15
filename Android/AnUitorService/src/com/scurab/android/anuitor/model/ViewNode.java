package com.scurab.android.anuitor.model;

import com.google.gson.annotations.SerializedName;
import com.scurab.android.anuitor.hierarchy.IdsHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 14:11
 */
public class ViewNode {

    @SerializedName("Nodes")
    private List<ViewNode> nodes;

    @SerializedName("IDi")
    private int mId;
    @SerializedName("IDs")
    private String mIdReadable;

    @SerializedName("Level")
    private final int mLevel;

    @SerializedName("Data")
    private HashMap<String, Object> mData;

    public ViewNode(int id, int level, HashMap<String, Object> data) {
        mId = id;
        mLevel = level;
        mIdReadable = IdsHelper.getValueForId(mId);
        mData = data;
    }

    public void addChild(ViewNode n) {
        if(nodes == null){
            nodes = new ArrayList<ViewNode>();
        }
        nodes.add(n);
    }

    public int getChildCount(){
        return nodes != null ? nodes.size() : 0;
    }

    public ViewNode getChildAt(int index) {
        return nodes.get(index);
    }

    public HashMap<String, Object> getData() {
        return mData;
    }
}
