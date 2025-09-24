package com.xiaobaitiao.springbootinit.model.dto.commodityType;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新商品类别表请求
 *
 * 
 * 
 */
@Data
public class CommodityTypeUpdateRequest implements Serializable {
    /**
     * 商品分类 ID
     */
    private Long id;

    /**
     * 商品类别名称
     */
    private String typeName;

    private static final long serialVersionUID = 1L;
}