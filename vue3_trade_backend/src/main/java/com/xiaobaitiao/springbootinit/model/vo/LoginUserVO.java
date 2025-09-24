package com.xiaobaitiao.springbootinit.model.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 已登录用户视图（脱敏）
 */
@Data
public class LoginUserVO implements Serializable {

    /**
     * 用户 id
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
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

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
     * 是否拥有出售权限（0-否 1-是）
     */
    private Integer sellPermission;

    /**
     * 是否拥有出租权限（0-否 1-是）
     */
    private Integer rentPermission;

    /**
     * 出售权限申请状态（0-未申请 1-审核中 2-已通过 3-已拒绝）
     */
    private Integer sellApplyStatus;

    /**
     * 出租权限申请状态（0-未申请 1-审核中 2-已通过 3-已拒绝）
     */
    private Integer rentApplyStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
