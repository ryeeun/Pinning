package com.econo21.pinning;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Category {
    private String name;
    private String color;;
    private String cid;

    public Category(){}

    protected Category(String name, String color, String cid){
        this.name = name;
        this.color = color;
        this.cid = cid;
    }

    public String getName(){
        return name;
    }

    public String getColor(){
        return color;
    }

    public String getCid() { return cid; }


}
