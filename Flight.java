package com.company;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Flight {
    Airport start;
    Airport destination;
    int capacity;
    Seat[][] seats;
    int departureTime;
    int arrivalTime;
    double distance;

    /* Initialize each flight with its starting and ending destinations, total capacity, seating arrangements,
     departure and arrival times, and total distance
     */
    public Flight (Airport start, Airport destination, int capacity, Seat[][] seats, int departureTime, int arrivalTime) {
        this.start = start;
        this.destination = destination;
        this.capacity = capacity;
        this.seats = seats;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;

        this.distance = Math.sqrt(Math.pow(start.x - destination.x, 2) - Math.pow(start.y - destination.y, 2));
    }

    public double cheapest_seat (String seatType, boolean window, boolean aisle, boolean middle) {
        double lowest = Double.POSITIVE_INFINITY;

        // Parse through every seat in the flight to locate the cheapest one which complies with the user's restrictions
        for (Seat[] row : seats) {
            for (Seat spot : row) {

                // Checks for availability and correct class
                if (!spot.isAvailable || !Objects.equals(seatType, spot.type)) {
                    continue;
                }

                // Checks if the seat is window, aisle, or middle and if the user permits it
                if ((window && !spot.isWindowSeat) || (aisle && spot.isAisleSeat) || (middle && spot.isMiddle)) {
                    lowest = Math.min(lowest, spot.cost);
                }
            }
        }

        // Return the cheapest option for the user
        return lowest;
    }
}
