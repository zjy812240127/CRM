package com.zjut.controller;

import com.zjut.dao.CustomerDao;
import com.zjut.dao.UserDao;
import com.zjut.domain.Tran;
import com.zjut.domain.TranHistory;
import com.zjut.domain.User;
import com.zjut.service.CustomerService;
import com.zjut.service.TranService;
import com.zjut.util.DateTimeUtil;
import com.zjut.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TranController {


    @Autowired
    private TranService tranService;

    @Autowired
    private CustomerService customerService;

    @RequestMapping(value = "workbench/transaction/save.do")
    public String save(Model model){



        List<User> userList = tranService.getUserList();

        model.addAttribute("userList",userList);

        return "workbench/transaction/save";
    }

    @ResponseBody
    @RequestMapping(value = "/getCustomerName.do")
    public List<String> getCustomerName(String name){
        System.out.println("后台获取自动补全信息");

        List<String> list = customerService.getFullName(name);

        return list;

    }


    @RequestMapping(value = "workbench/transaction/saveTran.do", method = RequestMethod.POST)
    public String saveTran(Tran tran, String customerName,HttpServletRequest request){

        // 由于前台能拿到的只有customerName而tbl_tran表中需要customerId，所以要先根据name找id
        // 为了方便，将customerName的值保存到tran的customerId字段，就不用map传参了
        tran.setCustomerId(customerName);

        tran.setCreateBy(((User)request.getSession().getAttribute("user")).getName());
        tran.setCreateTime(DateTimeUtil.getSysTime());
        tran.setId(UUIDUtil.getUUID());
        boolean flag = tranService.addTran(tran);
        if (flag == false){
            System.out.println("添加交易失败");
        }else {
            System.out.println("添加交易成功");
        }

        return "workbench/transaction/index";
    }

    @RequestMapping(value = "workbench/transaction/detail.do")
    public String transactionDetail(String id, Model model,HttpServletRequest request){
        System.out.println("跳转到交易详细信息页");

        Tran tran = tranService.findTranById(id);
        String stage = tran.getStage();
        System.out.println("stage:"+stage);
        Map<String,String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");
        System.out.println("get pMap");
        String possibility = pMap.get(stage);
        System.out.println("possibility:" + possibility);
        tran.setPossibility(pMap.get(stage));


        model.addAttribute("tran",tran);
        System.out.println("检索到的tran的name:" + tran.getName());

        return "workbench/transaction/detail";
    }

    @ResponseBody
    @RequestMapping(value = "/getHistoryList.do")
    public List<TranHistory> getHistoryList(String tranId, HttpServletRequest request){
        System.out.println("获取交易历史列表前端的tranId:" + tranId);
        Map<String,String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");

        List<TranHistory> tranHistoryList = tranService.getHistoryList(tranId,pMap);

        for(TranHistory tranHistory: tranHistoryList){
            System.out.println("数据库检索到的交易历史阶段：" + tranHistory.getStage());
        }

        return tranHistoryList;
    }


    @RequestMapping(value = "/changeStage.do", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> changeStage(HttpServletRequest request, String stage, String tranId, String money, String expectedDate){
        System.out.println("进入修改状态的controller");
        Tran tran = new Tran();
        tran.setId(tranId);
        tran.setMoney(money);
        tran.setExpectedDate(expectedDate);
        tran.setStage(stage);
        String user = ((User) request.getSession().getAttribute("user")).getName();
        tran.setEditBy(user);
        tran.setEditTime(DateTimeUtil.getSysTime());
        Map <String, String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);
        tran.setPossibility(possibility);
        System.out.println("对应的可能性为：" + possibility);
        Map<String, Object> map = tranService.changeStage(tran);

        return map;
    }


    @ResponseBody
    @RequestMapping(value = "/getEcharts.do")
    public Map<String,Object> getEcharts(){
        System.out.println("获取ECharts图表数据");

        Map<String,Object> map = tranService.getEcharts();
//        System.out.println("map的total属性值"+map.get("total"));



        return map;

    }



}
