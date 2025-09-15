package com.xiaobaitiao.springbootinit.model.dto.userCommodityFavorites;

import com.xiaobaitiao.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询用户商品收藏表请求
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserCommodityFavoritesQueryRequest extends PageRequest implements Serializable {


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