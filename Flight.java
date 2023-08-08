package com.company;

public class Flight {
    Airport start;
    Airport destination;
    //int capacity;
    //Seat[][] seats;
    int departureTime;
    int arrivalTime;
    int distance;

    public Flight (Airport start, Airport destination, int capacity, Seat[][] seats, int departureTime, int arrivalTime) {
        this.start = start;
        this.destination = destination;
        //this.capacity = capacity;
        //this.seats = seats;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public int cheapest_seat () {
        int lowest = 10;
        /*for (Seat[] row : seats) {
            for (Seat seat : row) {
                lowest = Math.min(lowest, seat.cost);
            }
        } */

        return lowest;
    }
}
