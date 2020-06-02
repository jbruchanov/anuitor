package com.scurab.android.uitor.model;

import com.scurab.android.uitor.Constants;
import com.scurab.android.uitor.hierarchy.IdsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ViewNode {

    private List<ViewNode> Nodes;
    private int IDi;
    private String IDs;
    private final int Level;
    private final int Position;
    private Map<String, Object> Data;
    private final String Owner;

    public ViewNode(int id, int level, int position, Map<String, Object> data) {
        IDi = id;
        Level = level;
        IDs = IdsHelper.getNameForId(IDi);
        Position = position;
        Data = data;

        Data.put("Position", Position);
        validateDataSet(Data, position);
        Object owner = data.get(Constants.OWNER);
        Owner = owner != null ? owner.toString() : "null";
        data.remove(Constants.OWNER);
    }

    public void addChild(ViewNode n) {
        if(Nodes == null){
            Nodes = new ArrayList<>();
        }
        Nodes.add(n);
    }

    public int getChildCount(){
        return Nodes != null ? Nodes.size() : 0;
    }

    public ViewNode getChildAt(int index) {
        return Nodes.get(index);
    }

    public Map<String, Object> getData() {
        return Data;
    }

    public int getId() {
        return IDi;
    }

    public int getLevel() {
        return Level;
    }

    public int getPosition() {
        return Position;
    }

    void validateDataSet(Map<String, Object> data, int position) {
        String[] mandatoryKeys = Constants.getMandatoryKeys();
        for (String key : mandatoryKeys) {
            if (!data.containsKey(key)) {
                throw new IllegalStateException(String.format("Missing mandatory field:'%s' on View with position:%s\n\tMandatoryFields:%s", key, position, Arrays.toString(mandatoryKeys)));
            }
        }
    }

    //seems like necessary evil, gson/jackson have troubles to do it
    //because of serializing using reflection => circular references
    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("IDi", IDi);
        obj.put("IDs", IDs);
        obj.put("Level", Level);
        obj.put("Position", Position);
        obj.put(Constants.OWNER, Owner);
        if (Data != null) {
            JSONObject data = new JSONObject();
            for (String key : Data.keySet()) {
                data.put(key, Data.get(key));
            }
            obj.put("Data", data);
        }
        if (Nodes != null) {
            JSONArray array = new JSONArray();
            obj.put("Nodes", array);
            for (ViewNode node : Nodes) {
                array.put(node.toJson());
            }
        }
        return obj;
    }
}
