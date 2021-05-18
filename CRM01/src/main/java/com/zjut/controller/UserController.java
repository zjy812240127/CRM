package com.zjut.controller;

import com.zjut.domain.JsonResult;
import com.zjut.domain.User;
import com.zjut.service.UserService;
import com.zjut.service.impl.UserServiceImpl;
import com.zjut.util.MD5Util;
import com.zjut.util.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserServiceImpl service;

    @ResponseBody
    // 账号密码用post传输数据
    @RequestMapping(value = "/queryUser.do", method = RequestMethod.POST)
    public JsonResult queryUser(String loginAct, String loginPwd, HttpServletRequest request){

        /**
         * 接收账号、密码、ip地址
         * 将密码的明文形式转换为MD5
         */
        // new一个结果对象，参数中存放要传给前端的数据
        JsonResult result = new JsonResult();

        System.out.println("进入到验证登录操作");
        // 密码明文转换
        loginPwd = MD5Util.getMD5(loginPwd);
        // 获取浏览器ip
        String ip = request.getRemoteAddr();
        System.out.println("浏览器端ip：" + ip);

        // 未来开发，统一使用代理类形态的接口对象
        // UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        try{
            User user = service.login(loginAct,loginPwd,ip);
            request.getSession().setAttribute("user",user);
            // 如果程序执行到此处说明，成功登录 要传输数据{"success":true},由于JsonResult类默认success为true
            // 所以不需要再设置该参数

        }catch (Exception e){
            e.printStackTrace();
            // 运行到此处说明登录失败，要传参数{"success":false, "msg"}
            /**
             * 自定义一个JsonResult类，设置属性
             *      success，message来存储{"success":false, "msg"}
             *      new 一个JsonResult对象，为其属性赋值
             *      返回该对象
             */
            String msg = e.getMessage();
            // result 的属性是message和success
            result.setMessage(msg);
            result.setSuccess(false);
            return result;
        }

        return result;

    }
}
