package org.hse.baseproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Group {
    private Integer id;
    private String name;

    public List<Group> ChildGroups;


    public Group(Integer id, String name){
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Integer getId(){
        return this.id;
    }

}



