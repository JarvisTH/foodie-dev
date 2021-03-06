package com.imooc.service.impl;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.OrderItemsMapper;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.pojo.*;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.service.AddressService;
import com.imooc.service.ItemService;
import com.imooc.service.OrdersService;
import com.imooc.utils.DateUtil;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderVO createOrder(SubmitOrderBO submitOrderBO) {

        String orderId=sid.nextShort();

        String userId=submitOrderBO.getUserId();
        String addressId=submitOrderBO.getAddressId();
        String itemSpecIds=submitOrderBO.getItemSpecIds();
        int payMethod=submitOrderBO.getPayMethod();
        String leftMsg=submitOrderBO.getLeftMsg();
        //包邮费
        Integer postAmount = 0;

        UserAddress address=addressService.queryUserAddress(userId,addressId);

        //1.新订单数据保存
        Orders newOrder=new Orders();
        newOrder.setId(orderId);
        newOrder.setUserId(userId);


        newOrder.setReceiverName(address.getReceiver());
        newOrder.setReceiverMobile(address.getMobile());
        newOrder.setReceiverAddress(address.getProvince()+" "+address.getCity()+" "
                +address.getDistrict()+" "+address.getDetail());
        newOrder.setPostAmount(postAmount);
        newOrder.setPayMethod(payMethod);
        newOrder.setLeftMsg(leftMsg);
        newOrder.setIsComment(YesOrNo.NO.type);
        newOrder.setIsDelete(YesOrNo.NO.type);
        newOrder.setCreatedTime(new Date());
        newOrder.setUpdatedTime(new Date());

        //2.循环itemSpecIds保存订单商品信息表
        String[] itemSpecIdArr=itemSpecIds.split(",");
        int totalAmount=0;  // 商品原价累计
        int realPayAmount=0;    // 优惠后的实际支付价格累计
        for(String itemSpecId:itemSpecIdArr){

            // TODO 整合redis后，商品购买数量重新从redis购物车获取
            int buyCounts= 1;

            //2.1根据规格id 查询规格具体信息，主要获取价格
            ItemsSpec itemsSpec=itemService.queryItemSpecById(itemSpecId);
            totalAmount += itemsSpec.getPriceNormal() * buyCounts;
            realPayAmount += itemsSpec.getPriceDiscount() * buyCounts;

            //2.2 根据规格id 获得商品信息以及图片
            String itemId=itemsSpec.getItemId();
            Items item=itemService.queryItemById(itemId);
            String imgUrl=itemService.queryItemMainImgById(itemId);

            //2.3循环保存子订单数据到数据库
            OrderItems subOrderItem=new OrderItems();
            subOrderItem.setId(sid.nextShort());
            subOrderItem.setOrderId(orderId);
            subOrderItem.setItemId(itemId);
            subOrderItem.setItemName(item.getItemName());
            subOrderItem.setItemImg(imgUrl);
            subOrderItem.setBuyCounts(buyCounts);
            subOrderItem.setItemSpecId(itemSpecId);
            subOrderItem.setItemSpecName(itemsSpec.getName());
            subOrderItem.setPrice(itemsSpec.getPriceDiscount());

            orderItemsMapper.insert(subOrderItem);

            //2.4在用户提交订单后，规格表中需要扣除库存
            itemService.decreaseItemSpecStock(itemSpecId,buyCounts);
        }
        newOrder.setTotalAmount(totalAmount);
        newOrder.setRealPayAmount(realPayAmount);
        ordersMapper.insert(newOrder);

        //3.保存订单状态表
        OrderStatus waitPayOrderStatus=new OrderStatus();
        waitPayOrderStatus.setOrderId(orderId);
        waitPayOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        waitPayOrderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(waitPayOrderStatus);

        //4.构建商户订单，用于传给支付中心
        MerchantOrdersVO merchantOrdersVO=new MerchantOrdersVO();
        merchantOrdersVO.setMerchantOrderId(orderId);
        merchantOrdersVO.setMerchantUserId(userId);
        merchantOrdersVO.setAmount(realPayAmount+postAmount);
        merchantOrdersVO.setPayMethod(payMethod);

        //5.构建自定义订单vo
        OrderVO orderVO=new OrderVO();

        orderVO.setOrderId(orderId);
        orderVO.setMerchantOrdersVO(merchantOrdersVO);

        return orderVO;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {

        OrderStatus paidStatus=new OrderStatus();
        paidStatus.setOrderId(orderId);
        paidStatus.setOrderStatus(orderStatus);
        paidStatus.setPayTime(new Date());

        orderStatusMapper.updateByPrimaryKeySelective(paidStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatus queryOrderStatusInfo(String orderId) {

        return orderStatusMapper.selectByPrimaryKey(orderId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void closeOrder() {

        //查询所有未付款订单，判断时间是否超时（1天），超时则关闭交易
        OrderStatus queryOrder=new OrderStatus();
        queryOrder.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);

        List<OrderStatus> list=orderStatusMapper.select(queryOrder);
        for(OrderStatus os:list){
            //获得订单创建时间
            Date createTime=os.getCreatedTime();

            //和当前时间进行对比
            int days = DateUtil.daysBetween(createTime,new Date());
            if(days >= 1){
                //超过1天关闭订单
                doCloseOrder(os.getOrderId());
            }

        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    void doCloseOrder(String orderId){
        OrderStatus close=new OrderStatus();
        close.setOrderId(orderId);
        close.setOrderStatus(OrderStatusEnum.CLOSE.type);
        close.setCreatedTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(close);
    }
}
