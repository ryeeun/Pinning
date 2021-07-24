package com.econo21.pinning;

import java.io.Serializable;
import java.util.List;

public class Pin implements Serializable{
    private String pin_name;
    private String x;
    private String y;
    private String category;
    private String color;
    private List<String> photo;
    private String contents;

    public Pin(){}

    public Pin(String name, String x, String y, String category, String color, List<String> photo, String contents){
        this.pin_name = name;
        this.x = x;
        this.y = y;
        this.category = category;
        this.color = color;
        this.photo = photo;
        this.contents = contents;
    }

    public String getPin_name(){
        return pin_name;
    }

    public String getX(){
        return x;
    }

    public String getY(){
        return y;
    }

    public String getCategory() { return category; }

    public String getColor() { return color; }

    public List<String> getPhoto() { return photo; }

    public String getContents() { return contents;}

}
