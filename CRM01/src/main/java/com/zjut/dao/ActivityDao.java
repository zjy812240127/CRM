package com.zjut.dao;

import com.zjut.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityDao {
     int save(Activity a);


     List<Activity> searchActivity(Map<String,Object> map);

     int getTotalByCondition(Map<String, Object> map);



     int delete(String[] ids);

}
