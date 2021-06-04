package com.zjut.dao;

import com.zjut.domain.TranHistory;

import java.util.List;

public interface TranHistoryDao {

    int saveTranHistory(TranHistory tranHistory);

    List<TranHistory> getHistoryById(String tranId);

}
