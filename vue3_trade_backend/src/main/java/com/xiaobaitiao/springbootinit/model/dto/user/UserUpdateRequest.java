package com.xiaobaitiao.springbootinit.model.dto.user;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * 用户更新请求
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

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
     * 用户角色：user/admin/ban
     */
    private String userRole;
    /**
     * 用户余额
     */
    private BigDecimal balance;
    /**
     * 用户 AI 剩余可使用次数
     */
    private Integer aiRemainNumber;

    /**
     * 出售权限
     */
    private Integer sellPermission;

    /**
     * 出租权限
     */
    private Integer rentPermission;

    /**
     * 出售申请状态
     */
    private Integer sellApplyStatus;

    /**
     * 出租申请状态
     */
    private Integer rentApplyStatus;
    private static final long serialVersionUID = 1L;
}
