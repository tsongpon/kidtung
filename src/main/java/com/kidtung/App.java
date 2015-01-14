package com.kidtung;

import com.kidtung.dao.TripDAO;
import com.kidtung.domain.*;
import com.kidtung.service.DeptService;
import com.kidtung.transport.TripRequestTransport;
import com.kidtung.util.KidtungUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.kidtung.util.KidtungUtil.json;
import static spark.Spark.*;
import static spark.Spark.get;
import static spark.Spark.put;
import static spark.SparkBase.staticFileLocation;


public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        //port(80);

        //config static file location
        staticFileLocation("/public");

        get("/kidtung", (request, response) -> {
            log.debug("Redering kidtung.html");
            // The hello.html file is located in directory:
            // src/resources/spark/template/freemarker
            return new ModelAndView(null, "kidtung.html");
        }, new FreeMarkerEngine());
//        get("/kidtung/paymentlist", (request, response) -> {
//            // The hello.html file is located in directory:
//            // src/resources/spark/template/freemarker
//            return new ModelAndView(null, "paymentlist.html");
//        }, new FreeMarkerEngine());
//        get("/kidtung/summary", (request, response) -> {
//            // The hello.html file is located in directory:
//            // src/resources/spark/template/freemarker
//            return new ModelAndView(null, "summary.html");
//        }, new FreeMarkerEngine());
//
//        get("/kidtung/api/trips/:code/members", "application/json", (request, response) -> {
//            // mockdata
//            System.out.println("tripCode :" + request.params(":code"));
//            List<Member> memberList = new KidtungMock().mockTrip().getMemberList();
//
//            return memberList;
//        }, json());


        //static route
        get("/kidtung/:tripcode", (request, response) -> {
            log.info("GET kidtung/:tripcode");
            String tripCode = request.params(":tripcode");
            log.info(tripCode);

            TripDAO dao = new TripDAO();
            Trip trip = dao.loadTripByCode(tripCode);
            if(trip == null) {
                return new ModelAndView(null, "notfound.html");
            }
            return new ModelAndView(null, "paymentlist.html");
        }, new FreeMarkerEngine());

        //static route
        get("/kidtung/:tripcode/summary", (request, response) -> {
            log.info("GET kidtung/:tripcode");
            String tripCode = request.params(":tripcode");
            log.info(tripCode);

            TripDAO dao = new TripDAO();
            Trip trip = dao.loadTripByCode(tripCode);
            if (trip == null) {
                new ModelAndView(null, "notfound.html");
            }
            return new ModelAndView(null, "summary.html");
        }, new FreeMarkerEngine());

        get("/kidtung/:tripcode/:name", (request, response) -> {
            log.info("GET /kidtung/:tripcode/:name");

            String tripCode = request.params(":tripcode");
            String name = request.params(":name");
            log.info(tripCode + "--" + name);
            TripDAO dao = new TripDAO();
            Trip aTrip = dao.loadTripByCode(tripCode);
            if (aTrip == null) {
                return new ModelAndView(null, "notfound.html");
            }

            return new ModelAndView(null, "paymentlist.html");
        }, new FreeMarkerEngine());

        //for expend
        get("/api/kidtung/trips/:code/expends", (request, response) -> {
            log.info("GET /api/kidtung/trips/" + request.params(":code") + "/expends");
            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.loadTripByCode(request.params(":code"));


            if (trip != null) {
                List<Expend> expendListAll = new ArrayList<>();
                trip.getMemberList().stream().forEach(m -> expendListAll.addAll(m.getExpendList()));
                return expendListAll.stream().collect(Collectors.groupingBy(Expend::getDateString));
            }else{
                return "Sorry, Trip not found.";
            }
        }, json());

        get("/api/kidtung/trips/:code/members/:name/expends", "application/json", (request, response) -> {
            log.info("GET /api/kidtung/trips/:code/members/:name/expends");
            String name = request.params(":name");
            String tripCode = request.params(":code");
            List<Expend> expendList = new ArrayList();
            TripDAO tripDAO = new TripDAO();
            List<Member> memberList = tripDAO.loadTripByCode(tripCode).getMemberList();
            for (int i = 0; i < memberList.size(); i++) {
                Member member = memberList.get(i);
                if (name.equals(member.getName())) {
                    expendList = member.getExpendList();
                    break;
                }
            }
            log.info(name + " = " + expendList.size());
            return expendList;
        }, json());

        get("/api/kidtung/trips/:code/members/:name/expends/:expendCode", "application/json", (request, response) -> {
            log.info("GET /api/kidtung/trips/:code/members/:name/expends");
            String name = request.params(":name");
            String tripCode = request.params(":code");
            String expendCode = request.params(":expendCode");
            List<Expend> expendList = new ArrayList();
            TripDAO tripDAO = new TripDAO();
            List<Member> memberList = tripDAO.loadTripByCode(tripCode).getMemberList();
            Expend result = null;
            for (int i = 0; i < memberList.size(); i++) {
                Member member = memberList.get(i);
                if (name.equals(member.getName())) {
                    expendList = member.getExpendList();
                    for(Expend expend : expendList) {
                        if(expendCode.equalsIgnoreCase(expend.getCode())) {
                            log.debug("Found expend code {}", expend.getCode());
                            return expend;
                        }
                    }
                }
            }
            response.status(404);
            return null;
        }, json());

        post("/api/kidtung/trips/:code/members/:name/expends", (request, response) -> {
            log.info("POST /api/kidtung/trips/:code/members/:name/expends");
            String code = request.params(":code");
            String name = request.params(":name");
            log.info("request.body()  = " + request.body());
            Expend expend = KidtungUtil.toExpenseObj(request.body());
            expend.setCode(KidtungUtil.generateRandomCode(3));
            //expend.setDate(new Date());

            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.loadTripByCode(code);
            List<Member> memberList = trip.getMemberList();
            List<Expend> expendList = new ArrayList();
            for (int i = 0; i < memberList.size(); i++) {
                Member member = memberList.get(i);
                if (name.equals(member.getName())) {
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
            log.info("PUT /api/kidtung/trips/:code/members/:name/expends/:id");
            String code = request.params(":code");
            String name = request.params(":name");
            String id = request.params(":id");
            log.info("request.body()  = " + request.body());
            Expend expend = KidtungUtil.toExpenseObj(request.body());
            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.loadTripByCode(code);
            List<Member> memberList = trip.getMemberList();
            List<Expend> expendList = new ArrayList();
            for (int i = 0; i < memberList.size(); i++) {
                Member member = memberList.get(i);
                if (name.equals(member.getName())) {
                    expendList = member.getExpendList();
                    for (int j = 0; j < expendList.size(); j++) {
                        Expend expendRow = expendList.get(j);
                        if (id.equals(expendRow.getCode())) {
                            expendRow.setItem(expend.getItem());
                            expendRow.setPrice(expend.getPrice());
                            expendRow.setDate(expend.getDate());
                            break;
                        }
                    }
                    break;
                }
            }
            tripDAO.update(code, trip);
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
            for (int i = 0; i < memberList.size(); i++) {
                Member member = memberList.get(i);
                if (name.equals(member.getName())) {
                    expendList = member.getExpendList();
                    log.info("#### expendList size before remove = " + expendList.size());
                    for (int j = 0; j < expendList.size(); j++) {
                        Expend expendRow = expendList.get(j);
                        if (id.equals(expendRow.getCode())) {
                            expendList.remove(j);
                            log.info("----- expendList size after remove = " + expendList.size());
                            break;
                        }
                    }
                    break;
                }
            }
            tripDAO.update(code, trip);
            response.status(200);
            response.body("Delete Expend");
            return response;
        });

        // for member
        get("/api/kidtung/trips/:code/members", (request, response) -> {
            log.info("GET /api/kidtung/trips/" + request.params(":code") + "/members/");
            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.loadTripByCode(request.params(":code"));
            if (trip != null) {
                return trip.getMemberList();
            } else {
                return "Sorry, Trip not found.";
            }
        }, json());

        get("/api/kidtung/trips/:code/members/:name", (request, response) -> {
            String name = request.params(":name");
            log.info("GET /api/kidtung/trips/" + request.params(":code") + "/members/" + name);
            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.loadTripByCode(request.params(":code"));
            if (trip != null) {
                Optional<Member> member = trip.getMemberList().stream().filter(m -> m.getName().equals(name)).findAny();
                if (member.isPresent()) {
                    return member.get();
                } else {
                    return "Sorry, " + name + " not found in this trip.";
                }
            } else {
                return "Sorry, Trip not found.";
            }
        }, json());

        head("/api/kidtung/trips/:tripcode", (request, response) -> {
            String tripCode = request.params(":tripcode");
            log.info(tripCode);

            TripDAO dao = new TripDAO();
            Trip trip = dao.loadTripByCode(tripCode);
            if(trip == null) {
                response.status(404);
            } else {
                response.status(200);
            }
            return "";
        });

        get("/api/kidtung/trips/:code", (request, response) -> {
            TripDAO tripDAO = new TripDAO();
            return tripDAO.loadTripByCode(request.params(":code"));
        }, json());

        get("/api/kidtung/trips", (request, response) -> {
            TripDAO tripDAO = new TripDAO();
            return tripDAO.loadTrips();
        }, json());

        put("/api/kidtung/trips/:code", (request, response) -> {
            log.debug("request body : {}", request.body());
            TripRequestTransport transport = KidtungUtil.toTripTransport(request.body());
            Trip trip = KidtungUtil.fromTransport(transport);
            TripDAO tripDAO = new TripDAO();
            tripDAO.save(trip);
            response.status(201);
            response.body("http://" + request.host() + "/kidtung/" + trip.getCode());
            return "http://" + request.host() + "/kidtung/" + trip.getCode();
        });

        get("api/kidtung/trips/:code/reports", (request, response) -> {
            log.debug("summary trips");
            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.loadTripByCode(request.params(":code"));
            TripReport report = new TripReport();
            int memberNo = trip.getMemberList().size();
            report.setMemberNo(memberNo);
            BigDecimal price = new BigDecimal(0.0);
            for (Member member : trip.getMemberList()) {
                for (Expend expend : member.getExpendList()) {
                    if (expend.getPrice() != null) {
                        price = price.add(BigDecimal.valueOf(expend.getPrice()));
                        log.debug("expend: {}", expend.getPrice());
                        log.debug("price: {}", price);
                    }
                }
            }
            report.setTotal(price);
            report.setAverage(price.divide(BigDecimal.valueOf(memberNo), RoundingMode.HALF_UP));
            return report;
        }, json());

        get("api/kidtung/trips/:code/members/:name/reports", (request, response) -> {
            log.debug("summary trips");
            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.loadTripByCode(request.params(":code"));
            int memberNo = trip.getMemberList().size();
            DecimalFormat decim = new DecimalFormat("0.00");
            PersonalTripReport personalTripReport = new PersonalTripReport();
            BigDecimal price = new BigDecimal(0.00);
            for (Member member : trip.getMemberList()) {
                for (Expend expend : member.getExpendList()) {
                    if (expend.getPrice() != null) {
                        price = price.add(BigDecimal.valueOf(expend.getPrice()));
                        log.debug("expend: {}", expend.getPrice());
                        log.debug("price: {}", price);
                    }
                }
            }
            BigDecimal total = price;
            BigDecimal avg = price.divide(BigDecimal.valueOf(memberNo), RoundingMode.HALF_UP);
            personalTripReport.setTotal(total);
            personalTripReport.setAverage(avg);
            BigDecimal personalPay = new BigDecimal(0.00);
            for (Member member : trip.getMemberList()) {
                if (request.params(":name").equals(member.getName())) {
                    for (Expend personExpend : member.getExpendList()) {
                        if (personExpend.getPrice() != null) {
                            personalPay = personalPay.add(BigDecimal.valueOf(personExpend.getPrice()));
                            log.debug("expend: {}", personExpend.getPrice());
                            log.debug("price: {}", personalPay);
                        }
                    }
                }
            }
            BigDecimal pay = personalPay;
            personalTripReport.setPay(pay);
            BigDecimal balance = pay.subtract(avg);
            personalTripReport.setBalance(balance);
            return personalTripReport;
        }, json());

        get("api/kidtung/trips/:code/reports/eachmember", (request, response) -> {
            log.debug("summary trips");
            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.loadTripByCode(request.params(":code"));
            int memberNo = trip.getMemberList().size();
            DecimalFormat decim = new DecimalFormat("0.00");
            List<PersonalTripReport> reportList = new ArrayList<>();
            BigDecimal price = new BigDecimal(0.00);
            for (Member member : trip.getMemberList()) {
                for (Expend expend : member.getExpendList()) {
                    if (expend.getPrice() != null) {
                        price = price.add(BigDecimal.valueOf(expend.getPrice()));
                    }
                }

            }
            BigDecimal total = price;
            BigDecimal avg = price.divide(BigDecimal.valueOf(memberNo), RoundingMode.HALF_UP);

            for (Member member : trip.getMemberList()) {
                PersonalTripReport personalTripReport = new PersonalTripReport();

                personalTripReport.setTotal(total);
                personalTripReport.setAverage(avg);
                personalTripReport.setMemberName(member.getName());

                BigDecimal personalPay = BigDecimal.valueOf(0.00);
                BigDecimal payPrice = BigDecimal.valueOf(0.00);
                if (member.getExpendList().size() > 0) {
                    for (Expend personExpend : member.getExpendList()) {
                        if (personExpend.getPrice() != null) {
                            payPrice = BigDecimal.valueOf(personExpend.getPrice());
                            log.debug("expend: {}", payPrice);
                            log.debug("price: {}", personalPay);
                            personalPay = personalPay.add(payPrice);
                            BigDecimal pay = personalPay;
                            personalTripReport.setPay(pay);
                            BigDecimal balance = pay.subtract(avg);
                            personalTripReport.setBalance(balance);
                        }
                    }
                } else {
                    personalTripReport.setPay(payPrice);
                    personalTripReport.setBalance(payPrice.subtract(avg));
                }
                reportList.add(personalTripReport);
            }
            return reportList;
        }, json());

        get("/api/kidtung/trips/:code/dept", (request, response) -> {
            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.loadTripByCode(request.params(":code"));
            return new DeptService().calDept(trip);
        }, json());

        exception(Exception.class, (e, request, response) -> {
            log.error(e.getMessage(), e);
            response.status(500);
            response.body(e.getMessage());
        });
    }
}
