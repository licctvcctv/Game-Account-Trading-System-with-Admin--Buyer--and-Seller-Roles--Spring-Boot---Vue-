package com.xiaobaitiao.springbootinit.model.dto.commodityOrder;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 创建商品订单表请求
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@Data
public class CommodityOrderAddRequest implements Serializable {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 商品 ID
     */
    private Long commodityId;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 购买数量
     */
    private Integer buyNumber;

    /**
     * 订单总支付金额
     */
    private BigDecimal paymentAmount;

    /**
     * 0-未支付 1-已支付
     */
    private Integer payStatus;

    private static final long serialVersionUID = 1L;
}