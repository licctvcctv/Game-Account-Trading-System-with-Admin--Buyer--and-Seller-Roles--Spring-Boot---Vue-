package com.xiaobaitiao.springbootinit.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xiaobaitiao.springbootinit.model.entity.CommodityScore;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 商品评分表视图
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@Data
public class CommodityScoreVO implements Serializable {

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
     * 关联查询用户
     */
    private UserVO userVO;
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



    private static final long serialVersionUID = 1L;
    /**
     * 封装类转对象
     *
     * @param commodityScoreVO
     * @return
     */
    public static CommodityScore voToObj(CommodityScoreVO commodityScoreVO) {
        if (commodityScoreVO == null) {
            return null;
        }
        CommodityScore commodityScore = new CommodityScore();
        BeanUtils.copyProperties(commodityScoreVO, commodityScore);
        return commodityScore;
    }

    /**
     * 对象转封装类
     *
     * @param commodityScore
     * @return
     */
    public static CommodityScoreVO objToVo(CommodityScore commodityScore) {
        if (commodityScore == null) {
            return null;
        }
        CommodityScoreVO commodityScoreVO = new CommodityScoreVO();
        BeanUtils.copyProperties(commodityScore, commodityScoreVO);
        return commodityScoreVO;
    }
}
