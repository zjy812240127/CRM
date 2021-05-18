package com.zjut.controller;

import com.zjut.domain.Activity;
import com.zjut.domain.JsonResult;
import com.zjut.domain.User;
import com.zjut.service.ActivityService;
import com.zjut.service.UserService;
import com.zjut.service.impl.ActivityServiceImpl;
import com.zjut.util.DateTimeUtil;
import com.zjut.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class ActivityController {
    @Autowired
    private UserService service;

    @Autowired
    private ActivityService activityService;

    @RequestMapping("/getUserList.do")
    @ResponseBody
    public List<User> getUserList(){
        List<User> users = service.getUserList();
        return users;
    }

    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    // springMVC框架可以直接传入一个类对象来存储大量参数
    public JsonResult save(Activity a, HttpServletRequest request, HttpServletResponse response){
        JsonResult result = new JsonResult();
        System.out.println("执行市场活动添加操作");

        String  id = UUIDUtil.getUUID();
        String  owner = request.getParameter("owner");
        String  name = request.getParameter("name");
        String  startDate = request.getParameter("startDate");
        String  endDate = request.getParameter("endDate");
        String  cost = request.getParameter("cost");
        String  description = request.getParameter("description");
        // 创建时间为系统当前时间
        String  createTime = DateTimeUtil.getSysTime();
        // 从session域中取得用户的名字
        String  createBy = ((User)request.getSession().getAttribute("user")).getName();

        // Activity a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setCreateTime(createTime);
        a.setCreateBy(createBy);

        System.out.println(a.getCost()+a.getCreateBy()+a.getCreateTime());
        boolean flag = activityService.saveActivity(a);
        //result.setSuccess(flag);
        System.out.println("++++++++++++++++++");
//        System.out.println("flag:"+flag);
//        if (flag > 0){
//            result.setSuccess(true);
//        }else{
//            result.setSuccess(false);
//        }
        result.setSuccess(flag);

        return result;
    }
}
