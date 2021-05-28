package com.zjut.controller;

import com.zjut.domain.Activity;
import com.zjut.domain.Clue;
import com.zjut.domain.JsonResult;
import com.zjut.domain.User;
import com.zjut.service.ClueService;
import com.zjut.service.UserService;
import com.zjut.util.DateTimeUtil;
import com.zjut.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ClueController {

    @Autowired
    private UserService userService;

    @Autowired
    private ClueService clueService;

    @ResponseBody
    @RequestMapping(value = "/addClue.do")
    public List<User> addClue(){
        System.out.println("添加线索模态框的userName下拉列表元素");
        List<User> uList = userService.getUserList();
        return uList;
    }

    @ResponseBody
    @RequestMapping(value = "/savaClue.do", method = RequestMethod.POST)
    public JsonResult savaClue(Clue clue , HttpServletRequest request){

        JsonResult result = new JsonResult();
        clue.setId(UUIDUtil.getUUID());
        clue.setCreateBy(((User)request.getSession().getAttribute("user")).getName());
        clue.setCreateTime(DateTimeUtil.getSysTime());

        System.out.println("进入添加线索保存操作");
        boolean flag = clueService.saveClue(clue);

        result.setSuccess(flag);

        return result;
    }

    // 页面跳转---》线索详细信息
    @RequestMapping(value = "workbench/clue/detail.do")
    public String detail(String id, Model model){
        System.out.println("跳转到详细信息页");

        // 根据网址中的id参数查询数据库中对应的线索详细信息
        Clue clue = clueService.detail(id);
        // 前端可以通过el表达式取到线索的属性值
        model.addAttribute(clue);
        // 请求转发到详细信息页
        return "workbench/clue/detail";
    }

    @ResponseBody
    @RequestMapping(value = "/activity.do")
    public List<Activity> activity(String clueId){

        List<Activity> list = clueService.findActivity(clueId);

        return list;

    }

    @ResponseBody
    @RequestMapping(value = "/unbund.do", method = RequestMethod.POST)
    public JsonResult unbund(String id){
        JsonResult result = new JsonResult();

        boolean flag = clueService.unbund(id);

        result.setSuccess(flag);

        System.out.println("解除市场活动关联成功");
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/searchActivity.do")
    public List<Activity> searchActivity(String aName, String clueId){
        System.out.println("前端传过来的检索关键字：" + aName);
        System.out.println("前端传过来的线索ID：" + clueId);

        Map<String, String> map = new HashMap<>();
        map.put("aName", aName);
        map.put("clueId", clueId);
        List<Activity> saList = clueService.findActivityByNameAndNotClueId(map);

        // Map<String,Object> map = clueService.searchActivity(aName,clueId);


        return saList;
    }

    @ResponseBody
    @RequestMapping(value = "/associate.do", method = RequestMethod.POST)
    public JsonResult associate(HttpServletRequest request){
        String cid = request.getParameter("cid");
        String[] aids = request.getParameterValues("aid");
        System.out.println("接收到关联市场活动的参数cid：" + cid);

        JsonResult result = new JsonResult();
        Map<String,Object> map = new HashMap<>();
        boolean flag = false;
        for(int i=0; i<aids.length; i++){
            String id = UUIDUtil.getUUID();
            map.put("id",id);
            map.put("cid",cid);
            map.put("aid",aids[i]);
            flag = clueService.associate(map);
            result.setSuccess(flag);
            if(flag = false){
                System.out.println("关联失败");
                break;
            }
        }
        // System.out.println("最终flag：" + flag);
        return result;

    }

    @ResponseBody
    @RequestMapping(value = "/changeSerachActivity.do")
    public List<Activity> changeSerachActivity(String aName){
        List<Activity> aList = clueService.changeSA(aName);

        return aList;
    }

    @RequestMapping(value = "/convert.do")
    public String convert(HttpServletRequest request){
        System.out.println("处理转换操作");
        // 用flag判断是否需要创建交易
        String flag = request.getParameter("flag");
        if ("a".equals(flag)){
            System.out.println("需要创建交易");
        }else{
            System.out.println("不需要创建交易");
        }

        return null;
    }




}
