package com.econo21.pinning;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Category {
    private String name;
    private String color;;

    public Category(){}

    protected Category(String name, String color){
        this.name = name;
        this.color = color;
    }

    public String getName(){
        return name;
    }

    public String getColor(){
        return color;
    }


}
