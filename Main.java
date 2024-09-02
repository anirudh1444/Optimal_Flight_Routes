package com.company;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final String URI = "mongodb://localhost:27017";
    public static final MongoClient mongoClient = MongoClients.create(URI);
    public static MongoDatabase database = mongoClient.getDatabase("Flight_Planning"); // Entire database
    public static MongoCollection<Document> airports = database.getCollection("Airports"); // Airports collections
    public static MongoCollection<Document> flights = database.getCollection("Flights"); // Flights collection

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            throw new IllegalArgumentException("Incorrect Argument Count");
        }

        switch (args[0]) {
            case "Setup": setupPersistence(args); break;
            case "addAirport": addAirport(args); break;
            case "addFlight": addFlight(args); break;
            case "removeFlight": removeFlight(args); break;
            case "pathFind":  pathFind(args); break;
            default: throw new IllegalArgumentException("Invalid Request");
        }
    }

    // Reads the CSV file of airports and inputs this data into MongoDB
    public static void setupPersistence(String[] args) throws IOException {

        validateArgs(1, args);

        // Reading from CSV file containing and real-world airports
        BufferedReader console = new BufferedReader(new FileReader("C:/Users/aniru/Flight_Planner/PathFinder/src/main/java/com/company/Airports.csv"));

        String line = console.readLine();
        line = console.readLine();

        // File Reading Process: break down each airport from the csv file into its name, latitude, longitude, and city of residence
        while (line != null) {

            // Break down into all the different attributes given for each airport (number, name, latitude, longitude, and city)
            String[] attributes = line.split(",");
            attributes = new String[]{"Placeholder", attributes[0], attributes[2], attributes[3], attributes[4]};

            for (int i = 1; i < attributes.length; i++) {
                if (!attributes[i].isEmpty()) {
                    attributes[i] = attributes[i].substring(1, attributes[i].length() - 1);
                }
            }

            // Add the airport into MongoDB
            addAirport(attributes);
            line = console.readLine();
        }
    }

    // Contains arguments : IATA, name, country, city
    public static void addAirport(String[] args) {

        validateArgs(5, args);

        if (!validateAirport(args)) {
            return;
        }

        // Setup document with relevant information
        Document airport = new Document("_id", new ObjectId())
                .append("IATA", args[1])
                .append("name", args[2])
                .append("country", args[3])
                .append("city", args[4])
                .append("outgoingFlights", new ArrayList<>());

        // Insert the document in MongoDB
        airports.insertOne(airport);
    }

    // Contains arguments: startAirportIATA, destAirportIATA, departureTime, arrivalTime, Remaining: Seats (Code, Class, Cost, Window / Aisle / Middle)
    public static void addFlight(String[] args) {

        // List containing all the seats in the flight
        List<Document> seats = new ArrayList<>();

        // Construct a document for each seat
        for (int index = 5; index < args.length; index++) {

            args[index] = args[index].substring(1, args[index].length() - 1);
            String[] information = args[index].split(",");

            Document seat = new Document("_id", new ObjectId())
                    .append("code", information[0])
                    .append("class", information[1])
                    .append("available", true)
                    .append("cost", Double.parseDouble(information[2]))
                    .append("location", information[3]);

            // Add the seat to the list
            seats.add(seat);
        }

        // Generate an ID for the flight
        ObjectId id = new ObjectId();

        Document flight = new Document("_id", id)
                .append("startIATA", args[1])
                .append("destIATA", args[2])
                .append("arrivalTime", args[3])
                .append("departureTime", args[4])
                .append("seats", seats);

        // Obtain the document of the starting airport
        Document filter = new Document("IATA", args[1]);
        Object planes = airports.find(filter).first().get("outgoingFlights");

        // Modify its list of outgoing flights
        ((ArrayList<ObjectId>) planes).add(id);

        // Update its value in MongoDB
        Document updates = new Document("$set", new Document("outgoingFlights", planes));
        airports.updateOne(filter, updates);

        // Insert the flight into the database
        flights.insertOne(flight);
    }

    // Given the objectID in MongoDB, removes it from the database
    public static void removeFlight(String[] args) {

        validateArgs(2, args);
        ObjectId id = new ObjectId(args[1]);

        // Remove the desired flight
        Bson filter = Filters.eq("_id", id);
        Document removedFlight = flights.findOneAndDelete(filter);

        // Locate the document of the starting airport
        String sourceIATA = (String) removedFlight.get("startIATA");
        filter = Filters.eq("IATA", sourceIATA);
        Document startAirport = airports.find(filter).first();

        // Delete this flight from the starting airport's outgoing flights
        ArrayList<ObjectId> outgoingFlights = (ArrayList<ObjectId>) startAirport.get("outgoingFlights");
        outgoingFlights.remove(id);

        // Update the information on MongoDB
        Document updates = new Document("$set", new Document("outgoingFlights", outgoingFlights));
        airports.updateOne(filter, updates);

    }

    // Triggers a search
    public static void pathFind(String[] args) {
        new PathFinding(args);
    }

    // Checks that args contains the requested number of parameters
    private static void validateArgs (int num, String[] args) {
        if (args.length != num) {
            throw new IllegalArgumentException("Incorrect Argument Count");
        }
    }

    // Makes sure that the arguments for the airport are valid
    private static boolean validateAirport(String[] args) {

        for (String arg : args) {
            if (arg.isEmpty()) {
                return false;
            }
        }

        return true;
    }
}