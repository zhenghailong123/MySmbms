package cn.smbms.controller;

import cn.smbms.pojo.User;
import cn.smbms.service.user.UserService;
import cn.smbms.tools.Constants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    private ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    private UserService userService = (UserService) context.getBean("userService");
    private String path = null;

    @RequestMapping("/login.do")
    public String login(@RequestParam("userCode")String userCode
                        , @RequestParam("userPassword")String userPassword
                        , Model model
                        , HttpSession session){
        System.out.println("login ============ " );
        User user = userService.login(userCode,userPassword);
        if(null != user){//登录成功
            //放入session
            session.setAttribute(Constants.USER_SESSION, user);
            //页面跳转（frame.jsp）
            path = "frame";
        }else{
            //页面跳转（login.jsp）带出提示信息--转发
            model.addAttribute("error", "用户名或密码不正确");
            path = "loginInit/login";
        }
        return path;
    }

    @RequestMapping("/login")
    public String loginInit(){
        return "loginInit/login";
    }
}
