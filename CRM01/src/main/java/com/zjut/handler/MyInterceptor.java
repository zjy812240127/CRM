package com.zjut.handler;

import com.zjut.domain.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MyInterceptor implements HandlerInterceptor {

    /**
     * 预处理方法
     * Object handler是指被拦截的Controller对象
     * 在dosome方法执行前运行
     *
     * 可以获取请求信息，验证请求是否合理
     * 可以验证用户是否登录，是否有权访问某个连接
     *
     * 返回：true则请求可以继续执行
     * 返回：false则请求不继续执行，强制被拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("拦截器的prehandler方法");
        HttpSession session = request.getSession();
        // 获得session域中的数据，如果浏览器之前登陆过，就会有用户信息
        User user = (User) session.getAttribute("user");

        /**
         * 如果访问的是登录页面，则拦截器直接放行，不拦截登录操作
         */
        String path = request.getServletPath();
        System.out.println("浏览器地址栏地址"+path);
        if ("/login.jsp".equals(path) || "/queryUser.do".equals(path)){
            System.out.println("访问登录页，直接通过拦截器");
            return  true;
        }else{
            // 如果浏览器之前没有登陆，则拒绝访问，跳转到登陆页面
            if (user == null){
                /**
                 * 转发：
                 *      使用的是一种特殊的绝对路径的方式，前面不加/项目名，也称为内部路径/login.jsp
                 * 重定向：
                 *      使用传统的绝对路径的写法，必须以/项目名开头，后面跟具体资源路径/crmTest/login.jsp
                 * 为什么拦截器要使用重定向：
                 *      使用请求转发，路径会停留在老路径上，页面跳转到登录页后，地址栏也应该是登录页的地址
                 * 获取项目名：
                 *      request.getContextPath()
                 *      jsp页面: ${pageContext.request.getContextPath},pageContext只是为了拿到request
                 *
                 */
                // 重定向回登录页面
                System.out.println("请进行用户登录");
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return false;
            }
            else {
                return true;
            }
            // 已经登陆过了就通过拦截器
//            System.out.println("通过了拦截器");
//            return true;
        }

    }

    /**
     *
     * dosome执行后的处理，
     * 主要作用是得到处理结果MoedlAndView，对结果进行修改
     */

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("拦截器的posthandler方法");

    }

    /**
     *请求处理完后，forward之后，
     * 一般做资源的回收工作，程序请求过程中创建的一些对象，可以在这里删除，释放内存

     */

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("拦截器的afterCompletion方法");
    }
}
