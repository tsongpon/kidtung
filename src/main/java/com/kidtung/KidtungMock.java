package com.kidtung;

import com.kidtung.domain.Expend;
import com.kidtung.domain.Member;
import com.kidtung.domain.Trip;
import com.kidtung.util.KidtungUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class KidtungMock {

    public Trip mockTrip() {
        Trip mockTrip = new Trip();
        mockTrip.setCode("kidtung1");
        mockTrip.setName("Let go to Japan");
        mockTrip.setDescription("Japan tour");

        List<Member> members = new ArrayList<>();

        {
            Member tum = new Member();
            tum.setName("tum");
            List<Expend> tumExpends = new ArrayList<>();
            Expend gas = new Expend();
            gas.setCode(KidtungUtil.generateRandomCode(3));
            gas.setItem("Gas");
            gas.setPayDate(new Date());
            gas.setPrice(1700.00);
            tumExpends.add(gas);

            Expend zoo = new Expend();
            zoo.setCode(KidtungUtil.generateRandomCode(3));
            zoo.setItem("zoo");
            zoo.setPayDate(new Date());
            zoo.setPrice(2500.00);
            tumExpends.add(zoo);

            Expend amazon = new Expend();
            amazon.setCode(KidtungUtil.generateRandomCode(3));
            amazon.setItem("amazon");
            amazon.setPayDate(new Date());
            amazon.setPrice(250.00);
            tumExpends.add(amazon);

            Expend dinner = new Expend();
            dinner.setPayDate(new Date());
            dinner.setItem("dinner");
            dinner.setCode(KidtungUtil.generateRandomCode(3));
            tumExpends.add(dinner);

            tum.setExpendList(tumExpends);
            members.add(tum);
        }

        {
            Member pang = new Member();
            pang.setName("pang");
            List<Expend> pangExpends = new ArrayList<>();
            Expend lunch = new Expend();
            lunch.setCode(KidtungUtil.generateRandomCode(3));
            lunch.setItem("lunch");
            lunch.setPayDate(new Date());
            lunch.setPrice(1200.00);
            pangExpends.add(lunch);

            Expend snack = new Expend();
            snack.setPayDate(new Date());
            snack.setItem("pangExpends");
            snack.setCode(KidtungUtil.generateRandomCode(3));
            pangExpends.add(snack);

            pang.setExpendList(pangExpends);
            members.add(pang);
        }

        {
            Member tai = new Member();
            tai.setName("tai");
            List<Expend> taiExpends = new ArrayList<>();
            Expend starbuck = new Expend();
            starbuck.setCode(KidtungUtil.generateRandomCode(3));
            starbuck.setItem("starbuck");
            starbuck.setPayDate(new Date());
            starbuck.setPrice(2200.00);
            taiExpends.add(starbuck);

            Expend snack = new Expend();
            snack.setPayDate(new Date());
            snack.setItem("pangExpends");
            snack.setCode(KidtungUtil.generateRandomCode(3));
            taiExpends.add(snack);

            tai.setExpendList(taiExpends);
            members.add(tai);
        }

        {
            Member ni = new Member();
            ni.setName("ni");
            List<Expend> niExpends = new ArrayList<>();

            ni.setExpendList(niExpends);
            members.add(ni);
        }

        {
            Member bee = new Member();
            bee.setName("bee");
            List<Expend> beeExpends = new ArrayList<>();

            bee.setExpendList(beeExpends);
            members.add(bee);
        }

        {
            Member som = new Member();
            som.setName("som");
            List<Expend> somExpends = new ArrayList<>();
            Expend planTicket = new Expend();
            planTicket.setCode(KidtungUtil.generateRandomCode(3));
            planTicket.setItem("planTicket");
            planTicket.setPayDate(new Date());
            planTicket.setPrice(200000.00);
            somExpends.add(planTicket);

            som.setExpendList(somExpends);
            members.add(som);
        }

        mockTrip.setMemberList(members);

        return mockTrip;
    }
}
