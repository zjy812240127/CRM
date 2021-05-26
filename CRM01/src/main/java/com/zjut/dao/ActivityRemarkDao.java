package com.zjut.dao;

import com.zjut.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkDao {
    int getCountByAids(String[] ids);

    int deleteByAids(String[] ids);

    List<ActivityRemark> getRemarkListByAid(String activityId);

    int deleteRemarkById(String id);

    int saveRemark(ActivityRemark ar);

    ActivityRemark findRemark(String arId);

    int updateRemark(ActivityRemark ar);
}
