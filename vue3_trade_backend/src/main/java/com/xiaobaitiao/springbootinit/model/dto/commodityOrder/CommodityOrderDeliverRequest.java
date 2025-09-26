package com.xiaobaitiao.springbootinit.model.dto.commodityOrder;

import java.io.Serializable;
import lombok.Data;

/**
 * 订单发货请求
 */
@Data
public class CommodityOrderDeliverRequest implements Serializable {

    private Long id;

    private String deliveryContent;

    private static final long serialVersionUID = 1L;
}
