package com.kidtung;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.kidtung.dao.TripDAO;
import com.kidtung.domain.Expend;
import com.kidtung.domain.Member;
import com.kidtung.domain.Trip;
import com.kidtung.util.KidtungUtil;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.kidtung.util.KidtungUtil.json;
import static spark.Spark.*;
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


        //static route
        get("/kidtung/:tripcode", (request, response) -> {
            log.info("GET kidtung/:tripcode");
            String tripCode = request.params(":tripcode");
            log.info(tripCode);

            return new ModelAndView(null, "summary.html");
        }, new FreeMarkerEngine());

        get("/kidtung/:tripcode/:name", (request, response) -> {
            log.info("GET /kidtung/:tripcode/:name");

            String tripCode = request.params(":tripcode");
            String name = request.params(":name");
            log.info(tripCode + "--" + name);

            return new ModelAndView(null, "kidtung.html");
        }, new FreeMarkerEngine());


        //for expend
        get("kidtung/api/trips/:code/members/:name/expends", "application/json", (request, response) -> {
            log.info("GET kidtung/api/trips/:code/members/:name/expends");
            String name = request.params(":name");
            
            List<Expend> expendList = new ArrayList();
            List<Member> memberList = new KidtungMock().mockTrip().getMemberList();
            for(int i=0; i<memberList.size(); i++){
                Member member = memberList.get(i);
                if(name.equals(member.getName())){
                    expendList = member.getExpendList();
                    break;
                }
            }
            log.info(name + " = " + expendList.size());
            return expendList;
        }, json());

        post("kidtung/api/trips/:code/members/:name/expends", (request, response) -> {
            log.info("POST kidtung/api/trips/:code/members/:name/expends");
            String jsonData = request.body();
            Expend expend = new Expend();  //should be data from request.body()
            expend.setCode(KidtungUtil.generateRandomCode(3));
            expend.setItem("Gas");
            expend.setPayDate(new Date());
            expend.setPrice(1700.00);

            String code = request.params(":code");
            String name = request.params(":name");
            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.loadTripByCode(code);
            List<Member> memberList = trip.getMemberList();
            List<Expend> expendList = new ArrayList();
            for(int i=0; i<memberList.size(); i++){
                Member member = memberList.get(i);
                if(name.equals(member.getName())){
                    expendList = member.getExpendList();
                    expendList.add(expend);
                    member.setExpendList(expendList); //set new expendList
                    break;
                }
            }

            tripDAO.save(trip);
            log.info(jsonData);
            return jsonData;
        },json());

        put("/kidtung/api/trips/:code/members/:id", (request, response) -> {
            log.info("PUT /kidtung/api/trips/:code/members/:id");
            String id = request.params(":id");
            String jsonData = request.body();

            log.info(id);
            return "Update expend at id = " + id;
        });

        delete("/kidtung/api/trips/:code/members/:id", (request, response) -> {
            log.info("DELETE /kidtung/api/trips/:code/members/:id");
            String id = request.params(":id");
            String jsonData = request.body();

            log.info(id);
            return "Delete expend at id = " + id;
        });


    }
}
