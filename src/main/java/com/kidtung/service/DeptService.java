package com.kidtung.service;

import com.kidtung.domain.Expend;
import com.kidtung.domain.Trip;
import com.kidtung.transport.DeptTransport;
import com.kidtung.transport.MemberDept;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
public class DeptService {

    public DeptTransport calDept(Trip trip) {
        List<Expend> allExpend = new ArrayList<>();
        trip.getMemberList().stream().forEach(m -> allExpend.addAll(m.getExpendList()));
        DoubleSummaryStatistics summaryFroTrip = allExpend.stream().collect(Collectors.summarizingDouble(Expend::getPrice));
        Map<String, List<Expend>> expendByName =  allExpend.stream().collect(Collectors.groupingBy(Expend::getName));
        List<MemberDept> memberDeptsPositive = new ArrayList<>();
        List<MemberDept> memberDeptsNegative = new ArrayList<>();
        for (Map.Entry<String, List<Expend>> entry : expendByName.entrySet()) {
            MemberDept memberDept = new MemberDept();
            DoubleSummaryStatistics balanceSummaryForEach = entry.getValue().stream().collect(Collectors.summarizingDouble(Expend::getPrice));
            memberDept.setNane(entry.getKey());
            memberDept.setBalance(balanceSummaryForEach.getSum() - summaryFroTrip.getAverage());
            if(balanceSummaryForEach.getSum() < 0) {
                memberDeptsNegative.add(memberDept);
            } else {
                memberDeptsPositive.add(memberDept);
            }
        }

        for(MemberDept eachMemPos : memberDeptsPositive) {
            while(eachMemPos.getBalance() != 0.0) {
                for(MemberDept eachMemNeg : memberDeptsNegative) {
                    DeptTransport deptTransport = new DeptTransport();
                    deptTransport.setTo(eachMemPos.getNane());
                    deptTransport.setFrom(eachMemNeg.getNane());
                    double amount = eachMemPos.getBalance() - eachMemNeg.getBalance();
                    deptTransport.setAmount(amount);
                    if(eachMemPos.getBalance() - amount > 0) {

                    }
                }
            }
        }

        //memberDepts.stream().sorted((p1, p2) -> Double.compare(p1.getBalance(), p2.getBalance())).collect(Collectors.toList());

        return null;

    }
}
