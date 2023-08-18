package com.company;

import java.util.*;

public class Flight_PathFinder {
    List<Flight> flights;
    double layovers = Double.POSITIVE_INFINITY;
    double priceLimit = Double.POSITIVE_INFINITY;
    String requiredClass;
    Map<Airport, ArrayList<Flight>> planes = new HashMap<>();
    boolean window;
    boolean aisle;
    boolean middle;


    public Flight_PathFinder (ArrayList<Flight> flights) {
        this.flights = flights; // All available flights

        //Assigns to a hashmap (key: airport, value: all outgoing flights)
        for (Flight flight : flights) {
            ArrayList<Flight> departures = planes.getOrDefault(flight.start, new ArrayList<>());
            departures.add(flight);
            planes.put(flight.start, departures);
        }
    }

    //Price Sorting Comparator
    public static class SortByPrice implements Comparator<List<Object>> {

        @Override
        public int compare(List<Object> first, List<Object> second) {
            return (int)first.get(2) - (int)second.get(2);
        }
    }

    //Layovers Sorting Comparator
    public static class SortByStops implements Comparator<List<Object>> {

        @Override
        public int compare(List<Object> first, List<Object> second) {
            return (int)first.get(1) - (int)second.get(1);
        }
    }


    public List<List<Flight>> flight_options (Airport start, Airport destination, int startTime, double layovers, double priceLimit, String requiredClass, String sort, boolean window, boolean aisle, boolean middle) {

        // Initializes restrictions
        this.layovers = Math.min(layovers, 5);
        this.priceLimit = priceLimit;
        this.requiredClass = requiredClass;
        this.window = window; // User is fine with a window seat
        this.aisle = aisle; // User is fine with an aisle seat
        this.middle = middle; // User is fine with a middle seat

        // List storing all potential travel paths and corresponding attributes
        List<List<Object>> options = new ArrayList<>();

        // Calls DFS algorithm
        traversal(start, destination, startTime, -1, 0, new ArrayList<>(), options);

        // Triggers appropriate sorting comparator
        List<List<Flight>> answer = new ArrayList<>();
        if (Objects.equals(sort, "Price")) {
            options.sort(new SortByPrice());
        } else if (Objects.equals(sort, "Stops")){
            options.sort(new SortByStops());
        }

        // Adds all valid paths in order
        for (List<Object> path : options) {
            answer.add((List<Flight>) path.get(0));
        }

        return answer;
    }

    public void traversal (Airport start, Airport destination, int arrivalTime, int stops, double cost, ArrayList<Flight> path, List<List<Object>> options) {

        // Ends this line of search if conditions are exceeded
        if (stops > layovers || cost > priceLimit) {
            return;
        }

        // Adds path, number of stops, and cost if destination has been reached
        if (start == destination) {
            List<Object> solution = new ArrayList<>();
            solution.add(path);
            solution.add(stops);
            solution.add(cost);
            options.add(solution);
            return;
        }

        // Triggers traversal for each outgoing flight from this specific starting airport
        for (Flight flight : planes.get(start)) {

            // Find best seat fitting the conditions
            double bestSeat = flight.cheapest_seat(requiredClass, window, aisle, middle);

            // Not viable option if this flight departs before the previous one arrived or if there is no permissible seat
            if (flight.departureTime <= arrivalTime || bestSeat == Double.POSITIVE_INFINITY) {
                continue;
            }

            // Next flight in DFS search
            ArrayList<Flight> new_path = new ArrayList<>(path);
            new_path.add(flight);
            traversal(flight.destination, destination, flight.arrivalTime, stops + 1, cost + bestSeat, new_path, options);
        }

    }

}
