package com.print.printcloud.order.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import com.lly835.bestpay.utils.JsonUtil;
import com.print.printcloud.order.config.WechatAccountConfig;
import com.print.printcloud.order.dto.OrderDTO;
import com.print.printcloud.order.enums.ResultEnum;
import com.print.printcloud.order.exception.OrderException;
import com.print.printcloud.order.service.OrderService;
import com.print.printcloud.order.service.PayService;
import com.print.printcloud.order.utils.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.io.*;

/**
 * Created by 郑钦泓
 * 2019-10-26 17:13
 */

@RequestMapping("/wechat")
@Slf4j
@Controller
public class WechatController {

    @Autowired
    WechatAccountConfig wechatAccountConfig;

    @Autowired
    private PayService payService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BestPayServiceImpl bestPayService;

    @PostMapping("/getOpenid")
    @ResponseBody
    public String getOpenid(@RequestParam(value="code",required=false)String code) {
        //接收用户传过来的code，required=false表明如果这个参数没有传过来也可以。
        //接收从客户端获取的code
        //向微信后台发起请求获取openid的url
        String WX_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";

        //这三个参数就是之后要填上自己的值。
        log.info("return APPID is ：{}", wechatAccountConfig.getMiniAppId());
        log.info("return SECRET is ：{}", wechatAccountConfig.getMiniAppSecret());
        log.info("return code is ：{}", code);

        //替换appid，appsecret，和code
        String requestUrl = WX_URL.replace("APPID", wechatAccountConfig.getMiniAppId()).//填写自己的appid
                replace("SECRET", wechatAccountConfig.getMiniAppSecret()).replace("JSCODE", code).//填写自己的appsecret，
                replace("authorization_code", "authorization_code");

        //调用get方法发起get请求，并把返回值赋值给returnvalue
        String  returnvalue=GET(requestUrl);
        System.out.println(requestUrl);//打印发起请求的url
        System.out.println(returnvalue);//打印调用GET方法返回值
        //定义一个json对象。
        JSONObject convertvalue=new JSONObject();

        //将得到的字符串转换为json
        convertvalue=(JSONObject) JSON.parse(returnvalue);

        log.info("return openid is ：{}", (String)convertvalue.get("openid"));//打印得到的openid
        log.info("return sessionkey is ：{}", (String)convertvalue.get("session_key"));//打印得到的sessionkey，

        //把openid和sessionkey分别赋值给openid和sessionkey
        String openid=(String) convertvalue.get("openid");
        String sessionkey=(String) convertvalue.get("session_key");//定义两个变量存储得到的openid和session_key.

        return openid;//返回openid
    }
    //发起get请求的方法。
    public static String GET(String url) {
        String result = "";
        BufferedReader in = null;
        InputStream is = null;
        InputStreamReader isr = null;
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.connect();
            Map<String, List<String>> map = conn.getHeaderFields();
            is = conn.getInputStream();
            isr = new InputStreamReader(is);
            in = new BufferedReader(isr);
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            // 异常记录
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (is != null) {
                    is.close();
                }
                if (isr != null) {
                    isr.close();
                }
            } catch (Exception e2) {
                // 异常记录
            }
        }
        return result;
    }

    /**
     * 小程序支付
     * @param code
     * @return
     */
    @GetMapping(value = "/mini_pay")
    @ResponseBody
    public PayResponse minipay(@RequestParam(value ="orderId") String orderId,
                               @RequestParam(value = "code") String code){

        //1. 查询订单
        OrderDTO orderDTO = orderService.findOne(orderId);
        if (orderDTO == null) {
            throw new OrderException(ResultEnum.ORDER_NOT_EXIST);
        }

        //2. 发起支付
        PayResponse payResponse = payService.pay(orderDTO,code);

        return payResponse;
    }

    /**
     * 异步回调
     */
    @PostMapping(value = "/notify")
    public ModelAndView notify(@RequestBody String notifyData) {

        payService.notify(notifyData);

        //返回给微信处理结果
        return new ModelAndView("pay/success");
    }
}
