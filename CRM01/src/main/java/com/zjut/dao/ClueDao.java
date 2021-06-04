package com.zjut.dao;


import com.zjut.domain.Clue;

public interface ClueDao {


    int saveClue(Clue clue);

    Clue detail(String id);

    Clue findClueByName(String clueId);

    int deleteById(String clueId);
}
