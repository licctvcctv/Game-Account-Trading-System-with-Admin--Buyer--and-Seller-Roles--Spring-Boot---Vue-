package com.xiaobaitiao.springbootinit.model.dto.commodityScore;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 编辑商品评分表请求
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@Data
public class CommodityScoreEditRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
}