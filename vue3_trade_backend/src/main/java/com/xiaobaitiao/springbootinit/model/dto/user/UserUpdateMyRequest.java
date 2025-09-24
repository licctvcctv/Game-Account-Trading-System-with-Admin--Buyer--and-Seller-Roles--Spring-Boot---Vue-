package com.xiaobaitiao.springbootinit.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户更新个人信息请求
 *
 * 
 * 
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 联系方式
     */
    private String userPhone;

    /**
     * 用户实名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCardNumber;

    private static final long serialVersionUID = 1L;
}