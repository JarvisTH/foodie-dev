package com.imooc.controller.center;

import com.imooc.pojo.Users;
import com.imooc.service.center.CenterUsersService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jarvis(Tang Hui)
 * @version 1.0
 * @date 2020/4/12 12:06
 */
@Api(value = "center 用户中心",tags = {"用户中心展示的相关接口"})
@RestController
@RequestMapping("center")
public class CenterController {

    @Autowired
    private CenterUsersService centerUsersService;

    @ApiOperation(value = "获取用户信息",notes = "获取用户信息",httpMethod = "GET")
    @GetMapping("userInfo")
    public IMOOCJSONResult userInfo(
            @ApiParam(name = "userId",value = "用户id",required = true)
            @RequestParam String userId){

        Users user=centerUsersService.queryUserInfo(userId);
        return IMOOCJSONResult.ok(user);
    }

}
