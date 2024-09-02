package com.company;

import java.util.*;
import org.bson.Document;
import org.bson.types.ObjectId;

public class PathFinding {

    String start;
    String destination;
    String startTime;
    double layoverLimit;
    double priceLimit;
    String requiredClass;
    String sort;
    List<String> locations;
    List<Path> options;

    boolean Boeing737Toggle;

    // Arguments : startIATA, destIATA, startTime, layoverLimit, priceLimit, class, sort, (Viable locations), True/False for allowing Boeing 737
    public PathFinding (String[] args) {

        // Initializes variables
        this.start = args[1];
        this.destination = args[2];
        this.startTime = args[3];
        this.layoverLimit = Math.min(Integer.parseInt(args[4]), 3);
        this.priceLimit = Double.parseDouble(args[5]);
        this.requiredClass = args[6];
        this.sort = args[7];
        this.locations = new ArrayList<>(Arrays.asList(args[8].split(",")));
        this.options = new ArrayList<>();
        this.Boeing737Toggle = Boolean.parseBoolean(args[9]);

        // Calls DFS algorithm
        traversal(start, destination, startTime, new Path(start));

        // Triggers appropriate sorting comparator
        if (sort.equals("Price")) {
            options.sort(new SortByPrice());
        } else if (sort.equals("Stops")){
            options.sort(new SortByStops());
        }

        displayOptions();
    }

    //Price Sorting Comparator
    public static class SortByPrice implements Comparator<Path> {

        @Override
        public int compare(Path o1, Path o2) {
            return Double.compare(o1.cost, o2.cost);
        }
    }

    // Layovers Sorting Comparator
    public static class SortByStops implements Comparator<Path> {

        @Override
        public int compare(Path o1, Path o2) {
            return o1.cities.size() - o2.cities.size();
        }
    }

    public void traversal (String start, String destination, String arrivalTime, Path path) {

        // Ends this line of search if conditions are exceeded
        if (path.layovers > layoverLimit || path.cost > priceLimit) {
            return;
        }

        // Adds path, number of stops, and cost if destination has been reached
        if (start.equals(destination)) {
            options.add(path);
            return;
        }

        // All outgoing flights from the current airport
        Object all_flights = Main.airports.find(new Document("IATA", start)).first().get("outgoingFlights");

        // Triggers traversal for each outgoing flight from this specific starting airport
        for (ObjectId flight_id : (ArrayList<ObjectId>) all_flights) {

            // Flight document
            Document flight = Main.flights.find(new Document("_id", flight_id)).first();

            // Remove all Boeing 737s from the search parameters if requested by the user
            if (!Boeing737Toggle && "Boeing".equals(flight.get("brand")) && "737".equals(flight.get("model"))) {
                continue;
            }

            // Find best seat fitting the conditions
            double bestSeat = cheapestSeat(flight);

            // Not viable option if this flight departs before the previous one arrived or if there is no permissible seat
            if (arrivalTime.compareTo((String) flight.get("departureTime")) > 0 || bestSeat == Double.POSITIVE_INFINITY) {
                continue;
            }

            // Next flight in DFS search
            Path new_path = new Path(new ArrayList<>(path.cities), new ArrayList<>(path.flights), path.cost + bestSeat, path.layovers + 1);
            new_path.cities.add((String) flight.get("destIATA"));
            new_path.flights.add(flight_id);
            traversal((String) flight.get("destIATA"), destination, (String) flight.get("arrivalTime"), new_path);
        }
    }

    public double cheapestSeat(Document flight) {

        double cheapest_seat = Double.POSITIVE_INFINITY;

        for (Document seat : (List<Document>) flight.get("seats")) {
            if (seat.get("class").equals(requiredClass) && locations.contains((String)seat.get("location"))) {
                cheapest_seat = Math.min(cheapest_seat, (Double) seat.get("cost"));
            }
        }

        return cheapest_seat;
    }

    public void displayOptions() {

        if (options.isEmpty()) {
            System.out.println("NO PATHS FOUND! Please alter your search parameters.");
        }

        for (Path option : options) {

            List<String> stops = option.cities;

            for (int index = 0; index < stops.size() - 1; index++) {
                System.out.print(stops.get(index) + " --> ");
            }

            System.out.print(stops.get(stops.size() - 1));
            System.out.println(" Total Cost: $" + option.cost);
        }
    }
}
