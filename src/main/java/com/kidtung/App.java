package com.kidtung;

import com.kidtung.dao.TripDAO;
import com.kidtung.domain.Member;
import com.kidtung.domain.Trip;
import com.kidtung.transport.TripRequestTransport;
import com.kidtung.util.KidtungUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.List;

import static com.kidtung.util.KidtungUtil.json;
import static spark.Spark.get;
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

        get("/api/kidtung/trips", (request, response) -> {
            TripDAO tripDAO = new TripDAO();
            return new KidtungMock().mockTrip();
        }, json());

        get("/api/kidtung/trips/:code", (request, response) -> {
            TripDAO tripDAO = new TripDAO();
            return tripDAO.loadTripByCode(request.params(":code"));
        }, json());

        put("/api/kidtung/trips/:code", (request, response) -> {
            log.debug("request body : {}", request.body());
            TripDAO tripDAO = new TripDAO();
            TripRequestTransport transport = KidtungUtil.toTripTransport(request.body());
            Trip trip = KidtungUtil.fromTransport(transport);
            tripDAO.save(trip);
            response.status(201);
            response.body("Created");
            return "http://"+request.host()+"/trips/"+trip.getCode();
        });

    }
}
