package com.kidtung.dao;

import com.kidtung.domain.Trip;
import com.kidtung.exception.KidtungDaoException;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.mongojack.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class TripDAO {

    final static String IP_ADDRESS = "localhost";

    public void save(Trip trip) {
        Optional<MongoClient> mongoClient = Optional.empty();
        try {
            mongoClient = Optional.of(new MongoClient(IP_ADDRESS));
            mongoClient.ifPresent(opt -> {
                DB db = opt.getDB("kidtung");

                DBCollection collection = db.getCollection("trip");
                JacksonDBCollection<Trip, String> tripCollection = JacksonDBCollection.wrap(collection, Trip.class, String.class);
                tripCollection.insert(trip);
            });
        } catch (UnknownHostException e) {
            throw new KidtungDaoException("Cannot connet to mongo db", e);
        } finally {
            mongoClient.ifPresent(Mongo::close);
        }

    }

    public List<Trip> loadTrips () {
        List<Trip> results = new ArrayList<>();
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(IP_ADDRESS);
        } catch (UnknownHostException e) {
            throw new KidtungDaoException("Cannot connet to mongo db", e);
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
        Optional<MongoClient> mongoClient = Optional.empty();
        Trip result = null;
        try {
            mongoClient = Optional.of(new MongoClient(IP_ADDRESS));
            if(mongoClient.isPresent()) {
                DB db = mongoClient.get().getDB("kidtung");
                DBCollection trip = db.getCollection("trip");
                JacksonDBCollection<Trip, String> tripCollection = JacksonDBCollection.wrap(trip,
                        Trip.class, String.class);
                result = tripCollection.findOne(DBQuery.is("_id", code));
            }
        } catch (UnknownHostException e) {
            throw new KidtungDaoException("Cannot connect to mondodb", e);
        } finally {
            mongoClient.ifPresent(Mongo::close);
        }
        return result;
    }

    public void update(String code, Trip trip) {
        Optional<MongoClient> mongoClient = Optional.empty();
        try {
            mongoClient = Optional.of(new MongoClient(IP_ADDRESS));
            mongoClient.ifPresent(opt -> {
                DB db = opt.getDB("kidtung");
                DBCollection collection = db.getCollection("trip");
                JacksonDBCollection<Trip, String> tripCollection = JacksonDBCollection.wrap(collection,
                        Trip.class, String.class);
                tripCollection.updateById(code, trip);
            });
        } catch (UnknownHostException e) {
            throw new KidtungDaoException("Cannot update data", e);
        } finally {
            mongoClient.ifPresent(Mongo::close);
        }
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
