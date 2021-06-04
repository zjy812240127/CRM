package com.zjut.dao;

import com.zjut.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranDao {

    int saveTran(Tran tran);


    int addTran(Tran tran);

    Tran searchTranById(String id);

    int changeStage(Tran tran);

    int getTotal();

    List<Map<String, Object>> getEchartsParams();

}
