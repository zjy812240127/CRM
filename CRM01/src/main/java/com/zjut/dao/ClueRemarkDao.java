package com.zjut.dao;

import com.zjut.domain.ClueRemark;

import java.util.List;

public interface ClueRemarkDao {

    List<ClueRemark> findClueRemarkByClueId(String clueId);

    int deleteClueRemarkByClueId(String clueId);
}
