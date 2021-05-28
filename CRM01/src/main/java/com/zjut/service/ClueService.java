package com.zjut.service;

import com.zjut.domain.Activity;
import com.zjut.domain.Clue;
import com.zjut.domain.DicValue;

import java.util.List;
import java.util.Map;

public interface ClueService {

    boolean saveClue(Clue clue);

    Clue detail(String id);

    List<Activity> findActivity(String clueId);

    boolean unbund(String id);

    Map<String, Object> searchActivity(String aName, String clueId);

    List<Activity> findActivityByNameAndNotClueId(Map<String, String> map);

    boolean associate(Map<String, Object> map);

    List<Activity> changeSA(String aName);
}
