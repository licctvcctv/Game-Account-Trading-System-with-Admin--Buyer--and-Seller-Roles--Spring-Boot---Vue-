package com.xiaobaitiao.springbootinit.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xiaobaitiao.springbootinit.model.entity.Commodity;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品表视图
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@Data
public class CommodityVO implements Serializable {

    /**
     * 商品 ID
     */
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
     * 商品分类名称
     */
    private String commodityTypeName;

    /**
     * 管理员 ID （某人创建该商品）
     */
    private Long adminId;
    /**
     * 管理员昵称
     */
    private String adminName;
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


    private static final long serialVersionUID = 1L;
    /**
     * 封装类转对象
     *
     * @param commodityVO
     * @return
     */
    public static Commodity voToObj(CommodityVO commodityVO) {
        if (commodityVO == null) {
            return null;
        }
        Commodity commodity = new Commodity();
        BeanUtils.copyProperties(commodityVO, commodity);
        return commodity;
    }

    /**
     * 对象转封装类
     *
     * @param commodity
     * @return
     */
    public static CommodityVO objToVo(Commodity commodity) {
        if (commodity == null) {
            return null;
        }
        CommodityVO commodityVO = new CommodityVO();
        BeanUtils.copyProperties(commodity, commodityVO);
        return commodityVO;
    }
}
