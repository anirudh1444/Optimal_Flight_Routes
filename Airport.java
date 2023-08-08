package com.company;

public class Airport {
    String name;
    String city;
    double x;
    double y;

    public Airport(String name, double Xcoordinate, double Ycoordinate, String city) {
        this.name = name;
        this.x = Xcoordinate;
        this.y = Ycoordinate;
        this.city = city;
    }
}
