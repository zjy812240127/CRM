package com.zjut.dao;

import com.zjut.domain.DicValue;

import java.util.List;

public interface DicValueDao {
    List<DicValue> getValue(String dType);
}
