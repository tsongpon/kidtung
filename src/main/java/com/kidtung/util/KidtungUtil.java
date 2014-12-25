package com.kidtung.util;

import com.google.gson.Gson;
import com.kidtung.domain.Member;
import com.kidtung.domain.Trip;
import com.kidtung.transport.TripRequestTransport;
import org.apache.commons.lang3.RandomStringUtils;
import spark.ResponseTransformer;

import java.util.ArrayList;

public final class KidtungUtil {

    private KidtungUtil() {
        //intentional
    }

    public static String generateRandomCode(int codeLength) {
        return RandomStringUtils.randomAlphabetic(codeLength);
    }

    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }

    public static ResponseTransformer json() {
        return KidtungUtil::toJson;
    }

    public static Trip toObject(String jsonStr){
        return new Gson().fromJson(jsonStr, Trip.class);
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

}
