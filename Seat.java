package com.company;

public class Seat {
    boolean available;
    boolean aisle_seat;
    boolean window_seat;
    String type;
    int cost;

    public Seat (String type, int cost) {
        this.available = true;
        this.type = type;
        this.cost = cost;
    }

    public boolean reserve () {
        if (this.available) {
            this.available = false;
            return true;
        }
        return false;
    }

    public boolean remove () {
        if (!this.available) {
            this.available = true;
            return true;
        }
        return false;
    }

    private void changeCost (int newcost) {
        this.cost = newcost;
    }
}
