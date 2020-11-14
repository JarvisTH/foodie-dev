package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;
import com.imooc.service.UsersService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "注册登录",tags = {"用于注册登录的相关接口"})     //优化标题显示
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UsersService usersService;

    @ApiOperation(value = "用户名是否存在",notes = "用户名是否存在",httpMethod = "GET")    //解释方法含义
    @GetMapping("/usernameIsExist")
    public IMOOCJSONResult usernameIsExist(@RequestParam String username){

        //1.判断用户名不为空
        if(StringUtils.isBlank(username)){
            return IMOOCJSONResult.errorMsg("用户名不能为空");
        }

        //2.查找注册的用户名是否存在
        boolean isExist=usersService.queryUsernameIsExist(username);
        if(isExist){
            return IMOOCJSONResult.errorMsg("用户名已存在");
        }

        //3.请求成功，用户名没有重复
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户注册",notes = "用户注册",httpMethod = "POST")    //解释方法含义
    @PostMapping("/regist")
    public IMOOCJSONResult regist(@RequestBody UserBO userBO,HttpServletRequest request, HttpServletResponse response){

       String username=userBO.getUsername();
       String password=userBO.getPassword();
       String confirmPwd=userBO.getConfirmPassword();

       //1.判断用户名和密码不为空
        if(StringUtils.isBlank(username)||
                StringUtils.isBlank(password)||
                StringUtils.isBlank(confirmPwd)){
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }

        //2.查询用户名是否存在
        boolean isExist=usersService.queryUsernameIsExist(username);
        if(isExist){
            return IMOOCJSONResult.errorMsg("用户名已存在");
        }

        //3.密码长度不能小于6位
        if(password.length()<6){
            return IMOOCJSONResult.errorMsg("密码长度不能小于6");
        }

        //4.判断两次密码是否一致
        if(!password.equals(confirmPwd)){
            return IMOOCJSONResult.errorMsg("两次密码输入不一致");
        }

        //5.实现注册
        Users userResult=usersService.createUser(userBO);

        //关键信息保护
        userResult=setNullProperty(userResult);
        //设置cookie
        CookieUtils.setCookie(request,response,"user",
                JsonUtils.objectToJson(userResult),true);

        //TODO 生成用户token，存入redis会话
        //TODO 同步购物车数据

        return IMOOCJSONResult.ok();
    }


    @ApiOperation(value = "用户登录",notes = "用户登录",httpMethod = "POST")    //解释方法含义
    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBO userBO,HttpServletRequest request, HttpServletResponse response) throws Exception{

        String username=userBO.getUsername();
        String password=userBO.getPassword();

        //1.判断用户名和密码不为空
        if(StringUtils.isBlank(username)||
                StringUtils.isBlank(password)){
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }

        //2.实现登录
        Users userResult=usersService.queryUserForLogin(username, MD5Utils.getMD5Str(password));

        if(userResult==null){
            return IMOOCJSONResult.errorMsg("用户名或密码不正确");
        }

        //关键信息保护
        userResult=setNullProperty(userResult);
        //设置cookie
        CookieUtils.setCookie(request,response,"user",
                JsonUtils.objectToJson(userResult),true);

        //TODO 生成用户token，存入redis会话
        //TODO 同步购物车数据

        return IMOOCJSONResult.ok(userResult);
    }

    private Users setNullProperty(Users userResult){
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }

    @ApiOperation(value = "用户退出登录",notes = "用户退出登录",httpMethod = "POST")    //解释方法含义
    @PostMapping("/logout")
    public IMOOCJSONResult logout(@RequestParam String userId,HttpServletRequest request, HttpServletResponse response){

        //清楚用户信息cookie
        CookieUtils.deleteCookie(request,response,"user");

        //TODO 用户推出登录需要清空购物车
        //TODO 分布式会话中需要清除用户数据


        return IMOOCJSONResult.ok();
    }


}
