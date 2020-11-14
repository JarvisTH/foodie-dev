package com.imooc.controller;

import org.springframework.stereotype.Controller;

import java.io.File;

@Controller
public class BaseController {

    public static final Integer COMMENT_PAGE_SEIZ=10;
    public static final Integer PAGE_SEIZ=20;
    public static final String FOODIE_SHOPCART="shopcart";

    // 用户上传头像的地址
    public static final String IMAGE_USER_FACE_LOCATION= "D:"+File.separator+"workspaces"+File.separator+"faces";

    //微信支付成功->支付中心->天天吃货平台
    //                       |->回调通知的url
    public static final String PAY_RETURN_URL="http://localhost:8088/foodie-dev-api/orders/notifyMerchantOrderPaid";

    //支付中心的调用地址
    String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";		// produce



}
