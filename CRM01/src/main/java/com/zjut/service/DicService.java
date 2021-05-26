package com.zjut.service;

import com.zjut.domain.DicValue;

import java.util.List;
import java.util.Map;

public interface DicService {

    Map<String, List<DicValue>> getAll();
}
