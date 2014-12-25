package com.kidtung;

import com.kidtung.dao.TripDAO;
import com.kidtung.domain.Member;
import com.kidtung.domain.Trip;
import com.kidtung.util.KidtungUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.ArrayList;
import java.util.List;

import static com.kidtung.util.KidtungUtil.json;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.SparkBase.staticFileLocation;


public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) {
        //config static file location
        staticFileLocation("/public");

        get("/kidtung", (request, response) -> {
            log.debug("Redering kidtung.html");
            // The hello.html file is located in directory:
            // src/resources/spark/template/freemarker
            return new ModelAndView(null, "kidtung.html");
        }, new FreeMarkerEngine());
        get("/kidtung/paymentlist", (request, response) -> {
            // The hello.html file is located in directory:
            // src/resources/spark/template/freemarker
            return new ModelAndView(null, "paymentlist.html");
        }, new FreeMarkerEngine());
        get("/kidtung/summary", (request, response) -> {
            // The hello.html file is located in directory:
            // src/resources/spark/template/freemarker
            return new ModelAndView(null, "summary.html");
        }, new FreeMarkerEngine());

        get("/kidtung/api/trips/:code/members", "application/json", (request, response) -> {
            // mockdata
            System.out.println("tripCode :" + request.params(":code"));
            List<Member> memberList = new KidtungMock().mockTrip().getMemberList();

            return memberList;
        }, json());

        get("/kidtung/api/trips", (request, response) -> {
            TripDAO tripDAO = new TripDAO();
            return tripDAO.loadTrips();
        }, json());

        get("/kidtung/api/trips/:code", (request, response) -> {
            TripDAO tripDAO = new TripDAO();
            return tripDAO.loadTripByCode(request.params(":code"));
        }, json());

        put("/kidtung/api/trips", (request, response) -> {
            TripDAO tripDAO = new TripDAO();
            tripDAO.save(KidtungUtil.toObject(request.body()));
            response.status(200);
            response.body("Created");
            return response;
        });
    }
}
