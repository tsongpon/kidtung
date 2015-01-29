package com.kidtung.service;

import com.kidtung.domain.Expend;
import com.kidtung.domain.Member;
import com.kidtung.domain.Trip;
import com.kidtung.transport.DeptTransport;
import com.kidtung.transport.MemberDept;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        BigDecimal tripAvg = (BigDecimal.valueOf(tripStatistics.getSum()).divide(BigDecimal.valueOf(trip.getMemberList().size()), RoundingMode.HALF_UP));

        Map<String,List<Expend>> groupByName = allExpend.stream().collect(Collectors.groupingBy(Expend::getName));

        List<MemberDept> positiveMember = new ArrayList<>();
        List<MemberDept> negativeMember = new ArrayList<>();

        List<DeptTransport> deptTransports = new LinkedList<>();

        for (Map.Entry<String,List<Expend>> deptEachName : groupByName.entrySet()) {
            //DoubleSummaryStatistics statisticsForThisName = deptEachName.getValue().stream().collect(Collectors.summarizingDouble(Expend::getPrice));
            BigDecimal sumForThisName = BigDecimal.ZERO;
            for(Expend each : deptEachName.getValue()) {
                sumForThisName = sumForThisName.add(BigDecimal.valueOf(each.getPrice()));
            }
            MemberDept memberDept = new MemberDept();
            memberDept.setNane(deptEachName.getKey());
            BigDecimal balance = sumForThisName.subtract(tripAvg);
            memberDept.setBalance(balance);
            if(balance.compareTo(BigDecimal.ZERO) == -1) {
                negativeMember.add(memberDept);
            } else if(balance.compareTo(BigDecimal.ZERO) == 1) {
                positiveMember.add(memberDept);
            }
        }

        //Now we have 2 listm positive and negative
        for(MemberDept positiveDept : positiveMember) {
            while(positiveDept.getBalance().compareTo(BigDecimal.ZERO) != 0 && !isAllElementBalanceZero(negativeMember)) {
                for(MemberDept negDept : negativeMember) {
                    DeptTransport dept = new DeptTransport();
                    dept.setFrom(negDept.getNane());
                    dept.setTo(positiveDept.getNane());
                    if (negDept.getBalance().compareTo(positiveDept.getBalance()) == 0) {
                        dept.setAmount(positiveDept.getBalance());
                        if(!dept.getAmount().equals(BigDecimal.ZERO)) {
                            deptTransports.add(dept);
                        }
                        positiveDept.setBalance(BigDecimal.ZERO);
                        negDept.setBalance(BigDecimal.ZERO);
                    } else {
                        if(negDept.getBalance().abs().compareTo(positiveDept.getBalance().abs()) == 1) {
                            dept.setAmount(positiveDept.getBalance());
                            negDept.setBalance(negDept.getBalance().add(positiveDept.getBalance()));
                            positiveDept.setBalance(BigDecimal.ZERO);
                        } else {
                            dept.setAmount(negDept.getBalance().abs());
                            positiveDept.setBalance(positiveDept.getBalance().add(negDept.getBalance()));
                            negDept.setBalance(BigDecimal.ZERO);
                            //negativeMember.remove(negDept);
                        }
                        if(!dept.getAmount().equals(BigDecimal.ZERO)) {
                            deptTransports.add(dept);
                        }
                    }
                }
            }
        }

        return deptTransports;
    }

    private boolean isAllElementBalanceZero(List<MemberDept> memberDepts) {
        for(MemberDept mem : memberDepts) {
            if(mem.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                return false;
            }
        }
        return true;
    }
}
