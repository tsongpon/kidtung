package com.kidtung.util;

import com.google.gson.Gson;
import com.kidtung.domain.Trip;
import org.apache.commons.lang3.RandomStringUtils;
import spark.ResponseTransformer;

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

}
