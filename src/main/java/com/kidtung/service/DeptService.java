package com.kidtung.service;

import com.kidtung.domain.Expend;
import com.kidtung.domain.Member;
import com.kidtung.domain.Trip;
import com.kidtung.transport.DeptTransport;
import com.kidtung.transport.MemberDept;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
public class DeptService {

    public List<DeptTransport> calDept(Trip trip) {
        List<Expend> allExpend = new ArrayList<>();
        for(Member mem : trip.getMemberList()) {
            if(mem.getExpendList().size() == 0) {
                // set dummy
                Expend dummy = new Expend();
                dummy.setCode("dummy");
                dummy.setItem("dummy");
                dummy.setName(mem.getName());
                dummy.setPrice(0.0);
                mem.getExpendList().add(dummy);
            }
        }
        trip.getMemberList().stream().forEach(m -> allExpend.addAll(m.getExpendList()));
        DoubleSummaryStatistics tripStatistics = allExpend.stream().collect(Collectors.summarizingDouble(Expend::getPrice));
        double tripAvg = tripStatistics.getSum() / trip.getMemberList().size();

        Map<String,List<Expend>> groupByName = allExpend.stream().collect(Collectors.groupingBy(Expend::getName));

        List<MemberDept> positiveMember = new ArrayList<>();
        List<MemberDept> negativeMember = new ArrayList<>();

        List<DeptTransport> deptTransports = new LinkedList<>();

        for (Map.Entry<String,List<Expend>> deptEachName : groupByName.entrySet()) {
            DoubleSummaryStatistics statisticsForThisName = deptEachName.getValue().stream().collect(Collectors.summarizingDouble(Expend::getPrice));
            MemberDept memberDept = new MemberDept();
            memberDept.setNane(deptEachName.getKey());
            double balance = statisticsForThisName.getSum() - tripAvg;
            memberDept.setBalance(balance);
            if(balance < 0) {
                negativeMember.add(memberDept);
            } else if(balance > 0) {
                positiveMember.add(memberDept);
            }
        }

        //Now we have 2 listm positive and negative
        for(MemberDept positiveDept : positiveMember) {
            while(positiveDept.getBalance() != 0) {
                for(MemberDept negDept : negativeMember) {

                    if(negDept.getBalance() == positiveDept.getBalance()) {
                        DeptTransport dept = new DeptTransport();
                        dept.setFrom(negDept.getNane());
                        dept.setTo(positiveDept.getNane());
                        dept.setAmount(positiveDept.getBalance());
                        deptTransports.add(dept);
                        positiveDept.setBalance(0);
                        negDept.setBalance(0);
                    }
                    if(negDept.getBalance() > positiveDept.getBalance()) {
                        DeptTransport dept = new DeptTransport();
                        dept.setFrom(negDept.getNane());
                        dept.setTo(positiveDept.getNane());
                        dept.setAmount(positiveDept.getBalance());
                        deptTransports.add(dept);
                        negDept.setBalance(negDept.getBalance() + positiveDept.getBalance());
                        positiveDept.setBalance(0);
                    } else {
                        if(negDept.getBalance() < positiveDept.getBalance()) {
                            DeptTransport dept = new DeptTransport();
                            dept.setFrom(negDept.getNane());
                            dept.setTo(positiveDept.getNane());
                            dept.setAmount(Math.abs(negDept.getBalance()));
                            deptTransports.add(dept);
                            positiveDept.setBalance(positiveDept.getBalance() + negDept.getBalance());
                            negDept.setBalance(0);
                        }
                    }
                }
            }
        }

        //memberDepts.stream().sorted((p1, p2) -> Double.compare(p1.getBalance(), p2.getBalance())).collect(Collectors.toList());

        return deptTransports;

    }
}
