package com.xiaobaitiao.springbootinit.model.dto.commodityType;

import com.xiaobaitiao.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询商品类别表请求
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommodityTypeQueryRequest extends PageRequest implements Serializable {

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