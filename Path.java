package com.company;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Path {

    public List<String> cities;
    public List<ObjectId> flights;
    public double cost;
    public int layovers;

    public Path (String start) {
        this.cities = new ArrayList<String>(Collections.singletonList(start));
        this.flights = new ArrayList<>();
        this.cost = 0;
        this.layovers = 0;
    }

    public Path (ArrayList<String> cities, ArrayList<ObjectId> flights, double cost, int layovers) {
        this.cities = cities;
        this.flights = flights;
        this.cost = cost;
        this.layovers = layovers;
    }
}
