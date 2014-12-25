package com.kidtung.domain;

import java.util.List;

public class Member {
    private String name;
    private List<Expend> expendList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Expend> getExpendList() {
        return expendList;
    }

    public void setExpendList(List<Expend> expendList) {
        this.expendList = expendList;
    }
}