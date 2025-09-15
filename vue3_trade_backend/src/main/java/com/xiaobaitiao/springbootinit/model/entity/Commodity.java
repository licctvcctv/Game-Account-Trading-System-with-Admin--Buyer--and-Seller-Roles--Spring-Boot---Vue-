package com.xiaobaitiao.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName commodity
 */
@TableName(value ="commodity")
@Data
public class Commodity implements Serializable {
    /**
     * 商品 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 商品名称
     */
    private String commodityName;

    /**
     * 商品简介
     */
    private String commodityDescription;

    /**
     * 商品封面图
     */
    private String commodityAvatar;

    /**
     * 商品新旧程度（例如 9成新）
     */
    private String degree;

    /**
     * 商品分类 ID
     */
    private Long commodityTypeId;

    /**
     * 管理员 ID （某人创建该商品）
     */
    private Long adminId;

    /**
     * 是否上架（默认0未上架，1已上架）
     */
    private Integer isListed;

    /**
     * 商品数量（默认0）
     */
    private Integer commodityInventory;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品浏览量
     */
    private Integer viewNum;

    /**
     * 商品收藏量
     */
    private Integer favourNum;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}