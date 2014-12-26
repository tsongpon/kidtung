package com.kidtung;

import com.kidtung.dao.TripDAO;
import com.kidtung.domain.*;
import com.kidtung.domain.Trip;
import com.kidtung.transport.TripRequestTransport;
import com.kidtung.util.KidtungUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import java.net.UnknownHostException;
import java.text.DecimalFormat;
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
            TripDAO dao = new TripDAO();
            Trip aTrip = null;
            try {
                aTrip = dao.loadTripByCode(tripCode);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            if(aTrip == null) {
                return new ModelAndView(null, "notfound.html");
            }

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
            return "http://" + request.host() + "/kidtung/" + trip.getCode();
        });

        get("api/kidtung/trips/:code/reports", (request, response) -> {
            log.debug("summary trips");
            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.loadTripByCode(request.params(":code"));
            TripReport report = new TripReport();
            int memberNo = trip.getMemberList().size();
            report.setMemberNo(memberNo);
            double price = 0.00;
            for(Member member: trip.getMemberList()){
                for(Expend expend: member.getExpendList()){
                    if(expend.getPrice() != null){
                        price = price + expend.getPrice();
                        log.debug("expend: {}", expend.getPrice());
                        log.debug("price: {}", price);
                    }
                }
            }
            DecimalFormat decim = new DecimalFormat("0.00");
            report.setTotal(Double.parseDouble(decim.format(price)));
            report.setAverage(Double.parseDouble(decim.format(price/memberNo)));
            return report;
        }, json());

        get("api/kidtung/trips/:code/members/:name/reports", (request, response) -> {
            log.debug("summary trips");
            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.loadTripByCode(request.params(":code"));
            int memberNo = trip.getMemberList().size();
            DecimalFormat decim = new DecimalFormat("0.00");
            PersonalTripReport personalTripReport = new PersonalTripReport();
            double price = 0.00;
            for (Member member : trip.getMemberList()) {
                for (Expend expend : member.getExpendList()) {
                    if (expend.getPrice() != null) {
                        price = price + expend.getPrice();
                        log.debug("expend: {}", expend.getPrice());
                        log.debug("price: {}", price);
                    }
                }
            }
            Double total = Double.parseDouble(decim.format(price));
            Double avg = Double.parseDouble(decim.format(price / memberNo));
            personalTripReport.setTotal(total);
            personalTripReport.setAverage(avg);
            double personalPay = 0.00;
            for (Member member : trip.getMemberList()) {
                if (request.params(":name").equals(member.getName())) {
                    for (Expend personExpend : member.getExpendList()) {
                        if (personExpend.getPrice() != null) {
                            personalPay = personalPay + personExpend.getPrice();
                            log.debug("expend: {}", personExpend.getPrice());
                            log.debug("price: {}", personalPay);
                        }
                    }
                }
            }
            Double pay = Double.parseDouble(decim.format(personalPay));
            personalTripReport.setPay(pay);
            Double balance = Double.parseDouble(decim.format(pay - avg));
            personalTripReport.setBalance(balance);
            return personalTripReport;
        }, json());

    }
}
