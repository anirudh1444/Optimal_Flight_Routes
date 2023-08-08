package com.company;

import java.util.*;

public class Flight_PathFinder {
    List<Flight> flights;
    double layovers = Double.POSITIVE_INFINITY;
    double priceLimit = Double.POSITIVE_INFINITY;
    String requiredClass;
    Map<Airport, ArrayList<Flight>> planes = new HashMap<>();


    public Flight_PathFinder (ArrayList<Flight> flights) {
        this.flights = flights; // All available flights

        //Assigns to a hashmap (key: airport, value: all outgoing flights)
        for (Flight flight : flights) {
            ArrayList<Flight> departures = planes.getOrDefault(flight.start, new ArrayList<>());
            departures.add(flight);
            planes.put(flight.start, departures);
        }

    }

    public List<List<Flight>> flight_options (Airport start, Airport destination, double layovers, double priceLimit, String requiredClass, String sort) {

        //Initializes restrictions
        if (layovers != -1) {
            this.layovers = layovers;
        }

        if (priceLimit != -1) {
            this.priceLimit = priceLimit;
        }

        if (requiredClass != null) {
            this.requiredClass = requiredClass;
        }

        //List storing all potential travel paths and corresponding attributes
        List<List<Object>> options = new ArrayList<>();

        //Calls DFS algorithm
        traversal(start, destination, -1, 0, new ArrayList<>(), options);

        //Price Sorting Comparator
        class SortByPrice implements Comparator<List<Object>> {

            @Override
            public int compare(List<Object> first, List<Object> second) {
                return (int)first.get(2) - (int)second.get(2);
            }
        }

        //Stop Count Sorting Comparator
        class SortByStops implements Comparator<List<Object>> {

            @Override
            public int compare(List<Object> first, List<Object> second) {
                return (int)first.get(1) - (int)second.get(1);
            }
        }

        //Triggers appropriate sorting comparator
        List<List<Flight>> answer = new ArrayList<>();
        if (Objects.equals(sort, "Price")) {
            options.sort(new SortByPrice());
        } else if (Objects.equals(sort, "Stops")){
            options.sort(new SortByStops());
        }

        //Adds all valid paths in order
        for (List<Object> path : options) {
            answer.add((List<Flight>) path.get(0));
        }

        return answer;
    }

    public void traversal (Airport start, Airport destination, int stops, int cost, ArrayList<Flight> path, List<List<Object>> options) {

        //Ends this line of search if conditions are exceeded
        if (stops > layovers || cost > priceLimit) {
            return;
        }

        //Adds path, number of stops, and cost if destination has been reached
        if (start == destination) {
            List<Object> solution = new ArrayList<>();
            solution.add(path);
            solution.add(stops);
            solution.add(cost);
            options.add(solution);
            return;
        }

        //Triggers traversal for each outgoing flight from this specific starting airport
        for (Flight flight : planes.get(start)) {
            ArrayList<Flight> new_path = new ArrayList<>(path);
            new_path.add(flight);
            traversal(flight.destination, destination, stops + 1, cost  + flight.cheapest_seat(), new_path, options);
        }

    }

}
