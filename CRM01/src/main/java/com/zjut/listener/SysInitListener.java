package com.zjut.listener;

import com.zjut.domain.DicValue;
import com.zjut.service.ClueService;
import com.zjut.service.DicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;
import java.util.Map;
import java.util.Set;



// 实现的是ServletContextListener接口，也就是说监听的是servlet的上下文对象
public class SysInitListener implements ServletContextListener {

    /**
     * 该方法用来监听上下文对象的创建，一旦服务器启动就会创建上下文对象
     *      此时就会触发该方法
     * @param servletContextEvent
     */

    // 初始化时不能通过@Autowired来注入对象，非controller包路径下的普通类也不能用依赖注入
//    @Autowired
//    private DicService dicService;



    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        /**
         *      servletContextEvent: 该参数能够取得监听的对象
         *          由于该类实现的是ServletContextListener接口，所以监听的是上下文对象
         *
         */


        // 获取上下文对象
        ServletContext application = servletContextEvent.getServletContext();

        System.out.println("服务器缓存处理数据字典开始");
        /**
         * 要调用service向数据库访问数据字典，按照数据字典的typeCode返回多个list对象，
         *     将这些list对象存到application中
         *      map.put("appellationList", dvList1)
         *      map.put("clueStateList", dvList2)
         *      ...
         *
         */
        // 初始化类无法通过@Autowired依赖注入来调用对象，对于不能依赖注入对象时，可以调用一下工具包得到对象
        DicService dicService = (DicService) WebApplicationContextUtils.getWebApplicationContext(application).getBean(DicService.class);
      //  BookServiceImpl bookServiceImpl = WebApplicationContextUtils.getWebApplicationContext(arg0.getServletContext()).getBean(BookServiceImpl.class);

        System.out.println("得到dicService对象");
        // service读取数据库中的数据字典，返回map按照typeCode存储的list对象
        Map<String, List<DicValue>> map = dicService.getAll();
        // 解析map成为上下文域中要保存的键值对，也就是用map中的key作为application中的key，map中的value作为application中的value
        // 遍历hashmap的方法
        Set<String> set = map.keySet();
        for(String key:set){
            // 一一保存map中的key和value到application中
            application.setAttribute(key,map.get(key));
        }

        System.out.println("服务器缓存处理数据字典结束");




    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
