package com.kidtung.dao;

import com.kidtung.domain.TestDomain;
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

    public void save(Trip trip) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB("kidtung");
        DBCollection collection = db.getCollection("trip");
        JacksonDBCollection<Trip, String> tripCollection = JacksonDBCollection.wrap(collection, Trip.class, String.class);
        tripCollection.insert(trip);
    }

    public List<Trip> loadTrips () throws UnknownHostException {
        List<Trip> results = new ArrayList<>();
        MongoClient mongoClient = new MongoClient();
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

    public Trip loadTripByCode (String code) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB("kidtung");
        DBCollection trip = db.getCollection("trip");
        JacksonDBCollection<Trip, String> tripCollection = JacksonDBCollection.wrap(trip,
                Trip.class, String.class);
        return tripCollection.findOne(DBQuery.is("_id", code));
    }

    public void update(String code, Trip trip) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB("kidtung");
        DBCollection collection = db.getCollection("trip");
        JacksonDBCollection<Trip, String> tripCollection = JacksonDBCollection.wrap(collection,
                Trip.class, String.class);
        tripCollection.updateById(code, trip);

    }

    public void delete(String code) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB("kidtung");
        DBCollection collection = db.getCollection("trip");
        JacksonDBCollection<Trip, String> tripCollection = JacksonDBCollection.wrap(collection,
                Trip.class, String.class);
        tripCollection.removeById(code);

    }

}
