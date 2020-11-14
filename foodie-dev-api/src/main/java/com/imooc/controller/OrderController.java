package com.imooc.controller;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayMethod;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.service.AddressService;
import com.imooc.service.OrdersService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = "订单相关" ,tags = {"订单相关的api接口"})
@RestController
@RequestMapping("orders")
public class OrderController extends BaseController{

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private RestTemplate restTemplate;


    @ApiOperation(value = "用户下单",notes = "用户下单",httpMethod = "POST")
    @PostMapping("/create")
    public IMOOCJSONResult create(@RequestBody SubmitOrderBO submitOrderBO,
                                   HttpServletRequest request,
                                   HttpServletResponse response){

        if(submitOrderBO.getPayMethod() != PayMethod.WEIXIN.type &&
            submitOrderBO.getPayMethod() != PayMethod.ALIPAY.type){
            return IMOOCJSONResult.errorMsg("支付方式不支持");
        }

        //1.创建订单
        OrderVO orderVO=ordersService.createOrder(submitOrderBO);
        String orderId=orderVO.getOrderId();

        //2.创建订单后，移除购物车中已结算（已提交）的商品
        //TODO 整合redis 之后，完善购物车中的已结算商品清除，并且需要同步到前端cookie
       // CookieUtils.setCookie(request,response,FOODIE_SHOPCART,"",true);


        //3.向支付中心发送当前订单，用于保存支付中心的订单数据
        MerchantOrdersVO merchantOrdersVO=orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(PAY_RETURN_URL);

        //方便测试购买，支付金额改为1分钱
        merchantOrdersVO.setAmount(1);

        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("imoocUserId","imooc");
        httpHeaders.add("password","imooc");

        HttpEntity<MerchantOrdersVO> entity=new HttpEntity<>(merchantOrdersVO,httpHeaders);

        ResponseEntity<IMOOCJSONResult> responseEntity=restTemplate.postForEntity(paymentUrl,entity,IMOOCJSONResult.class);
        IMOOCJSONResult paymentResult=responseEntity.getBody();
        if(paymentResult.getStatus()!=200){
            return IMOOCJSONResult.errorMsg("支付中心订单创建失败，请联系管理员!");
        }

        return IMOOCJSONResult.ok(orderId);
    }

    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId){

        ordersService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    @PostMapping("getPaidOrderInfo")
    public IMOOCJSONResult getPaidOrderInfo(String orderId){

        OrderStatus orderStatus=ordersService.queryOrderStatusInfo(orderId);
        return IMOOCJSONResult.ok(orderStatus);
    }

}
