package com.xiaobaitiao.springbootinit.model.dto.commodityScore;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新商品评分表请求
 *
 * 
 * 
 */
@Data
public class CommodityScoreUpdateRequest implements Serializable {
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
     * 评分评论
     */
    private String comment;

    private static final long serialVersionUID = 1L;
}