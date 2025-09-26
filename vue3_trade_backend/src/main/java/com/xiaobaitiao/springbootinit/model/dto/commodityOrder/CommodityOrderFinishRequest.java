package com.xiaobaitiao.springbootinit.model.dto.commodityOrder;

import java.io.Serializable;
import lombok.Data;

/**
 * 订单完成确认请求
 */
@Data
public class CommodityOrderFinishRequest implements Serializable {

    private Long id;

    private String reviewMessage;

    private static final long serialVersionUID = 1L;
}
