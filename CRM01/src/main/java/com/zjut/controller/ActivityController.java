package com.zjut.controller;

import com.zjut.domain.Activity;
import com.zjut.domain.ActivityRemark;
import com.zjut.domain.JsonResult;
import com.zjut.domain.User;
import com.zjut.service.ActivityService;
import com.zjut.service.UserService;
import com.zjut.service.impl.ActivityServiceImpl;
import com.zjut.util.DateTimeUtil;
import com.zjut.util.UUIDUtil;
import com.zjut.vo.PaginationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ActivityController {
    @Autowired
    private UserService service;

    @Autowired
    private ActivityService activityService;

    // 删除按钮
    @ResponseBody
    @RequestMapping(value = "/delete.do")
    public JsonResult delete(HttpServletRequest request, HttpServletResponse response){
        System.out.println("执行市场活动的删除操作");
        JsonResult result = new JsonResult();
        // 获取数组，getParameterValues()
        String[] ids = request.getParameterValues("id");
        boolean flag = activityService.delete(ids);
        result.setSuccess(flag);

        return result;
    }

    // 创建按钮绑定事件
    @RequestMapping("/getUserList.do")
    @ResponseBody
    public List<User> getUserList(){
        List<User> users = service.getUserList();
        return users;
    }


    // 市场活动添加按钮事件
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

    // 刷新市场活动列表
    @RequestMapping(value = "/pageList.do")
    @ResponseBody
    public PaginationVO<Activity> pageList( HttpServletResponse response, HttpServletRequest request){
        PaginationVO<Activity> vo = new PaginationVO<>();
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        int pageNo = Integer.valueOf(pageNoStr);
        int pageSize = Integer.valueOf(pageSizeStr);
        // 计算略过的记录数
        int count = (pageNo-1) * pageSize;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("count",count);
        map.put("pageSize",pageSize);
        /**
         *  用vo对象来传递参数，因为之后有很多分页查询操作，所以同意建立一个vo对象给前端传递参数
         *      但是vo类要用泛型类，并不是每次都传递Activity对象
         *      Pagination<Activity> vo = new Pagination<>();
         *      vo.setTotal(total);
         *      vo.setDataList(dataList);
         */

       vo = activityService.searchActivity(map);

       return vo;



    }

    // 市场活动修改按钮事件
    @RequestMapping(value = "/getUserListAndActivity.do")
    @ResponseBody
    public Map<String, Object> getUserListAndActivity( String id){
        System.out.println("进入查询修改按钮的表单controller");

        System.out.println("前端传递的id:" + id);
        Map<String,Object> map = new HashMap<>();
        map = activityService.getUserListAndActivity(id);
        return map;
    }


    // 提交对市场活动的修改
    @ResponseBody
    @RequestMapping(value = "/update.do", method = RequestMethod.POST)
    public JsonResult update(Activity a, HttpServletRequest request){
        System.out.println("进入修改提交按钮controller");
        boolean flag = activityService.update(a);
        a.setCreateBy(((User)request.getSession().getAttribute("user")).getName());
        a.setCreateTime(DateTimeUtil.getSysTime());

        JsonResult result = new JsonResult();
        result.setSuccess(flag);
        return result;
    }


    // 跳转到市场活动详细信息页面，注意前端地址栏workbench前面没有/，所以这里value前不加/
    @RequestMapping(value = "workbench/activity/detail.do")
    // @ResponseBody  // 加了表示返回字符串，不加返回页面
    public String detail(String id, Model model){
        System.out.println("跳转到市场活动详细信息页面");
         Activity a = activityService.detail(id);
        // 前端可以通过el表达式获取对象a的信息 ${a}
        model.addAttribute("a",a);
        return "workbench/activity/detail";

    }

    // detail页面备注栏局部刷新操作
    @ResponseBody
    @RequestMapping(value = "/getRemarkListByAid.do")
    public List<ActivityRemark> getRemarkListByAid(String activityId){
        System.out.println("进入detail页面备注栏局部刷新操作");

        List<ActivityRemark> list = activityService.getRemarkListByAid(activityId);

        return list;
    }

    @RequestMapping(value = "/deleteRemark.do", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult deleteRemark(String id){
        System.out.println("进入删除备注操作");
        JsonResult result = new JsonResult();
        boolean flag = activityService.deleteRemarkById(id);
        result.setSuccess(flag);
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/saveRemark.do", method = RequestMethod.POST)
    public Map<String, Object> saveRemark(String noteContent, String activityId, HttpServletRequest request){

        System.out.println("执行添加备注操作");
        ActivityRemark ar = new ActivityRemark();
        ar.setCreateBy(((User)request.getSession().getAttribute("user")).getName());
        ar.setNoteContent(noteContent);
        ar.setActivityId(activityId);
        ar.setCreateTime(DateTimeUtil.getSysTime());
        ar.setEditFlag("0");
        ar.setId(UUIDUtil.getUUID());

        Map<String, Object> map = activityService.saveRemark(ar);
        System.out.println("map的ar：" + map.get("ar"));
        System.out.println("map的success："+ map.get("success"));

        return map;

    }

    @ResponseBody
    @RequestMapping(value = "/updateRemark.do", method = RequestMethod.POST)
    public Map<String, Object> updateRemark(String id, String noteContent,HttpServletRequest request){
        System.out.println("后台进入更新备注操作");
        System.out.println("前端传过来的noteContent内容为：" + noteContent);
        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setEditTime(DateTimeUtil.getSysTime());
        ar.setEditBy(((User)request.getSession().getAttribute("user")).getName());
        ar.setEditFlag("1");
        Map<String,Object> map = activityService.updateRemark(ar);

        return map;
    }


}
