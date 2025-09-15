package com.xiaobaitiao.springbootinit.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xiaobaitiao.springbootinit.model.entity.PrivateMessage;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 私信表视图
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@Data
public class PrivateMessageVO implements Serializable {
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


    private static final long serialVersionUID = 1L;
    /**
     * 封装类转对象
     *
     * @param privateMessageVO
     * @return
     */
    public static PrivateMessage voToObj(PrivateMessageVO privateMessageVO) {
        if (privateMessageVO == null) {
            return null;
        }
        PrivateMessage privateMessage = new PrivateMessage();
        BeanUtils.copyProperties(privateMessageVO, privateMessage);
        return privateMessage;
    }

    /**
     * 对象转封装类
     *
     * @param privateMessage
     * @return
     */
    public static PrivateMessageVO objToVo(PrivateMessage privateMessage) {
        if (privateMessage == null) {
            return null;
        }
        PrivateMessageVO privateMessageVO = new PrivateMessageVO();
        BeanUtils.copyProperties(privateMessage, privateMessageVO);
        return privateMessageVO;
    }
}
