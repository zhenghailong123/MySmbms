package cn.smbms.controller;

import cn.smbms.pojo.Bill;
import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.tools.Constants;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/provider")
public class ProviderController {
    private ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    private ProviderService providerService = (ProviderService)context.getBean("providerService");

    @RequestMapping("/query")
    public String query(@RequestParam(value = "queryProName",required = false)String queryProName,
                        @RequestParam(value = "queryProCode",required = false)String queryProCode,
                        Model model){
        if(StringUtils.isNullOrEmpty(queryProName)){
            queryProName = "";
        }
        if(StringUtils.isNullOrEmpty(queryProCode)){
            queryProCode = "";
        }
        List<Provider> providerList = new ArrayList<Provider>();
        providerList = providerService.getProviderList(queryProName,queryProCode);
        model.addAttribute("providerList", providerList);
        model.addAttribute("queryProName", queryProName);
        model.addAttribute("queryProCode", queryProCode);
        return "providerlist";
    }

    @RequestMapping("/add")
    public String add(@RequestParam("proCode")String proCode,
                      @RequestParam("proName")String proName,
                      @RequestParam("proContact")String proContact,
                      @RequestParam("proPhone")String proPhone,
                      @RequestParam(value = "proAddress",required = false)String proAddress,
                      @RequestParam(value = "proFax",required = false)String proFax,
                      @RequestParam(value = "proDesc",required = false)String proDesc,
                      HttpSession session
                      ){
        Provider provider = new Provider();
        provider.setProCode(proCode);
        provider.setProName(proName);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProFax(proFax);
        provider.setProAddress(proAddress);
        provider.setProDesc(proDesc);
        provider.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        provider.setCreationDate(new Date());
        boolean flag = false;
        flag = providerService.add(provider);
        if(flag){
            return "redirect:/provider/query";
        }else{
            return "provideradd";
        }
    }

    @RequestMapping("/view")
    public String getProviderViewById(@RequestParam("proid")String id,
                                  Model model){
        if(!StringUtils.isNullOrEmpty(id)){
            Provider provider = null;
            provider = providerService.getProviderById(id);
            model.addAttribute("provider", provider);
        }
        return "providerview";
    }

    @RequestMapping("/modify")
    public String getProviderModifyById(@RequestParam("proid")String id,
                                    Model model){
        if(!StringUtils.isNullOrEmpty(id)){
            Provider provider = null;
            provider = providerService.getProviderById(id);
            model.addAttribute("provider", provider);
        }
        return "providermodify";
    }

    @RequestMapping("/modifysave")
    public String modify(@RequestParam("proid")String id,
                         @RequestParam(value = "proName",required = false)String proName,
                         @RequestParam(value = "proContact",required = false)String proContact,
                         @RequestParam(value = "proPhone",required = false)String proPhone,
                         @RequestParam(value = "proAddress",required = false)String proAddress,
                         @RequestParam(value = "proFax",required = false)String proFax,
                         @RequestParam(value = "proDesc",required = false)String proDesc,
                         HttpSession session){
        Provider provider = new Provider();
        provider.setId(Integer.valueOf(id));
        provider.setProName(proName);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProFax(proFax);
        provider.setProAddress(proAddress);
        provider.setProDesc(proDesc);
        provider.setModifyBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        provider.setModifyDate(new Date());
        boolean flag = false;
        flag = providerService.modify(provider);
        if(flag){
            return "redirect:/provider/query";
        }else{
            return "providermodify";
        }
    }

    @RequestMapping("/delprovider")
    @ResponseBody
    public String delProvider(@RequestParam("proid")String id){
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(!StringUtils.isNullOrEmpty(id)){
            int flag = providerService.deleteProviderById(id);
            if(flag == 0){//删除成功
                resultMap.put("delResult", "true");
            }else if(flag == -1){//删除失败
                resultMap.put("delResult", "false");
            }else if(flag > 0){//该供应商下有订单，不能删除，返回订单数
                resultMap.put("delResult", String.valueOf(flag));
            }
        }else{
            resultMap.put("delResult", "notexit");
        }
        //把resultMap转换成json对象输出
        return JSONArray.toJSONString(resultMap);
    }

    @RequestMapping("/toadd")
    public String toAdd(){
        return "provideradd";
    }
}
