package com.xiaobaitiao.springbootinit.model.dto.user;

import com.xiaobaitiao.springbootinit.common.PageRequest;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

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
    /**
     * 用户余额
     */
    private BigDecimal balance;
    /**
     * 用户 AI 剩余可使用次数
     */
    private Integer aiRemainNumber;
    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 是否拥有出售权限
     */
    private Integer sellPermission;

    /**
     * 是否拥有出租权限
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
