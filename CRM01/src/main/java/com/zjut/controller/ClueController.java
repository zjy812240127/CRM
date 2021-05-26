package com.zjut.controller;

import com.zjut.domain.Clue;
import com.zjut.domain.JsonResult;
import com.zjut.domain.User;
import com.zjut.service.ClueService;
import com.zjut.service.UserService;
import com.zjut.util.DateTimeUtil;
import com.zjut.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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




}
