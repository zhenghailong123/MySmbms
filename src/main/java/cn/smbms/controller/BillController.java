package cn.smbms.controller;

import cn.smbms.pojo.Bill;
import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.bill.BillService;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.service.provider.ProviderServiceImpl;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/bill")
public class BillController {
    private ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    private BillService billService = (BillService)context.getBean("billService");

    @RequestMapping("/query")
    public String query(@RequestParam(value = "queryProductName",required = false)String queryProductName,
                        @RequestParam(value = "queryProviderId",required = false)String queryProviderId,
                        @RequestParam(value = "queryIsPayment",required = false)String queryIsPayment,
                        Model model){
        List<Provider> providerList = new ArrayList<Provider>();
        ProviderService providerService = (ProviderService) context.getBean("providerService");
        providerList = providerService.getProviderList("","");
        model.addAttribute("providerList", providerList);
        if(StringUtils.isNullOrEmpty(queryProductName)){
            queryProductName = "";
        }
        List<Bill> billList = new ArrayList<Bill>();
        Bill bill = new Bill();
        if(StringUtils.isNullOrEmpty(queryIsPayment)){
            bill.setIsPayment(0);
        }else{
            bill.setIsPayment(Integer.parseInt(queryIsPayment));
        }

        if(StringUtils.isNullOrEmpty(queryProviderId)){
            bill.setProviderId(0);
        }else{
            bill.setProviderId(Integer.parseInt(queryProviderId));
        }
        bill.setProductName(queryProductName);
        billList = billService.getBillList(bill);
        model.addAttribute("billList", billList);
        model.addAttribute("queryProductName", queryProductName);
        model.addAttribute("queryProviderId", queryProviderId);
        model.addAttribute("queryIsPayment", queryIsPayment);
        return "billlist";
    }

    @RequestMapping("/add")
    public String add(@RequestParam(value = "billCode")String billCode,
                      @RequestParam(value = "productName")String productName,
                      @RequestParam(value = "productDesc",required = false)String productDesc,
                      @RequestParam("productUnit")String productUnit,
                      @RequestParam("productCount")String productCount,
                      @RequestParam("totalPrice")String totalPrice,
                      @RequestParam("providerId")String providerId,
                      @RequestParam("isPayment")String isPayment,
                      HttpSession session){
        Bill bill = new Bill();
        bill.setBillCode(billCode);
        bill.setProductName(productName);
        bill.setProductDesc(productDesc);
        bill.setProductUnit(productUnit);
        bill.setProductCount(new BigDecimal(productCount).setScale(2,BigDecimal.ROUND_DOWN));
        bill.setIsPayment(Integer.parseInt(isPayment));
        bill.setTotalPrice(new BigDecimal(totalPrice).setScale(2,BigDecimal.ROUND_DOWN));
        bill.setProviderId(Integer.parseInt(providerId));
        bill.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        bill.setCreationDate(new Date());
        boolean flag = false;
        flag = billService.add(bill);
        System.out.println("add flag -- > " + flag);
        if(flag){
            return "redirect:/bill/query";
        }else{
            return "billadd";
        }
    }


    @RequestMapping("/view")
    public String getBillViewById(@RequestParam("billid")String billid,
                              Model model){
        if(!StringUtils.isNullOrEmpty(billid)){
            Bill bill = null;
            bill = billService.getBillById(billid);
            model.addAttribute("bill", bill);
        }
        return "billview";
    }

    @RequestMapping("/modify")
    public String getBillModifyById(@RequestParam("billid")String billid,
                              Model model){
        if(!StringUtils.isNullOrEmpty(billid)){
            Bill bill = null;
            bill = billService.getBillById(billid);
            model.addAttribute("bill", bill);
        }
        return "billmodify";
    }

    @RequestMapping("/modifysave")
    public String modify(@RequestParam("id")String id,
                         @RequestParam("productName")String productName,
                         @RequestParam(value = "productDesc",required = false)String productDesc,
                         @RequestParam("productUnit")String productUnit,
                         @RequestParam("productCount")String productCount,
                         @RequestParam("totalPrice")String totalPrice,
                         @RequestParam("providerId")String providerId,
                         @RequestParam("isPayment")String isPayment,
                         HttpSession session){
        Bill bill = new Bill();
        bill.setId(Integer.valueOf(id));
        bill.setProductName(productName);
        bill.setProductDesc(productDesc);
        bill.setProductUnit(productUnit);
        bill.setProductCount(new BigDecimal(productCount).setScale(2,BigDecimal.ROUND_DOWN));
        bill.setIsPayment(Integer.parseInt(isPayment));
        bill.setTotalPrice(new BigDecimal(totalPrice).setScale(2,BigDecimal.ROUND_DOWN));
        bill.setProviderId(Integer.parseInt(providerId));

        bill.setModifyBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        bill.setModifyDate(new Date());
        boolean flag = false;
        flag = billService.modify(bill);
        if(flag){
            return "redirect:/bill/query";
        }else{
            return "billmodify";
        }
    }


    @RequestMapping("/delbill")
    @ResponseBody
    public String delBill(@RequestParam("billid")String id){
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(!StringUtils.isNullOrEmpty(id)){
            //BillService billService = new BillServiceImpl();
            boolean flag = billService.deleteBillById(id);
            if(flag){//删除成功
                resultMap.put("delResult", "true");
            }else{//删除失败
                resultMap.put("delResult", "false");
            }
        }else{
            resultMap.put("delResult", "notexit");
        }
        //把resultMap转换成json对象输出
        return JSONArray.toJSONString(resultMap);
    }

    @RequestMapping("/getproviderlist")
    @ResponseBody
    public String getProviderlist(){
        System.out.println("getproviderlist ========================= ");
        List<Provider> providerList = new ArrayList<Provider>();
        ProviderService providerService = (ProviderService)context.getBean("providerService");
        providerList = providerService.getProviderList("","");
        //把providerList转换成json对象输出
        return JSONArray.toJSONString(providerList);
    }

    @RequestMapping("/toadd")
    public String toadd(){
        return "billadd";
    }
}
