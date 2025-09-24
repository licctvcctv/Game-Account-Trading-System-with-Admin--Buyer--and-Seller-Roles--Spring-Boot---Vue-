package com.xiaobaitiao.springbootinit.model.dto.commodityOrder;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 创建商品订单表请求
 *
 * 
 * 
 */
@Data
public class CommodityOrderAddRequest implements Serializable {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 商品 ID
     */
    private Long commodityId;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 购买数量
     */
    private Integer buyNumber;

    /**
     * 租用时长
     */
    private Integer rentalDuration;

    /**
     * 租用单位
     */
    private String rentalUnit;

    /**
     * 订单总支付金额
     */
    private BigDecimal paymentAmount;

    /**
     * 交易类型：1-出售 2-出租
     */
    private Integer tradeType;

    /**
     * 0-未支付 1-已支付
     */
    private Integer payStatus;

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

    private static final long serialVersionUID = 1L;
}