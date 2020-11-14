package com.imooc.controller;

import com.imooc.Application;
import com.imooc.service.UsersService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Jarvis(Tang Hui)
 * @version 1.0
 * @date 2020/10/21 23:10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class HelloTest {
    @Autowired
    private UsersService usersService;

    @Test
    public void test01(){
        usersService.queryUsernameIsExist("test");
    }

}
