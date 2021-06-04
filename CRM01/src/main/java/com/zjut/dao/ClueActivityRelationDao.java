package com.zjut.dao;


import com.zjut.domain.Activity;
import com.zjut.domain.ClueActivityRelation;

import java.util.List;
import java.util.Map;

public interface ClueActivityRelationDao {


    List<Activity> findActivityId(String clueId);

    int unbund(String id);

    int associateActivity(Map<String, Object> map);

    List<ClueActivityRelation> findClueActivityRelationByClueId(String clueId);

    int deleteClueActivityRelationByClueId(String clueId);

}
