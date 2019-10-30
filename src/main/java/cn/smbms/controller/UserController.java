package cn.smbms.controller;

import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.user.UserService;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    private UserService userService = (UserService)context.getBean("userService");

    @RequestMapping("/toPwdmodify")
    public String toPwdmodify(){
        return "pwdmodify";
    }

    @RequestMapping("/toAdd")
    public String toAdd(){
        return "useradd";
    }

    @RequestMapping("/pwdmodify")
    @ResponseBody    //作用是将方法的返回值以特定的格式写入到response的body区域，进而将数据返回给客户端
    public String getPwdByUserId(@RequestParam("oldpassword")String oldpassword
                            , HttpSession session){
        Object o = session.getAttribute(Constants.USER_SESSION);
        Map<String, String> resultMap = new HashMap<String, String>();
        if(null == o ){//session过期
            resultMap.put("result", "sessionerror");
        }else if(StringUtils.isNullOrEmpty(oldpassword)){//旧密码输入为空
            resultMap.put("result", "error");
        }else{
            String sessionPwd = ((User)o).getUserPassword();
            if(oldpassword.equals(sessionPwd)){
                resultMap.put("result", "true");
            }else{//旧密码输入不正确
                resultMap.put("result", "false");
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

    @RequestMapping("/savepwd")
    public String updatePwd(@RequestParam("newpassword")String newpassword
                            , Model model
                            , HttpSession session){
        Object o = session.getAttribute(Constants.USER_SESSION);
        boolean flag = false;
        if(o != null && !StringUtils.isNullOrEmpty(newpassword)){
            flag = userService.updatePwd(((User)o).getId(),newpassword);
            if(flag){
                model.addAttribute(Constants.SYS_MESSAGE, "修改密码成功,请退出并使用新密码重新登录！");
                session.removeAttribute(Constants.USER_SESSION);//session注销
            }else{
                model.addAttribute(Constants.SYS_MESSAGE, "修改密码失败！");
            }
        }else{
            model.addAttribute(Constants.SYS_MESSAGE, "修改密码失败！");
        }
        return "pwdmodify";
    }

    @RequestMapping("/add")
    public String useradd(@RequestParam("userCode")String userCode
            , @RequestParam("userName")String userName
            , @RequestParam("userPassword")String userPassword
            , @RequestParam("gender")String gender
            , @RequestParam("birthday")String birthday
            , @RequestParam("phone")String phone
            , @RequestParam("address")String address
            , @RequestParam("userRole")String userRole
            , @RequestParam(value = "idPicPath",required = false) MultipartFile attach
            , HttpServletRequest request
            , HttpSession session){
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setAddress(address);
        String idPicPath = null;
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        user.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        if (!attach.isEmpty()){
            String path = session.getServletContext().getRealPath("statics"+ File.separator+"uploadfiles");
            String oldFileName = attach.getOriginalFilename();//原文件名
            System.out.println("oldFileName====>"+oldFileName);
            String prefix = FilenameUtils.getExtension(oldFileName);//源文件后缀
            System.out.println("prefix====>"+prefix);
            int filesize = 500000;
            System.out.println("filesize====>"+attach.getSize());//文件大小
            if (attach.getSize() > filesize){//上传大小不得超过500kb
                request.setAttribute("uploadFileError","上传文件不得超过500kb");
                return "useradd";
            }else if(prefix.equalsIgnoreCase("jpg")
                    ||prefix.equalsIgnoreCase("png")
                    ||prefix.equalsIgnoreCase("jpeg")
                    ||prefix.equalsIgnoreCase("pneg")){//上传图片格式不正确
                String fileName = System.currentTimeMillis() + RandomUtils.nextInt(1000000)+"_Personal.jpg";
                System.out.println("fileName====>"+attach.getName());
                File targetFile = new File(path,fileName);
                if(!targetFile.exists()){
                    targetFile.mkdirs();
                }
                //保存
                try {
                    attach.transferTo(targetFile);
                }catch (Exception e){
                    e.printStackTrace();
                    request.setAttribute("uploadFileError","*上传失败！");
                    return "useradd";
                }
                idPicPath=path+File.separator+fileName;
            }else {
                request.setAttribute("uploadFileError","*上传图片格式不正确");
                return "useradd";
            }
        }
        user.setIdPicPath(idPicPath);
        if(userService.add(user)){
            return "redirect:/user/query";
        }else{
            return "useradd";
        }
    }

    @RequestMapping("/query")
    //required=flase表示空的也行，不写会报错
    public String query(@RequestParam(value = "queryname",required = false)String queryUserName
            ,@RequestParam(value = "queryUserRole",required = false)String temp
            ,@RequestParam(value = "pageIndex",required = false)String pageIndex
            ,Model model){
        int queryUserRole = 0;
        List<User> userList = null;
        //设置页面容量
        int pageSize = Constants.pageSize;
        //当前页码
        int currentPageNo = 1;
        /**
         * http://localhost:8090/SMBMS/userlist.do
         * ----queryUserName --NULL
         * http://localhost:8090/SMBMS/userlist.do?queryname=
         * --queryUserName ---""
         */
        System.out.println("queryUserName servlet--------"+queryUserName);
        System.out.println("queryUserRole servlet--------"+queryUserRole);
        System.out.println("query pageIndex--------- > " + pageIndex);
        if(queryUserName == null){
            queryUserName = "";
        }
        if(temp != null && !temp.equals("")){
            queryUserRole = Integer.parseInt(temp);
        }

        if(pageIndex != null){
            try{
                currentPageNo = Integer.valueOf(pageIndex);
            }catch(NumberFormatException e){
                return "error";
            }
        }
        //总数量（表）
        int totalCount	= userService.getUserCount(queryUserName,queryUserRole);
        //总页数
        PageSupport pages=new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);
        int totalPageCount = pages.getTotalPageCount();
        //控制首页和尾页
        if(currentPageNo < 1){
            currentPageNo = 1;
        }else if(currentPageNo > totalPageCount){
            currentPageNo = totalPageCount;
        }
        userList = userService.getUserList(queryUserName,queryUserRole,currentPageNo, pageSize);
        model.addAttribute("userList", userList);
        List<Role> roleList = null;
        //RoleService roleService = new RoleServiceImpl();
        RoleService roleService = (RoleService)context.getBean("roleService");
        roleList = roleService.getRoleList();
        model.addAttribute("roleList", roleList);
        model.addAttribute("queryUserName", queryUserName);
        model.addAttribute("queryUserRole", queryUserRole);
        model.addAttribute("totalPageCount", totalPageCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPageNo", currentPageNo);
        return "userlist";
    }

    @RequestMapping("/getrolelist")
    @ResponseBody
    public String getRolelist(HttpSession session){
        List<Role> roleList = null;
        RoleService roleService = (RoleService) context.getBean("roleService");
        roleList = roleService.getRoleList();
        //把roleList转换成json对象输出
        return  JSONArray.toJSONString(roleList);
    }

    @RequestMapping("/ucexist")
    @ResponseBody
    public String userCodeExist(@RequestParam("userCode")String userCode){
        //判断用户账号是否可用
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(StringUtils.isNullOrEmpty(userCode)){
            resultMap.put("userCode", "exist");
        }else{
            User user = userService.selectUserCodeExist(userCode);
            if(null != user){
                resultMap.put("userCode","exist");
            }else{
                resultMap.put("userCode", "notexist");
            }
        }
        //把resultMap转为json字符串以json的形式输出
        return JSONArray.toJSONString(resultMap);
    }

    @RequestMapping("/deluser")
    @ResponseBody
    public String delUser(@RequestParam("uid")String id){
        Integer delId = 0;
        try{
            delId = Integer.parseInt(id);
        }catch (Exception e) {
            delId = 0;
        }
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(delId <= 0){
            resultMap.put("delResult", "notexist");
        }else{
            if(userService.deleteUserById(delId)){
                resultMap.put("delResult", "true");
            }else{
                resultMap.put("delResult", "false");
            }
        }
        //把resultMap转换成json对象输出
        return JSONArray.toJSONString(resultMap);
    }

    //以用户id获取用户数据
    @RequestMapping("/view")
    public String getUserViewById(@RequestParam("uid")String uid,Model model){
        if(!StringUtils.isNullOrEmpty(uid)){
            //调用后台方法得到user对象
            User user = userService.getUserById(uid);
            model.addAttribute("user",user);
        }
        return "userview";
    }

    //以用户id获取用户数据
    @RequestMapping("/modify")
    public String getUserModifyById(@RequestParam("uid")String uid,Model model){
        if(!StringUtils.isNullOrEmpty(uid)){
            //调用后台方法得到user对象
            User user = userService.getUserById(uid);
            model.addAttribute("user",user);
        }
        return "usermodify";
    }

    @RequestMapping("/modifyexe")
    public String modify(@RequestParam("uid")String id
            ,@RequestParam(value = "userName",required = false)String userName
            ,@RequestParam(value = "gender",required = false)String gender
            ,@RequestParam(value = "birthday",required = false)String birthday
            ,@RequestParam(value = "phone",required = false)String phone
            ,@RequestParam(value = "address",required = false)String address
            ,@RequestParam(value = "userRole",required = false)String userRole
            ,HttpSession session){
        User user = new User();
        user.setId(Integer.valueOf(id));
        user.setUserName(userName);
        user.setGender(Integer.valueOf(gender));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        user.setModifyBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());
        if(userService.modify(user)){
            return "redirect:/user/query";
        }else{
            return "usermodify";
        }
    }
}
