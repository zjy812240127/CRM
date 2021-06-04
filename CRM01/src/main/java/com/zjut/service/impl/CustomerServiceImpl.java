package com.zjut.service.impl;

import com.zjut.dao.CustomerDao;
import com.zjut.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Override
    public List<String> getFullName(String name) {
        List<String> fullNameList = customerDao.getFullName(name);

        return fullNameList;
    }

}
