package com.econo21.pinning;

public class Pin {
    private String pin_name;
    private String x;
    private String y;
    private String category;

    public Pin(){}

    public Pin(String name, String x, String y, String category){
        this.pin_name = name;
        this.x = x;
        this.y = y;
        this.category = category;
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
}
