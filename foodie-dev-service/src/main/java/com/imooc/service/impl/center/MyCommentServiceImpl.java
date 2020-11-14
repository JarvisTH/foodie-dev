package com.imooc.service.impl.center;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.*;
import com.imooc.pojo.OrderItems;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.center.OrderItemsCommentBO;
import com.imooc.pojo.vo.MyCommentVO;
import com.imooc.pojo.vo.MyOrdersVO;
import com.imooc.service.center.MyCommentService;
import com.imooc.service.center.MyOrdersService;
import com.imooc.utils.PagedGridResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyCommentServiceImpl implements MyCommentService {

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrderItems> queryPendingComment(String orderId) {

        OrderItems query=new OrderItems();
        query.setOrderId(orderId);

        return orderItemsMapper.select(query);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComments(String orderId, String userId, List<OrderItemsCommentBO> commentBOList) {

        // 1.保存评价
        for(OrderItemsCommentBO oic:commentBOList){
            oic.setCommentId(sid.nextShort());
        }
        Map<String,Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("commentList",commentBOList);
        itemsCommentsMapperCustom.saveComments(map);


        // 2.修改订单表改已评价
        Orders orders=new Orders();
        orders.setId(orderId);
        orders.setIsComment(YesOrNo.YES.type);
        ordersMapper.updateByPrimaryKeySelective(orders);

        // 3.修改订单状态的留言时间
        OrderStatus orderStatus=new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());
        orderStatusMapper.updateByPrimaryKey(orderStatus);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize) {

        Map<String,Object> map=new HashMap<>();
        map.put("userId",userId);

        PageHelper.startPage(page,pageSize);

        List<MyCommentVO> list=itemsCommentsMapperCustom.queryMyComments(map);

        return setterPagedGrid(list,page);
    }

    /**
     * 分页代码提取
     * @param list
     * @param page
     * @return
     */
    private PagedGridResult setterPagedGrid(List<?> list,Integer page){
        PageInfo<?> pageList=new PageInfo<>(list);
        PagedGridResult grid=new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }
}
