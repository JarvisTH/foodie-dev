package com.imooc.controller;

import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "购物车接口controller" ,tags = {"购物车接口相关的api"})
@RestController
@RequestMapping("shopcart")
public class ShopcartController {

    @ApiOperation(value = "添加到购物车",notes = "添加到购物车",httpMethod = "POST")
    @PostMapping("/add")
    public Object add(@RequestParam String userId,
                      @RequestBody ShopcartBO shopcartBO,
                      HttpServletRequest request,
                      HttpServletResponse response){

        if(StringUtils.isBlank(userId)){
           return IMOOCJSONResult.errorMsg(null);
        }

        //TODO  前端用户登录情况下，添加商品到购物车，会同时在后端同步到Redis保存

        return IMOOCJSONResult.ok();
    }


    @ApiOperation(value = "从购物车中删除商品",notes = "从购物车中删除商品",httpMethod = "POST")
    @PostMapping("/del")
    public Object del(@RequestParam String userId,
                      @RequestParam String itemSpecId){
        if(StringUtils.isBlank(userId)||StringUtils.isBlank(itemSpecId)){
            return IMOOCJSONResult.errorMsg("参数不能为空");
        }

        //TODO  用户在页面删除购物车中的商品数据，如果用户此时已登录，则需要同步删除后端购物车中的商品

        return IMOOCJSONResult.ok();
    }
}
