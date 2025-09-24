package com.xiaobaitiao.springbootinit.model.dto.commodityScore;

import com.xiaobaitiao.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询商品评分表请求
 *
 * 
 * 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommodityScoreQueryRequest extends PageRequest implements Serializable {

    /**
     * 商品评分 ID
     */
    private Long id;

    /**
     * 商品 ID
     */
    private Long commodityId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 评分（0-5，星级评分）
     */
    private Integer score;

    /**
     * 评论内容
     */
    private String comment;

    private static final long serialVersionUID = 1L;
}