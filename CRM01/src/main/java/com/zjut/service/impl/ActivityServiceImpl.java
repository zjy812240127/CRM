package com.zjut.service.impl;

import com.zjut.dao.ActivityDao;
import com.zjut.dao.ActivityRemarkDao;
import com.zjut.domain.Activity;
import com.zjut.service.ActivityService;
import com.zjut.vo.PaginationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private ActivityRemarkDao activityRemarkDao;


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

    @Override
    public PaginationVO<Activity> searchActivity(Map<String,Object> map) {
        List<Activity> list = new ArrayList<>();
        PaginationVO<Activity> vo = new PaginationVO<>();

        list = activityDao.searchActivity(map);

        vo.setDataList(list);

        int total = activityDao.getTotalByCondition(map);

        vo.setTotal(total);
        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        /**
         * 一张市场活动主表，一张备注表
         *      删除主表前，先根据主表的id关联备注表的外键，如果该市场活动存在备注就先删除备注表中的记录
         *      如果没有备注，就直接删除活动表记录
         */
        boolean flag = true;
        // 查询需要删除的备注条数
         int count1 = activityRemarkDao.getCountByAids(ids);
        // 删除备注，返回收到影响的条数（实际删除的数量），应该等于查询到的要删除的备注条数
        int count2 = activityRemarkDao.deleteByAids(ids);

        if (count1 != count2){
            flag = false;
        }

        // 删除市场表中的记录
        int count3 = activityDao.delete(ids);

        if (count3 != ids.length){
            flag = false;
        }


        return flag;
    }


}
