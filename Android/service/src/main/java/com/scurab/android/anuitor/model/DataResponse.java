package com.scurab.android.anuitor.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JBruchanov on 06/02/2016.
 */
public class DataResponse {

    @SerializedName("Name")
    public String name;

    @SerializedName("Data")
    public Object data;

    @SerializedName("Context")
    public Object context;

    @SerializedName("DataType")
    public String dataType;
}
