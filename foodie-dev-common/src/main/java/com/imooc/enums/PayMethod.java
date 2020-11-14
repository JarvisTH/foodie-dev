package com.imooc.enums;

/**
 * @author Jarvis(Tang Hui)
 * @version 1.0
 * @date 2020/3/15 19:13
 */

/**
 * @Description: 支付相关
 */
public enum  PayMethod {

    WEIXIN(1,"微信"),
    ALIPAY(2,"支付宝");

    public final Integer type;
    public final String value;

    PayMethod(Integer type,String value){
        this.type=type;
        this.value=value;
    }
}
