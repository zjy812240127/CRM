package com.zjut.dao;

import com.zjut.domain.User;

import java.util.Map;

public interface UserDao {

    User login(Map<String, String> map);
}
