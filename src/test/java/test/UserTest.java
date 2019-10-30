package test;

import cn.smbms.dao.user.UserDao;
import cn.smbms.pojo.Role;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.user.UserService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class UserTest {

    @Test
    public void getUser(){
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        RoleService roleService = (RoleService) context.getBean("roleService");
        /*List<Role> roles = roleService.getRoleList();
        for (Role r:roles
             ) {
            System.out.println(r.getRoleName());
        }*/



    }
}
