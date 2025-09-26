package com.xiaobaitiao.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 用户
 *
 * 
 * 
 */
@TableName(value = "user")
@Data
public class User implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

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
     * 出售功能申请状态（0-未申请 1-审核中 2-已通过 3-已拒绝）
     */
    private Integer sellApplyStatus;
    /**
     * 出租功能申请状态（0-未申请 1-审核中 2-已通过 3-已拒绝）
     */
    private Integer rentApplyStatus;
    /**
     * 用户 AI 剩余可使用次数
     */
    private Integer aiRemainNumber;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
