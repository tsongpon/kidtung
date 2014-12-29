package com.kidtung.service;

import com.google.gson.Gson;
import com.kidtung.dao.TripDAO;
import com.kidtung.domain.Trip;
import com.kidtung.transport.DeptTransport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.*;

public class DeptServiceTest {
    Logger log = LoggerFactory.getLogger(DeptServiceTest.class);

    @Test
    public void testCal() {
        Trip trip = new TripDAO().loadTripByCode("moon");
        List<DeptTransport> deptTransports = new DeptService().calDept(trip);
        log.debug(new Gson().toJson(deptTransports));
    }
}