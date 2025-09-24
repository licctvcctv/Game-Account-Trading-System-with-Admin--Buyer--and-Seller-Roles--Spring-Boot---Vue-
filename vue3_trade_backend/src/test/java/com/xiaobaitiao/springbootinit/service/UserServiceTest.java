package com.xiaobaitiao.springbootinit.service;

import javax.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.xiaobaitiao.springbootinit.exception.BusinessException;

/**
 * 用户服务测试
 *
 * 
 * 
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void userRegister() {
        String userAccount = "xiaobaitiao";
        String userPassword = "";
        String checkPassword = "123456";
        String phone = "13800000000";
        String realName = "张测试";
        String idCard = "11010519900101001X";
        Assertions.assertThrows(BusinessException.class, () ->
                userService.userRegister(userAccount, userPassword, checkPassword, phone, realName, idCard));
        Assertions.assertThrows(BusinessException.class, () ->
                userService.userRegister("yu", "12345678", "12345678", phone, realName, idCard));
    }
}
