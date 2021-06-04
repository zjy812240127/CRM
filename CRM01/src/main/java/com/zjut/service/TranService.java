package com.zjut.service;

import com.zjut.domain.Tran;
import com.zjut.domain.TranHistory;
import com.zjut.domain.User;

import java.util.List;
import java.util.Map;

public interface TranService {
    List<User> getUserList();



    boolean addTran(Tran tran);

    Tran findTranById(String id);

    List<TranHistory> getHistoryList(String tranId, Map<String,String> pMap);

    Map<String, Object> changeStage(Tran tran);

    Map<String, Object> getEcharts();
}
