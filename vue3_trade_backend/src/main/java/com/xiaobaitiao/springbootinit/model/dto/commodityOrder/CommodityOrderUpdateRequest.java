package com.xiaobaitiao.springbootinit.model.dto.commodityOrder;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 更新商品订单请求
 */
@Data
public class CommodityOrderUpdateRequest implements Serializable {

    private Long id;

    private Long userId;

    private Long sellerId;

    private Long commodityId;

    private String remark;

    private Integer buyNumber;

    private Integer rentalDuration;

    private String rentalUnit;

    private BigDecimal paymentAmount;

    private Integer tradeType;

    private Integer payStatus;

    private Integer deliveryStatus;

    private Integer finishStatus;

    private static final long serialVersionUID = 1L;
}
