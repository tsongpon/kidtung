package com.kidtung.domain;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import java.util.List;

public class Trip {

    @Id
    private String code;
    private String name;
    private String description;
    private List<Member> memberList ;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }
}
