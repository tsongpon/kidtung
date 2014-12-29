package com.kidtung.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kidtung.domain.Expend;
import com.kidtung.domain.Member;
import com.kidtung.domain.Trip;
import com.kidtung.transport.TripRequestTransport;
import org.apache.commons.lang3.RandomStringUtils;
import spark.ResponseTransformer;

import java.util.ArrayList;
import java.util.Random;

public final class KidtungUtil {

    private KidtungUtil() {
        //intentional
    }

    public static String generateRandomCode(int codeLength) {
        return RandomStringUtils.randomAlphabetic(codeLength);
    }

    public static String toJson(Object object) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
        return gson.toJson(object);
    }

    public static ResponseTransformer json() {
        return KidtungUtil::toJson;
    }

    public static Trip toObject(String jsonStr){
        return new Gson().fromJson(jsonStr, Trip.class);
    }

    public static Expend toExpenseObj(String jsonStr){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
        return gson.fromJson(jsonStr, Expend.class);
    }


    public static TripRequestTransport toTripTransport(String json) {
        return new Gson().fromJson(json, TripRequestTransport.class);
    }

    public static Trip fromTransport(TripRequestTransport transport) {
        Trip trip = new Trip();
        trip.setName(transport.getName());
        //use code as a name
        trip.setCode(transport.getName());
        trip.setDescription(transport.getDescription());
        trip.setMemberList(new ArrayList<>());
        for(String each : transport.getMembers()) {
            Member member = new Member();
            member.setName(each);
            member.setExpendList(new ArrayList<>());
            trip.getMemberList().add(member);
        }
        return trip;
    }

    public static String randomColor() {
        String [] colors = {"blue", "green", "pink", "yellow", "red", "violet", "orange", "gray", "brown"};
        Random rand = new Random();
        int randomNum = rand.nextInt((8 - 0) + 1) + 0;

        return colors[randomNum];
    }

}
