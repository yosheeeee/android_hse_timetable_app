package org.hse.baseproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EpInfo {
    private final Integer id;
    private final String name;
    private final Map<Integer, Integer> coursesInfo;

    public EpInfo(String name, Map<Integer, Integer> coursesInfo, Integer id) {
        this.name = name;
        this.coursesInfo = new LinkedHashMap<>(coursesInfo);
        this.id = id;
    }

    public List<Integer> getCoursesCount() {
        return new ArrayList<>(coursesInfo.keySet());
    }

    public Integer getGroupsCount(int courseKey) {
        return coursesInfo.getOrDefault(courseKey, 0);
    }

    @Override
    public String toString() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public String getName(){
        return this.name;
    }
}
