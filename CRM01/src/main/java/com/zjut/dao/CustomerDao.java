package com.zjut.dao;

import com.zjut.domain.Customer;

import java.util.List;

public interface CustomerDao {

    Customer findCustomerByName(String company);

    int saveCustomer(Customer customer);

    List<String> getFullName(String name);

    Customer searchByName(String customerName);
}
