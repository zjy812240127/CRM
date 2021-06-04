package com.zjut.dao;

import com.zjut.domain.Tran;

public interface TranDao {

    int saveTran(Tran tran);


    int addTran(Tran tran);

    Tran searchTranById(String id);

    int changeStage(Tran tran);
}
