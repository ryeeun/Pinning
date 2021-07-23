package com.econo21.pinning;

public class Pin {
    private String pin_name;
    private String x;
    private String y;
    private String category;
    private String color;

    public Pin(){}

    public Pin(String name, String x, String y, String category, String color){
        this.pin_name = name;
        this.x = x;
        this.y = y;
        this.category = category;
        this.color = color;
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

}
