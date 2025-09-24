package com.xiaobaitiao.springbootinit.model.dto.userCommodityFavorites;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新用户商品收藏表请求
 *
 * 
 * 
 */
@Data
public class UserCommodityFavoritesUpdateRequest implements Serializable {


    /**
     *
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
     * 1-正常收藏 0-取消收藏
     */
    private Integer status;

    /**
     * 用户备注
     */
    private String remark;

    private static final long serialVersionUID = 1L;
}