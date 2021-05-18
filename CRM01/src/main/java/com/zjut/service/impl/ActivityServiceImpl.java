package com.zjut.service.impl;

import com.zjut.dao.ActivityDao;
import com.zjut.domain.Activity;
import com.zjut.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityDao activityDao;

    @Override
    public boolean saveActivity(Activity a) {
        boolean flag = true;
        System.out.println("成功调用service");
        System.out.println(a.getCost()+a.getCreateTime());
        int count = activityDao.save(a);
        if (count != 1){
            flag = false;
        }
        return flag;

    }


}
