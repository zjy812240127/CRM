package com.zjut.service;

import com.zjut.domain.Activity;
import com.zjut.domain.ActivityRemark;
import com.zjut.vo.PaginationVO;

import java.util.List;
import java.util.Map;

public interface ActivityService {
        boolean saveActivity(Activity a);
        PaginationVO<Activity> searchActivity(Map<String, Object> map);

    boolean delete(String[] ids);

    Map<String, Object> getUserListAndActivity(String id);

    boolean update(Activity a);

    Activity detail(String id);

    List<ActivityRemark> getRemarkListByAid(String activityId);

    boolean deleteRemarkById(String id);

    Map<String, Object> saveRemark(ActivityRemark ar);

    Map<String, Object> updateRemark(ActivityRemark ar);
}
