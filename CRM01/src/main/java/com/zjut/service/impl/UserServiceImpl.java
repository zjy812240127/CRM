package com.zjut.service.impl;

import com.zjut.dao.UserDao;
import com.zjut.domain.User;
import com.zjut.exception.LoginException;
import com.zjut.service.UserService;
import com.zjut.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {

        Map<String,String> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd", loginPwd);
        User user = userDao.login(map);

        if (user == null){
            throw new LoginException("账号密码错误");
        }

        // 如果程序运行到这里说明账号密码正确，继续验证

        // 验证失效时间
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        // 前面的时间减去后面的时间，小于零表示当前登录时间超过规定的时间
        if (expireTime.compareTo(currentTime) < 0){
            throw new LoginException("超过时间，账号已失效");
        }

        // 验证账号锁定状态
        String lockState = user.getLockState();
        if ("0".equals(lockState)){
            throw new LoginException("账号已锁定");
        }

        // 验证是否是允许访问的ip
        /** 如果浏览器端是用localhost：8080/MyWeb来访问的
         * 则用request.getRemoteAddr()返回的是0:0:0.....1，而不是我们想要的127.0.0.1
         * 所以浏览器端地址栏要把localhost换成127.0.0.1
         */
        String allowIps = user.getAllowIps();
        if (!allowIps.contains(ip)){
            throw new LoginException("ip地址受限");
        }
        return user;
    }

    @Override
    public List<User> getUserList() {
        List<User> users = userDao.getUserList();
        return users;
    }
}
