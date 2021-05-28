package com.zjut.service.impl;

import com.zjut.dao.*;
import com.zjut.domain.*;
import com.zjut.service.ClueService;
import com.zjut.service.UserService;
import com.zjut.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClueServiceImpl implements ClueService {

    @Autowired
    private ClueDao clueDao;

    @Autowired
    private ClueActivityRelationDao clueActivityRelationDao;

    @Autowired
    private ActivityDao activityDao;

    @Override
    public boolean saveClue(Clue clue) {
        System.out.println("ClueServiceImpl添加线索保存操作");

        boolean flag = false;

        int count = clueDao.saveClue(clue);
        System.out.println("保存操作影响的数据库记录条数：" + count);
        if (count > 0){
            flag = true;
        }
        return flag;
    }

    @Override
    public Clue detail(String id) {
        Clue clue = clueDao.detail(id);
        return clue;
    }

    @Override
    public List<Activity> findActivity(String clueId) {
        System.out.println("执行查询线索关联的市场活动serviceImpl");

        List<Activity> activityList = new ArrayList<>();
        List<Activity> aIdList = clueActivityRelationDao.findActivityId(clueId);

        return aIdList;
    }

    @Override
    public boolean unbund(String id) {

        boolean flag = false;
        int count = clueActivityRelationDao.unbund(id);
        if(count >0){
            flag = true;
        }
        return flag;
    }

    @Override
    public Map<String, Object> searchActivity(String aName, String clueId) {
        Map<String,Object> map = new HashMap<>();
        boolean flag = false;
        // 模糊查询市场活动
        List<Activity> alist = activityDao.findActivity(aName, clueId);
        System.out.println("返回数据库模糊查询结果");
        if (alist.size()>0){
            flag = true;
        }
        map.put("success",flag);
        map.put("activity",alist);

        return map;
    }

    @Override
    public List<Activity> findActivityByNameAndNotClueId(Map<String,String> map) {

        List<Activity> saList = activityDao.findActivityByName(map);
        System.out.println("从数据库返回查询结果");
        return saList;
    }

    @Override
    public boolean associate(Map<String, Object> map) {
        boolean flag = false;
        int count = clueActivityRelationDao.associateActivity(map);
        if(count > 0){
            System.out.println("数据库中插入关联市场活动成功");
            flag = true;
        }
        return flag;
    }

    @Override
    public List<Activity> changeSA(String aName) {
        List<Activity> activityList = activityDao.changeSearchActivity(aName);
        return activityList;
    }


}
