package com.scurab.android.anuitor.model;

import com.google.gson.annotations.SerializedName;
import com.scurab.android.anuitor.extract.view.ViewExtractor;
import com.scurab.android.anuitor.hierarchy.IdsHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 14:11
 */
public class ViewNode {

    @SerializedName("Nodes")
    private List<ViewNode> mNodes;

    @SerializedName("IDi")
    private int mId;
    @SerializedName("IDs")
    private String mIdReadable;

    @SerializedName("Level")
    private final int mLevel;

    @SerializedName("Position")
    private final int mPosition;

    @SerializedName("Data")
    private HashMap<String, Object> mData;

    public ViewNode(int id, int level, int position, HashMap<String, Object> data) {
        mId = id;
        mLevel = level;
        mIdReadable = IdsHelper.getNameForId(mId);
        mPosition = position;
        mData = data;

        mData.put("Position", mPosition);
        validateDataSet(mData, position);
    }

    public void addChild(ViewNode n) {
        if(mNodes == null){
            mNodes = new ArrayList<ViewNode>();
        }
        mNodes.add(n);
    }

    public int getChildCount(){
        return mNodes != null ? mNodes.size() : 0;
    }

    public ViewNode getChildAt(int index) {
        return mNodes.get(index);
    }

    public HashMap<String, Object> getData() {
        return mData;
    }

    public int getId() {
        return mId;
    }

    public int getLevel() {
        return mLevel;
    }

    public int getPosition() {
        return mPosition;
    }

    void validateDataSet(HashMap<String, Object> data, int position) {
        for (String key : ViewExtractor.MANDATORY_KEYS) {
            if (!data.containsKey(key)) {
                throw new IllegalStateException(String.format("Missing mandatory field:'%s' on View with position:%s\n\tMandatoryFields:%s", key, position, Arrays.toString(ViewExtractor.MANDATORY_KEYS)));
            }
        }
    }
}
