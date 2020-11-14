package com.imooc.config;

import com.imooc.service.OrdersService;
import com.imooc.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Jarvis(Tang Hui)
 * @version 1.0
 * @date 2020/4/8 11:50
 */

@Component
public class OrderJob {

    @Autowired
    private OrdersService ordersService;

    /**
     * 使用定时任务关闭超时未支付订单，弊端：
     * 1.有时间差：
     *          10：39下单，11点检查不足一小时，12：00检查，超过1小时多39分钟
     * 2.不支持集群：
     *          单机没问题，使用集群后会有多个定时任务
     *          解决方案：只使用一台计算机节点，单独运行所有定时任务
     * 3.会对数据库全表搜索，及其影响数据库性能：select * from order where order_status = 10;
     *
     * 定时任务仅仅适用于小型轻量级项目，传统项目
     *
     * 消息队列：MQ
     *          延时任务（队列）：
     *                 10：12分下单，未付款（10）状态，11：12分检查，如果当前状态还是10，则直接关闭订单即可
     *
     */

    //@Scheduled(cron = "0 0 0/1 * * ? *")
    public void autoCloseOrder(){
        ordersService.closeOrder();
        System.out.println("执行定时任务，当前时间为：0" +
                DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN));
    }

}
