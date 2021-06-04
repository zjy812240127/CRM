package com.zjut.service.impl;

import com.zjut.dao.*;
import com.zjut.domain.*;
import com.zjut.service.ClueService;
import com.zjut.service.UserService;
import com.zjut.util.DateTimeUtil;
import com.zjut.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClueServiceImpl implements ClueService {

    // 线索相关
    @Autowired
    private ClueDao clueDao;
    @Autowired
    private ClueActivityRelationDao clueActivityRelationDao;
    @Autowired
    private ClueRemarkDao clueRemarkDao;

    // 客户相关
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private CustomerRemarkDao customerRemarkDao;

    // 联系人相关
    @Autowired
    private ContactsDao contactsDao;
    @Autowired
    private ContactsRemarkDao contactsRemarkDao;
    @Autowired
    private ContactsActivityRelationDao contactsActivityRelationDao;

    // 交易相关
    @Autowired
    private TranDao tranDao;
    @Autowired
    private TranHistoryDao tranHistoryDao;

    @Autowired
    private ActivityDao activityDao;



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

    @Override
    public Clue detail(String id) {
        Clue clue = clueDao.detail(id);
        return clue;
    }

    @Override
    public List<Activity> findActivity(String clueId) {
        System.out.println("执行查询线索关联的市场活动serviceImpl");

        List<Activity> activityList = new ArrayList<>();
        List<Activity> aIdList = clueActivityRelationDao.findActivityId(clueId);

        return aIdList;
    }

    @Override
    public boolean unbund(String id) {

        boolean flag = false;
        int count = clueActivityRelationDao.unbund(id);
        if(count >0){
            flag = true;
        }
        return flag;
    }

    @Override
    public Map<String, Object> searchActivity(String aName, String clueId) {
        Map<String,Object> map = new HashMap<>();
        boolean flag = false;
        // 模糊查询市场活动
        List<Activity> alist = activityDao.findActivity(aName, clueId);
        System.out.println("返回数据库模糊查询结果");
        if (alist.size()>0){
            flag = true;
        }
        map.put("success",flag);
        map.put("activity",alist);

        return map;
    }

    @Override
    public List<Activity> findActivityByNameAndNotClueId(Map<String,String> map) {

        List<Activity> saList = activityDao.findActivityByName(map);
        System.out.println("从数据库返回查询结果");
        return saList;
    }

    @Override
    public boolean associate(Map<String, Object> map) {
        boolean flag = false;
        int count = clueActivityRelationDao.associateActivity(map);
        if(count > 0){
            System.out.println("数据库中插入关联市场活动成功");
            flag = true;
        }
        return flag;
    }

    @Override
    public List<Activity> changeSA(String aName) {
        List<Activity> activityList = activityDao.changeSearchActivity(aName);
        return activityList;
    }

    @Override
    public boolean convert(Map<String, Object> map) {
        // 每张表的每条记录都需要创建时间和创建人以及id
        System.out.println("进入转换操作的impl服务层");
        String createTime = DateTimeUtil.getSysTime();
        Tran t = (Tran) map.get("t");
        String clueId = (String) map.get("clueId");
        String createBy = (String) map.get("createBy");
        boolean flag = true;
        System.out.println("前端传入的clueId：" + clueId);

        // System.out.println("测试controller传过来的tran的money:" + t.getMoney());

        // (1) 获取到线索id，通过线索id获取线索对象（线索对象当中封装了线索的信息）
        Clue clue = clueDao.findClueByName(clueId);
        System.out.println("1找到对应的线索记录");
        System.out.println("线索的fullname：" + clue.getFullname());

        // (2) 通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在！）
        String company = clue.getCompany();
        Customer customer = customerDao.findCustomerByName(company);
            // 如果customer为null，则要新建一条客户信息
        if(customer == null){
            customer = new Customer();
            customer.setCreateBy(createBy);
            customer.setCreateTime(createTime);
            customer.setAddress(clue.getAddress());
            customer.setContactSummary(clue.getContactSummary());
            customer.setDescription(clue.getDescription());
            customer.setId(UUIDUtil.getUUID());
            customer.setOwner(clue.getOwner());
            customer.setName(company);
            customer.setPhone(clue.getPhone());
            customer.setWebsite(clue.getWebsite());
            customer.setNextContactTime(clue.getNextContactTime());
            System.out.println(customer.getCreateBy() + customer.getCreateTime() + customer.getAddress() + customer.getContactSummary()
            + customer.getDescription() + customer.getId() + customer.getOwner() + customer.getOwner()+
                    customer.getName() + customer.getPhone() + customer.getWebsite() + customer.getNextContactTime());
            int count = customerDao.saveCustomer(customer);
            if (count != 1){
                flag = false;
            }
            System.out.println("2新建customer客户成功");
        }

        // (3) 通过线索对象提取联系人信息，保存联系人
        Contacts contacts = new Contacts();
        contacts.setAddress(customer.getAddress());
        contacts.setAppellation(clue.getAppellation());
        contacts.setContactSummary(customer.getContactSummary());
        contacts.setCreateBy(createBy);
        contacts.setCreateTime(createTime);
        contacts.setCustomerId(customer.getId());
        contacts.setDescription(customer.getDescription());
        contacts.setEmail(clue.getEmail());
        contacts.setFullname(clue.getFullname());
        contacts.setId(UUIDUtil.getUUID());
        contacts.setJob(clue.getJob());
        contacts.setMphone(clue.getMphone());
        contacts.setNextContactTime(customer.getNextContactTime());
        contacts.setOwner(clue.getOwner());
        contacts.setSource(clue.getSource());
        int count2 = contactsDao.saveContacts(contacts);
        if (count2 != 1){
            flag = false;
        }
        System.out.println("3保存联系人信息成功");

        // (4) 线索备注转换到客户备注以及联系人备注
        // 根据clueId查询线索对应的备注
        List<ClueRemark> clueRemarkList = clueRemarkDao.findClueRemarkByClueId(clueId);
        // 取出每条clueRemark的noteContent写入到客户备注与联系人备注中
        for(ClueRemark clueRemark: clueRemarkList){
           String noteContent = clueRemark.getNoteContent();
           // 写入客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setCustomerId(customer.getId());
            customerRemark.setCreateTime(createTime);
            customerRemark.setCreateBy(createBy);
            customerRemark.setNoteContent(noteContent);
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setEditFlag("0");
            int count3 = customerRemarkDao.saveCustomerRemark(customerRemark);
            if(count3 != 1 ){
                flag = false;
            }
            // 写入联系人备注
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setNoteContent(noteContent);
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setEditFlag("0");
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setContactsId(contacts.getId());
            int count4 = contactsRemarkDao.saveContactsRemark(contactsRemark);
            if(count4 != 1){
                flag = false;
            }
            System.out.println("4线索备注转换成功");
        }

        // (5) “线索和市场活动”的关系转换到“联系人和市场活动”的关系
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.findClueActivityRelationByClueId(clueId);
        for(ClueActivityRelation clueActivityRelation: clueActivityRelationList){
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setContactsId(contacts.getId());
            contactsActivityRelation.setActivityId(clueActivityRelation.getActivityId());
            int count5 = contactsActivityRelationDao.saveContactsActivityRelation(contactsActivityRelation);
            if(count5 != 1){
                flag = false;
            }
            System.out.println("“线索和市场活动”的关系转换");
        }
        // (6) 如果有创建交易需求，创建一条交易
        if ( t!= null){
            Tran tran = new Tran();
            t.setSource(clue.getSource());
            t.setOwner(clue.getOwner());
            t.setNextContactTime(clue.getNextContactTime());
            t.setDescription(clue.getDescription());
            t.setCustomerId(customer.getId());
            t.setContactSummary(clue.getContactSummary());
            t.setContactsId(contacts.getId());
            int count6 = tranDao.saveTran(t);
            if(count6 != 1){
                flag = false;
            }
            System.out.println("6创建一条交易");

            // (7) 如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setTranId(tran.getId());
            tranHistory.setStage(tran.getStage());
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setCreateTime(tran.getCreateTime());
            tranHistory.setCreateBy(tran.getCreateBy());
            int count7 = tranHistoryDao.saveTranHistory(tranHistory);
            if(count7 != 1){
                flag = false;
            }
            System.out.println("7创建一条该交易下的交易历史");
        }

        // (8) 删除线索备注
        int count8 = clueRemarkDao.deleteClueRemarkByClueId(clueId);
        if(count8 == 0){
            flag = false;
        }

        // (9) 删除线索和市场活动的关系
        int count9 = clueActivityRelationDao.deleteClueActivityRelationByClueId(clueId);
        if(count9 == 0){
            flag = false;
        }

        // (10) 删除线索
        int count10 = clueDao.deleteById(clueId);
        if(count10 == 0){
            flag = false;
        }

        return flag;
    }


}
