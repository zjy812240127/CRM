package com.zjut.service;

import com.zjut.domain.Activity;
import com.zjut.vo.PaginationVO;

import java.util.List;
import java.util.Map;

public interface ActivityService {
        boolean saveActivity(Activity a);
        PaginationVO<Activity> searchActivity(Map<String, Object> map);

    boolean delete(String[] ids);
}
