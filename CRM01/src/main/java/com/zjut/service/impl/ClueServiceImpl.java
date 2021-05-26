package com.zjut.service.impl;

import com.zjut.dao.ClueDao;
import com.zjut.dao.DicTypeDao;
import com.zjut.dao.DicValueDao;
import com.zjut.domain.Clue;
import com.zjut.domain.DicType;
import com.zjut.domain.DicValue;
import com.zjut.domain.User;
import com.zjut.service.ClueService;
import com.zjut.service.UserService;
import com.zjut.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public class ClueServiceImpl implements ClueService {

    @Autowired
    private ClueDao clueDao;

    @Override
    public boolean saveClue(Clue clue) {
        System.out.println("ClueServiceImpl添加线索保存操作");

        boolean flag = false;

        int count = clueDao.saveClue(clue);
        System.out.println("保存操作影响的数据库记录条数：" + count);
        if (count > 0){
            flag = true;
        }
        return flag;
    }
}
