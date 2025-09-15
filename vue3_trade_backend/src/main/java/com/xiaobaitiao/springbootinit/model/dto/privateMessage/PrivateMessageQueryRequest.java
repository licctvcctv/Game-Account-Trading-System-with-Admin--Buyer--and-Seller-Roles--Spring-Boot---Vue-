package com.xiaobaitiao.springbootinit.model.dto.privateMessage;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xiaobaitiao.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 查询私信表请求
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PrivateMessageQueryRequest extends PageRequest implements Serializable {

    /**
     * 消息 ID
     */
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


    private static final long serialVersionUID = 1L;
}