package cn.smbms.controller;

import cn.smbms.tools.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
public class LoginoutController {

    @RequestMapping("/logout")
    public String loginOut(HttpSession session){
        //清除session
        if (session!=null){
            session.removeAttribute(Constants.USER_SESSION);
        }
        return "loginInit/login";
    }
}
