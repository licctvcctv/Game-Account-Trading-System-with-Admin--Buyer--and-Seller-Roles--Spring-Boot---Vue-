package com.xiaobaitiao.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName private_message
 */
@TableName(value ="private_message")
@Data
public class PrivateMessage implements Serializable {
    /**
     * 消息 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 发送者 ID
     */
    private Long senderId;

    /**
     * 接收者 ID
     */
    private Long recipientId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 0-未阅读 1-已阅读
     */
    private Integer alreadyRead;

    /**
     * 消息发送类型（用户发送还是管理员发送,user Or admin)枚举
     */
    private String type;

    /**
     * 是否撤回  0-未撤回 1-已撤回
     */
    private Integer isRecalled;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}