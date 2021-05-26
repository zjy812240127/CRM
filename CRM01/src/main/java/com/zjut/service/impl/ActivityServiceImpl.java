package com.zjut.service.impl;

import com.zjut.dao.ActivityDao;
import com.zjut.dao.ActivityRemarkDao;
import com.zjut.domain.Activity;
import com.zjut.domain.ActivityRemark;
import com.zjut.domain.User;
import com.zjut.service.ActivityService;
import com.zjut.service.UserService;
import com.zjut.vo.PaginationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private ActivityRemarkDao activityRemarkDao;

    @Autowired
    private UserService userService;


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

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {

        Map<String,Object> map = new HashMap<>();
        List<User> list = userService.getUserList();
        System.out.println(list);
        Activity a = activityDao.getById(id);
        System.out.println(a.getCreateTime());
        map.put("uList",list);
        map.put("a",a);

        return map;
    }

    @Override
    public boolean update(Activity a) {
        int nums = activityDao.update(a);
        System.out.println("修改提交受影响的记录数");
        boolean flag = false;
        if (nums > 0 ){
            flag = true;
        }
        return flag;
    }

    @Override
    public Activity detail(String id) {
        Activity a = activityDao.detail(id);
        return a;
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String activityId) {
        //List<ActivityRemark> list = activityDao.getRemarkListByAid(activityId);
        List<ActivityRemark> list = activityRemarkDao.getRemarkListByAid(activityId);
        return list;
    }

    @Override
    public boolean deleteRemarkById(String id) {
        boolean flag = false;
        int count = activityRemarkDao.deleteRemarkById(id);
        if (count > 0){
            flag = true;
        }
        return flag;
    }

    @Override
    public Map<String, Object> saveRemark(ActivityRemark ar) {
        Map<String,Object> map = new HashMap<>();
        boolean flag = false;

        int count = activityRemarkDao.saveRemark(ar);
        if (count > 0){
            flag = true;
            String arId = ar.getId();
            ActivityRemark activityRemark = activityRemarkDao.findRemark(arId);
            map.put("ar", activityRemark);
            map.put("success",flag);
        }else{
            System.out.println("数据库添加备注失败");
        }

        return map;
    }

    @Override
    public Map<String, Object> updateRemark(ActivityRemark ar) {
        Map<String,Object> map = new HashMap<>();
        boolean flag = false;
        String arId = ar.getId();
        int count = activityRemarkDao.updateRemark(ar);
        if (count >0){
            flag = true;
            ActivityRemark activityRemark = activityRemarkDao.findRemark(arId);
            map.put("success",flag);
            map.put("ar", activityRemark);
        }
        return map;
    }


}
