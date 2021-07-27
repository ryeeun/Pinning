package com.econo21.pinning;

import java.io.Serializable;
import java.util.List;

public class Pin implements Serializable{
    private String pin_name;
    private String x;
    private String y;
    private String address;
    private String category;
    private String color;
    private List<String> photo;
    private List<String> uri;
    private String contents;
    private String id;

    public Pin(){}

    public Pin(String name, String x, String y,String address, String category, String color, List<String> photo, List<String> uri, String contents, String id){
        this.pin_name = name;
        this.x = x;
        this.y = y;
        this.address = address;
        this.category = category;
        this.color = color;
        this.photo = photo;
        this.uri = uri;
        this.contents = contents;
        this.id = id;
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

    public String getAddress() {return address;}

    public String getCategory() { return category; }

    public String getColor() { return color; }

    public List<String> getPhoto() { return photo; }  // downloadURL

    public List<String> getUri() {return uri;}  // image file in storage

    public String getContents() { return contents;}

    public void setId(String id) { this.id = id; }

    public String getId() {return id;}

}
