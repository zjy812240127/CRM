package com.zjut.controller;

import com.zjut.domain.Activity;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ActivityController {
    @Autowired
    private UserService service;

    @Autowired
    private ActivityService activityService;

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


}
