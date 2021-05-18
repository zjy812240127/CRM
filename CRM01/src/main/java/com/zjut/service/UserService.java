package com.zjut.service;

import com.zjut.domain.User;
import com.zjut.exception.LoginException;

public interface UserService {
    public User login( String loginAct, String loginPwd, String ip) throws LoginException;

}
