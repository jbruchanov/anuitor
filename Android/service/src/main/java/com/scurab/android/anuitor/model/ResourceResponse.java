package com.scurab.android.anuitor.model;

import com.google.gson.annotations.SerializedName;
import com.scurab.android.anuitor.hierarchy.IdsHelper;

/**
 * Created by jbruchanov on 09/06/2014.
 */
public class ResourceResponse extends DataResponse {
    @SerializedName("Type")
    public IdsHelper.RefType type;

    @SerializedName("Id")
    public int id;
}
