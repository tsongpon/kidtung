package com.kidtung.dao;

import com.kidtung.domain.Trip;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import org.mongojack.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TripDAO {

    final static String MONGO_IP = "192.168.50.48";
    final static int MONGO_PORT = 27017;

    private static final String IP_ADDRESS = System.getenv("OPENSHIFT_DIY_IP") != null ? System.getenv("OPENSHIFT_DIY_IP") : "localhost";
    private static final int PORT = System.getenv("OPENSHIFT_DIY_PORT") != null ? Integer.parseInt(System.getenv("OPENSHIFT_DIY_PORT")) : 8080;

    public void save(Trip trip) {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(IP_ADDRESS);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB db = mongoClient.getDB("kidtung");
        DBCollection collection = db.getCollection("trip");
        JacksonDBCollection<Trip, String> tripCollection = JacksonDBCollection.wrap(collection, Trip.class, String.class);
        tripCollection.insert(trip);
    }

    public List<Trip> loadTrips () {
        List<Trip> results = new ArrayList<>();
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(IP_ADDRESS);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB db = mongoClient.getDB("kidtung");
        DBCollection trip = db.getCollection("trip");
        try {
            JacksonDBCollection<Trip, String> tripListCollection = JacksonDBCollection.wrap(trip,
                    Trip.class, String.class);
            Iterable<Trip> all = tripListCollection.find();
            for (Trip current : all) {
                results.add(current);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex);
        }
        return results;
    }

    public Trip loadTripByCode (String code) {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(IP_ADDRESS);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB db = mongoClient.getDB("kidtung");
        DBCollection trip = db.getCollection("trip");
        JacksonDBCollection<Trip, String> tripCollection = JacksonDBCollection.wrap(trip,
                Trip.class, String.class);
        return tripCollection.findOne(DBQuery.is("_id", code));
    }

    public void update(String code, Trip trip) {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(IP_ADDRESS);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB db = mongoClient.getDB("kidtung");
        DBCollection collection = db.getCollection("trip");
        JacksonDBCollection<Trip, String> tripCollection = JacksonDBCollection.wrap(collection,
                Trip.class, String.class);
        tripCollection.updateById(code, trip);

    }

    public void delete(String code)
    {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(IP_ADDRESS);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB db = mongoClient.getDB("kidtung");
        DBCollection collection = db.getCollection("trip");
        JacksonDBCollection<Trip, String> tripCollection = JacksonDBCollection.wrap(collection,
                Trip.class, String.class);
        tripCollection.removeById(code);

    }

}
