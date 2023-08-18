package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    public static Map<String, List<Airport>> airports = new HashMap<>(); // List of all airports in the United States

    public static void main(String[] args) throws IOException {

        BufferedReader console = new BufferedReader(new FileReader("C:/Users/aniru/cs61b/Flight_Planner/src/com/company/Airports.csv"));

        String line = console.readLine();
        line = console.readLine();

        // File Reading Process: break down each airport from the csv file into its name, latitude, longitude, and city of residence
        while (line != null) {
            String name = "";
            String lat = "";
            String longit = "";
            String city = "";
            int cutoff = line.indexOf(',') + 1;
            int current = cutoff;

            // Loop to obtain the airport name
            while (current < line.length()) {
                if (line.charAt(current) == ',' && (int)line.charAt(current + 2) <= 57 && (int)line.charAt(current + 2) >= 48) {
                    name = line.substring(cutoff, current);
                    current++;
                    cutoff = current;
                    break;
                } else {
                    current++;
                }

            }

            // Loop to obtain the latitude
            while (line.charAt(current) != ',') {
                current += 1;
            }
            lat = line.substring(cutoff, current);
            cutoff = current + 1;
            current += 1;

            // Loop to obtain the longitude
            while (line.charAt(current) != ',') {
                current += 1;
            }
            longit = line.substring(cutoff, current);

            // City location is everything after the longitude
            city = line.substring(current + 1);

            Airport added = new Airport(name, Double.parseDouble(lat), Double.parseDouble(longit), city);

            // Add this airport to the HashMap of all airports
            if (airports.containsKey(city)) {
                airports.get(city).add(added);
            } else {
                airports.put(city, new ArrayList<>(List.of(added)));
            }

            line = console.readLine();
        }

        /*
        // TEST CASE
	    Airport Fremont = new Airport("Fremont", 13, 18.54151, "Fremont");
        Airport Boston = new Airport("Boston", 27, 14, "Boston");
        Airport Dallas = new Airport("Dallas", 53, 109, "Dallas");

        Flight Frost = new Flight(Fremont, Boston, 34, null, 500, 1432);
        Flight Frallas = new Flight(Fremont, Dallas, 100, null, 15, 324);
        Flight Daston = new Flight(Dallas, Boston, 14, null, 325, 1342);

        ArrayList<Flight> options = new ArrayList<>();
        options.add(Frallas);
        options.add(Daston);
        options.add(Frost);

        Flight_PathFinder trip1 = new Flight_PathFinder(options);

        List<List<Flight>> answer = trip1.flight_options(Fremont, Boston, 0, 2, 11, null, "Stops");

        for (List<Flight> path : answer) {
            System.out.print(path.get(0).start.name);
            for (Flight plane : path) {
                System.out.print(" " + plane.destination.name);
            }
            System.out.println();
        }
         */

    }
}
