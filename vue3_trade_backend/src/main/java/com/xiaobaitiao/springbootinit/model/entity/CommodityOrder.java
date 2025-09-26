package com.xiaobaitiao.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName commodity_order
 */
@TableName(value ="commodity_order")
@Data
public class CommodityOrder implements Serializable {
    /**
     * 订单 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 卖家 ID
     */
    private Long sellerId;

    /**
     * 商品 ID
     */
    private Long commodityId;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 购买数量（出售订单使用）
     */
    private Integer buyNumber;

    /**
     * 租用时长（租用订单使用）
     */
    private Integer rentalDuration;

    /**
     * 租用时长单位（HOUR/DAY等）
     */
    private String rentalUnit;

    /**
     * 订单总支付金额
     */
    private BigDecimal paymentAmount;

    /**
     * 订单对应交易类型：1-出售 2-出租
     */
    private Integer tradeType;

    /**
     * 0-未支付 1-已支付
     */
    private Integer payStatus;

    /**
     * 发货状态 0-未发货 1-已发货
     */
    private Integer deliveryStatus;

    /**
     * 发货内容（卡密/账号等）
     */
    private String deliveryContent;

    /**
     * 发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deliverTime;

    /**
     * 完成状态 0-未完成 1-已完成
     */
    private Integer finishStatus;

    /**
     * 完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date finishTime;

    /**
     * 租用开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date rentStartTime;

    /**
     * 租用结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date rentEndTime;

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