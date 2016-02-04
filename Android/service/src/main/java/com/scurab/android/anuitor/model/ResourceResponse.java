package com.scurab.android.anuitor.model;

import com.google.gson.annotations.SerializedName;
import com.scurab.android.anuitor.hierarchy.IdsHelper;

/**
 * Created by jbruchanov on 09/06/2014.
 */
public class ResourceResponse {
    @SerializedName("Type")
    public IdsHelper.RefType type;

    @SerializedName("Id")
    public int id;

    @SerializedName("Name")
    public String name;

    @SerializedName("Data")
    public Object data;

    @SerializedName("Context")
    public Object context;

    @SerializedName("DataType")
    public String dataType;
}
