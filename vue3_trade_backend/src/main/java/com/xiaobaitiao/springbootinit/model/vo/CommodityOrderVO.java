package com.xiaobaitiao.springbootinit.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xiaobaitiao.springbootinit.model.entity.CommodityOrder;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品订单表视图
 *
 * 
 * 
 */
@Data
public class CommodityOrderVO implements Serializable {

    /**
     * 订单 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;
    /**
     * 联系人
     */
    private String userName;
    /**
     * 联系电话
     */
    private String userPhone;
    /**
     * 商品 ID
     */
    private Long commodityId;
    /**
     * 商品名称
     */
    private String commodityName;
    /**
     * 订单备注
     */
    private String remark;

    /**
     * 购买数量
     */
    private Integer buyNumber;

    /**
     * 租用时长（租赁订单使用）
     */
    private Integer rentalDuration;

    /**
     * 租用时长单位
     */
    private String rentalUnit;

    /**
     * 订单总支付金额
     */
    private BigDecimal paymentAmount;

    /**
     * 订单对应交易类型：1-出售 2-出租
     */
    private Integer tradeType;

    /**
     * 0-未支付 1-已支付
     */
    private Integer payStatus;

    /**
     * 租用开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date rentStartTime;

    /**
     * 租用结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date rentEndTime;

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

    /**
     * 封装类转对象
     *
     * @param commodityOrderVO
     * @return
     */
    public static CommodityOrder voToObj(CommodityOrderVO commodityOrderVO) {
        if (commodityOrderVO == null) {
            return null;
        }
        CommodityOrder commodityOrder = new CommodityOrder();
        BeanUtils.copyProperties(commodityOrderVO, commodityOrder);
        return commodityOrder;
    }

    /**
     * 对象转封装类
     *
     * @param commodityOrder
     * @return
     */
    public static CommodityOrderVO objToVo(CommodityOrder commodityOrder) {
        if (commodityOrder == null) {
            return null;
        }
        CommodityOrderVO commodityOrderVO = new CommodityOrderVO();
        BeanUtils.copyProperties(commodityOrder, commodityOrderVO);
        return commodityOrderVO;
    }
}
