package com.econo21.pinning;

public class Pin {
    private String pin_name;
    private String x;
    private String y;

    public Pin(){}

    public Pin(String name, String x, String y){
        this.pin_name = name;
        this.x = x;
        this.y = y;
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
}
