package com.xiaobaitiao.springbootinit.model.dto.commodityOrder;

import com.xiaobaitiao.springbootinit.common.PageRequest;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品订单查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommodityOrderQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private Long userId;

    private String userName;

    private String userPhone;

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
