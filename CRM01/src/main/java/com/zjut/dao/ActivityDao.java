package com.zjut.dao;

import com.zjut.domain.Activity;
import com.zjut.domain.ActivityRemark;

import java.util.List;
import java.util.Map;

public interface ActivityDao {
     int save(Activity a);


     List<Activity> searchActivity(Map<String,Object> map);

     int getTotalByCondition(Map<String, Object> map);



     int delete(String[] ids);

     Activity getById(String id);

     int update(Activity a);

    Activity detail(String id);

     // List<ActivityRemark> getRemarkListByAid(String activityId);
}
