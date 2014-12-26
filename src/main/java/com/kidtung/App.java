package com.kidtung;

import com.kidtung.dao.TripDAO;
import com.kidtung.domain.Expend;
import com.kidtung.dao.TripDAO;
import com.kidtung.domain.Member;
import com.kidtung.domain.Trip;
import com.kidtung.domain.Trip;
import com.kidtung.transport.TripRequestTransport;
import com.kidtung.util.KidtungUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.kidtung.util.KidtungUtil.json;
import static spark.Spark.*;
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

            return new ModelAndView(null, "paymentlist.html");
        }, new FreeMarkerEngine());


        //for expend
        get("/api/kidtung/trips/:code/members/:name/expends", "application/json", (request, response) -> {
            log.info("GET /api/kidtung/trips/:code/members/:name/expends");
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

        post("/api/kidtung/trips/:code/members/:name/expends", (request, response) -> {
            log.info("POST /api/kidtung/trips/:code/members/:name/expends");
            String code = request.params(":code");
            String name = request.params(":name");
            log.info("request.body()  = " + request.body());
            Expend expend = KidtungUtil.toExpenseObj(request.body());
            expend.setCode(KidtungUtil.generateRandomCode(3));
            expend.setPayDate(new Date());

            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.loadTripByCode(code);
            List<Member> memberList = trip.getMemberList();
            List<Expend> expendList = new ArrayList();
            for(int i=0; i<memberList.size(); i++){
                Member member = memberList.get(i);
                if(name.equals(member.getName())){
                    expendList = member.getExpendList();
                    expendList.add(expend);
                    break;
                }
            }
            tripDAO.update(code, trip);
            response.status(200);
            response.body("Created Expend");
            return response;
        });

        put("/api/kidtung/trips/:code/members/:name/expends/:id", (request, response) -> {
            log.info("PUT /api/kidtung/trips/:code/members/:name/expends/:id") ;
            String code = request.params(":code");
            String name = request.params(":name");
            String id = request.params(":id");
            log.info("request.body()  = " + request.body());
            Expend expend = KidtungUtil.toExpenseObj(request.body());
            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.loadTripByCode(code);
            List<Member> memberList = trip.getMemberList();
            List<Expend> expendList = new ArrayList();
            for(int i=0; i<memberList.size();i++){
                Member member = memberList.get(i);
                if(name.equals(member.getName())){
                    expendList = member.getExpendList();
                    for(int j=0 ; j<expendList.size();j++){
                        Expend expendRow = expendList.get(j);
                        if(id.equals(expendRow.getCode())){
                            expendRow.setItem(expend.getItem());
                            expendRow.setPrice(expend.getPrice());
                            expendRow.setPayDate(expendRow.getPayDate());
                            break;
                        }
                    }
                    break;
                }
            }
            tripDAO.update(code,trip);
            response.status(200);
            response.body("Update Expend");
            log.info(id);
            return response;
        });

        delete("/api/kidtung/trips/:code/members/:name/expends/:id", (request, response) -> {
            log.info("DELETE /api/kidtung/trips/:code/members/:name/expends/:id");
            String code = request.params(":code");
            String name = request.params(":name");
            String id = request.params(":id");
            log.info("request.body()  = " + request.body());
            Expend expend = KidtungUtil.toExpenseObj(request.body());
            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.loadTripByCode(code);
            List<Member> memberList = trip.getMemberList();
            List<Expend> expendList = new ArrayList();
            for(int i=0;i<memberList.size();i++) {
                Member member = memberList.get(i);
                if(name.equals(member.getName())){
                    expendList = member.getExpendList();
                    log.info("#### expendList size before remove = " + expendList.size());
                    for(int j=0;j<expendList.size();j++){
                        Expend expendRow = expendList.get(j);
                        if(id.equals(expendRow.getCode())) {
                            expendList.remove(j);
                            log.info("----- expendList size after remove = " + expendList.size());
                            break;
                        }
                    }
                    break;
                }
            }
            tripDAO.update(code,trip);
            response.status(200);
            response.body("Delete Expend");
            return response;
        });



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
