package com.xiaobaitiao.springbootinit.model.dto.commodity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @date 2025/3/12 14:16
 * @gitee https://gitee.com/falle22222n-leaves/vue_-book-manage-system
 */
@Data
public class BuyCommodityRequest implements Serializable {

    /**
     * 商品 ID
     */
    private Long commodityId;


    /**
     * 购买商品的数量
     */
    private Integer buyNumber;

    /**
     * 租用时长（租用订单使用）
     */
    private Integer rentalDuration;

    /**
     * 租用单位（HOUR/DAY）
     */
    private String rentalUnit;
    /**
     * 支付金额
     */
    private BigDecimal paymentAmount;
    /**
     * 支付状态（1已支付，0未支付）默认0
     */
    private Integer payStatus;

    /**
     * 订单备注
     */
    private String remark;

    private static final long serialVersionUID = 1L;
}
