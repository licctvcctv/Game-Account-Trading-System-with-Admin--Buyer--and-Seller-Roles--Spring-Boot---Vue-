package com.xiaobaitiao.springbootinit.model.dto.commodityOrder;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 更新商品订单表请求
 *
 * 
 * 
 */
@Data
public class CommodityOrderUpdateRequest implements Serializable {

    /**
     * 订单 ID
     */
    private Long id;

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

    private static final long serialVersionUID = 1L;
}