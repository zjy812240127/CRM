package com.zjut.service.impl;

import com.zjut.dao.DicTypeDao;
import com.zjut.dao.DicValueDao;
import com.zjut.domain.DicType;
import com.zjut.domain.DicValue;
import com.zjut.service.DicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DicServiceImpl implements DicService {

    @Autowired
    private DicTypeDao dicTypeDao;

    @Autowired
    private DicValueDao dicValueDao;


    @Override
    public Map<String, List<DicValue>> getAll() {
        // 先取得类型对象列表

        Map<String, List<DicValue>> map = new HashMap<>();
        System.out.println("获取数据字典类型");
        List<DicType> typeList = dicTypeDao.getType();


        // 查找每个类型中包含的值（数据行）对象
        for(DicType type: typeList){
            // 以code作为select条件
            String dType = type.getCode();
            List<DicValue> dvList = dicValueDao.getValue(dType);
            System.out.println("获取数据字典数值");
            map.put(dType,dvList);
        }


        return map;

    }
}
