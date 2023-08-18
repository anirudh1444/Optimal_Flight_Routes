package com.company;

public class Seat {
    boolean isAvailable;
    boolean isAisleSeat;
    boolean isWindowSeat;
    boolean isMiddle;
    String type;
    double cost;

    public Seat (String type, double cost, boolean aisle, boolean window, boolean middle) {
        this.isAvailable = true;
        this.type = type;
        this.cost = cost;
        this.isAisleSeat = aisle;
        this.isWindowSeat = window;
        this.isMiddle = middle;
    }
}
