package com.zjut.service.impl;

import com.zjut.dao.CustomerDao;
import com.zjut.dao.TranDao;
import com.zjut.dao.TranHistoryDao;
import com.zjut.dao.UserDao;
import com.zjut.domain.Customer;
import com.zjut.domain.Tran;
import com.zjut.domain.TranHistory;
import com.zjut.domain.User;
import com.zjut.service.TranService;
import com.zjut.util.DateTimeUtil;
import com.zjut.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TranServiceImpl implements TranService {
    @Autowired
    private TranDao tranDao;

    @Autowired
    private TranHistoryDao tranHistoryDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CustomerDao customerDao;


    @Override
    public List<User> getUserList() {

        List<User> userList = userDao.getUserList();
        return userList;
    }

    @Override
    public boolean addTran(Tran tran) {

        boolean flag = true;
        String customerName = tran.getCustomerId();
        System.out.println("前端的客户名称：" + customerName);

        /**
         *      由于不知道是否存在该customer，所以要先查询，
         *          如果存在，查取它的id
         *          如果不存在则新建一个customer
         */

        Customer customer = customerDao.searchByName(customerName);
        if (customer == null){
            // 说明不存在该客户，新建一条客户记录
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setContactSummary(tran.getContactSummary());
            customer.setDescription(tran.getDescription());
            customer.setOwner(tran.getOwner());
            customer.setName(customerName);
            customer.setCreateBy(tran.getCreateBy());
            customer.setNextContactTime(tran.getNextContactTime());

            int count = customerDao.saveCustomer(customer);
            if(count == 0){
                flag = false;
            }
        }

        tran.setCustomerId(customer.getId());
        System.out.println("新的customerId为：" + tran.getCustomerId());
        int count = tranDao.addTran(tran);
        if(count == 0){
            flag = false;
        }else{
            // 增加一条交易后要增加一条交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setTranId(tran.getId());
            tranHistory.setStage(tran.getStage());
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setCreateTime(DateTimeUtil.getSysTime());
            tranHistory.setCreateBy(tran.getCreateBy());
            int count2 = tranHistoryDao.saveTranHistory(tranHistory);
            if(count2 == 0){
                flag =false;
            }
        }

        return flag;
    }

    @Override
    public Tran findTranById(String id) {

        Tran tran = tranDao.searchTranById(id);


        return tran;
    }

    @Override
    public List<TranHistory> getHistoryList(String tranId, Map<String,String> pMap) {

        List<TranHistory> historyList = tranHistoryDao.getHistoryById(tranId);

        for(TranHistory tranHistory: historyList){
            String stage = tranHistory.getStage();
            String possibility = pMap.get(stage);
            tranHistory.setPossibility(possibility);
            System.out.println("对应的possibility：" + possibility);

        }
        return historyList;
    }

    @Override
    public Map<String, Object> changeStage(Tran tran) {
        System.out.println("进入修改状态的impl业务层");

        // 改变tran表，阶段stage、pMao对应的possibility、修改人、修改时间
        boolean flag = true;

        int count = tranDao.changeStage(tran);
        System.out.println("数据库tran表修改成功");
        Tran newTran = tranDao.searchTranById(tran.getId());
        newTran.setPossibility(tran.getPossibility());
        if(count != 0 ){
            // 增加tranHistory表的一条记录
            TranHistory tranHistory = new TranHistory();
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setTranId(newTran.getId());
            tranHistory.setStage(newTran.getStage());
            tranHistory.setMoney(newTran.getMoney());
            tranHistory.setExpectedDate(newTran.getExpectedDate());
            tranHistory.setCreateTime(newTran.getCreateTime());
            tranHistory.setCreateBy(newTran.getCreateBy());
            tranHistory.setPossibility(tran.getPossibility());

            int count2 = tranHistoryDao.saveTranHistory(tranHistory);
            if (count2 == 0 ){
                flag = false;
            }else{
                System.out.println("数据库增加交易记录成功");
            }

        }else {
            flag = false;
        }

        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("tran",newTran);

        return map;

    }

}
